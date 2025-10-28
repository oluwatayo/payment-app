package com.example.cashi.domain.usecases

import com.example.cashi.domain.TransactionRepository
import com.example.cashi.domain.model.TransactionItem

class CreatePaymentUseCase(
    private val repository: TransactionRepository
) {
    suspend operator fun invoke(item: TransactionItem) = repository.makePayment(item)
}