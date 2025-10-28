package com.example.cashi.presentation.history

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.cashi.domain.model.Payment
import com.example.cashi.presentation.common.PaymentDetailSheet
import com.example.cashi.presentation.common.StatusChip
import com.example.cashi.presentation.history.utils.formatCurrency
import com.example.cashi.presentation.history.utils.formatTimestamp
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel
import kotlin.time.ExperimentalTime
import kotlin.time.Instant


@Composable
fun TransactionHistoryRoute(
    onNavigateBack: () -> Unit,
    viewModel: TransactionHistoryViewModel = koinViewModel()
) {
    val state: List<Payment> by viewModel.state.collectAsStateWithLifecycle()
    TransactionHistoryScreen(state = state, onNavigateBack)
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalTime::class)
@Composable
fun TransactionHistoryScreen(
    state: List<Payment>,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedPayment: Payment? by remember { mutableStateOf(null) }
    Scaffold(modifier = modifier, topBar = {
        TopAppBar(title = {
            Text(text = "Transaction History")
        }, navigationIcon = {
            Icon(
                imageVector = Icons.AutoMirrored.Default.ArrowBack,
                contentDescription = "Back",
                modifier = Modifier.clickable {
                    onNavigateBack()
                })
        })
    }) { paddingValues ->
        if (state.isEmpty()) {
            EmptyPaymentsState(modifier.fillMaxSize().padding(paddingValues))
        } else {
            val sorted = remember(state) {
                state.sortedByDescending { runCatching { Instant.parse(it.timestamp) }.getOrNull() }
            }

            LazyColumn(
                modifier = modifier
                    .fillMaxSize().padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(sorted, key = { it.id }) { payment ->
                    PaymentCard(payment = payment, onClick = {
                        selectedPayment = payment
                    })
                }
            }
        }
    }
    if (selectedPayment != null) {
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)
        ModalBottomSheet(
            onDismissRequest = { selectedPayment = null },
            sheetState = sheetState
        ) {
            PaymentDetailSheet(payment = selectedPayment!!, onCloseButtonClicked = {
                selectedPayment = null
            })
        }
    }

}

@Composable
fun PaymentCard(
    payment: Payment,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    val formattedAmount = remember(payment.amount, payment.currency) {
        formatCurrency(payment.amount, payment.currency)
    }
    val formattedTime = remember(payment.timestamp) {
        formatTimestamp(payment.timestamp)
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = MaterialTheme.shapes.medium
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = formattedAmount,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f)
                )
                StatusChip(status = payment.status)
            }

            Spacer(Modifier.height(8.dp))

            Text(
                text = payment.recipientEmail,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(Modifier.height(4.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Txn: ${payment.transactionId}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.weight(1f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = formattedTime,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun EmptyPaymentsState(modifier: Modifier = Modifier) {
    Box(modifier, contentAlignment = Alignment.Center) {
        Text(
            "No payments yet",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Preview
@Composable
private fun TransactionHistoryScreenPreview() {
    MaterialTheme {
        TransactionHistoryScreen(state = listOf(), onNavigateBack = {})
    }
}