package com.example.cashi.bdd.cucumber.setup

import com.example.cashi.domain.TransactionRepository
import com.example.cashi.domain.model.Payment
import com.example.cashi.domain.model.PaymentResponse
import com.example.cashi.domain.model.TransactionItem
import com.example.cashi.domain.error.InsufficientFunds
import com.example.cashi.domain.error.RecipientNotFound
import com.example.cashi.domain.error.UnsupportedCurrency
import com.example.cashi.domain.error.defaultErrorMessage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf


class TransactionRepositoryFake(
    var balance: Double = 1_000.0,
    var knownRecipients: MutableSet<String> = mutableSetOf("alice@example.com", "bob@example.com"),
    var supportedCurrencies: MutableSet<String> = mutableSetOf("USD", "NGN", "EUR")
) : TransactionRepository {

    val transactions: MutableList<Payment> = mutableListOf()

    override fun getAllTransactions(): Flow<List<Payment>> {
        return flowOf(transactions.toList())
    }

    override suspend fun makePayment(item: TransactionItem): Result<PaymentResponse> {
        if (!supportedCurrencies.contains(item.currency)) {
            return Result.failure(UnsupportedCurrency(message = defaultErrorMessage))
        }
        if (!knownRecipients.contains(item.recipientEmail)) {
            return Result.failure(RecipientNotFound(message = defaultErrorMessage))
        }
        if (item.amount > balance) {
            return Result.failure(InsufficientFunds(message = defaultErrorMessage))
        }

        balance -= item.amount
        val payment = Payment(
            id = (transactions.size + 1).toLong(),
            transactionId = "tx-${System.currentTimeMillis()}",
            recipientEmail = item.recipientEmail,
            amount = item.amount,
            currency = item.currency,
            status = "Success",
            timestamp = "2024-01-01T12:00:00Z",
        )
        transactions.add(payment)
        return Result.success(PaymentResponse(payment = payment, success = true))
    }
}