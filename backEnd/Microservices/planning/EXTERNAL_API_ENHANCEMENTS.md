# External API Enhancements

This document describes enhancements and improvements for how the platform integrates with external APIs (Google Calendar, GitHub, Firebase, Slack, AI, etc.). It complements [EXTERNAL_API_INTEGRATION.md](EXTERNAL_API_INTEGRATION.md), which covers setup and integration basics.

---

## Table of Contents

1. [Resilience & Fault Tolerance](#1-resilience--fault-tolerance)
2. [Caching Strategy](#2-caching-strategy)
3. [Rate Limiting & Throttling](#3-rate-limiting--throttling)
4. [Monitoring & Observability](#4-monitoring--observability)
5. [Async & Performance](#5-async--performance)
6. [Security Enhancements](#6-security-enhancements)

---

## 1. Resilience & Fault Tolerance

### Circuit Breaker (Resilience4j)

**Problem:** When an external API (e.g. GitHub, Google Calendar) is down or slow, the main flow (e.g. progress update creation) can block or fail.

**Enhancement:**
- Wrap external API calls with **Resilience4j Circuit Breaker**
- Configure: `failureRateThreshold=50`, `waitDurationInOpenState=30s`
- Fallback behavior: log and skip integration; do not fail the core operation
- Example: If GitHub is down, still create the progress update; mark "GitHub sync pending" for retry

### Retry with Backoff

**Problem:** Transient network errors cause unnecessary failures.

**Enhancement:**
- Use **Retry** with exponential backoff: 1s → 2s → 4s (max 3 attempts)
- Retry only on retryable status codes (429, 503) or transient exceptions
- Do not retry on 4xx client errors (except 429)

### Timeout Configuration

**Problem:** Hung calls to external APIs can block threads and degrade UX.

**Enhancement:**
- Set explicit timeouts on `RestTemplate` or `WebClient`: e.g. 5s connect, 10s read
- Use separate timeout values per integration (GitHub: 5s; Google Calendar: 8s)

---

## 2. Caching Strategy

### GitHub API Caching

**Problem:** GitHub rate limit (60/hr unauthenticated, 5000/hr with token); repeated fetches for same repo waste quota.

**Enhancement:**
- Cache `GET /repos/{owner}/{repo}/commits` and `GET /repos/{owner}/{repo}/branches` for 5–15 minutes
- Use Spring Cache (`@Cacheable`) or Caffeine with TTL
- Invalidate on "refresh" user action or when a new progress update links to that repo

### Google Calendar Caching

**Problem:** Calendar list/event fetches can be slow or hit quota.

**Enhancement:**
- Cache event list for a calendar for 2–5 minutes
- Consider caching only "next N events" for lightweight dashboards

### Configuration

```properties
# Example cache TTL (seconds)
github.cache.ttl=300
google.calendar.cache.ttl=120
```

---

## 3. Rate Limiting & Throttling

### Outbound Rate Limiting

**Problem:** Burst traffic to GitHub/Slack can trigger 429 (Too Many Requests).

**Enhancement:**
- Add in-memory rate limiter (e.g. Resilience4j **RateLimiter**) for outbound calls
- GitHub: ~80 requests/minute (under 5000/hr)
- Slack: ~1–2 messages/second for webhook; tier 2 for chat.postMessage

### Graceful Degradation

- When rate limited: queue for later (if possible) or drop with log
- Never block core flow (e.g. progress update creation)

---

## 4. Monitoring & Observability

### Metrics

| Metric | Purpose |
|--------|---------|
| `external_api_calls_total{service="github", status="success"}` | Success/failure counts |
| `external_api_latency_seconds{service="github"}` | Latency histogram |
| `external_api_circuit_breaker_state{name="githubApi"}` | Circuit state (open/closed) |

### Logging

- Log all external API calls: integration name, endpoint, status, duration
- On failure: log error details (without secrets) for debugging
- Use structured logging (JSON) for easy parsing in ELK/Loki

### Alerts

- Alert when circuit breaker opens for > 5 minutes
- Alert on sustained 429/503 from external APIs

---

## 5. Async & Performance

### Non-Blocking Calls

**Problem:** External API calls (FCM, Slack, Google Calendar) add latency to the main request.

**Enhancement:**
- Use `@Async` or message queue (e.g. RabbitMQ/Kafka) for non-critical notifications
- Fire-and-forget: progress update created → async send FCM + Slack; return 201 immediately
- Ensure async tasks have proper error handling (don’t fail silently)

### WebClient Over RestTemplate

**Enhancement:**
- Prefer **WebClient** (reactive) for external calls when integrating with reactive stack
- Or use `RestTemplate` with connection pooling and timeouts for blocking flows

---

## 6. Security Enhancements

### Secrets Management

- Never commit API keys, tokens, or service account JSON
- Use environment variables, Spring Cloud Config, or secret manager (Vault, AWS Secrets Manager)
- Rotate tokens periodically (especially GitHub PAT)

### Minimal Scopes

- GitHub: use fine-grained PAT with only needed repos and read-only where possible
- Google: request minimal OAuth scopes
- Slack: only `chat:write` if posting only

### Fail Securely

- If external API fails, do not expose internal error details to the client
- Return generic message; log detailed error server-side

---

## Summary Checklist

| Enhancement | Priority | Effort |
|-------------|----------|--------|
| Circuit Breaker + Retry | High | Medium |
| Timeouts on all external calls | High | Low |
| Async notifications (FCM, Slack) | High | Medium |
| GitHub/Calendar caching | Medium | Low |
| Rate limiting (outbound) | Medium | Low |
| Metrics & logging | Medium | Medium |
| Secrets in env/Config | High | Low |

---

*Document for external API enhancement planning. See [EXTERNAL_API_INTEGRATION.md](EXTERNAL_API_INTEGRATION.md) for setup details.*
