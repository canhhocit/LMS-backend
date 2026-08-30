# LearningHub - University LMS

LearningHub is a Modular Monolith Learning Management System designed for Universities and Schools.

## Prerequisites
- Java 17
- Maven 3.8+
- PostgreSQL 14+

## Environment Variables
```env
DB_URL=jdbc:postgresql://localhost:5432/learninghub
DB_USERNAME=postgres
DB_PASSWORD=postgres
JWT_SECRET=404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970
JWT_EXPIRATION=86400000

# Default Admin Credentials (Auto-generated on startup if no admin exists)
# APP_ADMIN_DEFAULT_EMAIL=admin@university.edu.vn
# APP_ADMIN_DEFAULT_PASSWORD=123456
# OTHER_DEFAULT_PASSWORD=Password@123
```

## Quick Start (Docker)
```bash
docker run -d \
  --name learninghub-postgres \
  -e POSTGRES_DB=learninghub \
  -e POSTGRES_USER=postgres \
  -e POSTGRES_PASSWORD=postgres \
  -p 5432:5432 \
  postgres:15
```
