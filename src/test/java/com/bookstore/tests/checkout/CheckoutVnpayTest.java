package com.bookstore.tests.checkout;

import com.bookstore.pages.CheckoutPage;
import com.bookstore.utils.JsonDataProvider;
import com.bookstore.utils.LoggerHelper;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.time.Duration;
import java.util.Map;

public class CheckoutVnpayTest extends CheckoutBaseTest {

    @Test(
            priority = 1,
            dataProvider = "GlobalJsonFeeder",
            dataProviderClass = JsonDataProvider.class,
            description = "CHK-VNP-01: Kiểm thử đặt hàng bằng hình thức thanh toán VNPay " +
                    "và chuyển hướng sang cổng thanh toán VNPay."
    )
    public void CHK_VNP_01_VnpayPaymentSuccess(Map<String, String> data) throws InterruptedException {
        LoggerHelper.info("[CHECKOUT][VNPAY] Bắt đầu kiểm thử thanh toán VNPay thành công");

        CheckoutPage cp = setupCheckoutReady(data);

        LoggerHelper.info("[CHECKOUT][VNPAY] Chọn phương thức thanh toán: " + data.get("paymentMethod"));
        cp.selectPaymentMethod(data.get("paymentMethod"));

        LoggerHelper.info("[CHECKOUT][VNPAY] Click nút đặt hàng");
        cp.clickBuyExpectingSuccess();

        LoggerHelper.info("[CHECKOUT][VNPAY] Chờ chuyển hướng sang cổng VNPay");
        WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(20));
        wait.until(driver -> driver.getCurrentUrl().contains("vnpayment.vn"));

        LoggerHelper.info("[CHECKOUT][VNPAY] URL hiện tại: " + getDriver().getCurrentUrl());
        Assert.assertTrue(getDriver().getCurrentUrl().contains("vnpayment.vn"),
                "Không chuyển hướng tới VNPay");

        LoggerHelper.info("[CHECKOUT][VNPAY] Chuyển hướng VNPay thành công");
    }

    @Test(
            priority = 2,
            dataProvider = "GlobalJsonFeeder",
            dataProviderClass = JsonDataProvider.class,
            description = "CHK-VNP-02: Kiểm thử người dùng quay lại trang trước sau khi chọn phương thức thanh toán VNPay."
    )
    public void CHK_VNP_02_VnpayRedirect(Map<String, String> data){
        LoggerHelper.info("[CHECKOUT][VNPAY] Bắt đầu kiểm thử redirect tới cổng thanh toán VNPay");

        CheckoutPage cp = setupCheckoutReady(data);

        LoggerHelper.info("[CHECKOUT][VNPAY] Chọn phương thức thanh toán: " + data.get("paymentMethod"));
        cp.selectPaymentMethod(data.get("paymentMethod"));

        LoggerHelper.info("[CHECKOUT][VNPAY] Quay lại trang trước");
        getDriver().navigate().back();

        LoggerHelper.info("[CHECKOUT][VNPAY] URL sau khi back: " + getDriver().getCurrentUrl());
        Assert.assertFalse(getDriver().getCurrentUrl().contains("vnpayment.vn"));

        LoggerHelper.info("[CHECKOUT][VNPAY] Kiểm tra redirect/back VNPay hoàn tất");
    }

}
