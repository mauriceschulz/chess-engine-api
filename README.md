# chess-engine-api

Spring Boot API fuer die Chess Engine.

## Lokal starten

```bash
mvn spring-boot:run
```

Die API laeuft standardmaessig auf Port `8080`.

```bash
curl http://localhost:8080/api/health
```

## Docker

```bash
docker compose up -d --build
```

Optional kann der Host-Port angepasst werden:

```bash
CHESS_ENGINE_API_PORT=8081 docker compose up -d --build
```

## Deployment

Der GitHub Actions Workflow liegt in `.github/workflows/deploy.yml` und deployed bei jedem Push auf `main` nach `/opt/chess-engine-api`.

Benoetigte Repository Secrets:

```text
SSH_PRIVATE_KEY
CLONE_GITHUB_PRIVATE_KEY
SERVER_IP
```

Details stehen in `GITHUB_ACTIONS_DEPLOYMENT.md`.
