package com.bookstore.pages;

import com.bookstore.base.BaseSetup;
import org.openqa.selenium.*;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

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
        driver.get(driver + rawUrl);
        return this;
    }

    public ProductDetailPage clickAddToCart() {
        clickElement(btnAddToCart);
        waitForLoadProductToCart();
        return this;
    }

    protected void waitForLoadProductToCart() {
        try {
            WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(5));
            shortWait.until(ExpectedConditions.presenceOfElementLocated(By.className("container_productDetail")));
        } catch (Exception ignored) {
        }
    }

    public ProductDetailPage forceSetQuantity(int qty) {
        System.out.println("[PRODUCT_DETAIL_PAGE] Tiến hành nhập số lượng: " + qty);
        try {
            isElementVisible(txtQuantityInput);
            txtQuantityInput.clear();
            txtQuantityInput.sendKeys(String.valueOf(qty), Keys.TAB);
        } catch (Exception e) {
            System.out.println("[ERROR] Lỗi khi gõ giá trị vào ô số lượng: " + e.getMessage());
        }
        return this;
    }

    public boolean isThumbnailDisplayed() {
        try {
            return isElementVisible(imgThumbnail);
        } catch (Exception e) {
            return false;
        }
    }

    public String getTitle() {
        return getTextOf(lblTitle);
    }

    public String getPrice() {
        return getTextOf(lblPrice);
    }

    public String getDescription() {
        return getTextOf(lblDescription);
    }

    public int getStockQuantity() {
        String rawText = getTextOf(lblStockQuantity);
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

    public String getErrorMessage() {
        try {
            return getTextOf(lblCartErrorAlert);
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

    public boolean is404Page() {
        String title = driver.getTitle().toLowerCase();
        return title.contains("404") || title.contains("not found");
    }

    public boolean isPageSafe() {
        String title = driver.getTitle().toLowerCase();
        return !title.contains("500") && !title.contains("whitelabel");
    }

}
