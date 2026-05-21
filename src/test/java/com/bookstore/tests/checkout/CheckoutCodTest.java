package com.bookstore.tests.checkout;

import com.bookstore.base.BaseSetup;
import com.bookstore.factory.PageFactoryManager;
import com.bookstore.pages.*;
import org.openqa.selenium.JavascriptExecutor;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Test Class: Checkout — COD Payment (CHECKOUT-COD)
 */
public class CheckoutCodTest extends BaseSetup {
    private static final String USERNAME     = "diem_tester";
    private static final String PASSWORD     = "Abc@12345";
    private static final String VALID_NAME   = "Nguyễn Thị Diễm";
    private static final String VALID_PHONE  = "0987654321";
    private static final String VALID_ADDR   = "123 Đường Lê Lợi";
    private static final String VALID_CITY   = "Hồ Chí Minh";
    private static final String VALID_DIST   = "Quận 1";
    private static final String VALID_WARD   = "Phường Bến Nghé";

    private CheckoutPage setupCheckoutReady() {
        System.out.println("[Precondition] Login → Add item → Checkout → Fill & Save shipping info");
        PageFactoryManager.getLoginPage(driver,baseUrl).loginAsCustomer(USERNAME,PASSWORD);
        PageFactoryManager.getProductListPage(driver,baseUrl).clickBookAt(0).clickAddToCart();
        CheckoutPage cp =  PageFactoryManager.getCheckoutPage(driver,baseUrl);
        cp.fillShippingInfo(VALID_NAME, VALID_PHONE, VALID_ADDR, VALID_CITY, VALID_DIST, VALID_WARD);
        cp.clickSaveAndGetAlert();
        return cp;
    }


    @Test(description = "CHECKOUT-COD-01: Verify placing order with standard COD.")
    public void CHECKOUT_COD_01_PlaceOrderCod() {
        CheckoutPage cp = setupCheckoutReady();
        cp.selectPaymentMethod("Thanh toán khi nhận hàng (COD)");
        InvoicePage invoice = cp.clickBuyExpectingSuccess();
        Assert.assertTrue(invoice.isOnInvoicePage(),
                "Expected /invoice URL. Got: " + invoice.getCurrentUrl());

        Assert.assertTrue(invoice.isStatusPending(),
                "Expected PENDING status for COD. Got: " + invoice.getOrderStatus());
    }

    @Test(description = "CHECKOUT-COD-02: Verify placing order when product just went out of stock.")
    public void CHECKOUT_COD_02_OutOfStockOnCheckout() {
        CheckoutPage cp = setupCheckoutReady();
        cp.selectPaymentMethod("Thanh toán khi nhận hàng (COD)");

        String alertText = cp.clickBuyExpectingFailure();

        Assert.assertTrue(
                alertText.contains("hết hàng") || alertText.contains("cập nhật giỏ hàng"),
                "Expected out-of-stock error. Got: " + alertText);

        Assert.assertFalse(cp.getCurrentUrl().contains("/invoice"),
                "Should NOT redirect to /invoice when product is out-of-stock.");
    }

    @Test(description = "CHECKOUT-COD-03: Verify accessing Checkout with empty cart (Boundary).")
    public void CHECKOUT_COD_03_EmptyCartBoundary() {
        PageFactoryManager.getLoginPage(driver,baseUrl).loginAsCustomer(USERNAME, PASSWORD);

        CartPage cart =  PageFactoryManager.getCartPage(driver,baseUrl);
        if (cart.getCartItemCount() > 0) {
            System.out.println("[Precondition] Clearing existing cart items...");
            cart.deleteAllItems();
        }

        driver.get(baseUrl + "/checkout");
        String url = driver.getCurrentUrl();

        boolean safe = url.contains("/cart") || url.contains("/home") || url.equals(baseUrl + "/");
        Assert.assertTrue(safe,
                "Expected redirect away from /checkout for empty cart. Got: " + url);

        String title = driver.getTitle().toLowerCase();
        Assert.assertFalse(title.contains("500"),
                "Server error for empty cart checkout access.");
    }
}
