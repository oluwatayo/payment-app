package com.example.cashi.service

@Suppress(names = ["EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING"])
actual class FirebaseService {
    actual fun getTransactions(): kotlinx.coroutines.flow.Flow<List<com.example.cashi.domain.model.Payment>> {
        TODO("Not yet implemented")
    }

    actual suspend fun addTransaction(item: com.example.cashi.domain.model.Payment): kotlin.Result<Any> {
        TODO("Not yet implemented")
    }
}