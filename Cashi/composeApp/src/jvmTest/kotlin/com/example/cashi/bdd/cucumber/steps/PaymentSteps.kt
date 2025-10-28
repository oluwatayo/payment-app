package com.example.cashi.bdd.cucumber.steps

import com.example.cashi.bdd.cucumber.setup.TransactionRepositoryFake
import com.example.cashi.domain.TransactionRepository
import com.example.cashi.domain.error.InsufficientFunds
import com.example.cashi.domain.error.RecipientNotFound
import com.example.cashi.domain.error.UnsupportedCurrency
import com.example.cashi.domain.model.Payment
import com.example.cashi.domain.model.TransactionItem
import com.example.cashi.domain.usecases.CreatePaymentUseCase
import com.example.cashi.domain.usecases.GetTransactionHistoryUseCase
import io.cucumber.java.After
import io.cucumber.java.Before
import io.cucumber.java.en.Given
import io.cucumber.java.en.Then
import io.cucumber.java.en.When
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.serialization.json.Json
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.assertNotNull

@OptIn(ExperimentalCoroutinesApi::class)
class PaymentSteps {
    private lateinit var repo: TransactionRepository
    private lateinit var createPaymentUseCase: CreatePaymentUseCase
    private lateinit var getTransactionHistoryUseCase: GetTransactionHistoryUseCase
    private val testScheduler = TestCoroutineScheduler()
    private val dispatcher = StandardTestDispatcher(testScheduler)
    private val testScope = TestScope(dispatcher)
    private var lastResult: Result<*>? = null


    @Before
    fun installMainDispatcher() {
        Dispatchers.setMain(dispatcher)
    }


    @After
    fun resetMainDispatcher() {
        Dispatchers.resetMain()
    }

    @Given("Valid payment request data")
    fun emptyRepo() {
        repo = TransactionRepositoryFake()
        getTransactionHistoryUseCase = GetTransactionHistoryUseCase(repo)
        createPaymentUseCase = CreatePaymentUseCase(repo)
    }

    @When("I create a payment object:")
    fun createPayment(payload: String) = runTest {
        println("payload is $payload")
        val json = Json { ignoreUnknownKeys = true }
        val item = json.decodeFromString<TransactionItem>(payload)
        lastResult = createPaymentUseCase(item)
    }

    @Then("I should see {int} transaction with amount {double}")
    fun verifyTransactions(count: Int, amount: Double) = runTest {
        val tx: List<Payment> = getTransactionHistoryUseCase().first()
        assertEquals(count, tx.size)
        assertEquals(amount, tx.first().amount, 1e-6)
    }

    @Then("I should get an UnsupportedCurrency error")
    fun verifyUnsupportedCurrencyError() = runTest {
        val result = lastResult
        assertNotNull(result)
        assertTrue(result.isFailure)
        val ex = result.exceptionOrNull()
        assertTrue(ex is UnsupportedCurrency)
        val tx: List<Payment> = getTransactionHistoryUseCase().first()
        assertEquals(0, tx.size)
    }

    @Then("I should get a RecipientNotFound error")
    fun verifyRecipientNotFoundError() = runTest {
        val result = lastResult
        assertNotNull(result)
        assertTrue(result.isFailure)
        val ex = result.exceptionOrNull()
        assertTrue(ex is RecipientNotFound)
        val tx: List<Payment> = getTransactionHistoryUseCase().first()
        assertEquals(0, tx.size)
    }

    @Then("I should get an InsufficientFunds error")
    fun verifyInsufficientFundsError() = runTest {
        val result = lastResult
        assertNotNull(result)
        assertTrue(result.isFailure)
        val ex = result.exceptionOrNull()
        assertTrue(ex is InsufficientFunds)
        val tx: List<Payment> = getTransactionHistoryUseCase().first()
        assertEquals(0, tx.size)
    }
}