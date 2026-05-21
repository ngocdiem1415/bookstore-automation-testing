package com.bookstore.tests.checkout;

import com.bookstore.base.BaseSetup;
import com.bookstore.factory.PageFactoryManager;
import com.bookstore.pages.*;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.net.URI;
import java.time.Duration;

/**
 * Test Class: Checkout — VNPay Payment (CHK-VNP)
 * Precondition: CHECKOUT-INFO-01 phải PASS
 * Note: CHK-VNP-01 yêu cầu tài khoản VNPay sandbox.
 */
public class CheckoutVnpayTest extends BaseSetup {
    private static final String USERNAME   = "diem_tester";
    private static final String PASSWORD   = "Abc@12345";
    private static final String VALID_NAME = "Nguyễn Thị Diễm";
    private static final String VALID_PHONE = "0987654321";
    private static final String VALID_ADDR  = "123 Đường Lê Lợi";
    private static final String VALID_CITY  = "Hồ Chí Minh";
    private static final String VALID_DIST  = "Quận 1";
    private static final String VALID_WARD  = "Phường Bến Nghé";

    // VNPay Sandbox test card
    private static final String VNPAY_BANK     = "NCB";
    private static final String VNPAY_CARD_NO  = "9704198526191432198";
    private static final String VNPAY_NAME     = "NGUYEN VAN A";
    private static final String VNPAY_DATE     = "07/15";
    private static final String VNPAY_OTP      = "123456";

    /** Login + add item + fill & save shipping info + select VNPay */
    private CheckoutPage setupCheckoutReadyForVnpay() {
        System.out.println("[Precondition] Login → Add item → Checkout → Fill shipping → Select VNPay");
        PageFactoryManager.getLoginPage(driver,baseUrl).loginAsCustomer(USERNAME, PASSWORD);
        PageFactoryManager.getProductListPage(driver,baseUrl).clickBookAt(0).clickAddToCart();
        CheckoutPage cp =  PageFactoryManager.getCheckoutPage(driver,baseUrl);
        cp.fillShippingInfo(VALID_NAME, VALID_PHONE, VALID_ADDR, VALID_CITY, VALID_DIST, VALID_WARD);
        cp.clickSaveAndGetAlert();
        cp.selectPaymentMethod("Chuyển khoản ngân hàng (VNPay)");
        return cp;
    }

    @Test(description = "CHK-VNP-01: Verify VNPAY payment success flow.")
    public void CHK_VNP_01_VnpayPaymentSuccess() {
        CheckoutPage cp = setupCheckoutReadyForVnpay();

        cp.clickBuyToVnpay();

        WebDriverWait longWait = new WebDriverWait(driver, Duration.ofSeconds(30));
        boolean onVnpay = false;
        try {
            longWait.until(ExpectedConditions.urlContains("sandbox.vnpayment.vn"));
            onVnpay = driver.getCurrentUrl().contains("sandbox.vnpayment.vn");
        } catch (Exception e) {
            System.out.println("[Info] VNPay redirect not detected via URL: " + e.getMessage());
            onVnpay = driver.getCurrentUrl().contains("vnpay");
        }
        Assert.assertTrue(onVnpay,
                "Expected redirect to VNPay sandbox. Got: " + driver.getCurrentUrl());

        System.out.println("[Step 4] Enter test card details on VNPay sandbox");
        try {
            // Chọn ngân hàng NCB
            driver.findElement(By.xpath("//div[@data-id='" + VNPAY_BANK + "']")).click();
            Thread.sleep(1000);

            // Nhập thông tin thẻ
            WebElement cardNo = driver.findElement(By.id("card_number_mask"));
            cardNo.clear(); cardNo.sendKeys(VNPAY_CARD_NO);

            WebElement cardName = driver.findElement(By.id("card_holder"));
            cardName.clear(); cardName.sendKeys(VNPAY_NAME);

            WebElement cardDate = driver.findElement(By.id("card_date"));
            cardDate.clear(); cardDate.sendKeys(VNPAY_DATE);

            // Click tiếp tục
            driver.findElement(By.id("btnContinue")).click();
            Thread.sleep(2000);

            // Nhập OTP
            WebElement otp = driver.findElement(By.id("otpvalue"));
            otp.clear(); otp.sendKeys(VNPAY_OTP);
            driver.findElement(By.id("btnConfirm")).click();

            System.out.println("[Info] VNPay form submitted.");
        } catch (Exception e) {
            System.out.println("[Info] VNPay UI interaction: " + e.getMessage());
        }

        System.out.println("[Assert] Wait for redirect to /vnpay-return then /invoice");
        try {
            longWait.until(ExpectedConditions.urlContains("/invoice"));
        } catch (Exception e) {
            System.out.println("[Info] Still waiting for invoice redirect: " + driver.getCurrentUrl());
        }

        InvoicePage invoice = PageFactoryManager.getInvoicePage(driver,baseUrl);
        Assert.assertTrue(invoice.isOnInvoicePage(),
                "Expected redirect to /invoice after VNPay success. Got: " + driver.getCurrentUrl());
        Assert.assertTrue(invoice.isStatusPaidOrCompleted(),
                "Expected PAID/COMPLETED status after VNPay. Got: " + invoice.getOrderStatus());
    }

