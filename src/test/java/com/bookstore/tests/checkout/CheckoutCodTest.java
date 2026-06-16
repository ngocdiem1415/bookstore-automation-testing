package com.bookstore.tests.checkout;

import com.bookstore.factory.PageFactoryManager;
import com.bookstore.helpers.CleanupRegistry;
import com.bookstore.pages.CartPage;
import com.bookstore.pages.CheckoutPage;
import com.bookstore.pages.InvoicePage;
import com.bookstore.utils.JsonDataProvider;
import com.bookstore.utils.LoggerHelper;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Map;

public class CheckoutCodTest extends CheckoutBaseTest {

    @Test(
            priority = 1,
            dataProvider = "GlobalJsonFeeder",
            dataProviderClass = JsonDataProvider.class,
            description = "CHECKOUT-COD-01: Đặt hàng bằng hình thức thanh toán COD tiêu chuẩn."
    )
    public void CHECKOUT_COD_01_PlaceOrderCod(Map<String, String> data) throws InterruptedException {
        LoggerHelper.info("[CHECKOUT][COD] Bắt đầu kiểm thử đặt hàng bằng COD");

        CheckoutPage cp = setupCheckoutReady(data);

        LoggerHelper.info("[CHECKOUT][COD] Chọn phương thức thanh toán: " + data.get("paymentMethod"));
        cp.selectPaymentMethod(data.get("paymentMethod"));

        LoggerHelper.info("[CHECKOUT][COD] Click nút đặt hàng");
        InvoicePage invoice = cp.clickBuyExpectingSuccess();

        LoggerHelper.info("[CHECKOUT][COD] Kiểm tra đã chuyển tới trang invoice");
        Assert.assertTrue(invoice.isOnInvoicePage(),
                "Kỳ vọng chuyển đến trang hóa đơn /invoice. Thực tế: " + invoice.getCurrentUrl());

        LoggerHelper.info("[CHECKOUT][COD] Kiểm tra trạng thái đơn hàng là PENDING");
        String status = invoice.getOrderStatusAt(0);
        Assert.assertTrue(isPendingStatus(status),
                "Đơn hàng COD mới đặt phải có trạng thái PENDING. Thực tế: " + status);

        String orderId = invoice.getFirstOrderId();
        if (orderId != null && !orderId.isBlank()) {
            CleanupRegistry.adminCompleteOrderIds.add(orderId);
            LoggerHelper.info("[CHECKOUT][COD] Đăng ký order cho admin complete: " + orderId);
        }

        LoggerHelper.info("[CHECKOUT][COD] Đặt hàng COD thành công");
    }

    @Test(
            priority = 2,
            description = "CHECKOUT-COD-03: Kiểm tra truy cập checkout khi giỏ hàng trống"
    )
    public void CHECKOUT_COD_02_EmptyCartBoundary() {
        LoggerHelper.info("[CHECKOUT][COD] Bắt đầu kiểm thử truy cập checkout khi giỏ hàng trống");
        LoggerHelper.info("[CHECKOUT][COD] Đăng nhập tài khoản CUSTOMER");
        loginAsCustomer();

        CartPage cart = PageFactoryManager.getCartPage(getDriver(), baseUrl);
        LoggerHelper.info("[CHECKOUT][COD] Mở trang giỏ hàng");
        cart.open();
        LoggerHelper.info("[CHECKOUT][COD] Dọn sạch giỏ hàng nếu còn sản phẩm");
        if (cart.getCartItemCount() > 0) {
            cart.deleteAllItems();
        }

        LoggerHelper.info("[CHECKOUT][COD] Truy cập trực tiếp URL /checkout");
        getDriver().get(baseUrl + "/checkout");

        String url = getDriver().getCurrentUrl();
        boolean redirectedSafely = url.contains("/cart");

        LoggerHelper.info("[CHECKOUT][COD] URL sau khi truy cập checkout: " + url);
        Assert.assertTrue(redirectedSafely,
                "Kỳ vọng tự động chuyển hướng khỏi trang checkout khi giỏ hàng trống. Thực tế: " + url);

        String title = getDriver().getTitle().toLowerCase();
        LoggerHelper.info("[CHECKOUT][COD] Kiểm tra hệ thống redirect an toàn khỏi checkout");
        Assert.assertFalse(title.contains("500") || title.contains("error"),
                "Hệ thống không được sập lỗi 500 khi truy cập checkout với giỏ trống.");
        LoggerHelper.info("[CHECKOUT][COD] Kiểm tra không phát sinh lỗi 500");
    }

    private boolean isPendingStatus(String status) {
        String normalized = status == null ? "" : status.toLowerCase();
        return normalized.contains("pending") || normalized.contains("chờ");
    }
}
