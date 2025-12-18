# Secure Customer API with JWT Authentication

## Student Information
- **Name:** Nguyễn Quang Trực
- **Student ID:** ITCSIU23041
- **Class:** Group 2

## Features Implemented

### Authentication
- ✅ User registration
- ✅ User login with JWT
- ✅ Logout
- ✅ Get current user
- ✅ Password hashing with BCrypt

### Authorization
- ✅ Role-based access control (USER, ADMIN)
- ✅ Protected endpoints
- ✅ Method-level security with @PreAuthorize

### Additional Features (Part B - Homework)
- ✅ Change password
- ✅ Forgot password / Reset password
- ✅ User profile management
- ✅ Admin user management
- ✅ Refresh token
- [ ] Email verification (Bonus)

## API Endpoints

### Public Endpoints
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/auth/register` | Register new user |
| POST | `/api/auth/login` | Login and get JWT tokens |
| POST | `/api/auth/forgot-password` | Request password reset token |
| POST | `/api/auth/reset-password` | Reset password with token |
| POST | `/api/auth/refresh` | Refresh access token |

### Protected Endpoints (Authenticated)
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/auth/me` | Get current user info |
| POST | `/api/auth/logout` | Logout user |
| PUT | `/api/auth/change-password` | Change password |
| GET | `/api/customers` | Get all customers |
| GET | `/api/customers/{id}` | Get customer by ID |
| GET | `/api/users/profile` | Get user profile |
| PUT | `/api/users/profile` | Update user profile |
| DELETE | `/api/users/account` | Delete user account (soft delete) |

### Admin Only Endpoints
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/customers` | Create new customer |
| PUT | `/api/customers/{id}` | Update customer |
| DELETE | `/api/customers/{id}` | Delete customer |
| GET | `/api/admin/users` | List all users |
| PUT | `/api/admin/users/{id}/role` | Update user role |
| PATCH | `/api/admin/users/{id}/status` | Toggle user active status |

## Test Users
| Username | Password | Role |
|----------|----------|------|
| admin | password123 | ADMIN |
| john | password123 | USER |
| jane | password123 | USER |

## How to Run
1. Create database: `customer_management`
2. Run SQL scripts to create tables
3. Update `application.properties` with your MySQL credentials
4. Run: `.\mvnw.cmd spring-boot:run`
5. Test with Thunder Client/Postman using provided collection

## Testing
Import Postman collection: `postman/Secure_Customer_API.postman_collection.json`

All endpoints tested and working.

## Security
- Passwords hashed with BCrypt
- JWT access tokens with 24-hour expiration
- Refresh tokens with 7-day expiration
- Stateless authentication
- CORS enabled for frontend
- Protected endpoints with Spring Security
- Role-based access control with @PreAuthorize

---

## API Documentation

### 1. User Registration
**Method:** `POST`  
**URL:** `http://localhost:8080/api/auth/register`

**Request Body:**
```json
{
    "username": "newuser",
    "password": "password123",
    "email": "newuser@example.com",
    "fullName": "New User"
}
```

**Expected Response (201 Created):**
```json
{
    "id": 4,
    "username": "newuser",
    "email": "newuser@example.com",
    "fullName": "New User",
    "role": "USER",
    "isActive": true,
    "createdAt": "2025-12-18T22:30:00"
}
```

---

### 2. User Login
**Method:** `POST`  
**URL:** `http://localhost:8080/api/auth/login`

**Request Body:**
```json
{
    "username": "admin",
    "password": "password123"
}
```

**Expected Response (200 OK):**
```json
{
    "token": "eyJhbGciOiJIUzI1NiJ9...",
    "type": "Bearer",
    "refreshToken": "550e8400-e29b-41d4-a716-446655440000",
    "username": "admin",
    "email": "admin@example.com",
    "role": "ADMIN"
}
```

---

### 3. Change Password
**Method:** `PUT`  
**URL:** `http://localhost:8080/api/auth/change-password`  
**Header:** `Authorization: Bearer <token>`

**Request Body:**
```json
{
    "currentPassword": "password123",
    "newPassword": "newpassword123",
    "confirmPassword": "newpassword123"
}
```

**Expected Response (200 OK):**
```json
{
    "message": "Password changed successfully"
}
```

---

### 4. Forgot Password
**Method:** `POST`  
**URL:** `http://localhost:8080/api/auth/forgot-password`

**Request Body:**
```json
{
    "email": "john@example.com"
}
```

**Expected Response (200 OK):**
```json
{
    "message": "Password reset token generated. In production, this would be sent via email.",
    "resetToken": "550e8400-e29b-41d4-a716-446655440000"
}
```

---

### 5. Reset Password
**Method:** `POST`  
**URL:** `http://localhost:8080/api/auth/reset-password`

**Request Body:**
```json
{
    "token": "<reset-token-from-forgot-password>",
    "newPassword": "newpassword123",
    "confirmPassword": "newpassword123"
}
```

**Expected Response (200 OK):**
```json
{
    "message": "Password reset successfully"
}
```

---

### 6. Refresh Access Token
**Method:** `POST`  
**URL:** `http://localhost:8080/api/auth/refresh`

