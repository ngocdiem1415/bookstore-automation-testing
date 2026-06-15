package com.bookstore.tests.cart;

import com.bookstore.base.BaseSetup;
import com.bookstore.factory.PageFactoryManager;
import com.bookstore.pages.CartPage;
import com.bookstore.pages.LoginPage;
import com.bookstore.pages.ProductDetailPage;
import com.bookstore.pages.ProductListPage;
import com.bookstore.utils.DataHelper;
import com.bookstore.utils.LoggerHelper;
import org.testng.Assert;

public abstract class CartBaseTest extends BaseSetup {
    protected void loginAsCustomer() {
        LoggerHelper.info("[CART][BASE] Đăng nhập bằng tài khoản CUSTOMER");
        LoginPage loginPage = PageFactoryManager.getLoginPage(getDriver(), baseUrl);
        loginPage.open();
        loginPage.loginAsCustomer(
                DataHelper.getValue("existing.username"),
                DataHelper.getValue("existing.password")
        );
        try {
            Thread.sleep(500);
            LoggerHelper.info("[CART][BASE] Đăng nhập xong, chờ 500ms để ổn định cookie");
        } catch (InterruptedException ignored) {
        }
    }

    protected CartPage loginAndAddOneItemToCart() {
        loginAsCustomer();
        CartPage cartPage = PageFactoryManager.getCartPage(getDriver(), baseUrl);
        LoggerHelper.info("[CART][BASE] Mở trang giỏ hàng");
        cartPage.open();
        int countItemn = cartPage.getCartItemCount();
        if (countItemn > 0) {
            LoggerHelper.info("[CART][BASE] Giỏ hàng còn " + cartPage.getCartItemCount() + " item từ test trước, tiến hành dọn sạch");
            cartPage.deleteAllItems();
            LoggerHelper.info("[CART][BASE] Giỏ hàng đã được làm sạch");
        }

        ProductListPage listPage = PageFactoryManager.getProductListPage(getDriver(), baseUrl);
        LoggerHelper.info("[CART][BASE] Mở trang danh sách sản phẩm");
        listPage.open();

        Assert.assertTrue(listPage.getBookCount() > 0,
                "[ERROR] Precondition: Không tìm thấy sản phẩm nào trên trang danh sách!");

        LoggerHelper.info("[CART][BASE] Click sản phẩm tại index: " + 0);
        ProductDetailPage detailPage = listPage.clickBookAt(0);

        int stock = detailPage.getStockQuantity();
        LoggerHelper.info("[CART][BASE] Stock sản phẩm: " + stock);
        Assert.assertTrue(stock > 0,
                "[ERROR] Precondition: Sản phẩm được chọn đang hết hàng (Stock = 0)!");

        LoggerHelper.info("[CART][BASE] Thêm sản phẩm [" + 0 + "] vào giỏ hàng thành công");
        detailPage.clickAddToCart();
        LoggerHelper.info("[CART][BASE] Chuyển hướng đến trang giỏ hàng /cart");
        cartPage.open();

        Assert.assertTrue(cartPage.getCartItemCount() > 0,
                "[ERROR] Precondition: Giỏ hàng vẫn trống sau khi thêm sản phẩm!");
        return cartPage;
    }

    protected CartPage loginAndAddItemsToCart(int itemCount) {
        loginAsCustomer();

        CartPage cartPage = PageFactoryManager.getCartPage(getDriver(), baseUrl);
        LoggerHelper.info("[CART][BASE] Mở trang giỏ hàng");
        cartPage.open();
        if (cartPage.getCartItemCount() > 0) {
            LoggerHelper.info("[CART][BASE] Giỏ hàng còn " + cartPage.getCartItemCount() + " item từ test trước, tiến hành dọn sạch");
            cartPage.deleteAllItems();
            LoggerHelper.info("[CART][BASE] Giỏ hàng đã được làm sạch");
        }

        ProductListPage listPage = PageFactoryManager.getProductListPage(getDriver(), baseUrl);

        LoggerHelper.info("[CART][BASE] Mở trang danh sách sản phẩm");
        listPage.open();
        int availableBooks = listPage.getBookCount();
        Assert.assertTrue(availableBooks > 0,
                "[ERROR] Precondition: Không tìm thấy sản phẩm nào trên trang danh sách!");

        for (int i = 0; i < itemCount; i++) {
            ProductDetailPage detail = listPage.clickBookAt(i);
            int stock = detail.getStockQuantity();
            if (stock > 0) {
                detail.clickAddToCart();
                LoggerHelper.info("[CART][BASE] Thêm sản phẩm [" + i + "] vào giỏ hàng thành công");
            } else {
                LoggerHelper.warn("[CART][BASE] Bỏ qua sản phẩm [" + i + "] vì hết hàng");
            }
            listPage.open();
        }

        cartPage.open();
        Assert.assertTrue(cartPage.getCartItemCount() > 0,
                "[ERROR] Precondition: Giỏ hàng vẫn trống sau khi thêm " + itemCount + " sản phẩm!");
        return cartPage;
    }

}
