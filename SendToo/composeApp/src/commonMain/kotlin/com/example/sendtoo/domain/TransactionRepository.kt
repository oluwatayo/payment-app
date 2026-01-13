package com.example.sendtoo.domain

import com.example.sendtoo.domain.model.Payment
import com.example.sendtoo.domain.model.PaymentResponse
import com.example.sendtoo.domain.model.TransactionItem
import kotlinx.coroutines.flow.Flow

interface TransactionRepository {
    fun getAllTransactions(): Flow<List<Payment>>
    suspend fun makePayment(item: TransactionItem): Result<PaymentResponse>
}