package com.bookstore.pages;

import com.bookstore.factory.PageFactoryManager;
import com.bookstore.utils.LoggerHelper;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import java.util.List;

public class InvoiceDetailPage extends BasePage {

    private static final String PAGE_URL = "/invoice";

    @FindBy(css = "[data-testid='order-detail-back-btn']")
    private WebElement btnDetailBack;

    @FindBy(css = "[data-testid='order-detail-status']")
    private WebElement lblDetailStatus;

    @FindBy(css = "[data-testid='order-detail-recipient-name']")
    private WebElement lblDetailRecipientName;

    @FindBy(css = "[data-testid='order-detail-recipient-phone']")
    private WebElement lblDetailRecipientPhone;

    @FindBy(css = "[data-testid='order-detail-recipient-address']")
    private WebElement lblDetailRecipientAddress;

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
        driver.get(baseUrl + PAGE_URL + "/" + normalizeOrderId(orderId));
        return this;
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
            return isElementVisible(lblDetailStatus) && isElementVisible(lblDetailRecipientName);
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
            return getTextOf(lblDetailRecipientPhone);
        } catch (Exception e) {
            return "";
        }
    }

    public String getDetailRecipientAddress() {
        try {
            return getTextOf(lblDetailRecipientAddress);
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

    private String normalizeOrderId(String orderId) {
        return orderId == null ? "" : orderId.replaceAll("[^0-9]", "");
    }
}
