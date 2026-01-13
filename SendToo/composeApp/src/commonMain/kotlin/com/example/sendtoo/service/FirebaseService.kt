package com.example.sendtoo.service

import com.example.sendtoo.domain.model.Payment
import kotlinx.coroutines.flow.Flow

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
expect class FirebaseService {
    fun getTransactions(): Flow<List<Payment>>
    suspend fun addTransaction(item: Payment): Result<Any>
}