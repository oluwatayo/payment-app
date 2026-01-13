package com.example.sendtoo.service

import com.example.sendtoo.domain.model.Payment
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual class FirebaseService {
    actual fun getTransactions(): Flow<List<Payment>> {
        return flowOf()
    }

    actual suspend fun addTransaction(item: Payment): Result<Any> {
        TODO("Not yet implemented")
    }
}