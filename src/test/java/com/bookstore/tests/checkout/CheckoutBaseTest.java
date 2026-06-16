package com.bookstore.tests.checkout;

import com.bookstore.base.BaseSetup;
import com.bookstore.factory.PageFactoryManager;
import com.bookstore.helpers.CleanupHelper;
import com.bookstore.pages.*;
import com.bookstore.utils.DataHelper;
import com.bookstore.utils.LoggerHelper;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;

import java.util.Map;

public abstract class CheckoutBaseTest extends BaseSetup {
    @AfterMethod(alwaysRun = true)
    public void cleanupCheckoutCartData() {
        CleanupHelper.cleanupCustomerCart(getDriver(), baseUrl);
    }

    protected void loginAsCustomer() {
        LoggerHelper.info("[CHECKOUT][BASE] Mở trang đăng nhập");
        LoginPage loginPage = PageFactoryManager.getLoginPage(getDriver(), baseUrl);
        loginPage.open();

        LoggerHelper.info("[CHECKOUT][BASE] Đăng nhập bằng tài khoản CUSTOMER");
        loginPage.loginAsCustomer(
                DataHelper.getValue("existing.username"),
                DataHelper.getValue("existing.password")
        );

        try {
            Thread.sleep(500);
            LoggerHelper.info("[CHECKOUT][BASE] Đăng nhập xong, chờ 500ms để ổn định cookie");
        } catch (InterruptedException e) {
            LoggerHelper.warn("[CHECKOUT][BASE] Luồng chờ sau đăng nhập bị gián đoạn");
            Thread.currentThread().interrupt();
        }
    }

    protected CheckoutPage loginAddItemAndOpenCheckout() {
        LoggerHelper.info("[CHECKOUT][BASE] Chuẩn bị dữ liệu checkout: đăng nhập, thêm sản phẩm, mở checkout");
        loginAsCustomer();

        // Làm sạch giỏ hàng để tránh dữ liệu rác từ các test trước
        CartPage cartPage = PageFactoryManager.getCartPage(getDriver(), baseUrl);
        LoggerHelper.info("[CHECKOUT][BASE] Mở trang giỏ hàng để kiểm tra dữ liệu cũ");
        cartPage.open();
        int countItemn = cartPage.getCartItemCount();
        if (countItemn > 0) {
            LoggerHelper.info("[CHECKOUT][BASE] Giỏ hàng còn " + cartPage.getCartItemCount() + " item, tiến hành dọn sạch");
            cartPage.deleteAllItems();
            LoggerHelper.info("[CHECKOUT][BASE] Giỏ hàng đã được làm sạch");
        }

        // Thêm 1 sản phẩm mới vào giỏ hàng
        ProductListPage listPage = PageFactoryManager.getProductListPage(getDriver(), baseUrl);
        LoggerHelper.info("[CHECKOUT][BASE] Mở trang danh sách sản phẩm");
        listPage.open();
        Assert.assertTrue(listPage.getBookCount() > 0,
                "[ERROR] Precondition: Không tìm thấy sản phẩm nào trên trang danh sách!");

        LoggerHelper.info("[CHECKOUT][BASE] Click sản phẩm đầu tiên");
        ProductDetailPage detailPage = listPage.clickBookAt(0);
        int stock = detailPage.getStockQuantity();
        LoggerHelper.info("[CHECKOUT][BASE] Stock sản phẩm: " + stock);
        Assert.assertTrue(stock > 0,
                "[ERROR] Precondition: Sản phẩm được chọn đang hết hàng (Stock = 0)!");

        LoggerHelper.info("[CHECKOUT][BASE] Thêm sản phẩm vào giỏ hàng");
        detailPage.clickAddToCart();
        detailPage.getSuccessMessage();

        LoggerHelper.info("[CHECKOUT][BASE] Mở trang giỏ hàng");
        cartPage.open();
        Assert.assertTrue(cartPage.getCartItemCount() > 0,
                "[ERROR] Precondition: Giỏ hàng vẫn trống sau khi thêm sản phẩm!");

        LoggerHelper.info("[CHECKOUT][BASE] Chọn checkbox sản phẩm trong giỏ hàng");
        cartPage.checkCheckboxAt(0);
        LoggerHelper.info("[CHECKOUT][BASE] Click nút mua hàng để mở trang checkout");
        return cartPage.clickBuyButton();
    }

    protected CheckoutPage setupCheckoutReady(Map<String, String> data) {
        LoggerHelper.info("[CHECKOUT][BASE] Điền thông tin giao hàng hợp lệ");
        CheckoutPage cp = loginAddItemAndOpenCheckout();
        cp.fillShippingInfo(
                data.get("name"),
                data.get("phone"),
                data.get("address"),
                data.get("city"),
                data.get("district"),
                data.get("ward")
        );
        LoggerHelper.info("[CHECKOUT][BASE] Lưu thông tin giao hàng");
        cp.clickSaveAndGetAlert();
        return cp;
    }

}
