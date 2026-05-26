package com.bookstore.pages;

import com.bookstore.base.BaseSetup;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

/**
 * Page Object: Trang Hóa đơn / Xác nhận đơn hàng (/invoice).
 * Covers: CHECKOUT-COD-01, CHK-VNP-01
 */
public class InvoicePage extends BasePage {

    private static final String PAGE_URL = "/invoice";

    // ===========================
    // LOCATORS (data-testid)
    // ===========================

    @FindBy(css = "[data-testid='invoice-order-id']")
    private WebElement lblOrderId;

    @FindBy(css = "[data-testid='invoice-order-status']")
    private WebElement lblOrderStatus;

    @FindBy(css = "[data-testid='invoice-payment-method']")
    private WebElement lblPaymentMethod;

    @FindBy(css = "[data-testid='invoice-total-price']")
    private WebElement lblTotalPrice;

    @FindBy(css = "[data-testid='invoice-container']")
    private WebElement containerInvoice;

    // ===========================
    // CONSTRUCTOR
    // ===========================

    public InvoicePage(WebDriver driver,String baseUrl) {
        super(driver,baseUrl);
    }

    // ===========================
    // NAVIGATION
    // ===========================

    public InvoicePage open() {
        driver.get(getCurrentUrl()+ PAGE_URL);
        return this;
    }

    // ===========================
    // GETTER METHODS (Assertions)
    // ===========================

    /** Kiểm tra đang ở trang /invoice */
    public boolean isOnInvoicePage() {
        return waitForUrlContains("/invoice");
    }

    /** Lấy Order ID */
    public String getOrderId() {
        try {
            return wait.until(ExpectedConditions.visibilityOf(lblOrderId)).getText().trim();
        } catch (Exception e) { return ""; }
    }

    /**
     * Lấy trạng thái đơn hàng.
     * CHECKOUT-COD-01: kỳ vọng "PENDING"
     * CHK-VNP-01: kỳ vọng "PAID" hoặc "COMPLETED"
     */
    public String getOrderStatus() {
        System.out.println("[InvoicePage] Getting order status...");
        try {
            return wait.until(ExpectedConditions.visibilityOf(lblOrderStatus)).getText().trim().toUpperCase();
        } catch (Exception e) { return ""; }
    }

    /** Lấy phương thức thanh toán hiển thị trên invoice */
    public String getPaymentMethod() {
        try {
            return wait.until(ExpectedConditions.visibilityOf(lblPaymentMethod)).getText().trim();
        } catch (Exception e) { return ""; }
    }

    /** Lấy tổng tiền dạng số */
    public long getTotalPriceAsLong() {
        try {
            String raw = lblTotalPrice.getText().replaceAll("[^0-9]", "");
            return raw.isEmpty() ? 0L : Long.parseLong(raw);
        } catch (Exception e) { return 0L; }
    }

    /** Kiểm tra invoice container đã hiển thị */
    public boolean isInvoiceDisplayed() {
        try {
            return wait.until(ExpectedConditions.visibilityOf(containerInvoice)).isDisplayed();
        } catch (Exception e) {
            return driver.getCurrentUrl().contains("/invoice");
        }
    }

    /**
     * CHECKOUT-COD-01: Kiểm tra trạng thái là PENDING (COD).
     */
    public boolean isStatusPending() {
        String status = getOrderStatus();
        System.out.println("[InvoicePage] Order status: " + status);
        return status.contains("PENDING") || status.contains("CHỜ XÁC NHẬN");
    }

    /**
     * CHK-VNP-01: Kiểm tra trạng thái là PAID hoặc COMPLETED (VNPay).
     */
    public boolean isStatusPaidOrCompleted() {
        String status = getOrderStatus();
        System.out.println("[InvoicePage] Order status: " + status);
        return status.contains("PAID") || status.contains("COMPLETED")
                || status.contains("ĐÃ THANH TOÁN") || status.contains("HOÀN THÀNH");
    }
}
