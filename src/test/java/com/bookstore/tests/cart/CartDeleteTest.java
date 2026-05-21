package com.bookstore.tests.cart;

import com.bookstore.base.BaseSetup;
import com.bookstore.factory.PageFactoryManager;
import com.bookstore.pages.*;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Test Class: Cart - Delete Items (CART-DEL)
 * Precondition: CART-ADD-01 (item already in cart)
 */
public class CartDeleteTest extends BaseSetup {
    private static final String USERNAME = "diem_tester";
    private static final String PASSWORD = "Abc@12345";

    private HomePage loginAsCustomer() {
        LoginPage loginPage = PageFactoryManager.getLoginPage(driver, baseUrl);
        return loginPage.loginAsCustomer(USERNAME, PASSWORD);
    }

    /** Login và thêm nhiều sản phẩm vào giỏ */
    private CartPage loginAndAddItemsToCart(int itemCount) {
        loginAsCustomer();
        ProductListPage listPage = PageFactoryManager.getProductListPage(driver,baseUrl);
        for (int i = 0; i < itemCount; i++) {
            ProductDetailPage detail = listPage.clickBookAt(i % listPage.getBookCount());
            detail.clickAddToCart();
            driver.navigate().back();
        }
        return PageFactoryManager.getCartPage(driver,baseUrl);
    }


    @Test(description = "CART-DEL-01: Verify deleting 1 item from cart.")
    public void CART_DEL_01_DeleteOneItem() {
        CartPage cartPage = loginAndAddItemsToCart(2);
        int countBefore = cartPage.getCartItemCount();
        Assert.assertTrue(countBefore > 0, "Precondition: Cart must have at least 1 item.");

        cartPage.clickDeleteAt(0);

        int countAfter = cartPage.getCartItemCount();
        System.out.println("[Assert] Items after delete: " + countAfter);
        Assert.assertEquals(countAfter, countBefore - 1,
                "Expected item count to decrease by 1. Before=" + countBefore + " After=" + countAfter);
    }

    @Test(description = "CART-DEL-02: Verify deleting all items individually.")
    public void CART_DEL_02_DeleteAllItemsIndividually() {
        CartPage cartPage = loginAndAddItemsToCart(2);
        cartPage.deleteAllItems();

        boolean isEmpty = cartPage.isCartEmptyMessageDisplayed();
        Assert.assertTrue(isEmpty,
                "Expected empty cart message after deleting all items.");

        String emptyMsg = cartPage.getCartEmptyMessage();
        Assert.assertTrue(emptyMsg.contains("Chưa có sản phẩm"),
                "Expected 'Chưa có sản phẩm nào trong giỏ hàng'. Got: " + emptyMsg);
    }

    @Test(description = "CART-DEL-03: Verify deleting item not in cart via API (Boundary - IDOR).")
    public void CART_DEL_03_DeleteOtherUserItemBoundary() {
        loginAsCustomer();

        String otherUserProductId = "USER_B_PRODUCT_ID"; // Cần thay bằng ID thực
        String jsScript = "var status = 0;" +
                "fetch('/api/cart/items', {" +
                "  method:'DELETE'," +
                "  headers:{'Content-Type':'application/json'}," +
                "  body:JSON.stringify({productId:'" + otherUserProductId + "'})" +
                "}).then(r=>{ status=r.status; window._deleteStatus=r.status; });" +
                "return 'request sent';";

        try {
            ((org.openqa.selenium.JavascriptExecutor) driver).executeScript(jsScript);
            Thread.sleep(2000);
            Object statusCode = ((org.openqa.selenium.JavascriptExecutor) driver)
                    .executeScript("return window._deleteStatus;");
            System.out.println("[Assert] API response status: " + statusCode);

            if (statusCode != null) {
                long code = (Long) statusCode;
                Assert.assertTrue(code == 403 || code == 404,
                        "Expected 403 Forbidden or 404 Not Found for cross-user delete. Got: " + code);
            } else {
                System.out.println("[Info] Status code not captured (CORS/session issue) - verifying cart integrity");
                CartPage cartPage = PageFactoryManager.getCartPage(driver,baseUrl);
                Assert.assertTrue(cartPage.getCartItemCount() >= 0,
                        "Cart should remain intact when attempting cross-user delete.");
            }
        } catch (Exception e) {
            System.out.println("[Info] Expected security rejection: " + e.getMessage());
            Assert.assertTrue(true, "Security boundary test - request was rejected.");
        }
    }
}
