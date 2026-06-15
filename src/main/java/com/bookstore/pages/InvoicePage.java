package com.bookstore.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/**
 * Page Object: Trang Hóa đơn / Xác nhận đơn hàng (/invoice).
 * Covers: CHECKOUT-COD-01, CHK-VNP-01
 */
public class InvoicePage extends BasePage {

    private static final String PAGE_URL = "/invoice";

    @FindBy(css = "[data-testid='invoice-order-id']")
    private WebElement lblOrderId;

    @FindBy(css = "[data-testid='order-item-status']")
    private WebElement lblOrderStatus;

    @FindBy(css = "[data-testid='invoice-payment-method']")
    private WebElement lblPaymentMethod;

    @FindBy(css = "[data-testid='invoice-total-price']")
    private WebElement lblTotalPrice;

    @FindBy(css = "[data-testid='invoice-container']")
    private WebElement containerInvoice;

    public InvoicePage(WebDriver driver,String baseUrl) {
        super(driver,baseUrl);
    }

    public InvoicePage open() {
        driver.get(driver+ PAGE_URL);
        return this;
    }

    public boolean isOnInvoicePage() {
        return waitForUrlContains("/invoice");
    }

    public String getOrderId() {
        try {
            return getTextOf(lblOrderId);
        } catch (Exception e) { return ""; }
    }

    public String getOrderStatus() {
        System.out.println("[INVOICE_PAGE] Getting order status...");
        try {
            return getTextOf(lblOrderStatus).toUpperCase();
        } catch (Exception e) { return ""; }
    }

    public String getPaymentMethod() {
        try {
            return getTextOf(lblPaymentMethod);
        } catch (Exception e) { return ""; }
    }

    public long getTotalPriceAsLong() {
        try {
            String raw = lblTotalPrice.getText().replaceAll("[^0-9]", "");
            return raw.isEmpty() ? 0L : Long.parseLong(raw);
        } catch (Exception e) { return 0L; }
    }

    public boolean isInvoiceDisplayed() {
        try {
            return isElementVisible(containerInvoice);
        } catch (Exception e) {
            return driver.getCurrentUrl().contains("/invoice");
        }
    }

    public boolean isStatusPending() {
        String status = getOrderStatus();
        System.out.println("[INVOICE_PAGE] Order status: " + status);
        return status.contains("PENDING");
    }

    public boolean isStatusCancelled() {
        String status = getOrderStatus();
        System.out.println("[INVOICE_PAGE] Order status: " + status);
        return status.contains("CANCELLED");
    }

    public boolean isStatusPaidOrCompleted() {
        String status = getOrderStatus();
        System.out.println("[INVOICE_PAGE] Order status: " + status);
        return status.contains("PAID") || status.contains("COMPLETED")
                || status.contains("ĐÃ THANH TOÁN") || status.contains("HOÀN THÀNH");
    }
}
