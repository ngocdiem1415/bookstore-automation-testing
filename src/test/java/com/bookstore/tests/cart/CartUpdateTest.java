package com.bookstore.tests.cart;

import com.bookstore.base.BaseSetup;
import com.bookstore.factory.PageFactoryManager;
import com.bookstore.pages.CartPage;
import com.bookstore.pages.LoginPage;
import com.bookstore.pages.ProductDetailPage;
import com.bookstore.pages.ProductListPage;
import com.bookstore.utils.DataHelper;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Test Class: Cart - Update Quantity (CART-UPD)
 * Precondition: CART-ADD-01 (item already in cart)
 */
public class CartUpdateTest extends BaseSetup {

    private CartPage loginAndAddOneItemToCart() {
        LoginPage loginPage = PageFactoryManager.getLoginPage(driver, baseUrl);
        loginPage.open();
        loginPage.loginAsCustomer(
                DataHelper.getValue("existing.username"),
                DataHelper.getValue("existing.password")
        );
        CartPage cartPage = PageFactoryManager.getCartPage(driver, baseUrl);
        cartPage.open();

        ProductListPage listPage = PageFactoryManager.getProductListPage(driver, baseUrl);
        listPage.open();

        // Kiểm tra bảo hiểm bảo vệ luồng test không bị sập nếu API lọc sách bị trống
        Assert.assertTrue(listPage.getBookCount() > 0,
                "[ERROR] Không tìm thấy bất kỳ sản phẩm nào hiển thị trên UI danh sách!");
        ProductDetailPage detailPage = listPage.clickBookAt(0);

        int stock = detailPage.getStockQuantity();
        Assert.assertTrue(stock > 0,
                "[ERROR] Sản phẩm mẫu được chọn ngẫu nhiên đang ở trạng thái hết hàng (Stock = 0)!");

        detailPage.clickAddToCart();
        return PageFactoryManager.getCartPage(driver, baseUrl);
    }

    @Test(description = "CART-UPD-01: Verify changing item quantity in Cart Page.")
    public void CART_UPD_01_IncreaseQuantity() {
        CartPage cartPage = loginAndAddOneItemToCart();
        cartPage.open();
        int qtyBefore = cartPage.getQuantityAt(0);
        System.out.println(qtyBefore);
        long totalBefore = cartPage.getTotalPriceAsLong();
        System.out.println(totalBefore);
        cartPage.clickIncreaseAt(0);
        int qtyAfter   = cartPage.getQuantityAt(0);
        long totalAfter = cartPage.getTotalPriceAsLong();
        Assert.assertEquals(qtyAfter, qtyBefore + 1,
                "Expected quantity to increase by 1. Before=" + qtyBefore + " After=" + qtyAfter);
        Assert.assertTrue(totalAfter >= totalBefore,
                "Expected total price to increase after adding quantity.");
    }

    @Test(description = "CART-UPD-02: Verify cannot input negative quantity manually.")
    public void CART_UPD_02_CannotTypeNegativeQuantity() {
        CartPage cartPage = loginAndAddOneItemToCart();

        System.out.println("[Step 1] Navigate to Cart page");
        System.out.println("[Step 2] Try to type '-2' into [cart-item-quantity]");
        cartPage.tryTypeIntoQuantityInput(0, "-2");

        System.out.println("[Assert] Verify [cart-item-quantity] has disabled attribute");
        boolean isDisabled = cartPage.isQuantityInputDisabled(0);
        System.out.println("[Assert] isDisabled=" + isDisabled);
        Assert.assertTrue(isDisabled,
                "Expected [cart-item-quantity] to be disabled (read-only). Manual input should be blocked.");
    }

    @Test(description = "CART-UPD-03: Verify updating quantity via concurrent API calls (Boundary - Race Condition).")
    public void CART_UPD_03_ConcurrentUpdateRaceCondition() throws InterruptedException {
        PageFactoryManager.getLoginPage(driver,baseUrl).loginAsCustomer(
                DataHelper.getValue("existing.username"),
                DataHelper.getValue("existing.password")
        );

        // Lấy product ID và stock từ detail page
        ProductDetailPage detailPage = PageFactoryManager.getProductListPage(driver,baseUrl).clickBookAt(0);
        int stock = detailPage.getStockQuantity();
        String productUrl = detailPage.getCurrentUrl();
        System.out.println("[Info] Product URL: " + productUrl + " | Stock: " + stock);
        detailPage.clickAddToCart();

        // Mô phỏng race condition bằng cách gọi JS fetch 10 lần đồng thời
        System.out.println("[Step 1] Send 10 concurrent PATCH requests via JS");
        String productId = productUrl.replaceAll(".*/books/", "");
        String jsScript = "var results = [];" +
                "for(var i=0;i<10;i++){" +
                "  fetch('/api/cart/items', {" +
                "    method:'PATCH'," +
                "    headers:{'Content-Type':'application/json'}," +
                "    body:JSON.stringify({productId:" + productId + ", quantity:1})" +
                "  }).then(r=>results.push(r.status));" +
                "}" +
                "return 'Requests sent';";
        try {
            Object result = ((org.openqa.selenium.JavascriptExecutor) driver).executeScript(jsScript);
            System.out.println("[Info] JS result: " + result);
        } catch (Exception e) {
            System.out.println("[Info] JS concurrent call: " + e.getMessage());
        }
        Thread.sleep(3000); // Chờ các request xử lý

        CartPage cartPage =PageFactoryManager.getCartPage(driver,baseUrl);
        int finalQty = cartPage.getQuantityAt(0);
        System.out.println("[Assert] Final qty in cart=" + finalQty + " | stock=" + stock);
        Assert.assertTrue(finalQty <= stock,
                "Race condition: cart quantity " + finalQty + " exceeds stock " + stock);
    }
}
