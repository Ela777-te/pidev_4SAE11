# API Enhancement Plan

This document outlines the strategy and standards for enhancing the internal REST APIs of the Smart Freelance Platform microservices. Use it as a reference when designing new endpoints or improving existing ones.

---

## Table of Contents

1. [REST Design Principles](#1-rest-design-principles)
2. [Enhancement Areas](#2-enhancement-areas)
3. [Implementation Guidelines](#3-implementation-guidelines)
4. [Versioning and Consistency](#4-versioning-and-consistency)

---

## 1. REST Design Principles

### Resource Naming

| Principle | Example |
|-----------|---------|
| Use **plural nouns** for collections | `/api/progress-updates`, `/api/projects` |
| Use **hierarchy for sub-resources** | `/api/projects/{id}/offers` |
| Avoid **verbs in URLs** | Prefer `POST /api/sessions` over `POST /api/users/login` |

### HTTP Methods

| Method | Use Case |
|--------|----------|
| `GET` | Retrieve single or list of resources |
| `POST` | Create new resource |
| `PUT` | Full replacement of resource |
| `PATCH` | Partial update of resource |
| `DELETE` | Remove resource |

### Status Codes

| Code | Use Case |
|------|----------|
| `200` | OK (GET, PUT, PATCH) |
| `201` | Created (POST) |
| `204` | No Content (DELETE) |
| `400` | Bad Request (validation errors) |
| `401` | Unauthorized |
| `403` | Forbidden |
| `404` | Not Found |
| `500` | Internal Server Error |

---

## 2. Enhancement Areas

### 2.1 Pagination & Filtering

**Current gap:** Large lists without pagination can cause performance issues.

**Enhancement:**
- Add `page`, `size`, `sort` query parameters to list endpoints
- Example: `GET /api/progress-updates?page=0&size=20&sort=createdAt,desc`
- Return metadata: `{ "content": [...], "totalElements": 100, "totalPages": 5 }`

### 2.2 Bulk & Summary Endpoints

**Purpose:** Reduce round-trips for dashboards and list views.

| Endpoint Pattern | Use Case |
|-----------------|----------|
| `GET /api/.../summary?ids=1,2,3` | Lightweight summary for multiple entities |
| `GET /api/.../latest` | Latest record only (e.g. current status) |

### 2.3 Validation Endpoints

**Purpose:** Allow frontend to validate before submit without persisting.

| Endpoint | Example |
|----------|---------|
| `POST /api/.../validate` | Body same as create; returns `{ valid, errors }` |
| `GET /api/.../next-allowed-value` | Pre-fill or client-side validation hints |

### 2.4 Health & Readiness

**Purpose:** Support CI/CD, Kubernetes probes, API Gateway health checks.

| Endpoint | Purpose |
|----------|---------|
| `GET /actuator/health` | Service up and dependencies reachable |
| `GET /actuator/health/db` | Database connectivity |

### 2.5 Export & Reporting

**Purpose:** Support audits and client reports.

| Endpoint | Response |
|----------|----------|
| `GET /api/.../export?format=csv` | CSV or Excel export with same filters as list |

---

## 3. Implementation Guidelines

### Request/Response Consistency

```json
// Success envelope (optional)
{ "data": { ... }, "meta": { "total": 100 } }

// Error envelope
{ "error": { "code": "VALIDATION_ERROR", "message": "...", "details": [...] } }
```

### Query Parameters Convention

| Parameter | Type | Description |
|-----------|------|-------------|
| `page` | int (0-based) | Page number |
| `size` | int | Page size (default 20, max 100) |
| `sort` | string | `field,asc` or `field,desc` |
| `from`, `to` | date | Date range filter |
| `ids` | string | Comma-separated IDs for bulk ops |

### OpenAPI/Swagger

- Document all endpoints with `@Operation` and `@ApiResponse`
- Group controllers by tag (e.g. `progress-updates`, `comments`)
- Document query parameters with `@Parameter`

---

## 4. Versioning and Consistency

- **URL versioning** (optional): `/api/v1/progress-updates` if breaking changes are expected
- **Casing:** Use `camelCase` for JSON fields (frontend-friendly)
- **IDs:** Prefer UUIDs or opaque identifiers; avoid exposing raw DB IDs when possible

---

*Document for API enhancement planning. Update as standards evolve.*
