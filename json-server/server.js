const jsonServer = require('json-server');
const server = jsonServer.create();
const router = jsonServer.router('database.json');
const middlewares = jsonServer.defaults();

// Add JSON body parsing middleware
server.use(jsonServer.bodyParser);

// Email validation function
function isValidEmail(email) {
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
  return emailRegex.test(email);
}

function isBlacklistedEmail(email) {
  const blacklistedEmails = ['test@gmail.com', 'test2@gmail.com'];
  return blacklistedEmails.includes(email);
}

// Currency validation function
function isValidCurrency(currency) {
  const validCurrencies = ['USD', 'EUR'];
  return validCurrencies.includes(currency.toUpperCase());
}

// Custom POST /payments endpoint
server.post('/payments', (req, res) => {
  const { recipientEmail, amount, currency } = req.body;
  console.log(req.body);

  // Validation
  if (!recipientEmail || !amount || !currency) {
    return res.status(400).json({
      success: false,
      error: 'Missing required fields. Please provide recipientEmail, amount, and currency.',
      code: "MISSING_REQUIRED_FIELDS",
    });
  }

  if (!isValidEmail(recipientEmail) || isBlacklistedEmail(recipientEmail)) {
    return res.status(400).json({
      success: false,
      error: `${recipientEmail} is not a valid receipient address`,
      code: "INVALID_EMAIL",
    });
  }

  if (typeof amount !== 'number' || amount <= 0) {
    return res.status(400).json({
      success: false,
      error: 'Amount must be a positive number',
      code: "INVALID_AMOUNT"
    });
  }

  if (!isValidCurrency(currency)) {
    return res.status(400).json({
      success: false,
      error: `Invalid currency. Supported currencies: USD, EUR`,
      code: "INVALID_CURRENCY"
    });
  }

  if (amount > 10000) {
    return res.status(400).json({
      success: false,
      error: 'Insufficient funds',
      code: "INSUFFICIENT_FUNDS"
    });
  }

  // Create payment record
  const payment = {
    id: Date.now(), // Simple ID generation
    recipientEmail,
    amount,
    currency: currency.toUpperCase(),
    status: 'processed',
    timestamp: new Date().toISOString(),
    transactionId: `TXN_${Date.now()}_${Math.random().toString(36).substr(2, 9)}`
  };

  const db = router.db;
  db.get('payments').push(payment).write();

  // Return success response
  res.status(201).json({
    success: true,
    message: 'Payment processed successfully',
    payment: {
      id: payment.id,
      transactionId: payment.transactionId,
      recipientEmail: payment.recipientEmail,
      amount: payment.amount,
      currency: payment.currency,
      status: payment.status,
      timestamp: payment.timestamp
    }
  });
});

server.use(middlewares);
server.use(router);

// Start server
const PORT = process.env.PORT || 3000;
server.listen(PORT, '0.0.0.0', () => {
  console.log(`JSON Server is running on port ${PORT}`);
  console.log(`Payments endpoint: POST http://localhost:${PORT}/payments`);
  console.log(`Database endpoint: http://localhost:${PORT}/payments`);
});

