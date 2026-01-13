package com.example.sendtoo.data

import com.example.sendtoo.data.network.PaymentService
import com.example.sendtoo.domain.TransactionRepository
import com.example.sendtoo.domain.model.Payment
import com.example.sendtoo.domain.model.PaymentResponse
import com.example.sendtoo.domain.model.TransactionItem
import com.example.sendtoo.service.FirebaseService
import kotlinx.coroutines.flow.Flow

class TransactionRepositoryImpl(
    private val firebaseService: FirebaseService,
    private val paymentService: PaymentService
) :
    TransactionRepository {
    override fun getAllTransactions(): Flow<List<Payment>> {
        return firebaseService.getTransactions()
    }

    override suspend fun makePayment(item: TransactionItem): Result<PaymentResponse> {
        val result = paymentService.makePayment(item)
        return if (result.isSuccess) {
            val paymentResponse = result.getOrNull()
            paymentResponse?.payment?.let {
                val firebaseResult = firebaseService.addTransaction(it)
                if (firebaseResult.isSuccess) {
                    Result.success(paymentResponse)
                } else {
                    Result.failure(
                        firebaseResult.exceptionOrNull() ?: Exception("Unknown Firebase error")
                    )
                }
            } ?: kotlin.run {
                Result.failure(Exception("Payment did not complete successfully"))
            }
        } else {
            result
        }
    }
}