**Request Body:**
```json
{
    "refreshToken": "<refresh-token-from-login>"
}
```

**Expected Response (200 OK):**
```json
{
    "token": "eyJhbGciOiJIUzI1NiJ9...",
    "type": "Bearer",
    "refreshToken": "new-refresh-token",
    "username": "admin",
    "email": "admin@example.com",
    "role": "ADMIN"
}
```

---

### 7. Update Profile
**Method:** `PUT`  
**URL:** `http://localhost:8080/api/users/profile`  
**Header:** `Authorization: Bearer <token>`

**Request Body:**
```json
{
    "fullName": "John Updated",
    "email": "john.updated@example.com"
}
```

---

### 8. Admin: List All Users
**Method:** `GET`  
**URL:** `http://localhost:8080/api/admin/users`  
**Header:** `Authorization: Bearer <admin-token>`

**Expected Response (200 OK):**
```json
[
    {
        "id": 1,
        "username": "admin",
        "email": "admin@example.com",
        "fullName": "Admin User",
        "role": "ADMIN",
        "isActive": true
    },
    ...
]
```

---

### 9. Admin: Update User Role
**Method:** `PUT`  
**URL:** `http://localhost:8080/api/admin/users/2/role`  
**Header:** `Authorization: Bearer <admin-token>`

**Request Body:**
```json
{
    "role": "ADMIN"
}
```

---

## Test Screenshots

1. Registration Success
Action: Register a new user account. URL: POST `http://localhost:8080/api/auth/register`

<img width="2979" height="592" alt="image" src="https://github.com/user-attachments/assets/02dbda96-a60e-4022-bbc2-66dc7fa14142" />

2. Login Success
Action: Authenticate and retrieve JWT token. URL: POST `http://localhost:8080/api/auth/login`

<img width="2973" height="533" alt="image" src="https://github.com/user-attachments/assets/3d1e263c-bf02-4787-85f0-8c58c54845f1" />

3. Access Protected Endpoint (Success)
Action: Access customer list with valid Bearer token. URL: GET `http://localhost:8080/api/customers?sortBy=id&sortDir=desc`

<img width="2972" height="806" alt="image" src="https://github.com/user-attachments/assets/3ec15c10-bc65-4973-bd28-795d57074901" />

4. Access Protected Endpoint (Unauthorized)
Action: Attempt to access customer list without token or with invalid token. URL: GET `http://localhost:8080/api/customers`

<img width="2955" height="603" alt="image" src="https://github.com/user-attachments/assets/97afcf19-70ff-4197-8e64-7a20488e9286" />

6. Authorization Failure (Forbidden)
Action: Logged in as ROLE_USER and try to delete a customer. URL: DELETE `http://localhost:8080/api/customers/1`

<img width="2970" height="573" alt="image" src="https://github.com/user-attachments/assets/180b9b9e-34a4-49d7-867b-0cd40ef1c7b6" />

8. Authorization Success (Admin Only)
Action: Logged in as ROLE_ADMIN and delete a customer. URL: DELETE `http://localhost:8080/api/customers/8`

<img width="2969" height="629" alt="image" src="https://github.com/user-attachments/assets/7f520926-0e2d-4731-b6ed-1fc25781a683" />

<img width="2972" height="692" alt="image" src="https://github.com/user-attachments/assets/bf55625d-666b-488b-8683-c6d3e58a70c8" />

9. Get Current User Profile
Action: Retrieve details of the currently logged-in user. URL: GET `http://localhost:8080/api/auth/me`

<img width="2981" height="646" alt="image" src="https://github.com/user-attachments/assets/83a3523e-3871-4a0b-a558-d2538d53b7a6" />

10. Change Password Success
Action: Change password for authenticated user. URL: PUT `http://localhost:8080/api/auth/change-password`

11. Forgot Password
Action: Request password reset token. URL: POST `http://localhost:8080/api/auth/forgot-password`

12. Reset Password Success
Action: Reset password using token. URL: POST `http://localhost:8080/api/auth/reset-password`

13. View Profile
Action: View current user's profile. URL: GET `http://localhost:8080/api/users/profile`

14. Update Profile
Action: Update user's full name and email. URL: PUT `http://localhost:8080/api/users/profile`

15. Delete Account (Soft Delete)
Action: Soft delete user account. URL: DELETE `http://localhost:8080/api/users/account?password=xxx`

16. Admin: List All Users
Action: Get all users (Admin only). URL: GET `http://localhost:8080/api/admin/users`

17. Admin: Update User Role
Action: Change user's role (Admin only). URL: PUT `http://localhost:8080/api/admin/users/2/role`

18. Admin: Toggle User Status
Action: Activate/Deactivate user (Admin only). URL: PATCH `http://localhost:8080/api/admin/users/2/status`

19. USER Accessing Admin Endpoint (403 Forbidden)
Action: Regular user trying to access admin endpoint. URL: GET `http://localhost:8080/api/admin/users`

20. Login with Refresh Token
Action: Login returns both access token and refresh token. URL: POST `http://localhost:8080/api/auth/login`

21. Refresh Access Token
Action: Get new access token using refresh token. URL: POST `http://localhost:8080/api/auth/refresh`

