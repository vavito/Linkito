#!/usr/bin/env bash
set -Eeuo pipefail

readonly AWS_REGION="us-east-1"
readonly ECR_REGISTRY="482545366347.dkr.ecr.us-east-1.amazonaws.com"
readonly CONTAINER_NAME="linkito-frontend"
readonly CANDIDATE_NAME="linkito-frontend-candidate"
readonly RETIRING_NAME="linkito-frontend-retiring"
readonly PREVIOUS_NAME="linkito-frontend-previous"
readonly NETWORK="linkito-net"
readonly MEMORY_LIMIT="384m"
readonly CPU_LIMIT="1"
readonly CANDIDATE_HOST_PORT="13000"
readonly PRODUCTION_HOST_PORT="3000"
readonly TLS_IP="100.58.229.18"
readonly HEALTH_ATTEMPTS=60
readonly HEALTH_INTERVAL_SECONDS=2

image_ref="${1:-}"
commit_sha="${2:-}"
expected_image="$ECR_REGISTRY/linkito-frontend:sha-$commit_sha"
old_container_id=""
swap_started=0
docker_config_dir=""

if [[ ! "$commit_sha" =~ ^[0-9a-f]{40}$ || "$image_ref" != "$expected_image" ]]; then
  echo "Invalid frontend image reference or commit SHA." >&2
  exit 2
fi

if ! docker inspect "$CONTAINER_NAME" >/dev/null 2>&1; then
  echo "Expected current frontend container was not found; refusing deployment." >&2
  exit 1
fi

if ! docker network inspect "$NETWORK" >/dev/null 2>&1; then
  echo "Expected Docker network was not found; refusing deployment." >&2
  exit 1
fi

if docker inspect "$CANDIDATE_NAME" >/dev/null 2>&1 || docker inspect "$RETIRING_NAME" >/dev/null 2>&1; then
  echo "A prior frontend deployment left a candidate or retiring container; refusing to continue." >&2
  exit 1
fi

if docker inspect "$PREVIOUS_NAME" >/dev/null 2>&1; then
  previous_state="$(docker inspect --format '{{.State.Status}}' "$PREVIOUS_NAME")"
  if [[ "$previous_state" != "exited" ]]; then
    echo "The previous frontend container is not stopped; refusing deployment." >&2
    exit 1
  fi
fi

current_image="$(docker inspect --format '{{.Config.Image}}' "$CONTAINER_NAME")"
current_state="$(docker inspect --format '{{.State.Status}}' "$CONTAINER_NAME")"
current_memory="$(docker inspect --format '{{.HostConfig.Memory}}' "$CONTAINER_NAME")"
current_nano_cpus="$(docker inspect --format '{{.HostConfig.NanoCpus}}' "$CONTAINER_NAME")"
current_ports="$(docker inspect --format '{{json .HostConfig.PortBindings}}' "$CONTAINER_NAME")"
current_networks="$(docker inspect --format '{{range $name, $config := .NetworkSettings.Networks}}{{$name}} {{end}}' "$CONTAINER_NAME")"
backend_url="$(docker inspect --format '{{range .Config.Env}}{{println .}}{{end}}' "$CONTAINER_NAME" | awk -F= '$1 == "BACKEND_URL" {sub(/^[^=]*=/, ""); print; count++} END {if (count != 1) exit 1}')"

if [[ "$current_state" != "running" || "$current_memory" != "402653184" || "$current_nano_cpus" != "1000000000" ]]; then
  echo "Current frontend state or resource limits differ from the expected configuration; refusing deployment." >&2
  exit 1
fi

if [[ "$current_ports" != *'"HostIp":"127.0.0.1"'* || "$current_ports" != *'"HostPort":"3000"'* || " $current_networks " != *" $NETWORK "* ]]; then
  echo "Current frontend port binding or Docker network differs from the expected configuration; refusing deployment." >&2
  exit 1
fi

if [[ "$backend_url" != "http://linkito-backend:8080" ]]; then
  echo "Current frontend BACKEND_URL differs from the expected internal backend address; refusing deployment." >&2
  exit 1
fi

old_container_id="$(docker inspect --format '{{.Id}}' "$CONTAINER_NAME")"
docker_config_dir="$(mktemp -d /tmp/linkito-frontend-ecr-auth.XXXXXX)"
chmod 700 "$docker_config_dir"
export DOCKER_CONFIG="$docker_config_dir"

wait_for_health() {
  local port="$1"
  local label="$2"
  local attempt

  for ((attempt = 1; attempt <= HEALTH_ATTEMPTS; attempt++)); do
    if curl --fail --silent --max-time 5 "http://127.0.0.1:${port}/" >/dev/null 2>&1; then
      echo "$label root health=200"
      return 0
    fi
    sleep "$HEALTH_INTERVAL_SECONDS"
  done

  echo "$label root health failed after $((HEALTH_ATTEMPTS * HEALTH_INTERVAL_SECONDS)) seconds." >&2
  return 1
}

api_status() {
  local port="$1"
  curl --silent --show-error --max-time 10 \
    --output /dev/null --write-out '%{http_code}' \
    "http://127.0.0.1:${port}/api/auth/me"
}

check_api_proxy() {
  local port="$1"
  local label="$2"
  local status

  status="$(api_status "$port")"
  if [[ "$status" != "$expected_api_status" ]]; then
    echo "$label API proxy returned HTTP $status; expected HTTP $expected_api_status." >&2
    return 1
  fi
  echo "$label API proxy=HTTP $status (matches current unauthenticated response)"
}

