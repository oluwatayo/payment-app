package com.example.cashi.presentation.form

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.cashi.presentation.common.PaymentDetailSheet
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

sealed class PaymentFormUIEvents {
    object SubmitPayment : PaymentFormUIEvents()
    data class OnAmountChanged(val amount: String) : PaymentFormUIEvents()
    data class OnRecipientEmailChanged(val email: String) : PaymentFormUIEvents()
    data class OnCurrencyChanged(val currency: String) : PaymentFormUIEvents()
    object NavigateToHistory : PaymentFormUIEvents()
    object OnPaymentCompletedDismissed : PaymentFormUIEvents()
}

const val LoadingIndicatorSemantic = "LoadingIndicator"
const val SubmitPaymentButtonSemantic = "SubmitPaymentButton"
const val RecipientEmailInputSemantic = "RecipientEmailInput"
const val AmountInputSemantic = "AmountInput"
const val CurrencyDropdownMenuItemSemantic = "CurrencyDropdownMenuItem"
const val TransactionHistoryButtonSemantic = "TransactionHistoryButton"

@Composable
fun PaymentFormRoute(
    onNavigateToHistory: () -> Unit,
    viewModel: PaymentViewModel = koinViewModel()
) {
    val state: PaymentFormUiState by viewModel.state.collectAsStateWithLifecycle()
    LaunchedEffect(state.startNavigationToHistory) {
        if (state.startNavigationToHistory) {
            onNavigateToHistory()
            viewModel.resetNavigationToHistory()
        }
    }
    PaymentFormScreen(state = state, uiEvents = viewModel::handleUIEvents)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentFormScreen(
    state: PaymentFormUiState,
    uiEvents: (PaymentFormUIEvents) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val currencyOptions = listOf("USD", "EUR")
    var expanded by remember { mutableStateOf(false) }

    Scaffold(topBar = {
        TopAppBar(title = {
            Text(text = "Cashi")
        }, actions = {
            Button(
                onClick = {
                    uiEvents(PaymentFormUIEvents.NavigateToHistory)
                }, modifier = Modifier
                    .padding(end = 8.dp)
                    .semantics {
                        contentDescription = TransactionHistoryButtonSemantic
                    }
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.DateRange, contentDescription = "")
                    Text(text = "History", modifier = Modifier.padding(start = 4.dp))
                }
            }
        })
    }, modifier = modifier) { paddingValues ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(vertical = 16.dp, horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("New Payment", style = MaterialTheme.typography.headlineSmall)

            OutlinedTextField(
                value = state.recipientEmail,
                onValueChange = { uiEvents(PaymentFormUIEvents.OnRecipientEmailChanged(email = it)) },
                label = { Text("Recipient email") },
                isError = state.recipientEmailError != null,
                supportingText = { state.recipientEmailError?.let { Text(it) } },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth()
                    .semantics {
                        contentDescription = RecipientEmailInputSemantic
                    }
            )

            OutlinedTextField(
                value = state.amount,
                onValueChange = {
                    uiEvents(PaymentFormUIEvents.OnAmountChanged(amount = it))
                },
                label = { Text("Amount") },
                isError = state.amountError != null,
                supportingText = { state.amountError?.let { Text(it) } },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth()
                    .semantics {
                        contentDescription = AmountInputSemantic
                    }
            )

            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = state.currency,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Currency") },
                    isError = state.currencyError != null,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                    modifier = Modifier
                        .menuAnchor(MenuAnchorType.PrimaryNotEditable, enabled = true)
                        .fillMaxWidth()
                        .semantics {
                            contentDescription = CurrencyDropdownMenuItemSemantic
                        }
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    currencyOptions.forEach { code ->
                        DropdownMenuItem(
                            text = { Text(code) },
                            onClick = {
                                uiEvents(PaymentFormUIEvents.OnCurrencyChanged(currency = code))
                                expanded = false
                            },
                            modifier = Modifier
                        )
                    }
                }
            }

            Button(
                onClick = { uiEvents(PaymentFormUIEvents.SubmitPayment) },
                enabled = state.isSubmitButtonEnabled,
                modifier = Modifier.fillMaxWidth()
                    .semantics {
                        contentDescription = SubmitPaymentButtonSemantic
                    }
            ) {
                if (state.submitting) {
                    CircularProgressIndicator(
                        strokeWidth = 2.dp, modifier = Modifier
                            .size(18.dp)
                            .semantics {
                                contentDescription = LoadingIndicatorSemantic
                            }
                    )
                    Spacer(Modifier.width(8.dp))
                    Text("Submitting…")
                } else {
                    Text("Submit")
                }
            }

            if (state.submitError != null) {
                Text(
                    text = state.submitError,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
        state.completedPayment?.let {
            ModalBottomSheet(onDismissRequest = {
                uiEvents(PaymentFormUIEvents.OnPaymentCompletedDismissed)
            }, sheetState = rememberModalBottomSheetState()) {
                PaymentDetailSheet(payment = it, onCloseButtonClicked = {
                    uiEvents(PaymentFormUIEvents.OnPaymentCompletedDismissed)
                }, isNewTransaction = true)
            }
        }
    }
}

@Preview
@Composable
private fun PaymentFormScreenPreview() {
    MaterialTheme {
        PaymentFormScreen(state = PaymentFormUiState())
    }
}