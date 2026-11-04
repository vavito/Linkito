#!/usr/bin/env bash
set -Eeuo pipefail

readonly AWS_REGION="us-east-1"
readonly ECR_REGISTRY="482545366347.dkr.ecr.us-east-1.amazonaws.com"
readonly CONTAINER_NAME="linkito-backend"
readonly CANDIDATE_NAME="linkito-backend-candidate"
readonly RETIRING_NAME="linkito-backend-retiring"
readonly PREVIOUS_NAME="linkito-backend-previous"
readonly ENV_FILE="/opt/linkito/backend.env"
readonly NETWORK="linkito-net"
readonly MEMORY_LIMIT="768m"
readonly CPU_LIMIT="1"
readonly HEALTH_ATTEMPTS=60
readonly HEALTH_INTERVAL_SECONDS=2

image_ref="${1:-}"
commit_sha="${2:-}"
expected_image="$ECR_REGISTRY/linkito-backend:sha-$commit_sha"
old_container_id=""
swap_started=0

if [[ ! "$commit_sha" =~ ^[0-9a-f]{40}$ || "$image_ref" != "$expected_image" ]]; then
  echo "Invalid image reference or commit SHA." >&2
  exit 2
fi

if [[ ! -s "$ENV_FILE" ]]; then
  echo "Backend environment file is missing or empty; existing service was not changed." >&2
  exit 1
fi

if ! docker inspect "$CONTAINER_NAME" >/dev/null 2>&1; then
  echo "Expected current backend container was not found; refusing deployment." >&2
  exit 1
fi

if ! docker network inspect "$NETWORK" >/dev/null 2>&1; then
  echo "Expected Docker network was not found; refusing deployment." >&2
  exit 1
fi

if docker inspect "$RETIRING_NAME" >/dev/null 2>&1; then
  echo "A prior interrupted deployment left a retiring container; refusing to continue." >&2
  exit 1
fi

old_container_id="$(docker inspect --format '{{.Id}}' "$CONTAINER_NAME")"

wait_for_health() {
  local port="$1"
  local label="$2"
  local attempt

  for ((attempt = 1; attempt <= HEALTH_ATTEMPTS; attempt++)); do
    if curl --fail --silent --show-error --max-time 5 \
      "http://127.0.0.1:${port}/actuator/health" >/dev/null 2>&1; then
      echo "$label health=200"
      return 0
    fi
    sleep "$HEALTH_INTERVAL_SECONDS"
  done

  echo "$label health check failed after $((HEALTH_ATTEMPTS * HEALTH_INTERVAL_SECONDS)) seconds." >&2
  return 1
}

verify_stability() {
  local attempt

  for attempt in {1..6}; do
    sleep 5
    if ! curl --fail --silent --show-error --max-time 5 \
      "http://127.0.0.1:8080/actuator/health" >/dev/null 2>&1; then
      echo "production health check failed during the 30-second stability window." >&2
      return 1
    fi
  done

  echo "production stability=30s"
}

finish() {
  local status=$?
  trap - EXIT
  set +e

  if [[ "$status" -ne 0 ]]; then
    echo "Deployment failed; restoring the previous backend if the swap started."
    docker rm -f "$CANDIDATE_NAME" >/dev/null 2>&1

    if [[ "$swap_started" -eq 1 ]]; then
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
        docker start "$CONTAINER_NAME" >/dev/null 2>&1
      fi

      if docker inspect "$CONTAINER_NAME" >/dev/null 2>&1; then
        wait_for_health 8080 rollback || echo "rollback health check failed; manual investigation is required." >&2
      fi
    fi
  fi

  docker logout "$ECR_REGISTRY" >/dev/null 2>&1
  exit "$status"
}
trap finish EXIT

docker rm -f "$CANDIDATE_NAME" >/dev/null 2>&1 || true
aws ecr get-login-password --region "$AWS_REGION" \
  | docker login --username AWS --password-stdin "$ECR_REGISTRY" >/dev/null
docker pull "$image_ref"
docker image inspect "$image_ref" >/dev/null

docker run --detach \
  --name "$CANDIDATE_NAME" \
  --network "$NETWORK" \
  --publish 127.0.0.1:18080:8080 \
  --env-file "$ENV_FILE" \
  --memory "$MEMORY_LIMIT" \
  --cpus "$CPU_LIMIT" \
  --restart no \
  "$image_ref" >/dev/null
wait_for_health 18080 candidate
docker rm -f "$CANDIDATE_NAME" >/dev/null

swap_started=1
docker stop --time 20 "$CONTAINER_NAME" >/dev/null
docker rename "$CONTAINER_NAME" "$RETIRING_NAME"
docker run --detach \
  --name "$CONTAINER_NAME" \
  --network "$NETWORK" \
  --publish 127.0.0.1:8080:8080 \
  --env-file "$ENV_FILE" \
  --memory "$MEMORY_LIMIT" \
  --cpus "$CPU_LIMIT" \
  --restart unless-stopped \
  "$image_ref" >/dev/null
wait_for_health 8080 production
verify_stability

docker rm -f "$PREVIOUS_NAME" >/dev/null 2>&1 || true
docker rename "$RETIRING_NAME" "$PREVIOUS_NAME"

echo "deployment=success"
echo "commit=$commit_sha"
echo "image=$image_ref"
