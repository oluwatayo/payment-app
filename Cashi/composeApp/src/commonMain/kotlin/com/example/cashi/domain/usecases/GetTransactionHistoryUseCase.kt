package com.example.cashi.domain.usecases

import com.example.cashi.domain.TransactionRepository
import com.example.cashi.domain.model.Payment
import kotlinx.coroutines.flow.Flow

class GetTransactionHistoryUseCase(
    private val repository: TransactionRepository
) {
    operator fun invoke(): Flow<List<Payment>> = repository.getAllTransactions()
}