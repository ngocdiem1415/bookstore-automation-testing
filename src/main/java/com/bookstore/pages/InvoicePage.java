package com.bookstore.pages;

import com.bookstore.factory.PageFactoryManager;
import com.bookstore.utils.LoggerHelper;
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

    @FindBy(css = "[data-testid='order-list-item'], .container-product-content-inner__right-inner-center-product")
    private List<WebElement> listOrderItems;

    @FindBy(css = "[data-testid='order-empty-msg'], .text-center")
    private WebElement lblEmptyMsg;

    @FindBy(css = "[data-testid='order-item-status'], .container-product-content-inner__right-inner-center-product__header-title")
    private List<WebElement> listOrderStatusTitles;

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
        LoggerHelper.info("[INVOICE_PAGE] Mở chi tiết hóa đơn tại index: " + index);
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
            wait.until(ExpectedConditions.visibilityOfAllElements(listOrderStatusTitles));
            return listOrderStatusTitles.get(index).getText().trim();
        } catch (Exception e) {
            return "";
        }
    }

    public int getCancelButtonCount() {
        try {
            return listCancelButtons.size();
        } catch (Exception e) {
            return 0;
        }
    }

    public int getReviewButtonCount() {
        try {
            return listReviewButtons.size();
        } catch (Exception e) {
            return 0;
        }
    }

    public InvoicePage clickReviewAt(int index) {
        LoggerHelper.info("[INVOICE_PAGE] Click nút đánh giá tại index: " + index);
        wait.until(ExpectedConditions.visibilityOfAllElements(listReviewButtons));
        clickElement(listReviewButtons.get(index));
        isElementVisible(lblReviewOverlay);
        return this;
    }

    public InvoicePage fillReviewForm(int starRating, String comment) {
        LoggerHelper.info("[INVOICE_PAGE] Điền form đánh giá với số sao: " + starRating);
        isElementVisible(lblReviewOverlay);

        int productsCount = listReviewItems.size();
        LoggerHelper.info("[INVOICE_PAGE] Số sản phẩm cần đánh giá trong overlay: " + productsCount);

        for (int i = 0; i < productsCount; i++) {
            int starIndex = i * 5 + (starRating - 1);
            if (starIndex < listAllStars.size()) {
                clickElement(listAllStars.get(starIndex));
            }

            if (i < listAllCommentTextareas.size()) {
                WebElement txtComment = listAllCommentTextareas.get(i);
                txtComment.clear();
                txtComment.sendKeys(comment);
            }
        }
        return this;
    }

    public InvoicePage submitReview() {
        LoggerHelper.info("[INVOICE_PAGE] Gửi đánh giá sản phẩm");
        clickElement(btnReviewSubmit);
        isElementVisible(lblReviewOverlay);
        return this;
    }

    public InvoicePage cancelReview() {
        LoggerHelper.info("[INVOICE_PAGE] Đóng overlay đánh giá");
        clickElement(btnReviewCancel);
        isElementVisible(lblReviewOverlay);
        return this;
    }
}
