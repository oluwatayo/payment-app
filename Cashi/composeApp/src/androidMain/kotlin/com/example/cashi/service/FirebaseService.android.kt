package com.example.cashi.service

import com.example.cashi.domain.model.Payment
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.suspendCancellableCoroutine

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual class FirebaseService {

    private val transactionCollection = "transactions"
    private val firestore: FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance()
    }

    actual fun getTransactions(): Flow<List<Payment>> = callbackFlow {
        val collection = firestore.collection(transactionCollection)
        val subscription = collection.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }
            val items = snapshot?.documents?.mapNotNull { doc ->
                doc.toObject(Payment::class.java)
            } ?: emptyList()
            trySend(items)
        }
        awaitClose {
            subscription.remove()
        }
    }

    actual suspend fun addTransaction(item: Payment): Result<Any> =
        suspendCancellableCoroutine { cont ->
            runCatching {
                val collection = firestore.collection(transactionCollection)
                collection.add(item)
                    .addOnSuccessListener { documentReference ->
                        cont.resume(Result.success(documentReference.id)) {}
                    }
                    .addOnFailureListener { exception ->
                        cont.resume(Result.failure(exception)) {}
                    }
            }.onFailure { exception ->
                cont.resume(Result.failure(exception)) {}
            }
        }
}