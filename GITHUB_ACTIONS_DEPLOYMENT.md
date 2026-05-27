# GitHub Actions Deployment

Diese Datei beschreibt den aktuellen Deployment-Workflow fuer das Portfolio und wie du das gleiche Prinzip fuer die Chess-Engine-API verwenden kannst.

## Portfolio Workflow

Der Workflow liegt in:

```text
.github/workflows/deploy.yml
```

Er startet automatisch bei jedem Push auf den Branch `main`:

```yaml
on:
  push:
    branches:
      - main
```

Das bedeutet: Sobald Code nach `main` gepusht wird, verbindet sich GitHub Actions mit dem Server und deployed den aktuellen Stand.

## Benutzte Secrets

Der Workflow erwartet diese GitHub Repository Secrets:

```text
SSH_PRIVATE_KEY
CLONE_GITHUB_PRIVATE_KEY
SERVER_IP
DB_PASSWORD
APP_KEY
```

`SSH_PRIVATE_KEY` erlaubt GitHub Actions, sich per SSH auf dem Server einzuloggen.

`CLONE_GITHUB_PRIVATE_KEY` wird ueber SSH Agent Forwarding an den Server weitergereicht, damit der Server das private GitHub Repository per SSH pullen kann.

`SERVER_IP` ist die IP-Adresse des Servers.

`DB_PASSWORD` und `APP_KEY` werden beim Deployment in die `.env` geschrieben.

## Ablauf

1. GitHub checkt den Code im Action Runner aus.
2. Der SSH Agent wird mit den privaten Keys aus den Secrets gestartet.
3. GitHub Actions verbindet sich per SSH mit `root@SERVER_IP`.
4. Auf dem Server wird nach `/opt/portfolio` gewechselt.
5. Das Git Remote wird auf `git@github.com:mauriceschulz/portfolio.git` gesetzt.
6. Der Server holt den neuesten Stand von `origin/main`.
7. `git reset --hard origin/main` setzt den Server-Code exakt auf den aktuellen Main-Stand.
8. Laravel Storage- und Cache-Ordner werden erstellt und berechtigt.
9. Eine neue `.env` wird aus `.env.example` erzeugt.
10. Secrets werden in die `.env` geschrieben.
11. `docker compose up -d --build` baut und startet die Container neu.

## Docker Setup

Das Portfolio besteht aus drei Services:

```text
app    PHP-FPM Laravel Container
nginx  Webserver Container
db     PostgreSQL Container
```

Der `app` Container baut Composer-Dependencies, Node-Dependencies und Vite Assets:

```dockerfile
RUN composer install --no-dev --optimize-autoloader
RUN npm ci && npm run build
```

Der `nginx` Container nimmt HTTP/HTTPS Traffic an und leitet PHP Requests an `app:9000` weiter.

## Wichtiger Punkt: public/build

`public/build` ist in Git ignoriert und wird nicht committed. Die Assets entstehen auf dem Server beim Docker Build durch:

```bash
npm run build
```

Weil `public` als Docker Volume zwischen App und Nginx geteilt wird, kopiert der App Container beim Start die frisch gebauten Public-Dateien in das Volume.

## Engine-API genauso deployen

Fuer die Chess-Engine-API ist das minimale Setup jetzt im Repository angelegt:

```text
Dockerfile
docker-compose.yml
.github/workflows/deploy.yml
```

Der Workflow deployed bei jedem Push auf `main` nach:

```text
/opt/chess-engine-api
```

Falls das Repository dort noch nicht existiert, cloned der Workflow es beim ersten Lauf automatisch.

## Server Vorbereitung

Auf dem Server muessen Docker und Docker Compose installiert sein. Danach reicht es, wenn GitHub Actions sich als `root` verbinden kann und der weitergereichte GitHub-Key Zugriff auf das Repository hat.

Optional kannst du den Ordner vorab manuell anlegen:

```bash
mkdir -p /opt/chess-engine-api
```

Der Workflow cloned sonst selbst nach `/opt/chess-engine-api`.

## Benoetigte Secrets fuer dieses Repository

```text
SSH_PRIVATE_KEY
CLONE_GITHUB_PRIVATE_KEY
SERVER_IP
```

`SSH_PRIVATE_KEY` erlaubt GitHub Actions, sich per SSH auf dem Server einzuloggen.

`CLONE_GITHUB_PRIVATE_KEY` wird ueber SSH Agent Forwarding an den Server weitergereicht, damit der Server das private GitHub Repository per SSH clonen oder pullen kann.

`SERVER_IP` ist die IP-Adresse des Servers.

## Deployment Ablauf der Engine

1. GitHub Actions startet bei Push auf `main`.
2. Der SSH Agent wird mit `SSH_PRIVATE_KEY` und `CLONE_GITHUB_PRIVATE_KEY` gestartet.
3. GitHub Actions verbindet sich per SSH mit `root@SERVER_IP`.
4. Auf dem Server wird `/opt/chess-engine-api` vorbereitet.
5. Falls noch kein Git Repository existiert, wird `git@github.com:mauriceschulz/chess-engine-api.git` cloned.
6. Der Server holt `origin/main`.
7. `git reset --hard origin/main` setzt den Server-Code exakt auf den aktuellen Main-Stand.
8. `docker compose up -d --build` baut und startet die API neu.

