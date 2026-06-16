package com.bookstore.pages;

import com.bookstore.factory.PageFactoryManager;
import com.bookstore.utils.LoggerHelper;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.List;

public class InvoiceDetailPage extends BasePage {

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

    @FindBy(css = "[data-testid='order-cancel-btn']")
    private List<WebElement> listCancelButtons;

    @FindBy(css = "[data-testid='order-detail-back-btn']")
    private WebElement btnDetailBack;

    @FindBy(css = "[data-testid='order-detail-status']")
    private WebElement lblDetailStatus;

    @FindBy(css = "[data-testid='order-detail-recipient-name']")
    private WebElement lblDetailRecipientName;

    @FindBy(css = ".right-inner-container--content-address__address span")
    private List<WebElement> listDetailAddressSpans;

    @FindBy(css = "[data-testid='order-detail-item-row']")
    private List<WebElement> listDetailProductRows;

    @FindBy(css = "[data-testid='order-detail-summary-total-price']")
    private WebElement lblDetailSummaryTotalPrice;

    @FindBy(css = "[data-testid='order-detail-summary-payment-method']")
    private WebElement lblDetailSummaryPaymentMethod;

    @FindBy(css = "[data-testid='order-detail-item-title-link']")
    private WebElement linkItemOrderDetail;

    public InvoiceDetailPage(WebDriver driver, String baseUrl) {
        super(driver, baseUrl);
    }

    public InvoiceDetailPage open() {
        return this;
    }

    public InvoiceDetailPage openWithId(String orderId) {
        driver.get(baseUrl + PAGE_URL + "/" + orderId);
        return this;
    }

    public boolean isOnInvoicePage() {
        return waitForUrlContains("/invoice");
    }

    public String getOrderId() {
        try {
            return getTextOf(lblOrderId).replaceAll("[^0-9]", "");
        } catch (Exception e) {
            return "";
        }
    }

    public String getOrderStatus() {
        try {
            return getTextOf(lblOrderStatus).toUpperCase();
        } catch (Exception e) {
            return "";
        }
    }

    public String getPaymentMethod() {
        try {
            return getTextOf(lblPaymentMethod);
        } catch (Exception e) {
            return "";
        }
    }

    public long getTotalPriceAsLong() {
        try {
            String raw = lblTotalPrice.getText().replaceAll("[^0-9]", "");
            return raw.isEmpty() ? 0L : Long.parseLong(raw);
        } catch (Exception e) {
            return 0L;
        }
    }

    public boolean isInvoiceDisplayed() {
        try {
            return isElementVisible(containerInvoice);
        } catch (Exception e) {
            return driver.getCurrentUrl().contains("/invoice");
        }
    }

    public boolean isStatusPending() {
        return getOrderStatus().contains("PENDING");
    }

    public boolean isStatusCancelled() {
        return getOrderStatus().contains("CANCELLED");
    }

    public boolean isStatusPaidOrCompleted() {
        String status = getOrderStatus();
        return status.contains("PAID") || status.contains("COMPLETED")
                || status.contains("ĐÃ THANH TOÁN") || status.contains("HOÀN THÀNH");
    }

    public int getCancelButtonCount() {
        try {
            return listCancelButtons.size();
        } catch (Exception e) {
            return 0;
        }
    }

    public InvoiceDetailPage clickCancelAt(int index) {
        LoggerHelper.info("[INVOICE_DETAIL_PAGE] Click hủy đơn hàng tại index: " + index);
        wait.until(ExpectedConditions.visibilityOfAllElements(listCancelButtons));
        scrollToElement(listCancelButtons.get(index));
        clickElement(listCancelButtons.get(index));

        try {
            Alert confirmAlert = wait.until(ExpectedConditions.alertIsPresent());
            LoggerHelper.info("[INVOICE_DETAIL_PAGE] Nội dung confirm dialog: " + confirmAlert.getText());
            confirmAlert.accept();
        } catch (Exception e) {
            LoggerHelper.warn("[INVOICE_DETAIL_PAGE] Không xuất hiện alert browser: " + e.getMessage());
        }
        return this;
    }

    public InvoiceDetailPage cancelOrderById(String orderId) {
        LoggerHelper.info("[INVOICE_DETAIL_PAGE] Hủy đơn hàng theo mã đơn: " + orderId);
        openWithId(orderId);

        if (getCancelButtonCount() <= 0) {
            LoggerHelper.warn("[INVOICE_DETAIL_PAGE] Không có nút hủy cho mã đơn: " + orderId);
            return this;
        }

        return clickCancelAt(0);
    }

    public String getDetailStatus() {
        try {
            return getTextOf(lblDetailStatus);
        } catch (Exception e) {
            return "";
        }
    }

    public boolean isDetailInfoDisplayed() {
        try {
            return isElementVisible(lblDetailRecipientName);
        } catch (Exception e) {
            return false;
        }
    }

    public String getDetailRecipientName() {
        try {
            return getTextOf(lblDetailRecipientName);
        } catch (Exception e) {
            return "";
        }
    }

    public String getDetailRecipientPhone() {
        try {
            return listDetailAddressSpans.get(0).getText().trim();
        } catch (Exception e) {
            return "";
        }
    }

    public String getDetailRecipientAddress() {
        try {
            return listDetailAddressSpans.get(1).getText().trim();
        } catch (Exception e) {
            return "";
        }
    }

    public int getDetailItemCount() {
        try {
            return listDetailProductRows.size();
        } catch (Exception e) {
            return 0;
        }
    }

    public long getDetailSummaryTotalPrice() {
        try {
            String raw = lblDetailSummaryTotalPrice.getText().replaceAll("[^0-9]", "");
            return raw.isEmpty() ? 0L : Long.parseLong(raw);
        } catch (Exception e) {
            return 0L;
        }
    }

    public String getDetailSummaryPaymentMethod() {
        try {
            return getTextOf(lblDetailSummaryPaymentMethod);
        } catch (Exception e) {
            return "";
        }
    }

    public InvoicePage clickBackToList() {
        LoggerHelper.info("[INVOICE_DETAIL_PAGE] Quay lại danh sách hóa đơn");
        clickElement(btnDetailBack);
        return PageFactoryManager.getInvoicePage(driver, baseUrl);
    }

    public boolean isPageSafe() {
        String title = driver.getTitle().toLowerCase();
        return !title.contains("500") && !title.contains("error");
    }

    public boolean is403Or404() {
        String title = driver.getTitle().toLowerCase();
        String body = "";
        try {
            body = driver.findElement(By.tagName("body")).getText().toLowerCase();
        } catch (Exception ignored) {
        }
        return title.contains("403") || title.contains("404")
                || body.contains("403") || body.contains("forbidden")
                || body.contains("không tìm thấy") || body.contains("access denied");
    }

    public ProductDetailPage clickProductInOrderDetail() {
        isElementVisible(linkItemOrderDetail);
        scrollToElement(linkItemOrderDetail);
        jsClick(linkItemOrderDetail);
        return PageFactoryManager.getProductDetailPage(driver, baseUrl);
    }
}
