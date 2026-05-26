package com.bookstore.pages;

import com.bookstore.base.BaseSetup;
import org.openqa.selenium.*;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

/**
 * Page Object: Trang chi tiết sản phẩm (/books/{id}).
 * Covers: PROD-DET-01, PROD-DET-02, PROD-DET-03, CART-ADD-01, CART-ADD-02, CART-ADD-03
 */
public class ProductDetailPage extends BasePage {
    private static final String PAGE_URL = "/books/";

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

    @FindBy(css = "[data-testid='cart-notification-success']")
    private WebElement lblCartSuccessAlert;

    @FindBy(css = "[data-testid='cart-notification-error']")
    private WebElement lblCartErrorAlert;

    @FindBy(id = "stock-quantity")
    private WebElement lblStockQuantity;

    @FindBy(css = "[data-testid='detail-quantity-input']")
    private WebElement txtQuantityInput;

    @FindBy(id = "cart-notification-success")
    private WebElement lblCartSuccessMessage;

    public ProductDetailPage(WebDriver driver, String baseUrl) {
        super(driver, baseUrl);
    }

    public ProductDetailPage openById(String productId) {
        String url = baseUrl + PAGE_URL + productId;
        driver.get(url);
        return this;
    }

    public ProductDetailPage navigateTo(String rawUrl) {
        driver.get(rawUrl);
        return this;
    }

    public ProductDetailPage clickAddToCart() {
        clickElement(btnAddToCart);
        return this;
    }

    public ProductDetailPage forceSetQuantity(int qty) {
        System.out.println("[ProductDetailPage] Force setting quantity to: " + qty);
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].removeAttribute('max'); arguments[0].value = arguments[1];",
                inputQuantity, String.valueOf(qty));
        return this;
    }

    public boolean isThumbnailDisplayed() {
        try {
            return wait.until(ExpectedConditions.visibilityOf(imgThumbnail)).isDisplayed();
        } catch (Exception e) {
            return false;
        }
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
        wait.until(ExpectedConditions.visibilityOf(lblStockQuantity));
        String rawText = lblStockQuantity.getText().trim();
        String numbersOnly = rawText.replaceAll("[^0-9]", "");
        if (numbersOnly.isEmpty()) {
            return 0;
        }
        return Integer.parseInt(numbersOnly);
    }

    public boolean isAddToCartButtonDisabled() {
        try {
            String disabled = btnAddToCart.getAttribute("disabled");
            return (disabled != null);
        } catch (Exception e) {
            return false;
        }
    }

    public String getAndAcceptSuccessAlert() {
        try {
            Alert alert = wait.until(ExpectedConditions.alertIsPresent());
            String alertText = alert.getText();
            alert.accept();
            return alertText;
        } catch (Exception e) {
            return "";
        }
    }

    public String getCartSuccessMessage() {
        try {
            return wait.until(ExpectedConditions.visibilityOf(lblCartSuccessAlert)).getText().trim();
        } catch (Exception ex) {
            return "";

        }
    }

    public String getErrorMessage() {
        try {
            return wait.until(ExpectedConditions.visibilityOf(lblCartErrorAlert)).getText().trim();
        } catch (Exception e) {
            return getAndAcceptSuccessAlert();
        }
    }


    public String getSuccessMessage() {
        try {
            WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(3));
            shortWait.until(ExpectedConditions.visibilityOf(lblCartSuccessAlert));
            return lblCartSuccessAlert.getText().trim();
        } catch (Exception e) {
            return getAndAcceptSuccessAlert();
        }
    }

    public int getCurrentQuantityValue() {
        String val = inputQuantity.getAttribute("value");
        return val == null || val.isEmpty() ? 0 : Integer.parseInt(val.trim());
    }

    /**
     * CART-ADD-03: Lấy giá trị max của [detail-quantity-input]
     */
    public int getQuantityMaxValue() {
        String max = inputQuantity.getAttribute("max");
        return max == null || max.isEmpty() ? 0 : Integer.parseInt(max.trim());
    }

    /**
     * PROD-DET-02/03: Kiểm tra trang 404 / "không tồn tại"
     */
    public boolean is404Page() {
        String title = driver.getTitle().toLowerCase();
        return title.contains("404") || title.contains("not found");
    }

    /**
     * PROD-DET-03: Kiểm tra không có DB crash (không có Error 500)
     */
    public boolean isPageSafe() {
        String title = driver.getTitle().toLowerCase();
        return !title.contains("500") && !title.contains("whitelabel");
    }

    /**
     * PROD-DET-01: Kiểm tra tất cả thông tin sản phẩm đủ
     */
    public boolean isProductInfoComplete() {
        return isThumbnailDisplayed()
                && !getTitle().isEmpty()
                && !getPrice().isEmpty()
                && !getDescription().isEmpty();
    }
}
