# 🔐 Authentication API Specification
```
Base URL: /api/auth
```

## 📄 Endpoints

### 1. ```POST /api/auth/register```
Register a new user (Auto Customer).

**Request Body:**

```json
{
  "username": "newuser",
  "email": "newuser@example.com",
  "password": "password123",
}
```

**Response (200 OK):**

```json
{
  true
}
```

**Response (400 Bad Request):**
```json
{
  "error": {
    "username": "Username is required",
    "role": "Role is required"
  }
}
```

**Response (409 Conflict):**
```json
{
  "error": "Username already taken"
}
```

### 2. ```POST /api/auth/login```
Login with username and password.

**Request Body:**

```json
{
  "username": "rafael",
  "password": "password123"
}
```

**Response (200 OK):**
```json
{
  "username": "rafael",
  "email": "rafael@example.com",
  "role": "ADMIN",
  "token": "jwt_token"
}
```

**Response (400 Bad Request):**
```json
{
  "error": {
    "username": "Username is required",
    "password": "Password is required"
  }
}
```

**Response (400 Bad Request):**
```json
{
  "error": "Invalid username or password"
}
```

## 📌 Notes
**Use the JWT token from the login response for endpoints that require authentication:**

```
Authorization: Bearer <Token>
```
**Role values allowed:**
```
ADMIN, CUSTOMER (sesuai Role.java)
```