verify_public_frontend() {
  local label="$1"
  local status

  curl --fail --silent --show-error --max-time 15 \
    --connect-to "$TLS_IP:443:127.0.0.1:443" \
    "https://$TLS_IP/" >/dev/null
  echo "$label HTTPS root=200"

  status="$(curl --silent --show-error --max-time 15 \
    --connect-to "$TLS_IP:443:127.0.0.1:443" \
    --output /dev/null --write-out '%{http_code}' \
    "https://$TLS_IP/api/auth/me")"
  if [[ "$status" != "$expected_api_status" ]]; then
    echo "$label HTTPS API proxy returned HTTP $status; expected HTTP $expected_api_status." >&2
    return 1
  fi
  echo "$label HTTPS API proxy=HTTP $status"
}

verify_stability() {
  local port="$1"
  local label="$2"
  local attempt

  for attempt in {1..6}; do
    sleep 5
    curl --fail --silent --max-time 5 "http://127.0.0.1:${port}/" >/dev/null
    check_api_proxy "$port" "$label"
  done

  echo "$label stability=30s"
}

restore_previous() {
  set +e

  if docker inspect "$CONTAINER_NAME" >/dev/null 2>&1; then
    current_container_id="$(docker inspect --format '{{.Id}}' "$CONTAINER_NAME" 2>/dev/null)"
    if [[ "$current_container_id" != "$old_container_id" ]]; then
      docker rm -f "$CONTAINER_NAME" >/dev/null 2>&1
    fi
  fi

  if docker inspect "$RETIRING_NAME" >/dev/null 2>&1; then
    docker rename "$RETIRING_NAME" "$CONTAINER_NAME" >/dev/null 2>&1
    docker start "$CONTAINER_NAME" >/dev/null 2>&1
  elif docker inspect "$CONTAINER_NAME" >/dev/null 2>&1; then
    current_container_state="$(docker inspect --format '{{.State.Status}}' "$CONTAINER_NAME" 2>/dev/null)"
    if [[ "$current_container_state" != "running" ]]; then
      docker start "$CONTAINER_NAME" >/dev/null 2>&1
    fi
  fi

  if docker inspect "$CONTAINER_NAME" >/dev/null 2>&1; then
    wait_for_health "$PRODUCTION_HOST_PORT" rollback || return 1
    verify_public_frontend rollback || return 1
    return 0
  fi

  echo "Could not restore the original frontend container; manual investigation is required." >&2
  return 1
}

finish() {
  local status=$?
  trap - EXIT
  set +e

  if [[ "$status" -ne 0 ]]; then
    echo "Frontend deployment failed; restoring the previous container if the swap started."
    docker rm -f "$CANDIDATE_NAME" >/dev/null 2>&1
    if [[ "$swap_started" -eq 1 ]]; then
      if ! restore_previous; then
        echo "Frontend rollback health check failed; manual investigation is required." >&2
      fi
    fi
  fi

  if [[ -n "$docker_config_dir" ]]; then
    docker logout "$ECR_REGISTRY" >/dev/null 2>&1
    if [[ "$docker_config_dir" == /tmp/linkito-frontend-ecr-auth.* ]]; then
      rm -rf -- "$docker_config_dir"
    fi
  fi
  exit "$status"
}
trap finish EXIT

docker login --username AWS --password-stdin "$ECR_REGISTRY" \
  < <(aws ecr get-login-password --region "$AWS_REGION") >/dev/null
docker pull "$image_ref"
docker image inspect "$image_ref" >/dev/null

docker run --detach \
  --name "$CANDIDATE_NAME" \
  --network "$NETWORK" \
  --publish "127.0.0.1:${CANDIDATE_HOST_PORT}:3000" \
  --env "BACKEND_URL=$backend_url" \
  --memory "$MEMORY_LIMIT" \
  --cpus "$CPU_LIMIT" \
  --restart no \
  "$image_ref" >/dev/null

expected_api_status="$(api_status "$PRODUCTION_HOST_PORT")"
if [[ "$expected_api_status" != "401" && "$expected_api_status" != "403" ]]; then
  echo "Current frontend API proxy returned unexpected unauthenticated status HTTP $expected_api_status." >&2
  exit 1
fi
echo "current API proxy baseline=HTTP $expected_api_status"

wait_for_health "$CANDIDATE_HOST_PORT" candidate
check_api_proxy "$CANDIDATE_HOST_PORT" candidate
docker rm -f "$CANDIDATE_NAME" >/dev/null

swap_started=1
docker stop --time 20 "$CONTAINER_NAME" >/dev/null
docker rename "$CONTAINER_NAME" "$RETIRING_NAME"
docker run --detach \
  --name "$CONTAINER_NAME" \
  --network "$NETWORK" \
  --log-driver awslogs \
  --log-opt "awslogs-region=$AWS_REGION" \
  --log-opt "awslogs-group=/linkito/frontend" \
  --log-opt "awslogs-stream=linkito-frontend" \
  --log-opt awslogs-create-group=false \
  --publish "127.0.0.1:${PRODUCTION_HOST_PORT}:3000" \
  --env "BACKEND_URL=$backend_url" \
  --memory "$MEMORY_LIMIT" \
  --cpus "$CPU_LIMIT" \
  --restart unless-stopped \
  "$image_ref" >/dev/null

wait_for_health "$PRODUCTION_HOST_PORT" production
check_api_proxy "$PRODUCTION_HOST_PORT" production
verify_public_frontend production
verify_stability "$PRODUCTION_HOST_PORT" production
verify_public_frontend production

docker rm -f "$PREVIOUS_NAME" >/dev/null 2>&1 || true
docker rename "$RETIRING_NAME" "$PREVIOUS_NAME"

echo "deployment=success"
echo "commit=$commit_sha"
echo "image=$image_ref"
echo "image_before_deploy=$current_image"
echo "previous_container=$PREVIOUS_NAME"
