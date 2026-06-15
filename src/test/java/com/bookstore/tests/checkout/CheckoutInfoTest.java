package com.bookstore.tests.checkout;

import com.bookstore.factory.PageFactoryManager;
import com.bookstore.pages.CheckoutPage;
import com.bookstore.pages.InvoicePage;
import com.bookstore.utils.JsonDataProvider;
import com.bookstore.utils.LoggerHelper;
import org.openqa.selenium.Alert;
import org.openqa.selenium.NoAlertPresentException;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Map;

/**
 * Test Suite: Checkout Shipping Information (CHECKOUT-INFO)
 * Kiểm thử điền thông tin giao hàng:
 * - CHECKOUT-INFO-01: Điền thông tin giao hàng hợp lệ thành công.
 * - CHECKOUT-INFO-02: Để trống trường bắt buộc (Ward) -> hệ thống chặn.
 * - CHECKOUT-INFO-03: Kiểm thử bảo mật XSS trong địa chỉ giao hàng.
 */
public class CheckoutInfoTest extends CheckoutBaseTest {

    @Test(
            priority = 1,
            dataProvider = "GlobalJsonFeeder",
            dataProviderClass = JsonDataProvider.class,
            description = "CHECKOUT-INFO-01: Kiểm thử điền thông tin giao hàng hợp lệ"
    )
    public void CHECKOUT_INFO_01_FillShippingInfoSuccess(Map<String, String> data) {
        LoggerHelper.info("[CHECKOUT][INFO] Bắt đầu kiểm thử điền thông tin giao hàng hợp lệ");

        CheckoutPage checkoutPage = loginAddItemAndOpenCheckout();

        LoggerHelper.info("[CHECKOUT][INFO] Điền họ tên: " + data.get("name"));
        LoggerHelper.info("[CHECKOUT][INFO] Điền số điện thoại: " + data.get("phone"));
        LoggerHelper.info("[CHECKOUT][INFO] Điền địa chỉ giao hàng");
        LoggerHelper.info("[CHECKOUT][INFO] Chọn tỉnh/thành phố: " + data.get("city"));
        LoggerHelper.info("[CHECKOUT][INFO] Chọn quận/huyện: " + data.get("district"));
        LoggerHelper.info("[CHECKOUT][INFO] Chọn phường/xã: " + data.get("ward"));

        checkoutPage.fillShippingInfo(
                data.get("name"),
                data.get("phone"),
                data.get("address"),
                data.get("city"),
                data.get("district"),
                data.get("ward")
        );

        LoggerHelper.info("[CHECKOUT][INFO] Click lưu thông tin giao hàng");
        String alertText = checkoutPage.clickSaveAndGetAlert();

        LoggerHelper.info("[CHECKOUT][INFO] Alert thực tế: " + alertText);

        Assert.assertTrue(alertText.contains("Cập nhật địa chỉ và phí giao hàng thành công!"),
                "[CheckoutPage] Kỳ vọng thông báo lưu thành công. Thực tế: " + alertText);

        LoggerHelper.info("[CHECKOUT][INFO] Kiểm tra phí giao hàng hiển thị");
        Assert.assertTrue(checkoutPage.isShippingFeeDisplayed(),
                "[CheckoutPage] Phí giao hàng phải hiển thị sau khi lưu địa chỉ.");

        LoggerHelper.info("[CHECKOUT][INFO] Kiểm tra nút đặt hàng được mở khóa");
        Assert.assertTrue(checkoutPage.isBuyButtonEnabled(),
                "[CheckoutPage] Nút Đặt hàng phải được enabled sau khi lưu thành công.");
    }

    @Test(
            priority = 2,
            dataProvider = "GlobalJsonFeeder",
            dataProviderClass = JsonDataProvider.class,
            description = "CHECKOUT-INFO-02: Kiểm thử điền thông tin giao hàng không hợp lệ"
    )
    public void CHECKOUT_INFO_02_EmptyWardBlocked(Map<String, String> data) {
        LoggerHelper.info("[CHECKOUT][INFO] Bắt đầu kiểm thử validation khi bỏ trống phường/xã");
        CheckoutPage checkoutPage = loginAddItemAndOpenCheckout();

        LoggerHelper.info("[CHECKOUT][INFO] Điền thông tin giao hàng nhưng không chọn phường/xã");
        checkoutPage.enterName(data.get("name"))
                .enterPhone(data.get("phone"))
                .enterAddress(data.get("address"))
                .selectCity(data.get("city"))
                .selectDistrict(data.get("district"));
        // Để trống Phường/Xã (Ward)

        LoggerHelper.info("[CHECKOUT][INFO] Click lưu thông tin giao hàng");
        String alertText = checkoutPage.clickSaveAndGetAlert();

        LoggerHelper.info("[CHECKOUT][INFO] Alert thực tế: " + alertText);
        Assert.assertTrue(
                alertText.contains("Vui lòng điền đầy đủ thông tin giao hàng."));

        Assert.assertFalse(checkoutPage.isBuyButtonEnabled(),
                "Nút Đặt hàng phải bị khóa (disabled) khi thông tin giao hàng không hợp lệ.");
        LoggerHelper.info("[CHECKOUT][INFO] Kiểm tra nút đặt hàng vẫn bị khóa");
    }

    @Test(
            priority = 3,
            dataProvider = "GlobalJsonFeeder",
            dataProviderClass = JsonDataProvider.class,
            description = "CHECKOUT-INFO-03: Kiểm thử lỗ hổng XSS trong địa chỉ giao hàng (Boundary)."
    )
    public void CHECKOUT_INFO_03_XssAddressBoundary(Map<String, String> data) {
        LoggerHelper.info("[CHECKOUT][INFO] Bắt đầu kiểm thử XSS trong địa chỉ giao hàng");
        CheckoutPage checkoutPage = loginAddItemAndOpenCheckout();

        String xssPayload = data.get("address");
        LoggerHelper.info("[CHECKOUT][INFO] Nhập payload XSS vào địa chỉ: " + xssPayload);
        checkoutPage.fillShippingInfo(
                data.get("name"),
                data.get("phone"),
                xssPayload,
                data.get("city"),
                data.get("district"),
                data.get("ward")
        );

        LoggerHelper.info("[CHECKOUT][INFO] Click lưu thông tin giao hàng");
        String alertText = checkoutPage.clickSaveAndGetAlert();

        LoggerHelper.info("[CHECKOUT][INFO] Alert thực tế: " + alertText);
        Assert.assertTrue(
                alertText.contains( "Địa chỉ không được chứa mã độc hoặc script!"));

        Assert.assertFalse(checkoutPage.isBuyButtonEnabled(),
                "Nút Đặt hàng phải bị khóa (disabled) khi thông tin giao hàng không hợp lệ.");
        LoggerHelper.info("[CHECKOUT][INFO] Kiểm tra hệ thống từ chối địa chỉ chứa script");
    }
}
