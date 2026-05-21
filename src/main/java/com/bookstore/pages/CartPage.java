package com.bookstore.pages;

import com.bookstore.base.BaseSetup;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.List;

/**
 * Page Object: Trang Giỏ hàng (/cart).
 * Covers: CART-UPD-01, CART-UPD-02, CART-UPD-03, CART-DEL-01, CART-DEL-02, CART-DEL-03
 */
public class CartPage extends BasePage {

    private static final String PAGE_URL = "/cart";

    // ===========================
    // LOCATORS (data-testid)
    // ===========================

    /** Nút tăng số lượng từng item */
    @FindBy(css = "[data-testid='cart-item-increase']")
    private List<WebElement> btnListIncrease;

    /** Nút giảm số lượng từng item */
    @FindBy(css = "[data-testid='cart-item-decrease']")
    private List<WebElement> btnListDecrease;

    /** Input số lượng từng item */
    @FindBy(css = "[data-testid='cart-item-quantity']")
    private List<WebElement> inputListQuantity;

    /** Nút xóa từng item */
    @FindBy(css = "[data-testid='cart-item-delete']")
    private List<WebElement> btnListDelete;

    /** Thông báo giỏ hàng trống */
    @FindBy(css = "[data-testid='cart-empty-msg']")
    private WebElement lblCartEmptyMsg;

    /** Tổng tiền giỏ hàng */
    @FindBy(css = "[data-testid='cart-total-price']")
    private WebElement lblTotalPrice;

    // ===========================
    // CONSTRUCTOR
    // ===========================

    public CartPage(WebDriver driver,String baseUrl) {
        super(driver , baseUrl);
    }

    // ===========================
    // NAVIGATION
    // ===========================

    public CartPage open() {
        driver.get(getCurrentUrl() + PAGE_URL);
        waitForCartToLoad();
        return this;
    }

    // ===========================
    // UPDATE: Increase / Decrease
    // ===========================

    /**
     * CART-UPD-01: Click [cart-item-increase] của item tại index (0-based).
     */
    public CartPage clickIncreaseAt(int index) {
        System.out.println("[CartPage] Clicking increase button at index: " + index);
        clickElement(btnListIncrease.get(index));
        waitForCartToLoad();
        return this;
    }

    /**
     * CART-UPD-01: Click [cart-item-decrease] của item tại index.
     */
    public CartPage clickDecreaseAt(int index) {
        System.out.println("[CartPage] Clicking decrease button at index: " + index);
        clickElement(btnListDecrease.get(index));
        waitForCartToLoad();
        return this;
    }

    /**
     * CART-UPD-02: Thử nhập số âm vào [cart-item-quantity] tại index.
     * @return CartPage để chain assertion
     */
    public CartPage tryTypeIntoQuantityInput(int index, String text) {
        System.out.println("[CartPage] Trying to type '" + text + "' into quantity input at index: " + index);
        try {
            inputListQuantity.get(index).sendKeys(text);
        } catch (Exception e) {
            System.out.println("[CartPage] Input is protected: " + e.getMessage());
        }
        return this;
    }

    // ===========================
    // DELETE
    // ===========================

    /**
     * CART-DEL-01: Click [cart-item-delete] của item tại index.
     */
    public CartPage clickDeleteAt(int index) {
        System.out.println("[CartPage] Clicking delete button at index: " + index);
        clickElement(btnListDelete.get(index));
        waitForCartToLoad();
        return this;
    }

    /**
     * CART-DEL-02: Xóa tất cả items lần lượt cho đến khi giỏ hàng trống.
     */
    public CartPage deleteAllItems() {
        System.out.println("[CartPage] Deleting all cart items...");
        int maxAttempts = 30;
        int attempt = 0;
        while (getCartItemCount() > 0 && attempt < maxAttempts) {
            attempt++;
            System.out.println("[CartPage] Attempt " + attempt + " - items remaining: " + getCartItemCount());
            clickElement(btnListDelete.get(0));
            waitForCartToLoad();
        }
        return this;
    }

    // ===========================
    // GETTER METHODS (Assertions)
    // ===========================

    /** Số lượng items trong giỏ hàng */
    public int getCartItemCount() {
        try { return btnListDelete.size(); } catch (Exception e) { return 0; }
    }

    /** CART-UPD-01: Lấy giá trị quantity của item tại index */
    public int getQuantityAt(int index) {
        String val = inputListQuantity.get(index).getAttribute("value");
        return val == null || val.isEmpty() ? 0 : Integer.parseInt(val.trim());
    }

    /**
     * CART-UPD-02: Kiểm tra [cart-item-quantity] có attribute disabled không.
     */
    public boolean isQuantityInputDisabled(int index) {
        WebElement input = inputListQuantity.get(index);
        String disabled = input.getAttribute("disabled");
        String readOnly = input.getAttribute("readonly");
        System.out.println("[CartPage] Quantity input[" + index + "] disabled=" + disabled + " readonly=" + readOnly);
        return disabled != null || readOnly != null;
    }

    /** CART-DEL-02: Kiểm tra [cart-empty-msg] hiển thị */
    public boolean isCartEmptyMessageDisplayed() {
        try {
            return wait.until(ExpectedConditions.visibilityOf(lblCartEmptyMsg)).isDisplayed();
        } catch (Exception e) { return false; }
    }

    /** Lấy text [cart-empty-msg] */
    public String getCartEmptyMessage() {
        return wait.until(ExpectedConditions.visibilityOf(lblCartEmptyMsg)).getText().trim();
    }

    /** Lấy tổng tiền giỏ hàng dạng số */
    public long getTotalPriceAsLong() {
        try {
            String raw = lblTotalPrice.getText().replaceAll("[^0-9]", "");
            return raw.isEmpty() ? 0L : Long.parseLong(raw);
        } catch (Exception e) { return 0L; }
    }

    // ===========================
    // INTERNAL
    // ===========================

    private void waitForCartToLoad() {
        try {
            Thread.sleep(800);
        } catch (InterruptedException ignored) {}
    }
}
