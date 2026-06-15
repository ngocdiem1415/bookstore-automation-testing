package com.bookstore.pages;

import com.bookstore.factory.PageFactoryManager;
import com.bookstore.utils.LoggerHelper;
import org.openqa.selenium.*;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public class OrderHistoryPage extends BasePage {

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

    @FindBy(css = "[data-testid='order-detail-back-btn']")
    private WebElement btnDetailBack;

    @FindBy(css = "[data-testid='order-detail-status']")
    private WebElement lblDetailStatus;

    @FindBy(css = "[data-testid='order-detail-recipient-name']")
    private WebElement lblDetailRecipientName;

    @FindBy(css = ".right-inner-container--content-address__address span")
    private List<WebElement> listDetailAddressSpans; // 0 = SĐT, 1 = Địa chỉ

    @FindBy(css = "[data-testid='order-detail-item-row']")
    private List<WebElement> listDetailProductRows;

    @FindBy(css = "[data-testid='order-detail-summary-total-price']")
    private WebElement lblDetailSummaryTotalPrice;

    @FindBy(css = "[data-testid='order-detail-summary-payment-method']")
    private WebElement lblDetailSummaryPaymentMethod;

    @FindBy(css = "[data-testid='order-detail-item-title-link']")
    private WebElement linkItemOrderDetail;

    @FindBy(css = "[data-testid='order-detail-item-row']")
    private WebElement orderDetailItemRow;

    public OrderHistoryPage(WebDriver driver, String baseUrl) {
        super(driver, baseUrl);
    }

    public OrderHistoryPage open() {
        driver.get(baseUrl + PAGE_URL);
        waitForPageLoaded();
        return this;
    }

    public OrderHistoryPage openWithId(String orderId) {
        driver.get(baseUrl + PAGE_URL + "/" + orderId);
        waitForPageLoaded();
        return this;
    }

    public OrderHistoryPage filterByStatus(String statusText) {
        LoggerHelper.info("[ORDER_PAGE] Lọc đơn hàng theo trạng thái: " + statusText);
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
        waitForPageLoaded();
        return this;
    }

    public OrderHistoryPage searchOrder(String keyword) {
        LoggerHelper.info("[ORDER_PAGE] Tìm kiếm đơn hàng với từ khóa: " + keyword);
        isElementVisible(txtSearchInput);
        txtSearchInput.clear();
        txtSearchInput.sendKeys(keyword);
        txtSearchInput.sendKeys(Keys.ENTER);
        waitForPageLoaded();
        return this;
    }

    public OrderHistoryPage clickDetailAt(int index) {
        LoggerHelper.info("[ORDER_PAGE] Click xem chi tiết đơn hàng tại index: " + index);
        wait.until(ExpectedConditions.visibilityOfAllElements(listDetailLinks));
        clickElement(listDetailLinks.get(index));
        waitForPageLoaded();
        return this;
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

    public OrderHistoryPage clickCancelAt(int index) {
        LoggerHelper.info("[ORDER_PAGE] Click hủy đơn hàng tại index: " + index);
        wait.until(ExpectedConditions.visibilityOfAllElements(listCancelButtons));
        scrollToElement(listCancelButtons.get(index));
        clickElement(listCancelButtons.get(index));

        try {
            Alert confirmAlert = wait.until(ExpectedConditions.alertIsPresent());
            LoggerHelper.info("[ORDER_PAGE] Nội dung confirm dialog: " + confirmAlert.getText());
            confirmAlert.accept();
            waitForPageLoaded();
        } catch (Exception e) {
            LoggerHelper.warn("[ORDER_PAGE] Không xuất hiện alert browser: " + e.getMessage());
        }
        return this;
    }

    public int getReviewButtonCount() {
        try {
            return listReviewButtons.size();
        } catch (Exception e) {
            return 0;
        }
    }

    public OrderHistoryPage clickReviewAt(int index) {
        LoggerHelper.info("[ORDER_PAGE] Click nút đánh giá tại index: " + index);
        wait.until(ExpectedConditions.visibilityOfAllElements(listReviewButtons));
        clickElement(listReviewButtons.get(index));
        // Chờ overlay đánh giá hiển thị
        isElementVisible(lblReviewOverlay);
        return this;
    }

    public OrderHistoryPage fillReviewForm(int starRating, String comment) {
        LoggerHelper.info("[ORDER_PAGE] Điền form đánh giá với số sao: " + starRating);
        isElementVisible(lblReviewOverlay);

        int productsCount = listReviewItems.size();
        LoggerHelper.info("[ORDER_PAGE] Số sản phẩm cần đánh giá trong overlay: " + productsCount);

        for (int i = 0; i < productsCount; i++) {
            // Chọn số sao: mỗi sản phẩm có 5 sao, nên vị trí sao của sản phẩm i bắt đầu từ i * 5
            int starIndex = i * 5 + (starRating - 1);
            if (starIndex < listAllStars.size()) {
                clickElement(listAllStars.get(starIndex));
            }

            // Nhập comment: sản phẩm thứ i có textarea tương ứng ở vị trí i
            if (i < listAllCommentTextareas.size()) {
                WebElement txtComment = listAllCommentTextareas.get(i);
                txtComment.clear();
                txtComment.sendKeys(comment);
            }
        }
        return this;
    }

    public OrderHistoryPage submitReview() {
        LoggerHelper.info("[ORDER_PAGE] Gửi đánh giá sản phẩm");
        clickElement(btnReviewSubmit);
        isElementVisible(lblReviewOverlay);
        return this;
    }

    public OrderHistoryPage cancelReview() {
        LoggerHelper.info("[ORDER_PAGE] Đóng overlay đánh giá");
        clickElement(btnReviewCancel);
        isElementVisible(lblReviewOverlay);
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

    public OrderHistoryPage clickBackToList() {
        LoggerHelper.info("[ORDER_PAGE] Quay lại danh sách đơn hàng");
        clickElement(btnDetailBack);
        waitForPageLoaded();
        return this;
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

    private void waitForPageLoaded() {
        try {
            WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(5));
            shortWait.until(webDriver -> ((JavascriptExecutor) webDriver)
                    .executeScript("return document.readyState").equals("complete"));
        } catch (Exception ignored) {
        }
    }

    public ProductDetailPage clickProductInOrderDetail() {
        isElementVisible(linkItemOrderDetail);
        scrollToElement(linkItemOrderDetail);
        jsClick(linkItemOrderDetail);
        waitForPageLoaded();
        return PageFactoryManager.getProductDetailPage(driver, baseUrl);
    }

}
