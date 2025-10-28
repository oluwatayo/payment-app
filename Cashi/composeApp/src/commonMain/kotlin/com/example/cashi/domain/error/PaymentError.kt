package com.example.cashi.domain.error


sealed class PaymentError(message: String) : Exception(message)
class InsufficientFunds(message: String) : PaymentError(message)
class InvalidAmount(message: String): PaymentError(message)
class RecipientNotFound(message: String) : PaymentError(message)
class UnsupportedCurrency(message: String) : PaymentError(message)
class NetworkError(message: String = "Network Error Occurred") : PaymentError(message)
class UnknownPaymentError(message: String = defaultErrorMessage) :
    PaymentError(message)

val defaultErrorMessage: String = "Something went wrong on our end, Please try again"