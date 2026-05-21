package com.bookstore.pages;

import com.bookstore.base.BaseSetup;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

/**
 * Page Object: Trang chi tiết sản phẩm (/books/{id}).
 * Covers: PROD-DET-01, PROD-DET-02, PROD-DET-03, CART-ADD-01, CART-ADD-02, CART-ADD-03
 */
public class ProductDetailPage extends BasePage {

    private static final String PAGE_URL = "/books/";

    // ===========================
    // LOCATORS (data-testid)
    // ===========================

    @FindBy(css = "[data-testid='detail-thumbnail']")
    private WebElement imgThumbnail;

    @FindBy(css = "[data-testid='detail-title']")
    private WebElement lblTitle;

    @FindBy(css = "[data-testid='detail-price']")
    private WebElement lblPrice;

    @FindBy(css = "[data-testid='detail-description']")
    private WebElement lblDescription;

    @FindBy(css = "[data-testid='detail-stock']")
    private WebElement lblStock;

    @FindBy(css = "[data-testid='detail-add-cart-btn']")
    private WebElement btnAddToCart;

    @FindBy(css = "[data-testid='detail-quantity-input']")
    private WebElement inputQuantity;

    @FindBy(css = "[data-testid='detail-cart-success-alert']")
    private WebElement lblCartSuccessAlert;

    public ProductDetailPage(WebDriver driver, String baseUrl) {
        super(driver, baseUrl);
    }

    // ===========================
    // NAVIGATION
    // ===========================

    public ProductDetailPage openById(String productId) {
        String url = getCurrentUrl() + PAGE_URL + productId;
        driver.get(url);
        return this;
    }

    /** PROD-DET-03: Điều hướng đến URL tùy ý (SQLi, invalid ID, v.v.) */
    public ProductDetailPage navigateTo(String rawUrl) {
        System.out.println("[ProductDetailPage] Navigating to raw URL: " + rawUrl);
        driver.get(rawUrl);
        return this;
    }

    /**
     * CART-ADD-01: Click [detail-add-cart-btn] để thêm sản phẩm vào giỏ.
     */
    public ProductDetailPage clickAddToCart() {
        System.out.println("[ProductDetailPage] Clicking [detail-add-cart-btn]...");
        clickElement(btnAddToCart);
        return this;
    }

    /**
     * CART-ADD-03: Force nhập số lượng vào [detail-quantity-input] bằng JS (bypass HTML5 max).
     * @param qty Số lượng muốn nhập (vượt quá stock để test boundary)
     */
    public ProductDetailPage forceSetQuantity(int qty) {
        System.out.println("[ProductDetailPage] Force setting quantity to: " + qty);
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].removeAttribute('max'); arguments[0].value = arguments[1];",
                inputQuantity, String.valueOf(qty));
        return this;
    }

    // ===========================
    // GETTER METHODS (Assertions)
    // ===========================

    public boolean isThumbnailDisplayed() {
        try { return wait.until(ExpectedConditions.visibilityOf(imgThumbnail)).isDisplayed(); }
        catch (Exception e) { return false; }
    }

    public String getTitle() {
        return wait.until(ExpectedConditions.visibilityOf(lblTitle)).getText().trim();
    }

    public String getPrice() {
        return wait.until(ExpectedConditions.visibilityOf(lblPrice)).getText().trim();
    }

    public String getDescription() {
        return wait.until(ExpectedConditions.visibilityOf(lblDescription)).getText().trim();
    }

    public int getStockQuantity() {
        String raw = wait.until(ExpectedConditions.visibilityOf(lblStock))
                .getText().replaceAll("[^0-9]", "");
        return raw.isEmpty() ? 0 : Integer.parseInt(raw);
    }

    /** CART-ADD-02: Kiểm tra [detail-add-cart-btn] bị disabled */
    public boolean isAddToCartButtonDisabled() {
        try {
            String disabled = btnAddToCart.getAttribute("disabled");
            String classes  = btnAddToCart.getAttribute("class");
            String style    = btnAddToCart.getAttribute("style");
            return (disabled != null)
                    || (classes != null && classes.contains("disabled"))
                    || (style  != null && style.contains("pointer-events: none"));
        } catch (Exception e) { return false; }
    }

    /** CART-ADD-01: Lấy text thông báo thêm giỏ hàng thành công */
    public String getCartSuccessMessage() {
        try {
            return wait.until(ExpectedConditions.visibilityOf(lblCartSuccessAlert)).getText().trim();
        } catch (Exception e) {
            // Thử JS alert fallback
            try {
                org.openqa.selenium.Alert alert = wait.until(
                        ExpectedConditions.alertIsPresent());
                String txt = alert.getText();
                alert.accept();
                return txt;
            } catch (Exception ex) { return ""; }
        }
    }

    /** CART-ADD-03: Lấy giá trị hiện tại của [detail-quantity-input] */
    public int getCurrentQuantityValue() {
        String val = inputQuantity.getAttribute("value");
        return val == null || val.isEmpty() ? 0 : Integer.parseInt(val.trim());
    }

    /** CART-ADD-03: Lấy giá trị max của [detail-quantity-input] */
    public int getQuantityMaxValue() {
        String max = inputQuantity.getAttribute("max");
        return max == null || max.isEmpty() ? 0 : Integer.parseInt(max.trim());
    }

    /** PROD-DET-02/03: Kiểm tra trang 404 / "không tồn tại" */
    public boolean is404Page() {
        String title = driver.getTitle().toLowerCase();
        return title.contains("404") || title.contains("not found");
    }

    /** PROD-DET-03: Kiểm tra không có DB crash (không có Error 500) */
    public boolean isPageSafe() {
        String title = driver.getTitle().toLowerCase();
        return !title.contains("500") && !title.contains("whitelabel");
    }

    /** PROD-DET-01: Kiểm tra tất cả thông tin sản phẩm đủ */
    public boolean isProductInfoComplete() {
        return isThumbnailDisplayed()
                && !getTitle().isEmpty()
                && !getPrice().isEmpty()
                && !getDescription().isEmpty();
    }
}
