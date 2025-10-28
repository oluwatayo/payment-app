package com.example.cashi.presentation.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cashi.domain.model.Payment
import com.example.cashi.domain.usecases.GetTransactionHistoryUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import org.koin.core.component.KoinScopeComponent
import org.koin.core.component.inject
import org.koin.core.scope.Scope
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class TransactionHistoryViewModel : ViewModel(), KoinScopeComponent {
    @OptIn(ExperimentalUuidApi::class)
    override val scope: Scope by lazy {
        getKoin().createScope<TransactionHistoryViewModel>(
            Uuid.random().toString()
        )
    }

    private val getTransactionsHistoryUseCase: GetTransactionHistoryUseCase by inject()
    val state: StateFlow<List<Payment>> =  getTransactionsHistoryUseCase().stateIn(
        viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )
}