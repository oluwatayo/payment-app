package com.example.cashi.domain

import com.example.cashi.domain.model.Payment
import com.example.cashi.domain.model.PaymentResponse
import com.example.cashi.domain.model.TransactionItem
import kotlinx.coroutines.flow.Flow

interface TransactionRepository {
    fun getAllTransactions(): Flow<List<Payment>>
    suspend fun makePayment(item: TransactionItem): Result<PaymentResponse>
}