package com.example.cashi.presentation.common

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.cashi.domain.model.Payment
import com.example.cashi.presentation.history.utils.formatCurrency
import com.example.cashi.presentation.history.utils.formatTimestamp

const val CloseTransactionDetailButtonSemantic = "CloseTransactionDetailButton"

@Composable
fun PaymentDetailSheet(
    payment: Payment,
    onCloseButtonClicked: () -> Unit,
    isNewTransaction: Boolean = false,
    modifier: Modifier = Modifier
) {
    val formattedAmount = remember(payment.amount, payment.currency) {
        formatCurrency(payment.amount, payment.currency)
    }
    val formattedTime = remember(payment.timestamp) {
        formatTimestamp(payment.timestamp)
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Close,
            contentDescription = CloseTransactionDetailButtonSemantic,
            modifier = Modifier.align(
                Alignment.End
            ).semantics {
                contentDescription = CloseTransactionDetailButtonSemantic
            }.clickable {
                onCloseButtonClicked()
            }
        )
        Text(
            "Payment ${if (isNewTransaction) "Sent" else "Details"}",
            style = MaterialTheme.typography.titleLarge
        )
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Amount", fontWeight = FontWeight.SemiBold)
            Text(formattedAmount)
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Status", fontWeight = FontWeight.SemiBold)
            StatusChip(status = payment.status)
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Recipient", fontWeight = FontWeight.SemiBold)
            Text(payment.recipientEmail)
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Transaction ID", fontWeight = FontWeight.SemiBold)
            Text(payment.transactionId, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Timestamp", fontWeight = FontWeight.SemiBold)
            Text(formattedTime)
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Currency", fontWeight = FontWeight.SemiBold)
            Text(payment.currency)
        }
    }
}
