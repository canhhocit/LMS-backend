# LearningHub Environment Configuration Guide

## Overview

This guide explains how to configure all environment variables required to run the LearningHub backend. All sensitive configuration is externalized to environment variables — **never hardcode secrets in source code**.

---

## Quick Start

```bash
# 1. Copy the template
cp .env.example .env

# 2. Edit with your values
nano .env   # or use your preferred editor

# 3. Load environment variables (choose one):
# Option A: Export manually before running
export $(cat .env | xargs) && ./mvnw spring-boot:run

# Option B: Use direnv (auto-loads .env on cd)
direnv allow

# Option C: IDE run configuration — add all vars from .env
```

---

## Required Variables

| Variable | Description | Required | Default | Example |
|----------|-------------|----------|---------|---------|
| `DB_URL` | MySQL JDBC connection string | ✅ | `jdbc:mysql://localhost:3306/learninghub?...` | `jdbc:mysql://db:3306/learninghub?...` |
| `DB_USERNAME` | Database username | ✅ | `root` | `learninghub_user` |
| `DB_PASSWORD` | Database password | ✅ | `123456` | `Str0ngP@ssw0rd!` |
| `JWT_SECRET` | HS256 signing key (≥32 chars) | ✅ | (hardcoded fallback) | `openssl rand -base64 32` |
| `MAIL_USERNAME` | Brevo SMTP login | ✅ | — | `user@domain.com` |
| `MAIL_PASSWORD` | Brevo SMTP password | ✅ | — | `xkeysib-xxxxxxxxx` |
| `CLOUDINARY_CLOUD_NAME` | Cloudinary cloud name | ✅ | — | `my-cloud` |
| `CLOUDINARY_API_KEY` | Cloudinary API key | ✅ | — | `123456789012345` |
| `CLOUDINARY_API_SECRET` | Cloudinary API secret | ✅ | — | `abcdefghijklmnopqrstuvwxyz` |

---

## Optional Variables (with Defaults)

| Variable | Description | Default |
|----------|-------------|---------|
| `JWT_EXPIRATION` | Access token TTL (ms) | `86400000` (24h) |
| `FRONTEND_URL` | Frontend base URL for email links | `http://localhost:3000` |
| `CORS_ALLOWED_ORIGINS` | Comma-separated allowed origins | `http://localhost:3000` |
| `ATTENDANCE_MAX_ABSENT_RATIO` | Max absent ratio (0.0–1.0) | `0.2` |
| `SERVER_PORT` | HTTP port (set via `server.port`) | `8080` |

---

## Obtaining Credentials

### 1. MySQL Database

**Local Development:**
```bash
# Using Docker
docker run -d \
  --name learninghub-mysql \
  -e MYSQL_ROOT_PASSWORD=your_password \
  -e MYSQL_DATABASE=learninghub \
  -p 3306:3306 \
  mysql:8.0
```
Then set:
```
DB_URL=jdbc:mysql://localhost:3306/learninghub?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
DB_USERNAME=root
DB_PASSWORD=your_password
```

**Production:** Use managed DB (AWS RDS, Azure Database, GCP Cloud SQL) — update `DB_URL` with the provided endpoint.

### 2. JWT Secret

Generate a secure key:
```bash
# Linux/macOS/Git Bash
openssl rand -base64 32

# PowerShell
[Convert]::ToBase64String((1..32 | ForEach-Object { Get-Random -Maximum 256 }))
```
Must be **≥ 32 characters** (256 bits) for HS256.

### 3. Brevo SMTP (Email)

1. Create account at [brevo.com](https://brevo.com)
2. Go to **SMTP & API** → **SMTP** → **SMTP Settings**
3. Copy:
   - `MAIL_USERNAME` = SMTP login (e.g., `user@domain.com`)
   - `MAIL_PASSWORD` = SMTP key (e.g., `xkeysib-xxxxxxxxxxxxx`)

The host (`smtp-relay.brevo.com`) and port (`587`) are pre-configured in `application.yml`.

### 4. Cloudinary (Video Upload)

1. Sign up at [cloudinary.com](https://cloudinary.com)
2. Dashboard → **Account Details** → copy:
   - `CLOUDINARY_CLOUD_NAME`
   - `CLOUDINARY_API_KEY`
   - `CLOUDINARY_API_SECRET`

Video upload limit is configured in `application.yml`:
```yaml
app:
  upload:
    max-video-size: 200MB
spring:
  servlet:
    multipart:
      max-file-size: 200MB
      max-request-size: 200MB
```

---

## Variable Reference by Module

| Module | Variables Used |
|--------|----------------|
| **Auth** (JWT, Forgot Password) | `JWT_SECRET`, `JWT_EXPIRATION`, `MAIL_USERNAME`, `MAIL_PASSWORD`, `FRONTEND_URL` |
| **Database / Flyway** | `DB_URL`, `DB_USERNAME`, `DB_PASSWORD` |
| **Email (Brevo)** | `MAIL_USERNAME`, `MAIL_PASSWORD` |
| **Video Upload** | `CLOUDINARY_CLOUD_NAME`, `CLOUDINARY_API_KEY`, `CLOUDINARY_API_SECRET` |
| **CORS / WebSocket** | `CORS_ALLOWED_ORIGINS`, `FRONTEND_URL` |
| **Attendance** | `ATTENDANCE_MAX_ABSENT_RATIO` |

---

## Validation Checklist

Before starting the application, verify:

- [ ] `.env` file exists in project root
- [ ] All **required** variables are set (no placeholder values)
- [ ] `JWT_SECRET` is ≥ 32 chars
- [ ] MySQL is running and accessible at `DB_URL`
- [ ] Brevo SMTP credentials work (test with `telnet smtp-relay.brevo.com 587`)
- [ ] Cloudinary credentials are valid (test via dashboard)
- [ ] `FRONTEND_URL` matches your frontend deployment

---

## Troubleshooting

| Error | Cause | Fix |
|-------|-------|-----|
| `Communications link failure` | MySQL not reachable | Check `DB_URL`, firewall, MySQL running |
| `Bad credentials` (DB) | Wrong user/pass | Verify `DB_USERNAME`, `DB_PASSWORD` |
| `JWT signature does not match` | Secret mismatch | Ensure same `JWT_SECRET` across restarts |
| `403 Forbidden` on email send | Invalid Brevo creds | Re-check `MAIL_USERNAME`/`MAIL_PASSWORD` |
| `Upload failed` (video) | Cloudinary auth error | Verify all 3 Cloudinary vars |
| `CORS error` in browser | Origin not allowed | Add frontend URL to `CORS_ALLOWED_ORIGINS` |

---

## Security Best Practices

1. **Never commit `.env`** — it's in `.gitignore`
2. **Use different values per environment** (dev/staging/prod)
3. **Rotate secrets periodically** (JWT secret, DB password, API keys)
4. **Use secret managers in production** (AWS Secrets Manager, Azure Key Vault, HashiCorp Vault)
5. **Restrict CORS origins** to only known frontends
6. **Use strong DB passwords** and dedicated DB users (not `root` in prod)
