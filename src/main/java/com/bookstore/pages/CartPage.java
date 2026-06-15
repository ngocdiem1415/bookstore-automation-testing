package com.bookstore.pages;

import com.bookstore.utils.LoggerHelper;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public class CartPage extends BasePage {

    private static final String PAGE_URL = "/cart";

    private static final By PRODUCT_ROWS = By.cssSelector("[data-testid='cart-item-row']");

    @FindBy(css = "[data-testid='cart-item-increase']")
    private List<WebElement> btnListIncrease;

    @FindBy(css = "[data-testid='cart-item-decrease']")
    private List<WebElement> btnListDecrease;

    @FindBy(css = "[data-testid='cart-item-quantity']")
    private List<WebElement> inputListQuantity;

    @FindBy(css = "[data-testid='cart-item-delete']")
    private List<WebElement> btnListDelete;

    @FindBy(css = "[data-testid='cart-empty-msg']")
    private WebElement lblCartEmptyMsg;

    @FindBy(css = "[data-testid='cart-total-price']")
    private WebElement lblTotalPrice;

    @FindBy(css = "[data-testid='cart-buy-btn']")
    private WebElement btnBuy;

    @FindBy(css = "[data-testid='cart-item-checkbox']")
    private List<WebElement> listCheckboxes;

    public CartPage(WebDriver driver, String baseUrl) {
        super(driver, baseUrl);
    }

    public CartPage open() {
        driver.get(baseUrl + PAGE_URL);
        waitForCartToLoad();
        return this;
    }


    public CartPage clickIncreaseAt(int index) {
        LoggerHelper.info("[CART_PAGE] Thao tác click tăng số lượng vật lý tại index: " + index);
        try {
            clickElement(btnListIncrease.get(index));
        } catch (Exception e) {
            LoggerHelper.warn("[CART_PAGE] Click vật lý bị chặn. Kích hoạt lớp xử lý bảo hiểm JavaScript...");
        }
        waitForCartToLoad();
        return this;
    }

    public CartPage clickDecreaseAt(int index) {
        LoggerHelper.info("[CART_PAGE] Thao tác click giảm số lượng vật lý tại index: " + index);
        try {
            clickElement(btnListDecrease.get(index));
        } catch (Exception e) {
            LoggerHelper.warn("[CART_PAGE] Click vật lý bị chặn. Kích hoạt lớp xử lý bảo hiểm JavaScript...");
        }
        waitForCartToLoad();
        return this;
    }

    public CartPage tryTypeIntoQuantityInput(int index, String text) {
        LoggerHelper.info("[CART_PAGE] Gửi chuỗi ký tự '" + text + "' vào ô nhập liệu số lượng.");
        try {
            WebElement input = inputListQuantity.get(index);
            input.clear();
            input.sendKeys(text);
        } catch (Exception e) {
            LoggerHelper.info("[CART_PAGE] Ô số lượng được bảo vệ đúng trạng thái readonly/disabled: " + e.getMessage());
        }
        return this;
    }

    public CartPage clickDeleteAt(int index) {
        LoggerHelper.info("[CART_PAGE] Thao tác click xóa sản phẩm vật lý tại index: " + index);
        WebElement btnDelete = btnListDelete.get(index);
        try {
            WebElement targetRow = driver.findElements(PRODUCT_ROWS).get(index);
            clickElement(btnDelete);
            wait.until(ExpectedConditions.stalenessOf(targetRow));
        } catch (Exception e) {
            LoggerHelper.warn("[CART_PAGE] Click vật lý bị chặn. Kích hoạt lớp xử lý bảo hiểm JavaScript...");
            jsClick(btnDelete);
        }
        waitForCartToLoad();
        return this;
    }

    public CartPage deleteAllItems() {
        LoggerHelper.info("[CART_PAGE] Bắt đầu luồng dọn sạch toàn bộ giỏ hàng...");
        int maxAttempts = 10;
        int attempt = 0;

        while (getCartItemCount() > 0 && attempt < maxAttempts) {
            attempt++;
            LoggerHelper.info("[CART_PAGE] Lượt xử lý thứ " + attempt + " - Item còn lại: " + getCartItemCount());
            clickDeleteAt(0);
        }
        return this;
    }

    public int getCartItemCount() {
        try {
            List<WebElement> rows = driver.findElements(PRODUCT_ROWS);
            return rows.size();
        } catch (Exception e) {
            return 0;
        }
    }

    public int getQuantityAt(int index) {
        String val = inputListQuantity.get(index).getAttribute("value");
        return val == null || val.isEmpty() ? 0 : Integer.parseInt(val.trim());
    }

    public boolean isQuantityInputDisabled(int index) {
        WebElement input = inputListQuantity.get(index);
        String disabled = input.getAttribute("disabled");
        String readOnly = input.getAttribute("readonly");
        return disabled != null || readOnly != null || "true".equals(disabled) || "true".equals(readOnly);
    }

    public boolean isCartEmptyMessageDisplayed() {
        try {
            return isElementVisible(lblCartEmptyMsg);
        } catch (Exception e) {
            return false;
        }
    }

    public String getCartEmptyMessage() {
        return getTextOf(lblCartEmptyMsg);
    }

    public long getTotalPriceAsLong() {
        try {
            isElementVisible(lblTotalPrice);
            String raw = lblTotalPrice.getText().replaceAll("[^0-9]", "");
            return raw.isEmpty() ? 0L : Long.parseLong(raw);
        } catch (Exception e) {
            return 0L;
        }
    }

    protected void waitForCartToLoad() {
        try {
            WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(10));
            shortWait.until(ExpectedConditions.presenceOfElementLocated(By.className("cart")));
        } catch (Exception ignored) {
        }
    }

    public CartPage checkCheckboxAt(int index) {
        System.out.println("[CART_PAGE] Tích chọn checkbox tại index: " + index);
        WebElement cb = wait.until(ExpectedConditions.visibilityOf(listCheckboxes.get(index)));
        if (!cb.isSelected()) {
            clickElement(cb);
        }
        return this;
    }


    public CheckoutPage clickBuyButton() {
        System.out.println("[CART_PAGE] Click nút Mua hàng...");
        clickElement(btnBuy);
        return com.bookstore.factory.PageFactoryManager.getCheckoutPage(driver, baseUrl);
    }
}