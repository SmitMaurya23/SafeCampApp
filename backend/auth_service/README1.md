
---

# 🧾 Backend Development Progress Report
**Project Theme:** Modular Campus Safety System
**Architect:** Smit
**Date:** October 2, 2025

---

## ✅ Architectural Decisions

| Decision | Description |
|---------|-------------|
| 🔗 Firebase Gateway | All Firebase interactions are routed through a dedicated `firebase-gateway-service` via REST APIs. No direct SDK access from other services. |
| 🧱 Modular Microservices | Auth logic lives in `auth-service`, Firebase logic in `firebase-gateway-service`, and admin logic is scoped within `auth-service` for now. |
| 🔐 JWT Authentication | Token-based login and validation using a shared `jwt.secret`. |
| 📋 Logging & Exception Handling | Global exception handler and SLF4J logging added to all services for traceability. |
| 🧪 Swagger Integration | Swagger UI configured with bearer token support for testing secured endpoints. |

---

## 🏗️ Modules Scaffolded

| Module | Status | Notes |
|--------|--------|-------|
| `auth-service` | ✅ Scaffolded | Signup, login, token validation, admin endpoints |
| `firebase-gateway-service` | ✅ Scaffolded | Handles Firestore logic for signup, login, approval |
| `admin-service` | ❌ Merged into `auth-service` | Admin logic lives inside `auth-service` for simplicity |

---

## 🧩 Problems Faced & Solutions

| Problem | Cause | Solution |
|--------|-------|----------|
| 🔴 Login returned 500 | User not moved from `pending-users` to `users` | Added approval flow via admin endpoint |
| 🔴 Token validation failed | Missing `Bearer` prefix or malformed header | Added defensive parsing and error handling |
| 🔴 Authorization header was null | Swagger UI didn’t send header | Configured Swagger with bearer token support |
| 🔴 Duplicate approval attempt | Email already existed in `users` | Firebase gateway checks for existing user and rejects with clear error |
| 🔴 Missing unique ID on approval | No ID assigned during migration | Added UUID generation during approval in Firebase gateway |

---

## 🔐 Token Validation Flow

- Controller checks for `Authorization` header
- Extracts token after verifying `Bearer` prefix
- Delegates to `AuthServiceImpl` for validation
- Uses `JwtUtil` to extract and verify claims
- Returns decoded claims or error response

---

## 📦 Firebase Gateway Responsibilities

- `signup()` → stores user in `pending-users`
- `login()` → fetches user from `users`, validates password
- `approveUser()` → moves user to `users`, assigns UUID
- `rejectUser()` → deletes user from `pending-users`

---

## 🧪 Testing Tools Used

- ✅ Postman for manual endpoint testing
- ✅ Swagger UI with bearer token support
- ✅ Console logs for traceability
- ✅ RESTTemplate for inter-service communication

---

## 🛠️ Next Steps (Suggested)

- 🔐 Add role-based access control using JWT claims
- 📊 Create audit logs for admin actions
- 🧾 Add pagination for pending user list
- 🧑‍💼 Build a dashboard for admin approvals

---
