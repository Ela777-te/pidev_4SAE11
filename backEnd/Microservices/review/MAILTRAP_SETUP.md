# Mailtrap Email Integration — Review Microservice

When a user receives a message on a review (someone responds to a review about them), the Review microservice sends an **email notification** via **Mailtrap** in addition to the push notification.

## Setup

### 1. Mailtrap Credentials

1. Go to [mailtrap.io](https://mailtrap.io) → **Sandboxes** → **My Sandbox**
2. Open **Integration** → **SMTP** tab
3. Copy: Host, Port, Username, Password

### 2. Configuration

Set these in `application.properties` or environment variables:

| Property | Env var | Example | Description |
|----------|---------|---------|-------------|
| `spring.mail.username` | `MAILTRAP_USERNAME` | `61500cf2fb9602` | Mailtrap SMTP username |
| `spring.mail.password` | `MAILTRAP_PASSWORD` | *(your Mailtrap password)* | Mailtrap SMTP password |

Default values already set:
- Host: `sandbox.smtp.mailtrap.io`
- Port: `2525`

**Important:** Never commit your Mailtrap password to Git. Use environment variables or a secret manager.

### 3. Enable Email

```properties
app.mail.enabled=true
```

Set `app.mail.enabled=false` to disable email (push notification will still work).

## How It Works

1. User A leaves a review about User B (reviewee)
2. User C (or A) responds to that review with a message
3. The Review microservice:
   - Sends a push notification to User B via the Notification microservice
   - Fetches User B's email from the User microservice
   - Sends an HTML email to User B via Mailtrap SMTP

## Testing

Emails sent via Mailtrap Sandbox appear in your Mailtrap inbox, not in real mailboxes. Check **Inbox** in the Mailtrap dashboard to see test emails.
