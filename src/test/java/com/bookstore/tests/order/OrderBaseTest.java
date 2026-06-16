package com.bookstore.tests.order;

import com.bookstore.base.BaseSetup;
import com.bookstore.factory.PageFactoryManager;
import com.bookstore.helpers.CleanupHelper;
import com.bookstore.pages.*;
import com.bookstore.utils.DataHelper;
import com.bookstore.utils.LoggerHelper;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;

public abstract class OrderBaseTest extends BaseSetup {
    @AfterMethod(alwaysRun = true)
    public void cleanupOrderCartData() {
        CleanupHelper.cancelCustomerOrders(getDriver(), baseUrl);
        CleanupHelper.cleanupCustomerCart(getDriver(), baseUrl);
    }

    protected void loginAsCustomer() {
        LoggerHelper.info("[ORDER][BASE] Mở trang đăng nhập");
        LoginPage loginPage = PageFactoryManager.getLoginPage(getDriver(), baseUrl);
        loginPage.open();

        LoggerHelper.info("[ORDER][BASE] Đăng nhập bằng tài khoản CUSTOMER");
        loginPage.loginAsCustomer(
                DataHelper.getValue("existing.username"),
                DataHelper.getValue("existing.password")
        );

        try {
            Thread.sleep(500);
            LoggerHelper.info("[ORDER][BASE] Đăng nhập xong, chờ 500ms để ổn định cookie");
        } catch (InterruptedException e) {
            LoggerHelper.warn("[ORDER][BASE] Luồng chờ sau đăng nhập bị gián đoạn");
            Thread.currentThread().interrupt();
        }
    }

    protected InvoicePage loginAsCustomerAndOpenInvoicePage() {
        LoggerHelper.info("[ORDER][BASE] Chuẩn bị mở trang lịch sử đơn hàng bằng tài khoản CUSTOMER");
        loginAsCustomer();

        LoggerHelper.info("[ORDER][BASE] Mở trang lịch sử đơn hàng");
        InvoicePage ordPage = PageFactoryManager.getInvoicePage(getDriver(), baseUrl);

        return ordPage.open();
    }

    protected InvoicePage loginAsCustomerNotOrderAndOpenInvoicePage(String user, String pass) {
        LoggerHelper.info("[ORDER][BASE] Mở trang đăng nhập");
        LoginPage loginPage = PageFactoryManager.getLoginPage(getDriver(), baseUrl);
        loginPage.open();

        LoggerHelper.info("[ORDER][BASE] Đăng nhập bằng tài khoản kiểm thử không có đơn hàng: " + user);
        loginPage.loginAsCustomer(user, pass);

        try {
            Thread.sleep(500);
            LoggerHelper.info("[ORDER][BASE] Đăng nhập xong, chờ 500ms để ổn định cookie");
        } catch (InterruptedException e) {
            LoggerHelper.warn("[ORDER][BASE] Luồng chờ sau đăng nhập bị gián đoạn");
            Thread.currentThread().interrupt();
        }

        LoggerHelper.info("[ORDER][BASE] Mở trang lịch sử đơn hàng");
        InvoicePage ordPage = PageFactoryManager.getInvoicePage(getDriver(), baseUrl);

        return ordPage.open();
    }

    protected CartPage loginAndCleanCart() {
        loginAsCustomer();
        CartPage cartPage = PageFactoryManager.getCartPage(getDriver(), baseUrl);
        LoggerHelper.info("[ORDER][BASE] Mở trang giỏ hàng");
        cartPage.open();
        int countItemn = cartPage.getCartItemCount();
        if (countItemn > 0) {
            LoggerHelper.info("[ORDER][BASE] Giỏ hàng còn " + cartPage.getCartItemCount() + " item từ test trước → dọn sạch...");
            cartPage.deleteAllItems();
            LoggerHelper.info("[ORDER][BASE] Giỏ hàng đã được làm sạch.");
        }

        ProductListPage listPage = PageFactoryManager.getProductListPage(getDriver(), baseUrl);
        LoggerHelper.info("[ORDER][BASE] Mở trang danh sách sản phẩm");
        listPage.open();

        int quantity = listPage.getBookCount();
        LoggerHelper.info("[ORDER][BASE] Số lượng sách trong trang là: " + quantity);
        Assert.assertTrue(quantity > 0,
                "[ERROR] Precondition: Không tìm thấy sản phẩm nào trên trang danh sách!");

        LoggerHelper.info("[ORDER][BASE] Mở trang giỏ hàng");
        cartPage.open();
        return cartPage.open();
    }

}
