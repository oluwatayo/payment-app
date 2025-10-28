package com.example.cashi.di

import com.example.cashi.data.TransactionRepositoryImpl
import com.example.cashi.data.network.PaymentService
import com.example.cashi.domain.TransactionRepository
import com.example.cashi.domain.usecases.CreatePaymentUseCase
import com.example.cashi.domain.usecases.GetTransactionHistoryUseCase
import com.example.cashi.presentation.form.PaymentViewModel
import com.example.cashi.presentation.history.TransactionHistoryViewModel
import com.example.cashi.utils.Validator
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