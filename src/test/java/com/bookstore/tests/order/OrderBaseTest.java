package com.bookstore.tests.cart;

import com.bookstore.base.BaseSetup;
import com.bookstore.factory.PageFactoryManager;
import com.bookstore.pages.CartPage;
import com.bookstore.pages.LoginPage;
import com.bookstore.pages.ProductDetailPage;
import com.bookstore.pages.ProductListPage;
import com.bookstore.utils.DataHelper;
import org.testng.Assert;

public abstract class CartBaseTest extends BaseSetup {
    protected void loginAsCustomer() {
        LoginPage loginPage = PageFactoryManager.getLoginPage(getDriver(), baseUrl);
        loginPage.open();
        loginPage.loginAsCustomer(
                DataHelper.getValue("existing.username"),
                DataHelper.getValue("existing.password")
        );
        try {
            Thread.sleep(500);
            System.out.println("[CartBaseTest] Đăng nhập xong, đã nghỉ 500ms để ổn định Cookie.");
        } catch (InterruptedException ignored) {}
    }

    protected CartPage loginAndAddOneItemToCart() {
        loginAsCustomer();
        CartPage cartPage = PageFactoryManager.getCartPage(getDriver(), baseUrl);
        cartPage.open();
        int countItemn = cartPage.getCartItemCount();
        if (countItemn > 0) {
            System.out.println("[Helper] Giỏ hàng còn " + cartPage.getCartItemCount() + " item từ test trước → dọn sạch...");
            cartPage.deleteAllItems();
            System.out.println("[Helper] Giỏ hàng đã được làm sạch.");
        }

        ProductListPage listPage = PageFactoryManager.getProductListPage(getDriver(), baseUrl);
        listPage.open();
        Assert.assertTrue(listPage.getBookCount() > 0,
                "[ERROR] Precondition: Không tìm thấy sản phẩm nào trên trang danh sách!");

        ProductDetailPage detailPage = listPage.clickBookAt(0);
        int stock = detailPage.getStockQuantity();
        Assert.assertTrue(stock > 0,
                "[ERROR] Precondition: Sản phẩm được chọn đang hết hàng (Stock = 0)!");

        detailPage.clickAddToCart();
        cartPage.open();
        Assert.assertTrue(cartPage.getCartItemCount() > 0,
                "[ERROR] Precondition: Giỏ hàng vẫn trống sau khi thêm sản phẩm!");
        return cartPage;
    }

    protected CartPage loginAndAddItemsToCart(int itemCount) {
        loginAsCustomer();

        CartPage cartPage = PageFactoryManager.getCartPage(getDriver(), baseUrl);
        cartPage.open();
        if (cartPage.getCartItemCount() > 0) {
            System.out.println("[Helper] Giỏ hàng còn " + cartPage.getCartItemCount()
                    + " item từ test trước → dọn sạch qua [cart-item-delete]...");
            cartPage.deleteAllItems();
            System.out.println("[Helper] Giỏ hàng đã sạch: " + cartPage.isCartEmptyMessageDisplayed());
        }

        ProductListPage listPage = PageFactoryManager.getProductListPage(getDriver(), baseUrl);
        listPage.open();
        int availableBooks = listPage.getBookCount();
        Assert.assertTrue(availableBooks > 0,
                "[ERROR] Precondition: Không tìm thấy sản phẩm nào trên trang danh sách!");

        for (int i = 0; i < itemCount; i++) {
            ProductDetailPage detail = listPage.clickBookAt(i);
            int stock = detail.getStockQuantity();
            if (stock > 0) {
                detail.clickAddToCart();
                System.out.println("[Helper] Thêm sản phẩm [" + i + "] vào giỏ hàng thành công.");
            } else {
                System.out.println("[Helper] Bỏ qua sản phẩm [" + i + "] vì hết hàng.");
            }
            listPage.open();
        }

        // Bước 4: Mở giỏ hàng và xác nhận có hàng
        cartPage.open();
        Assert.assertTrue(cartPage.getCartItemCount() > 0,
                "[ERROR] Precondition: Giỏ hàng vẫn trống sau khi thêm " + itemCount + " sản phẩm!");
        return cartPage;
    }

}
