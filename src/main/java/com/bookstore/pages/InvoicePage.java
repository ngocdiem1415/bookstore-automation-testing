package com.bookstore.pages;

import com.bookstore.factory.PageFactoryManager;
import com.bookstore.utils.LoggerHelper;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.List;

public class InvoicePage extends BasePage {

    private static final String PAGE_URL = "/invoice";

    @FindBy(css = "[data-testid='order-filter-all']")
    private WebElement tabAll;

    @FindBy(css = "[data-testid='order-filter-pending']")
    private WebElement tabPending;

    @FindBy(css = "[data-testid='order-filter-shipping']")
    private WebElement tabShipping;

    @FindBy(css = "[data-testid='order-filter-completed']")
    private WebElement tabCompleted;

    @FindBy(css = "[data-testid='order-filter-cancelled']")
    private WebElement tabCancelled;

    @FindBy(css = "[data-testid='order-search-input']")
    private WebElement txtSearchInput;

    @FindBy(css = "[data-testid='order-list-item']")
    private List<WebElement> listOrderItems;

    @FindBy(css = "[data-testid='order-empty-msg']")
    private WebElement lblEmptyMsg;

    @FindBy(css = "[data-testid='order-detail-link']")
    private List<WebElement> listDetailLinks;

    @FindBy(css = "[data-testid='order-cancel-btn']")
    private List<WebElement> listCancelButtons;

    @FindBy(css = "[data-testid='order-review-btn'], .stardust-button--primary")
    private List<WebElement> listReviewButtons;

    @FindBy(css = "[data-testid='order-review-overlay']")
    private WebElement lblReviewOverlay;

    @FindBy(css = "[data-testid='order-review-item'], .rating-container-form__main-container")
    private List<WebElement> listReviewItems;

    @FindBy(css = "[data-testid='order-review-star']")
    private List<WebElement> listAllStars;

    @FindBy(css = "textarea")
    private List<WebElement> listAllCommentTextareas;

    @FindBy(css = "[data-testid='order-review-cancel-btn']")
    private WebElement btnReviewCancel;

    @FindBy(css = "[data-testid='order-review-submit-btn']")
    private WebElement btnReviewSubmit;

    @FindBy(css = "[data-testid='order-item-status']")
    private List<WebElement> listOrderStatus;

    @FindBy(css = "[data-testid='invoice-order-id']")
    private List<WebElement> listOrderIds;

    public InvoicePage(WebDriver driver, String baseUrl) {
        super(driver, baseUrl);
    }

    public InvoicePage open() {
        driver.get(baseUrl + PAGE_URL);
        return this;
    }

    public boolean isOnInvoicePage() {
        return waitForUrlContains("/invoice");
    }

    public InvoicePage filterByStatus(String statusText) {
        LoggerHelper.info("[INVOICE_PAGE] Lọc hóa đơn theo trạng thái: " + statusText);
        WebElement tabLink;
        switch (statusText.toLowerCase()) {
            case "tất cả":
                tabLink = waitUntilClickable(tabAll);
                break;
            case "chờ thanh toán":
            case "chờ xử lý":
            case "pending":
                tabLink = waitUntilClickable(tabPending);
                break;
            case "chờ giao hàng":
            case "shipping":
                tabLink = waitUntilClickable(tabShipping);
                break;
            case "hoàn thành":
            case "completed":
                tabLink = waitUntilClickable(tabCompleted);
                break;
            case "đã hủy":
            case "cancelled":
                tabLink = waitUntilClickable(tabCancelled);
                break;
            default:
                throw new IllegalArgumentException("Không hỗ trợ lọc trạng thái: " + statusText);
        }
        clickElement(tabLink);
        return this;
    }

    public InvoicePage searchOrder(String keyword) {
        LoggerHelper.info("[INVOICE_PAGE] Tìm kiếm hóa đơn với từ khóa: " + keyword);
        isElementVisible(txtSearchInput);
        txtSearchInput.clear();
        txtSearchInput.sendKeys(keyword);
        txtSearchInput.sendKeys(Keys.ENTER);
        return this;
    }

    public InvoiceDetailPage clickDetailAt(int index) {
        LoggerHelper.info("[INVOICE_PAGE] Mở chi tiết hóa đơn tại vị trí: " + index);
        wait.until(ExpectedConditions.visibilityOfAllElements(listDetailLinks));
        clickElement(listDetailLinks.get(index));
        return PageFactoryManager.getInvoiceDetailPage(driver, baseUrl);
    }

    public int getOrderCount() {
        try {
            return listOrderItems.size();
        } catch (Exception e) {
            return 0;
        }
    }

    public boolean isEmptyMessageDisplayed() {
        try {
            return isElementVisible(lblEmptyMsg);
        } catch (Exception e) {
            return false;
        }
    }

    public String getEmptyMessage() {
        try {
            return getTextOf(lblEmptyMsg);
        } catch (Exception e) {
            return "";
        }
    }

    public String getOrderStatusAt(int index) {
        try {
            return getTextOf(listOrderStatus.get(index));
        } catch (Exception e) {
            return "";
        }
    }

    public String getOrderIdAt(int index) {
        try {
            String raw = getTextOf(listOrderIds.get(index));
            return normalizeOrderId(raw);
        } catch (Exception e) {
            return "";
        }
    }

    public String getFirstOrderId() {
        return getOrderIdAt(0);
    }

    public int findOrderIndexById(String orderId) {
        String expected = normalizeOrderId(orderId);
        if (expected.isBlank()) {
            return -1;
        }

        for (int i = 0; i < getOrderCount(); i++) {
            if (expected.equals(getOrderIdAt(i))) {
                return i;
            }
        }
        return -1;
    }

    public int getCancelButtonCount() {
        try {
            return listCancelButtons.size();
        } catch (Exception e) {
            return 0;
        }
    }

    public InvoicePage cancelOrderById(String orderId) {
        int index = findOrderIndexById(orderId);

        if (index < 0 || index >= listCancelButtons.size()) {
            LoggerHelper.warn("[INVOICE_PAGE] Không thể hủy đơn hàng: " + orderId);
            return this;
        }

        LoggerHelper.info("[INVOICE_PAGE] Hủy đơn hàng theo mã: " + orderId);

        scrollToElement(listCancelButtons.get(index));
        clickElement(listCancelButtons.get(index));

        try {
            Alert confirmAlert = wait.until(ExpectedConditions.alertIsPresent());
            LoggerHelper.info("[INVOICE_PAGE] Nội dung xác nhận hủy đơn: " + confirmAlert.getText());
            confirmAlert.accept();
        } catch (Exception e) {
            LoggerHelper.warn("[INVOICE_PAGE] Không xuất hiện hộp thoại xác nhận hủy đơn: "
                    + e.getMessage());
        }

        waitForUrlContains("/invoice");
        return this;
    }

    private WebElement getOrderItemAt(int index) {
        wait.until(ExpectedConditions.visibilityOfAllElements(listOrderItems));
        return listOrderItems.get(index);
    }

    private String normalizeOrderId(String orderId) {
        return orderId == null ? "" : orderId.replaceAll("[^0-9]", "");
    }
}
