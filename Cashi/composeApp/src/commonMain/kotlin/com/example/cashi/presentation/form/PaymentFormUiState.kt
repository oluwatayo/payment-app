package com.example.cashi.presentation.form

import androidx.compose.runtime.Immutable
import com.example.cashi.domain.model.Payment

@Immutable
data class PaymentFormUiState(
    val submitting: Boolean = false,
    val recipientEmail: String = "",
    val recipientEmailError: String? = null,
    val amount: String = "",
    val amountError: String? = null,
    val currency: String = "",
    val currencyError: String? = null,
    val submitError: String? = null,
    val startNavigationToHistory: Boolean = false,
    val completedPayment: Payment? = null
) {
    val isSubmitButtonEnabled: Boolean
        get() = recipientEmailError == null &&
                recipientEmail.isNotEmpty() &&
                amount.isNotEmpty() &&
                currency.isNotEmpty() &&
                !submitting
}