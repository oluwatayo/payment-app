package com.example.cashi.presentation.form

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cashi.domain.model.TransactionItem
import com.example.cashi.domain.usecases.CreatePaymentUseCase
import com.example.cashi.utils.Validator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.component.KoinScopeComponent
import org.koin.core.component.inject
import org.koin.core.scope.Scope
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class PaymentViewModel : ViewModel(), KoinScopeComponent {
    @OptIn(ExperimentalUuidApi::class)
    override val scope: Scope by lazy {
        getKoin().createScope<PaymentViewModel>(Uuid.random().toString())
    }
    private val createPaymentUseCase: CreatePaymentUseCase by inject()
    private val validator: Validator by inject()
    private val _state = MutableStateFlow(PaymentFormUiState())
    val state: StateFlow<PaymentFormUiState> = _state.asStateFlow()

    private fun makePayment() {
        viewModelScope.launch {
            _state.update { it.copy(submitting = true, submitError = null) }
            val uiState = state.value
            createPaymentUseCase(
                TransactionItem(
                    recipientEmail = uiState.recipientEmail,
                    amount = uiState.amount.toDoubleOrNull() ?: 0.0,
                    currency = uiState.currency
                )
            ).fold(onSuccess = {
                _state.update { state ->
                    state.copy(
                        submitting = false,
                        completedPayment = it.payment,
                        recipientEmail = "",
                        amount = "",
                        currency = ""
                    )
                }
                println(it)
            }, onFailure = { error ->
                _state.update { it.copy(submitting = false, submitError = error.message) }
                error.printStackTrace()
                println(error.message)
            })
        }
    }

    fun dismissPaymentResult() {
        _state.update {
            it.copy(
                completedPayment = null,
                submitError = null
            )
        }
    }

    fun handleUIEvents(uiEvents: PaymentFormUIEvents) {
        when (uiEvents) {
            is PaymentFormUIEvents.OnAmountChanged -> _state.update {
                it.copy(
                    amount = uiEvents.amount,
                    amountError = null,
                    submitError = null
                )
            }

            is PaymentFormUIEvents.OnCurrencyChanged -> _state.update {
                it.copy(
                    currency = uiEvents.currency,
                    currencyError = null,
                    submitError = null
                )
            }

            is PaymentFormUIEvents.OnRecipientEmailChanged -> _state.update {
                it.copy(
                    recipientEmail = uiEvents.email,
                    recipientEmailError = null,
                    submitError = null
                )
            }

            PaymentFormUIEvents.SubmitPayment -> validateFields()
            PaymentFormUIEvents.NavigateToHistory -> _state.update {
                it.copy(
                    startNavigationToHistory = true
                )
            }

            PaymentFormUIEvents.OnPaymentCompletedDismissed -> dismissPaymentResult()
        }
    }

    private fun validateFields() {
        val uiState = state.value
        if (!validator.isValidEmail(uiState.recipientEmail)) {
            _state.update { it.copy(recipientEmailError = "Provide a valid email address") }
            return
        }
        if (!validator.isAmountValid(uiState.amount)) {
            _state.update { it.copy(amountError = "Provide a valid amount") }
            return
        }
        if (!validator.isCurrencyValid(uiState.currency)) {
            _state.update { it.copy(currencyError = "Select a currency") }
            return
        }
        makePayment()
    }


    override fun onCleared() {
        super.onCleared()
        scope.close()
    }

    fun resetNavigationToHistory() {
        _state.update { it.copy(startNavigationToHistory = false) }
    }

    companion object Companion {
        private val EMAIL_REGEX =
            Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
    }
}