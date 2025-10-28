package com.example.cashi.data

import com.example.cashi.data.network.PaymentService
import com.example.cashi.domain.TransactionRepository
import com.example.cashi.domain.model.Payment
import com.example.cashi.domain.model.PaymentResponse
import com.example.cashi.domain.model.TransactionItem
import com.example.cashi.service.FirebaseService
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