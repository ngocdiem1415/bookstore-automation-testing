package com.bookstore.tests.checkout;

import com.bookstore.base.BaseSetup;
import com.bookstore.factory.PageFactoryManager;
import com.bookstore.pages.*;
import org.openqa.selenium.NoAlertPresentException;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Test Class: Checkout — Shipping Information (CHECKOUT-INFO)
 * Precondition: CART-ADD-01 (có sản phẩm trong giỏ hàng)
 */
public class CheckoutInfoTest extends BaseSetup {
    private static final String USERNAME = "diem_tester";
    private static final String PASSWORD = "Abc@12345";
    private static final String VALID_NAME     = "Nguyễn Thị Diễm";
    private static final String VALID_PHONE    = "0987654321";
    private static final String VALID_ADDRESS  = "123 Đường Lê Lợi";
    private static final String VALID_CITY     = "Hồ Chí Minh";
    private static final String VALID_DISTRICT = "Quận 1";
    private static final String VALID_WARD     = "Phường Bến Nghé";


    /** Login + thêm sản phẩm vào giỏ + mở Checkout */
    private CheckoutPage loginAddItemAndOpenCheckout() {
        System.out.println("[Precondition] Login → Add item to cart → Open Checkout");
        PageFactoryManager.getLoginPage(driver,baseUrl).loginAsCustomer(USERNAME, PASSWORD);
        PageFactoryManager.getProductListPage(driver,baseUrl).clickBookAt(0).clickAddToCart();
        return  PageFactoryManager.getCheckoutPage(driver,baseUrl);
    }

    @Test(description = "CHECKOUT-INFO-01: Verify filling shipping info successfully.")
    public void CHECKOUT_INFO_01_FillShippingInfoSuccess() {
        CheckoutPage checkoutPage = loginAddItemAndOpenCheckout();

        checkoutPage.fillShippingInfo(VALID_NAME, VALID_PHONE, VALID_ADDRESS,
                VALID_CITY, VALID_DISTRICT, VALID_WARD);

        String alertText = checkoutPage.clickSaveAndGetAlert();

        Assert.assertTrue(
                alertText.contains("thành công") || alertText.contains("Cập nhật"),
                "Expected success alert 'Cập nhật địa chỉ và phí giao hàng thành công'. Got: " + alertText);

        Assert.assertTrue(checkoutPage.isShippingFeeDisplayed(),
                "Expected shipping fee to be displayed after saving info.");

        Assert.assertTrue(checkoutPage.isBuyButtonEnabled(),
                "Expected [checkout-buy-btn] to become enabled after saving shipping info.");
    }

    @Test(description = "CHECKOUT-INFO-02: Verify validation on empty required fields.")
    public void CHECKOUT_INFO_02_EmptyWardBlocked() {
        CheckoutPage checkoutPage = loginAddItemAndOpenCheckout();

        checkoutPage.enterName(VALID_NAME)
                .enterPhone(VALID_PHONE)
                .enterAddress(VALID_ADDRESS)
                .selectCity(VALID_CITY)
                .selectDistrict(VALID_DISTRICT);
        // Không chọn ward → giữ trống

        String alertText = checkoutPage.clickSaveAndGetAlert();
        System.out.println("[Assert] Alert text: " + alertText);

        Assert.assertTrue(
                alertText.contains("đầy đủ") || alertText.contains("Vui lòng"),
                "Expected validation alert 'Vui lòng điền đầy đủ thông tin giao hàng'. Got: " + alertText);

        System.out.println("[Assert] Verify [checkout-buy-btn] remains disabled");
        Assert.assertFalse(checkoutPage.isBuyButtonEnabled(),
                "Expected [checkout-buy-btn] to remain DISABLED when ward is empty.");
    }

    @Test(description = "CHECKOUT-INFO-03: Verify XSS injection in shipping address (Boundary).")
    public void CHECKOUT_INFO_03_XssAddressBoundary() {
        CheckoutPage checkoutPage = loginAddItemAndOpenCheckout();

        String xssPayload = "<script>alert('XSS')</script>";
        System.out.println("[Step 2] Input XSS payload into [checkout-address]: " + xssPayload);
        checkoutPage.fillShippingInfo(VALID_NAME, VALID_PHONE, xssPayload,
                VALID_CITY, VALID_DISTRICT, VALID_WARD);

        System.out.println("[Step 3a] Click [checkout-save-btn]");
        String saveAlert = checkoutPage.clickSaveAndGetAlert();
        System.out.println("[Assert] Save alert: " + saveAlert);

        System.out.println("[Step 3b] Click [checkout-buy-btn]");
        checkoutPage.selectPaymentMethod("Thanh toán khi nhận hàng (COD)");
        InvoicePage invoicePage = checkoutPage.clickBuyExpectingSuccess();

        System.out.println("[Assert] Verify order saved and page not crashed");
        Assert.assertTrue(invoicePage.isInvoiceDisplayed(),
                "Expected order to be saved. Invoice page not displayed.");

        System.out.println("[Assert] Verify XSS payload NOT executed (no rogue alert from script)");
        boolean xssExecuted = false;
        try {
            // Nếu XSS thực thi → sẽ có JS alert popup
            org.openqa.selenium.Alert rogue = driver.switchTo().alert();
            String rogueText = rogue.getText();
            System.out.println("[SECURITY FAIL] Rogue alert detected: " + rogueText);
            rogue.accept();
            xssExecuted = true;
        } catch (NoAlertPresentException e) {
            System.out.println("[Assert] No rogue alert — XSS payload was escaped correctly.");
        }
        Assert.assertFalse(xssExecuted,
                "SECURITY VULNERABILITY: XSS payload was executed! Address field not sanitized.");
    }
}
