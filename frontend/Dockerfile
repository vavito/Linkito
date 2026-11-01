FROM node:24-alpine AS dependencies

WORKDIR /app
COPY package.json package-lock.json ./
RUN npm ci

FROM node:24-alpine AS build

WORKDIR /app
COPY --from=dependencies /app/node_modules ./node_modules
COPY . .

ARG NEXT_PUBLIC_PUBLIC_URL
ENV NEXT_PUBLIC_PUBLIC_URL=${NEXT_PUBLIC_PUBLIC_URL}
ENV NEXT_TELEMETRY_DISABLED=1

RUN node -e 'if (!/^https:\/\//.test(process.env.NEXT_PUBLIC_PUBLIC_URL || "")) { console.error("NEXT_PUBLIC_PUBLIC_URL must be an https URL"); process.exit(1); }' \
    && npm run build

FROM node:24-alpine AS runtime

ENV NODE_ENV=production \
    NEXT_TELEMETRY_DISABLED=1 \
    HOSTNAME=0.0.0.0 \
    PORT=3000

WORKDIR /app
RUN addgroup -S -g 10001 nextjs \
    && adduser -S -u 10001 -G nextjs nextjs

COPY --from=build --chown=nextjs:nextjs /app/.next/standalone ./
COPY --from=build --chown=nextjs:nextjs /app/.next/static ./.next/static

USER nextjs
EXPOSE 3000
CMD ["node", "server.js"]