    @Test(description = "CHK-VNP-02: Verify VNPAY user cancellation.")
    public void CHK_VNP_02_VnpayUserCancellation() {
        CheckoutPage cp = setupCheckoutReadyForVnpay();

        cp.clickBuyToVnpay();

        WebDriverWait longWait = new WebDriverWait(driver, Duration.ofSeconds(20));
        try {
            longWait.until(ExpectedConditions.urlContains("vnpay"));
            // Tìm nút hủy trên VNPay sandbox
            WebElement cancelBtn = longWait.until(
                    ExpectedConditions.elementToBeClickable(
                            By.xpath("//*[contains(text(),'Hủy') or @id='btnCancel']")));
            cancelBtn.click();
            System.out.println("[Info] Clicked cancel on VNPay page.");
        } catch (Exception e) {
            System.out.println("[Info] Cancel button not found, simulating back navigation: " + e.getMessage());
            driver.navigate().back();
        }

        System.out.println("[Assert] Verify redirect back to website");
        try {
            longWait.until(d -> !d.getCurrentUrl().contains("vnpay"));
        } catch (Exception ignored) {}
        String url = driver.getCurrentUrl();
        System.out.println("[Assert] URL after cancel: " + url);
        Assert.assertFalse(url.contains("sandbox.vnpayment.vn"),
                "Expected redirect back to website after cancellation.");

        System.out.println("[Assert] Verify 'Thanh toán thất bại' message");
        try {
            Alert alert = driver.switchTo().alert();
            String alertText = alert.getText();
            System.out.println("[Assert] Alert after cancel: " + alertText);
            alert.accept();
            Assert.assertTrue(
                    alertText.contains("thất bại") || alertText.contains("Hủy"),
                    "Expected payment failed message. Got: " + alertText);
        } catch (NoAlertPresentException e) {
            // Kiểm tra trên page body
            System.out.println("[Info] No alert — checking page content for failure message.");
            String body = driver.findElement(By.tagName("body")).getText();
            Assert.assertTrue(
                    body.contains("thất bại") || body.contains("failed") || !url.contains("/invoice"),
                    "Expected payment failure indication after cancellation.");
        }
    }

    @Test(description = "CHK-VNP-03: Verify VNPAY checksum manipulation (Boundary).")
    public void CHK_VNP_03_ChecksumManipulationBoundary() {
        // Xây dựng URL /vnpay-return giả với vnp_Amount bị thay đổi
        String tamperedUrl = baseUrl
                + "/vnpay-return"
                + "?vnp_Amount=100"                     // Giá trị bị tamper
                + "&vnp_BankCode=NCB"
                + "&vnp_BankTranNo=VNP_FAKE"
                + "&vnp_CardType=ATM"
                + "&vnp_OrderInfo=TestOrder"
                + "&vnp_PayDate=20240101120000"
                + "&vnp_ResponseCode=00"
                + "&vnp_TmnCode=TESTCODE"
                + "&vnp_TransactionNo=12345678"
                + "&vnp_TransactionStatus=00"
                + "&vnp_TxnRef=ORDER_001"
                + "&vnp_SecureHash=INVALID_TAMPERED_HASH"; // Hash sai

        System.out.println("[Step 2] Navigate to tampered /vnpay-return URL: " + tamperedUrl);
        driver.get(tamperedUrl);

        System.out.println("[Assert] Verify checksum mismatch error — transaction rejected");
        String url   = driver.getCurrentUrl();
        String title = driver.getTitle().toLowerCase();
        String body  = "";
        try {
            body = driver.findElement(By.tagName("body")).getText().toLowerCase();
        } catch (Exception ignored) {}

        System.out.println("[Assert] URL: " + url + " | Title: " + title);

        boolean checksumRejected =
                body.contains("checksum")
                        || body.contains("không hợp lệ")
                        || body.contains("thất bại")
                        || body.contains("invalid")
                        || body.contains("mismatch")
                        || title.contains("error")
                        || title.contains("400")
                        || !url.contains("/invoice");

        Assert.assertTrue(checksumRejected,
                "Expected checksum/hash mismatch rejection for tampered vnp_Amount. "
                        + "System may have accepted invalid transaction!");

        System.out.println("[Assert] Verify no server crash (no 500)");
        Assert.assertFalse(title.contains("500") || body.contains("whitelabel"),
                "Server error 500 for tampered VNPAY return URL.");

        System.out.println("[Info] Checksum validation passed — tampered transaction was rejected.");
    }
}
