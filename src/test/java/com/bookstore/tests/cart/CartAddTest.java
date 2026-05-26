package com.bookstore.tests.cart;

import com.bookstore.base.BaseSetup;
import com.bookstore.factory.PageFactoryManager;
import com.bookstore.pages.*;
import com.bookstore.pages.components.HeaderComponent;
import com.bookstore.utils.DataHelper;
import com.bookstore.utils.JsonDataProvider;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Map;

/**
 * Test Class: Cart - Add to Cart (CART-ADD)
 * Precondition: AUTH-LOG-01 (CUSTOMER logged in)
 */
public class CartAddTest extends BaseSetup {

    private void loginAsCustomer() {
        LoginPage loginPage = PageFactoryManager.getLoginPage(driver, baseUrl);
        loginPage.open();
        loginPage.loginAsCustomer(
                DataHelper.getValue("existing.username"),
                DataHelper.getValue("existing.password")
        );
    }

    @Test(priority = 1, description = "CART-ADD-01: Verify user can add an in-stock product to cart.")
    public void CART_ADD_01_AddInStockProduct() {
        loginAsCustomer();
        ProductListPage listPage = PageFactoryManager.getProductListPage(driver, baseUrl);
        listPage.open();
        Assert.assertTrue(listPage.getBookCount() > 0, "[FAIL] Không tìm thấy quyển sách nào hiển thị trên UI!");
        ProductDetailPage detailPage = listPage.clickBookAt(0);
        int stock = detailPage.getStockQuantity();
        Assert.assertTrue(stock > 0, "[FAIL] Quyển sách được chọn phải còn hàng (Stock > 0).");
        detailPage.clickAddToCart();
        String alertText = detailPage.getSuccessMessage();
        Assert.assertTrue(
                alertText.contains("Sản phẩm đã được thêm vào giỏ hàng!"),
                "[FAIL] Không xuất hiện thông báo thêm vào giỏ hàng thành công! Actual: " + alertText);
    }

    @Test(
            priority = 2,
            dataProvider = "GlobalJsonFeeder",
            dataProviderClass = JsonDataProvider.class,
            description = "CART-ADD-02: Verify user cannot add an out-of-stock product."
    )
    public void CART_ADD_02_OutOfStockButtonDisabled(Map<String, String> data) {
        loginAsCustomer();
        String outOfStockId = data.get("out_of_stock_id");
        ProductDetailPage detailPage = PageFactoryManager.getProductDetailPage(driver, baseUrl);
        detailPage.navigateTo(baseUrl + "/books/" + outOfStockId);
        boolean isDisabled = detailPage.isAddToCartButtonDisabled();
        Assert.assertTrue(isDisabled, "[FAIL] Nút 'Thêm vào giỏ hàng' phải bị khóa cứng khi sản phẩm đã hết hàng!");
    }

    @Test(priority = 3, description = "CART-ADD-03: Verify adding quantity exceeding stock (Boundary).")
    public void CART_ADD_03_ExceedStockQuantityBoundary() {
        loginAsCustomer();
        ProductListPage listPage = PageFactoryManager.getProductListPage(driver, baseUrl);
        listPage.open();
        Assert.assertTrue(listPage.getBookCount() > 0, "[FAIL] Không tìm thấy quyển sách nào hiển thị trên UI!");
        ProductDetailPage detailPage = listPage.clickBookAt(0);
        int stock = detailPage.getStockQuantity();
        int attemptQty = stock + 5;
        int actualQtyInInput = detailPage.getCurrentQuantityValue();
        Assert.assertEquals(actualQtyInInput, stock,
                "[FAIL] Hệ thống không tự động điều chỉnh giá trị ô nhập liệu về ngưỡng Stock tối đa!"
                        + "\n  Số lượng kho (Stock)       : " + stock
                        + "\n  Số lượng cố tình nhập      : " + attemptQty
                        + "\n  Số lượng thực tế trong ô ô Input: " + actualQtyInInput);
    }
}