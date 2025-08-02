# 📘 User API Specification
```
Base URL: /api/users
```


## 🔐 Authorization  
Endpoint dengan 🔐 membutuhkan token JWT dari pengguna yang memiliki peran ADMIN.

Endpoint dengan 🧑 hanya membutuhkan token JWT biasa (pengguna login).

## 📄 Endpoints

### 1. 🔐 ```GET /api/users```
Get paginated list of all users.

**Query Parameters (optional):**

- page: integer (default 0)

- size: integer (default 20)

- sort: string (e.g. username,asc)

**Response (200 OK):**

```json
{
  "content": [ { user objects... } ],
  "page": 0,
  "size": 20,
  "totalElements": 100,
  "totalPages": 5
}
```

### 2. 🔐 ```GET /api/users/{id}```
Get user details by ID.

**Response (200 OK):**

```json
{
  "id": 1,
  "username": "admin",
  "email": "admin@example.com",
  "role": "ADMIN"
}
```

### 3. 🔐 ```POST /api/users```
Create a new user.

**Request Body:**

```json
{
  "username": "newuser",
  "email": "new@example.com",
  "password": "password123",
  "role": "CUSTOMER"
}
```

**Response (200 OK):**

```json
{
  "id": 5,
  "username": "newuser",
  "email": "new@example.com",
  "role": "CUSTOMER"
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
  "error": "username already taken"
}
```

### 4. 🔐 ```PATCH /api/users/{id}```
Update an existing user.

**Request Body (partial):**

```json
{
  "username": "updateduser",
  "email": "updated@example.com",
  "password": "newpassword123",
  "role": "ADMIN"
}
```

**Response (200 OK):**

```json
{
  "id": 1,
  "username": "updateduser",
  "email": "updated@example.com",
  "role": "ADMIN"
}
```

**Response (400 Bad Request):**
```json
{
  "error": "Username can't be blank"
}
```

**Response (404 Not Found):**
```json 
{
  "error": "User with ID {id} not found"
}
```

**Response (409 Conflict):**
```json 
{
  "error": "Username already taken"
}
```


### 5. 🔐 ```DELETE /api/users/{id}```
Delete a user by ID.

**Response (200 OK):**
```json
{
  true
}
```

**Response (404 Not Found):**
```json
{
   "error": "User with ID {id} not found"
}
```

### 6. 🧑 ```GET /api/users/me```
Get the current logged-in user.

**Response (200 OK):**
```json
{
  "id": 1,
  "username": "currentuser",
  "email": "user@example.com",
  "role": "CUSTOMER"
}
```

### 7. 🧑 ```PATCH /api/users/me```
Update current logged-in user.

**Request Body (partial):**

```json
{
  "username": "newusername",
  "email": "newemail@example.com",
  "password": "newpassword"
}
```

**Response (200 OK):**

```json
{
  "id": 1,
  "username": "newusername",
  "email": "newemail@example.com",
  "role": "CUSTOMER"
}
```

**Response (400 Bad Request):**
```json
{
  error: "Email can't be blank"
}
```

**Response (409 Conflict):**
```json
{
  error: "Username already taken"
}
```

## 📌 Notes
**Use Bearer token in Authorization header:**  
```
Authorization: Bearer <token>
```
**Role values allowed:**
```
ADMIN, CUSTOMER (sesuai Role.java)
```