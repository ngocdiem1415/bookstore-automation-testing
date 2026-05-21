package com.bookstore.tests.cart;

import com.bookstore.base.BaseSetup;
import com.bookstore.factory.PageFactoryManager;
import com.bookstore.pages.CartPage;
import com.bookstore.pages.LoginPage;
import com.bookstore.pages.ProductDetailPage;
import com.bookstore.pages.ProductListPage;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Test Class: Cart - Add to Cart (CART-ADD)
 * Precondition: AUTH-LOG-01 (CUSTOMER logged in)
 */
public class CartAddTest extends BaseSetup {
    private static final String USERNAME = "diem_tester";
    private static final String PASSWORD = "Abc@12345";

    private void loginAsCustomer() {
        LoginPage loginPage = PageFactoryManager.getLoginPage(driver, baseUrl);
        loginPage.loginAsCustomer(USERNAME, PASSWORD);
    }

    @Test(description = "CART-ADD-01: Verify user can add an in-stock product to cart.")
    public void CART_ADD_01_AddInStockProduct() {
        loginAsCustomer();

        ProductListPage listPage = PageFactoryManager.getProductListPage(driver,baseUrl);
        ProductDetailPage detailPage = listPage.clickBookAt(0);

        System.out.println("[Assert] Verify product is in-stock");
        int stock = detailPage.getStockQuantity();
        System.out.println("[Info] Stock: " + stock);
        Assert.assertTrue(stock > 0, "Product should be in-stock for this test.");

        System.out.println("[Step 2] Click [detail-add-cart-btn]");
        detailPage.clickAddToCart();

        System.out.println("[Assert] Verify success alert message");
        String msg = detailPage.getCartSuccessMessage();
        System.out.println("[Assert] Alert: " + msg);
        Assert.assertTrue(msg.contains("thêm vào giỏ hàng") || !msg.isEmpty(),
                "Expected cart success message. Got: " + msg);

        System.out.println("[Assert] Verify item appears in /cart");
        CartPage cartPage = PageFactoryManager.getCartPage(driver,baseUrl);
        Assert.assertTrue(cartPage.getCartItemCount() > 0,
                "Expected at least 1 item in cart after adding.");
    }

    @Test(description = "CART-ADD-02: Verify user cannot add an out-of-stock product.")
    public void CART_ADD_02_OutOfStockButtonDisabled() {
        loginAsCustomer();
        // Cần thay ID = ID sản phẩm hết hàng thực tế trong DB
        ProductDetailPage detailPage = PageFactoryManager.getProductDetailPage(driver,baseUrl).openById("OUT_OF_STOCK_ID");

        System.out.println("[Step 2] Observe [detail-add-cart-btn]");
        boolean isDisabled = detailPage.isAddToCartButtonDisabled();
        System.out.println("[Assert] Add-to-cart button disabled: " + isDisabled);

        Assert.assertTrue(isDisabled,
                "Expected [detail-add-cart-btn] to be disabled for out-of-stock product.");
    }

    @Test(description = "CART-ADD-03: Verify adding quantity exceeding stock (Boundary).")
    public void CART_ADD_03_ExceedStockQuantityBoundary() {
        loginAsCustomer();
        ProductListPage listPage = PageFactoryManager.getProductListPage(driver,baseUrl);
        ProductDetailPage detailPage = listPage.clickBookAt(0);

        int stock = detailPage.getStockQuantity();
        int attemptQty = stock + 5; // > stock
        detailPage.forceSetQuantity(attemptQty);
        detailPage.clickAddToCart();
        int actualQty  = detailPage.getCurrentQuantityValue();
        int maxAllowed = detailPage.getQuantityMaxValue();
        Assert.assertTrue(actualQty <= stock || maxAllowed == stock,
                "Expected quantity capped at stock=" + stock + ". Actual qty=" + actualQty);
    }
}