## Manuell auf dem Server testen

```bash
cd /opt/chess-engine-api
docker compose up -d --build
curl http://localhost:8080/api/health
```

Der Container heisst:

```text
chess-engine-api
```

Der Host-Port kann in einer `.env` neben der `docker-compose.yml` angepasst werden:

```env
CHESS_ENGINE_API_PORT=8081
```

## Urspruenglicher Plan

Der folgende Abschnitt beschreibt das Prinzip aus dem Portfolio-Projekt und bleibt als Referenz erhalten.

Fuer die Chess-Engine-API kannst du denselben Ablauf verwenden:

1. Engine Repository auf dem Server ablegen, zum Beispiel:

```text
/opt/chess-engine-api
```

2. Im Engine Repository ein eigenes `docker-compose.yml` anlegen.
3. In GitHub ein eigenes Workflow File anlegen, zum Beispiel:

```text
.github/workflows/deploy.yml
```

4. Die gleichen SSH Secrets verwenden:

```text
SSH_PRIVATE_KEY
CLONE_GITHUB_PRIVATE_KEY
SERVER_IP
```

5. Der Workflow deployed dann nach `/opt/chess-engine-api` und startet dort Docker Compose.

Beispiel:

```yaml
name: Deploy Engine API

on:
  push:
    branches:
      - main

jobs:
  deploy:
    runs-on: ubuntu-latest

    steps:
      - name: Checkout code
        uses: actions/checkout@v4

      - name: Setup SSH agent
        uses: webfactory/ssh-agent@v0.8.0
        with:
          ssh-private-key: |
            ${{ secrets.SSH_PRIVATE_KEY }}
            ${{ secrets.CLONE_GITHUB_PRIVATE_KEY }}

      - name: Deploy via forwarded SSH agent
        run: |
          ssh -A -o StrictHostKeyChecking=no root@${{ secrets.SERVER_IP }} "
            mkdir -p ~/.ssh &&
            chmod 700 ~/.ssh &&
            ssh-keyscan github.com >> ~/.ssh/known_hosts 2>/dev/null || true &&
            cd /opt/chess-engine-api &&
            git remote set-url origin git@github.com:mauriceschulz/chess-engine-api.git &&
            git fetch origin &&
            git reset --hard origin/main &&
            docker compose up -d --build
          "
```

## Engine Docker Compose Beispiel

Wenn die Engine eine Spring Boot App ist, kann das Compose File ungefaehr so aussehen:

```yaml
services:
  chess-engine-api:
    build:
      context: .
      dockerfile: Dockerfile
    image: chess-engine-api
    container_name: chess-engine-api
    restart: unless-stopped
    ports:
      - "8080:8080"
```

Wenn die Engine nur vom Portfolio Server intern erreicht werden soll, ist es besser, sie nicht oeffentlich auf `8080` zu veroeffentlichen, sondern beide Compose Setups ueber ein gemeinsames Docker Network zu verbinden.

## Portfolio mit Engine verbinden

Das Portfolio liest die Engine URL aus:

```text
CHESS_ENGINE_URL
```

Wenn die Engine auf demselben Server direkt auf Port `8080` laeuft:

```env
CHESS_ENGINE_URL=http://host.docker.internal:8080
```

Bei Linux Docker funktioniert `host.docker.internal` nicht immer automatisch. Robuster ist ein gemeinsames Docker Network, dann kann das Portfolio die Engine per Container-Name erreichen:

```env
CHESS_ENGINE_URL=http://chess-engine-api:8080
```

Dann muessen Portfolio und Engine im selben Docker Network sein.

## Deployment Checkliste fuer die Engine

Vor dem ersten GitHub Actions Deploy:

1. Repository auf dem Server nach `/opt/chess-engine-api` clonen.
2. Sicherstellen, dass der Server per SSH Key Zugriff auf GitHub hat.
3. `docker compose up -d --build` einmal manuell testen.
4. Healthcheck testen:

```bash
curl http://localhost:8080/api/health
```

5. GitHub Secrets im Engine Repository setzen.
6. Workflow nach `.github/workflows/deploy.yml` committen.
7. Nach `main` pushen und den Actions Run pruefen.

## Typische Fehler

`Permission denied publickey`: Der Server oder GitHub Actions hat keinen passenden SSH Key fuer GitHub.

`cd /opt/chess-engine-api: No such file or directory`: Das Repository liegt auf dem Server nicht an diesem Pfad.

`port is already allocated`: Ein anderer Container oder Prozess nutzt bereits Port `8080`.

Portfolio kann Engine nicht erreichen: `CHESS_ENGINE_URL` zeigt aus Sicht des Laravel Containers auf die falsche Adresse.
