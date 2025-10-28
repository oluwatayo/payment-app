package com.example.cashi.test

import com.example.cashi.data.TransactionRepositoryImpl
import com.example.cashi.data.network.PaymentService
import com.example.cashi.domain.model.Payment
import com.example.cashi.domain.model.PaymentResponse
import com.example.cashi.domain.model.TransactionItem
import com.example.cashi.service.FirebaseService
import io.cucumber.java.After
import io.cucumber.java.Before
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.coVerifyOrder
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.unmockkAll
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class TransactionRepositoryImplMockTest {

    private lateinit var firebase: FirebaseService
    private lateinit var payment: PaymentService

    private lateinit var systemUnderTest: TransactionRepositoryImpl

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this, relaxed = true)
        firebase = mockk(relaxed = true)
        payment = mockk(relaxed = true)
        systemUnderTest = TransactionRepositoryImpl(firebase, payment)
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `getAllTransactions delegates to firebase flow`() = runTest {
        val expected = listOf(
            Payment(
                id = 1,
                amount = 100.0,
                currency = "USD",
                transactionId = "100",
                timestamp = "",
                recipientEmail = "test@gmail.com",
                status = ""
            ),
            Payment(
                id = 2,
                amount = 50.0,
                currency = "USD",
                transactionId = "101",
                timestamp = "",
                recipientEmail = "test2@gmail.com",
                status = ""
            )
        )
        every { firebase.getTransactions() } returns flowOf(expected)

        val result: Flow<List<Payment>> = systemUnderTest.getAllTransactions()
        var received: List<Payment>? = null
        result.collect { list ->
            received = list
            return@collect
        }
        assertEquals(expected, received)

        verify(exactly = 1) { firebase.getTransactions() }
        confirmVerified(firebase)
    }

    @Test
    fun `makePayment returns success when api and firebase succeed`() = runTest {
        val paymentObj = Payment(
            id = 10,
            amount = 49.99,
            currency = "USD",
            transactionId = "100",
            timestamp = "",
            recipientEmail = "test@gmail.com",
            status = ""
        )
        coEvery { payment.makePayment(any()) } returns Result.success(
            PaymentResponse(
                payment = paymentObj,
                success = true
            )
        )
        coEvery { firebase.addTransaction(paymentObj) } returns Result.success(Unit)

        val item =
            TransactionItem(recipientEmail = "test@gmail.com", amount = 49.99, currency = "USD")
        val result = systemUnderTest.makePayment(item)

        assertTrue(result.isSuccess)
        assertEquals(paymentObj, result.getOrNull()!!.payment)

        coVerifyOrder {
            payment.makePayment(item)
            firebase.addTransaction(paymentObj)
        }
        confirmVerified(payment, firebase)
    }

    @Test
    fun `makePayment bubbles up api failure`() = runTest {
        val err = IllegalStateException("network down")
        coEvery { payment.makePayment(any()) } returns Result.failure(err)

        val result =
            systemUnderTest.makePayment(
                TransactionItem(
                    recipientEmail = "test@gmail.com",
                    amount = 1.0,
                    currency = "USD"
                )
            )

        assertTrue(result.isFailure)
        assertSame(err, result.exceptionOrNull())

        coVerify(exactly = 1) { payment.makePayment(any()) }
        coVerify(exactly = 0) { firebase.addTransaction(any()) }
        confirmVerified(payment, firebase)
    }

    @Test
    fun `makePayment fails when firebase write fails`() = runTest {
        val paymentObj = Payment(
            id = 111,
            amount = 10.0,
            currency = "USD",
            transactionId = "103340",
            timestamp = "",
            recipientEmail = "test@gmail.com",
            status = ""
        )
        val fbErr = RuntimeException("firebase failed")

        coEvery { payment.makePayment(any()) } returns Result.success(
            PaymentResponse(
                payment = paymentObj,
                success = true
            )
        )
        coEvery { firebase.addTransaction(paymentObj) } returns Result.failure(fbErr)

        val result =
            systemUnderTest.makePayment(
                TransactionItem(
                    recipientEmail = "test@gmail.com",
                    amount = 10.0,
                    currency = "USD"
                )
            )

        assertTrue(result.isFailure)
        assertSame(fbErr, result.exceptionOrNull())

        coVerifyOrder {
            payment.makePayment(any())
            firebase.addTransaction(paymentObj)
        }
        confirmVerified(payment, firebase)
    }

    @Test
    fun `makePayment fails when api returns success without payment`() = runTest {
        coEvery { payment.makePayment(any()) } returns Result.success(
            PaymentResponse(
                payment = null,
                success = false
            )
        )

        val result =
            systemUnderTest.makePayment(
                TransactionItem(
                    recipientEmail = "test@gmail.com",
                    amount = 10.0,
                    currency = "USD"
                )
            )

        assertTrue(result.isFailure)
        assertEquals("Payment did not complete successfully", result.exceptionOrNull()?.message)

        coVerify(exactly = 1) { payment.makePayment(any()) }
        coVerify(exactly = 0) { firebase.addTransaction(any()) }
        confirmVerified(payment, firebase)
    }
}