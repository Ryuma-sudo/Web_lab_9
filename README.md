# Secure Customer API with JWT Authentication

## Student Information
- **Name:** Nguyễn Quang Trực
- **Student ID:** ITCSIU23041
- **Class:** Group 2

## Features Implemented

### Authentication
- [x] User registration
- [x] User login with JWT
- [x] Logout
- [x] Get current user
- [x] Password hashing with BCrypt

### Authorization
- [x] Role-based access control (USER, ADMIN)
- [x] Protected endpoints
- [x] Method-level security with @PreAuthorize

### Additional Features
- [ ] Change password
- [ ] Forgot password / Reset password
- [ ] User profile management
- [ ] Admin user management
- [ ] Refresh token
- [ ] Email verification (Bonus)

## API Endpoints

### Public Endpoints
- POST /api/auth/register
- POST /api/auth/login

### Protected Endpoints (Authenticated)
- GET /api/auth/me
- POST /api/auth/logout
- GET /api/customers
- GET /api/customers/{id}

### Admin Only Endpoints
- POST /api/customers
- PUT /api/customers/{id}
- DELETE /api/customers/{id}
- GET /api/admin/users
- PUT /api/admin/users/{id}/role

## Test Users
| Username | Password | Role |
|----------|----------|------|
| admin | password123 | ADMIN |
| duke | password123 | USER |

## How to Run
1. Create database: `customer_management`
2. Run SQL scripts to create tables
3. Update `application.properties` with your MySQL credentials
4. Run: `mvn spring-boot:run`
5. Test with Thunder Client using provided collection

## Testing
Import Postman collection: `Secure_Customer_API.postman_collection.json`

All endpoints tested and working.

## Security
- Passwords hashed with BCrypt
- JWT tokens with 24-hour expiration
- Stateless authentication
- CORS enabled for frontend
- Protected endpoints with Spring Security

### 1. User Registration FlowThis flow handles the creation of a new user account with encrypted credentials.

1. *HTTP Request*: Client sends `POST http://localhost:8080/api/auth/register` with user details (username, password, roles).
2. *Controller*: `AuthController.registerUser(@RequestBody RegisterRequestDto)` receives the payload.
3. *Validation*: Service checks if the username or email already exists in the database.
4. *Security*: `PasswordEncoder.encode()` (BCrypt) is called to hash the raw password.
5. *Persistence*: `UserRepository.save()` persists the new `User` entity with the hashed password.
6. *Response*: Returns `201 Created` with a success message.

*Testing:*
Method: `POST`
URL: `http://localhost:8080/api/auth/register`

**Request Body:**

```json
{
    "username": "researcher_01",
    "password": "StrongPassword123!",
    "email": "researcher@lab.edu",
    "role": "ROLE_USER"
}

```

**Expected Response (201 Created):**

```json
{
    "message": "User registered successfully",
    "userId": 42,
    "timestamp": "2025-11-21T10:05:00"
}

```

---

### 2. User Login FlowThis flow authenticates credentials and issues a JWT for session management.

1. *HTTP Request*: Client sends `POST http://localhost:8080/api/auth/login` with credentials.
2. *Controller*: `AuthController.login(@RequestBody LoginRequestDto)` receives the request.
3. *Authentication*: `AuthenticationManager.authenticate()` is invoked. It delegates to `DaoAuthenticationProvider`.
4. *Verification*: `UserDetailsService.loadUserByUsername()` fetches the user; `PasswordEncoder.matches()` compares the raw password with the stored hash.
5. *Token Generation*: Upon successful verification, `JwtTokenProvider.generateToken(authentication)` creates a signed JWT containing claims (sub, iat, exp, roles).
6. *Response*: Returns `200 OK` containing the Bearer token.

*Testing:*
Method: `POST`
URL: `http://localhost:8080/api/auth/login`

**Request Body:**

```json
{
    "username": "researcher_01",
    "password": "StrongPassword123!"
}

```

**Expected Response (200 OK):**

```json
{
    "accessToken": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJyZXNlYXJ...",
    "tokenType": "Bearer",
    "expiresIn": 3600
}

```

---

### 3. Protected Endpoint Flow (JWT Filter)This flow demonstrates how the application validates identity before processing a secured request.

1. *HTTP Request*: Client sends `GET http://localhost:8080/api/customers` with header `Authorization: Bearer <token>`.
2. *Filter Chain*: `JwtAuthenticationFilter.doFilterInternal()` intercepts the request before it reaches the Controller.
3. *Token Extraction*: Filter parses the JWT from the Authorization header.
4. *Validation*: `JwtTokenProvider.validateToken()` checks signature integrity and expiration.
5. *Context Setup*: `UserDetailsService` loads user details; `SecurityContextHolder.getContext().setAuthentication(authToken)` sets the user as authenticated for this thread.
6. *Controller Execution*: Request proceeds to `CustomerRestController` only if the context is valid.

*Testing:*
Method: `GET`
URL: `http://localhost:8080/api/customers`
Header: `Authorization: Bearer eyJhbGci...`

**Expected Response (200 OK):**

```json
[
    {
        "id": 1,
        "customerCode": "C001",
        "fullName": "John Doe",
        "status": "ACTIVE"
    }
]

```

---

### 4. Authorization Flow (RBAC)This flow enforces role-based restrictions (e.g., only ADMINs can delete customers).

1. *HTTP Request*: Client (with ROLE_USER) sends `DELETE http://localhost:8080/api/customers/1`.
2. *Security Filter*: `JwtAuthenticationFilter` authenticates the user successfully.
3. *Method Security*: Spring Security's `AuthorizationManager` (or AOP for `@PreAuthorize`) intercepts the Controller call.
4. *Role Check*: It inspects the `Authentication` object in the SecurityContext against the required authority (e.g., `hasRole('ADMIN')`).
5. *Decision*:
* *If Role Matches*: Execution passes to `CustomerRestController.deleteCustomer()`.
* *If Role Mismatch*: Throws `AccessDeniedException`.
6. *Response*: Global Exception Handler catches the exception and returns `403 Forbidden`.

*Testing (As ROLE_USER):*
Method: `DELETE`
URL: `http://localhost:8080/api/customers/1`

**Expected Response (403 Forbidden):**

```json
{
    "status": 403,
    "error": "Forbidden",
    "message": "Access Denied: You do not have permission to access this resource",
    "path": "/api/customers/1"
}

```

### 5. Test screenshot

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
