package com.example.sendtoo.di

import com.example.sendtoo.data.TransactionRepositoryImpl
import com.example.sendtoo.data.network.PaymentService
import com.example.sendtoo.domain.TransactionRepository
import com.example.sendtoo.domain.usecases.CreatePaymentUseCase
import com.example.sendtoo.domain.usecases.GetTransactionHistoryUseCase
import com.example.sendtoo.presentation.form.PaymentViewModel
import com.example.sendtoo.presentation.history.TransactionHistoryViewModel
import com.example.sendtoo.utils.Validator
import org.koin.core.module.dsl.viewModelOf
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

expect val platformModule: Module

val sharedModule = module {
    singleOf(::TransactionRepositoryImpl).bind(TransactionRepository::class)
    singleOf(::PaymentService)
    singleOf(::Validator)
    scope<PaymentViewModel> {
        scoped {
            CreatePaymentUseCase(get())
        }
    }
    scope<TransactionHistoryViewModel> {
        scoped {
            GetTransactionHistoryUseCase(get())
        }
    }
    viewModelOf(::PaymentViewModel)
    viewModelOf(::TransactionHistoryViewModel)
}