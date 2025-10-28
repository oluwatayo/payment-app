package com.example.cashi.appium

import com.example.cashi.presentation.common.CloseTransactionDetailButtonSemantic
import com.example.cashi.presentation.form.AmountInputSemantic
import com.example.cashi.presentation.form.CurrencyDropdownMenuItemSemantic
import com.example.cashi.presentation.form.LoadingIndicatorSemantic
import com.example.cashi.presentation.form.RecipientEmailInputSemantic
import com.example.cashi.presentation.form.SubmitPaymentButtonSemantic
import com.example.cashi.presentation.form.TransactionHistoryButtonSemantic
import com.example.cashi.presentation.history.utils.formatCurrency
import io.appium.java_client.android.AndroidDriver
import io.appium.java_client.android.options.UiAutomator2Options
import io.appium.java_client.AppiumBy
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.TestInstance
import org.openqa.selenium.JavascriptExecutor
import org.openqa.selenium.WebElement
import org.openqa.selenium.support.ui.WebDriverWait
import java.net.URL
import java.time.Duration
import kotlin.test.Test

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PaymentFlowTest {
    private lateinit var driver: AndroidDriver

    @BeforeAll
    fun setUp() {
        val options = UiAutomator2Options()
            .setPlatformName("Android")
            .setAutomationName("UiAutomator2")
            .setDeviceName("emulator-5554")
            .setAppPackage("com.example.cashi")
            .setAppActivity(".MainActivity")
            .amend("autoGrantPermissions", true)

        driver = AndroidDriver(URL("http://127.0.0.1:4723/"), options)
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(3))
    }

    @AfterAll
    fun tearDown() {
        driver.quit()
    }

    @Test
    fun makeSuccessfulPayment() {
        val random = (1000..9000).random()
        val randomAmount = (100..10000).random()
        val email = "alice$random@gmail.com"
        val currency = when ((0..1).random()) {
            0 -> "USD"
            else -> "EUR"
        }
        driver.findElement(AppiumBy.accessibilityId(RecipientEmailInputSemantic))
            .setText(email)
        driver.findElement(AppiumBy.accessibilityId(AmountInputSemantic))
            .setText(randomAmount.toString())

        driver.findElement(AppiumBy.accessibilityId(CurrencyDropdownMenuItemSemantic)).click()
        val wait = WebDriverWait(driver, Duration.ofSeconds(5))
        val usd = wait.until {
            driver.findElement(
                AppiumBy.androidUIAutomator(
                    """new UiSelector().className("android.widget.TextView").text("$currency")"""
                )
            )
        }
        usd.click()

        driver.findElement(AppiumBy.accessibilityId(SubmitPaymentButtonSemantic)).click()
        val loadingWait = WebDriverWait(driver, Duration.ofSeconds(15))
        loadingWait.until {
            val loading = driver.findElements(AppiumBy.accessibilityId(LoadingIndicatorSemantic))
            loading.isEmpty()
        }
        driver.findElement(AppiumBy.accessibilityId(CloseTransactionDetailButtonSemantic)).click()
        WebDriverWait(driver, Duration.ofSeconds(1)).until { }
        driver.findElement(AppiumBy.accessibilityId(TransactionHistoryButtonSemantic)).click()

        WebDriverWait(driver, Duration.ofSeconds(1)).until { }

        val formattedAmount = formatCurrency(randomAmount.toDouble(), currency)

        val emailInHistory =
            driver.findElement(AppiumBy.androidUIAutomator("""new UiSelector().textContains("$email")"""))
        Assertions.assertNotNull(emailInHistory)
        val amountInHistory =
            driver.findElement(AppiumBy.androidUIAutomator("""new UiSelector().textContains("$formattedAmount")"""))
        Assertions.assertNotNull(amountInHistory)
    }

    private fun WebElement.setText(text: String) {
        this.click()
        this.clear()
        try {
            this.sendKeys(text)
        } catch (_: Exception) {
            (driver as JavascriptExecutor).executeScript(
                "mobile: type",
                mapOf("text" to text)
            )
        }
    }
}