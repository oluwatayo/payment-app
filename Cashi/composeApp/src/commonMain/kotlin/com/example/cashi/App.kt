package com.example.cashi


import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import org.jetbrains.compose.ui.tooling.preview.Preview
import com.example.cashi.presentation.form.PaymentFormRoute
import com.example.cashi.presentation.history.TransactionHistoryRoute

@Composable
@Preview
fun App() {
    MaterialTheme {
        val navController = rememberNavController()
        NavHost(navController = navController, startDestination = "payment_form") {
            composable("payment_form") {
                PaymentFormRoute(onNavigateToHistory = { navController.navigate("transaction_history") })
            }
            composable("transaction_history") {
                TransactionHistoryRoute(onNavigateBack = { navController.popBackStack() })
            }
        }
    }
}