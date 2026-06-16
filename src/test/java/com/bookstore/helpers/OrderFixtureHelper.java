package com.bookstore.helpers;

import com.bookstore.factory.PageFactoryManager;
import com.bookstore.pages.*;
import com.bookstore.utils.DataHelper;
import com.bookstore.utils.LoggerHelper;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;

import java.util.HashMap;
import java.util.Map;

public class OrderFixtureHelper {
    private static final Map<String, String> DEFAULT_COD_ORDER_DATA = new HashMap<>();

    static {
        DEFAULT_COD_ORDER_DATA.put("name", "Nguyễn Thị Diễm");
        DEFAULT_COD_ORDER_DATA.put("phone", "0987654321");
        DEFAULT_COD_ORDER_DATA.put("address", "123 Đường Lê Lợi");
        DEFAULT_COD_ORDER_DATA.put("city", "Hồ Chí Minh");
        DEFAULT_COD_ORDER_DATA.put("district", "Quận 1");
        DEFAULT_COD_ORDER_DATA.put("ward", "Phường Bến Nghé");
        DEFAULT_COD_ORDER_DATA.put("paymentMethod", "Thanh toán khi nhận hàng (COD)");
    }

    private OrderFixtureHelper() {
    }

    public static String createCodOrder(WebDriver driver, String baseUrl) throws InterruptedException {
        return createCodOrder(driver, baseUrl, DEFAULT_COD_ORDER_DATA);
    }

    public static String createCodOrder(WebDriver driver, String baseUrl, Map<String, String> data)
            throws InterruptedException {
        LoggerHelper.info("[FIXTURE][ORDER] Bắt đầu tạo đơn hàng COD phục vụ test");

        LoginPage loginPage = PageFactoryManager.getLoginPage(driver, baseUrl);
        loginPage.open();
        loginPage.loginAsCustomer(
                DataHelper.getValue("existing.username"),
                DataHelper.getValue("existing.password")
        );
        Thread.sleep(500);

        CartPage cartPage = PageFactoryManager.getCartPage(driver, baseUrl);
        cartPage.open();
        if (cartPage.getCartItemCount() > 0) {
            cartPage.deleteAllItems();
        }

        ProductListPage listPage = PageFactoryManager.getProductListPage(driver, baseUrl);
        listPage.open();
        Assert.assertTrue(listPage.getBookCount() > 0,
                "[ERROR][FIXTURE]: Không tìm thấy sản phẩm nào để tạo đơn hàng.");

        ProductDetailPage detailPage = listPage.clickBookAt(0);
        Assert.assertTrue(detailPage.getStockQuantity() > 0,
                "[ERROR][FIXTURE]: Sản phẩm đầu tiên đã hết hàng..");
        detailPage.clickAddToCart();
        detailPage.getSuccessMessage();

        cartPage.open();
        Assert.assertTrue(cartPage.getCartItemCount() > 0,
                "[ERROR][FIXTURE]: Giỏ hàng trống sau khi thêm sản phẩm..");
        cartPage.checkCheckboxAt(0);

        CheckoutPage checkoutPage = cartPage.clickBuyButton();
        checkoutPage.fillShippingInfo(
                data.get("name"),
                data.get("phone"),
                data.get("address"),
                data.get("city"),
                data.get("district"),
                data.get("ward")
        );
        checkoutPage.clickSaveAndGetAlert();
        checkoutPage.selectPaymentMethod(data.get("paymentMethod"));

        InvoiceDetailPage invoicePage = checkoutPage.clickBuyExpectingSuccess();
        Assert.assertTrue(invoicePage.isOnInvoicePage(),
                "[ERROR][FIXTURE]: Trang hóa đơn dự kiến sau khi đặt hàng thanh toán khi nhận hàng (COD).");

        String orderId = invoicePage.getOrderId();
        Assert.assertTrue(orderId != null && !orderId.isBlank(),
                "[ERROR][FIXTURE]: Không thể thu thập ID đơn hàng đã tạo.");
        LoggerHelper.info("[FIXTURE][ORDER] Đã tạo đơn hàng COD, mã đơn: " + orderId);
        return orderId;
    }
}
