# LearningHub - University LMS

LearningHub is a Modular Monolith Learning Management System designed for Universities and Schools.

## Prerequisites
- Java 17
- Maven 3.8+
- MySQL 8.x

## Environment Variables
```env
DB_URL=jdbc:mysql://localhost:3306/learninghub?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
DB_USERNAME=root
DB_PASSWORD=your_mysql_password
JWT_SECRET=404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970
JWT_EXPIRATION=86400000

# Default Admin Credentials (Auto-generated on startup if no admin exists)
# APP_ADMIN_DEFAULT_EMAIL=admin@university.edu.vn
# APP_ADMIN_DEFAULT_PASSWORD=123456
# OTHER_DEFAULT_PASSWORD = Password@123
```
