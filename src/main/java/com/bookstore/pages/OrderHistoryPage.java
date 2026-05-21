package com.bookstore.pages;

import com.bookstore.base.BaseSetup;
import org.openqa.selenium.*;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;

import java.util.List;

/**
 * Page Object: Trang Lịch sử đơn hàng (/invoice).
 * Covers: ORD-LST-01/02/03, ORD-DET-01/02/03, ORD-CAN-01/02/03
 */
public class OrderHistoryPage extends BasePage {

    private static final String PAGE_URL = "/invoice";

    // --- List ---
    @FindBy(css = "[data-testid='order-list-item']")
    private List<WebElement> listOrderItems;

    @FindBy(css = "[data-testid='order-empty-msg']")
    private WebElement lblEmptyMsg;

    @FindBy(css = "[data-testid='order-detail-link']")
    private List<WebElement> listDetailLinks;

    // --- Cancel ---
    @FindBy(css = "[data-testid='order-cancel-btn']")
    private List<WebElement> listCancelBtns;

    // --- Pagination ---
    @FindBy(css = "#pagination .page-item a.page-link")
    private List<WebElement> listPageLinks;

    // --- Status filter ---
    @FindBy(css = "[data-testid='order-status-filter']")
    private WebElement selStatusFilter;

    // --- Detail page elements ---
    @FindBy(css = "[data-testid='order-detail-recipient']")
    private WebElement lblRecipient;

    @FindBy(css = "[data-testid='order-detail-status']")
    private WebElement lblDetailStatus;

    @FindBy(css = "[data-testid='order-detail-items']")
    private List<WebElement> listDetailItems;

    public OrderHistoryPage(WebDriver driver,String baseUrl) {
        super(driver,baseUrl);
    }

    // --- Navigation ---
    public OrderHistoryPage open() {
        driver.get(getCurrentUrl()+ PAGE_URL);
        return this;
    }

    public OrderHistoryPage openWithId(String orderId) {
        String url = getCurrentUrl() + PAGE_URL + "?id=" + orderId;
        driver.get(url);
        return this;
    }

    // --- List actions ---
    public int getOrderCount() {
        try { return listOrderItems.size(); } catch (Exception e) { return 0; }
    }

    public boolean isEmptyMessageDisplayed() {
        try { return wait.until(ExpectedConditions.visibilityOf(lblEmptyMsg)).isDisplayed(); }
        catch (Exception e) { return false; }
    }

    public String getEmptyMessage() {
        return wait.until(ExpectedConditions.visibilityOf(lblEmptyMsg)).getText().trim();
    }

    /** ORD-LST-03: Click page số `pageNumber` trong pagination */
    public OrderHistoryPage clickPage(int pageNumber) {
        System.out.println("[OrderHistoryPage] Clicking page: " + pageNumber);
        for (WebElement link : listPageLinks) {
            if (link.getText().trim().equals(String.valueOf(pageNumber))) {
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", link);
                try { Thread.sleep(800); } catch (InterruptedException ignored) {}
                return this;
            }
        }
        throw new NoSuchElementException("Page " + pageNumber + " not found in pagination.");
    }

    // --- Filter ---
    public OrderHistoryPage filterByStatus(String statusText) {
        System.out.println("[OrderHistoryPage] Filtering by status: " + statusText);
        new Select(wait.until(ExpectedConditions.elementToBeClickable(selStatusFilter)))
                .selectByVisibleText(statusText);
        try { Thread.sleep(800); } catch (InterruptedException ignored) {}
        return this;
    }

    // --- Detail ---
    public OrderHistoryPage clickDetailAt(int index) {
        System.out.println("[OrderHistoryPage] Clicking detail link at index: " + index);
        clickElement(listDetailLinks.get(index));
        return this;
    }

    public boolean isDetailInfoDisplayed() {
        try { return wait.until(ExpectedConditions.visibilityOf(lblRecipient)).isDisplayed(); }
        catch (Exception e) { return false; }
    }

    public String getDetailStatus() {
        try { return wait.until(ExpectedConditions.visibilityOf(lblDetailStatus)).getText().trim(); }
        catch (Exception e) { return ""; }
    }

    public int getDetailItemCount() {
        try { return listDetailItems.size(); } catch (Exception e) { return 0; }
    }

    // --- Cancel ---
    public int getCancelButtonCount() {
        try { return listCancelBtns.size(); } catch (Exception e) { return 0; }
    }

    /** ORD-CAN-01: Click cancel button tại index và confirm JS confirm dialog */
    public OrderHistoryPage clickCancelAt(int index) {
        System.out.println("[OrderHistoryPage] Clicking cancel button at index: " + index);
        clickElement(listCancelBtns.get(index));
        try {
            Alert confirm = wait.until(ExpectedConditions.alertIsPresent());
            System.out.println("[OrderHistoryPage] Confirm dialog: " + confirm.getText());
            confirm.accept();
            try { Thread.sleep(1000); } catch (InterruptedException ignored) {}
        } catch (Exception e) {
            System.out.println("[OrderHistoryPage] No confirm dialog: " + e.getMessage());
        }
        return this;
    }

    public String getOrderStatusAt(int index) {
        try {
            WebElement item = listOrderItems.get(index);
            return item.findElement(By.cssSelector("[data-testid='order-item-status']")).getText().trim();
        } catch (Exception e) { return ""; }
    }

    // --- Safety checks ---
    public boolean isPageSafe() {
        String title = driver.getTitle().toLowerCase();
        return !title.contains("500") && !title.contains("error");
    }

    public boolean is403Or404() {
        String title = driver.getTitle().toLowerCase();
        String body  = "";
        try { body = driver.findElement(By.tagName("body")).getText().toLowerCase(); }
        catch (Exception ignored) {}
        return title.contains("403") || title.contains("404")
                || body.contains("403") || body.contains("forbidden")
                || body.contains("không tìm thấy") || body.contains("access denied");
    }
}
