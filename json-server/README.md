# JSON Server Payments API

A JSON Server implementation with a custom POST /payments endpoint for processing payment requests.

## Features

- Custom POST /payments endpoint with validation
- Email format validation
- Currency validation (USD, EUR, GBP, CAD, AUD, JPY)
- Amount validation (must be positive number)
- Automatic transaction ID generation
- Payment status tracking
- Full JSON Server functionality for other endpoints

## Setup

1. Install dependencies:
```bash
npm install
```

2. Start the server:
```bash
npm start
```

For development with auto-restart:
```bash
npm run dev
```

The server will start on `http://localhost:3000`

## API Endpoints

### POST /payments

Process a payment request.

**Request Body:**
```json
{
  "recipientEmail": "user@example.com",
  "amount": 100.50,
  "currency": "USD"
}
```

**Success Response (201):**
```json
{
  "success": true,
  "message": "Payment processed successfully",
  "payment": {
    "id": 1703123456789,
    "transactionId": "TXN_1703123456789_abc123def",
    "recipientEmail": "user@example.com",
    "amount": 100.50,
    "currency": "USD",
    "status": "processed",
    "timestamp": "2023-12-21T10:30:45.123Z"
  }
}
```

**Error Response (400):**
```json
{
  "success": false,
  "error": "Invalid email format",
  "code": "INVALID_EMAIL_FORMAT"
}
```

### Other Endpoints

- `GET /payments` - List all payments
- `GET /users` - List all users
- Standard JSON Server CRUD operations for all resources

## Validation Rules

- **recipientEmail**: Must be a valid email format
- **amount**: Must be a positive number
- **currency**: Must be one of: USD or EUR

## Example Usage

### Using curl:
```bash
curl -X POST http://localhost:3000/payments \
  -H "Content-Type: application/json" \
  -d '{
    "recipientEmail": "john.doe@example.com",
    "amount": 250.75,
    "currency": "USD"
  }'
```

### Using JavaScript fetch:
```javascript
const response = await fetch('http://localhost:3000/payments', {
  method: 'POST',
  headers: {
    'Content-Type': 'application/json',
  },
  body: JSON.stringify({
    recipientEmail: 'user@example.com',
    amount: 100.50,
    currency: 'USD'
  })
});

const result = await response.json();
console.log(result);
```

## Database

The server uses `database.json` as the data store.

