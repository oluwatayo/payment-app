package com.example.sendtoo.domain.usecases

import com.example.sendtoo.domain.TransactionRepository
import com.example.sendtoo.domain.model.Payment
import kotlinx.coroutines.flow.Flow

class GetTransactionHistoryUseCase(
    private val repository: TransactionRepository
) {
    operator fun invoke(): Flow<List<Payment>> = repository.getAllTransactions()
}