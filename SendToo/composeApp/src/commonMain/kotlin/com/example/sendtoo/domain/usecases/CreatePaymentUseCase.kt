package com.example.sendtoo.domain.usecases

import com.example.sendtoo.domain.TransactionRepository
import com.example.sendtoo.domain.model.TransactionItem

class CreatePaymentUseCase(
    private val repository: TransactionRepository
) {
    suspend operator fun invoke(item: TransactionItem) = repository.makePayment(item)
}