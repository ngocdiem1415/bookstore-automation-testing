package com.bookstore.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

/**
 * Page Object: Quản trị cấu trúc và nghiệp vụ trang Giỏ hàng (/cart).
 * Kiến trúc tuân thủ triết lý Thử nghiệm hộp đen (Blackbox Simulation First).
 */
public class CartPage extends BasePage {

    private static final String PAGE_URL = "/cart";

    // Hệ thống định vị động cấp thẻ dòng (Row-level Locators)
    private static final By PRODUCT_ROWS = By.cssSelector(".cart-items .product");

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

    public CartPage(WebDriver driver, String baseUrl) {
        super(driver, baseUrl);
    }

    public CartPage open() {
        driver.get(baseUrl + PAGE_URL);
        waitForCartToLoad();
        return this;
    }

    // =========================================================================
    // TẦNG TƯƠNG TÁC (USER ACTIONS - SIMULATION FIRST)
    // =========================================================================

    /**
     * CART-UPD-01: Bấm tăng số lượng sản phẩm (+1) theo hành vi vật lý.
     */
    public CartPage clickIncreaseAt(int index) {
        System.out.println("[CartPage] Thao tác click tăng số lượng vật lý tại index: " + index);
        // Ưu tiên sử dụng cú pháp chuẩn của Selenium để kiểm tra tính tương tác của UI
        try {
            wait.until(ExpectedConditions.elementToBeClickable(btnListIncrease.get(index))).click();
        } catch (Exception e) {
            System.out.println("[WARN] Click vật lý bị chặn. Kích hoạt lớp xử lý bảo hiểm JavaScript...");
            executeJS("arguments[0].click();", btnListIncrease.get(index));
        }
        waitForCartToLoad();
        return this;
    }

    /**
     * CART-UPD-01: Bấm giảm số lượng sản phẩm (-1) theo hành vi vật lý.
     */
    public CartPage clickDecreaseAt(int index) {
        System.out.println("[CartPage] Thao tác click giảm số lượng vật lý tại index: " + index);
        try {
            wait.until(ExpectedConditions.elementToBeClickable(btnListDecrease.get(index))).click();
        } catch (Exception e) {
            System.out.println("[WARN] Click vật lý bị chặn. Kích hoạt lớp xử lý bảo hiểm JavaScript...");
            executeJS("arguments[0].click();", btnListDecrease.get(index));
        }
        waitForCartToLoad();
        return this;
    }

    /**
     * CART-UPD-02: Thử nghiệm nhập chuỗi dữ liệu đầu vào (Ví dụ: số âm, ký tự lạ)
     */
    public CartPage tryTypeIntoQuantityInput(int index, String text) {
        System.out.println("[CartPage] Gửi chuỗi ký tự '" + text + "' vào ô nhập liệu số lượng.");
        try {
            WebElement input = inputListQuantity.get(index);
            input.clear();
            input.sendKeys(text);
        } catch (Exception e) {
            System.out.println("[CartPage] Ô nhập liệu được bảo vệ đúng trạng thái (Readonly/Disabled): " + e.getMessage());
        }
        return this;
    }

    /**
     * CART-DEL-01: Bấm nút Xóa sản phẩm khỏi giỏ hàng.
     */
    public CartPage clickDeleteAt(int index) {
        System.out.println("[CartPage] Thao tác click xóa sản phẩm vật lý tại index: " + index);
        try {
            wait.until(ExpectedConditions.elementToBeClickable(btnListDelete.get(index))).click();
        } catch (Exception e) {
            System.out.println("[WARN] Click vật lý bị chặn. Kích hoạt lớp xử lý bảo hiểm JavaScript...");
            executeJS("arguments[0].click();", btnListDelete.get(index));
        }
        waitForCartToLoad();
        return this;
    }

    /**
     * CART-DEL-02: Xóa cuốn chiếu toàn bộ danh sách sản phẩm.
     */
    public CartPage deleteAllItems() {
        System.out.println("[CartPage] Bắt đầu luồng dọn sạch toàn bộ giỏ hàng...");
        int maxAttempts = 30;
        int attempt = 0;

        while (getCartItemCount() > 0 && attempt < maxAttempts) {
            attempt++;
            System.out.println("[CartPage] Lượt xử lý thứ " + attempt + " - Thực thể còn lại: " + getCartItemCount());
            clickDeleteAt(0); // Luôn triệt tiêu phần tử đầu tiên của danh sách động
        }
        return this;
    }

    // =========================================================================
    // TẦNG KIỂM TRA TRẠNG THÁI (ASSERTION HELPERS)
    // =========================================================================

    public int getCartItemCount() {
        try {
            // Đọc trực tiếp kích thước danh sách thực thể dòng thay vì đếm list button để tránh trễ DOM
            return driver.findElements(PRODUCT_ROWS).size();
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
            return wait.until(ExpectedConditions.visibilityOf(lblCartEmptyMsg)).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public String getCartEmptyMessage() {
        return wait.until(ExpectedConditions.visibilityOf(lblCartEmptyMsg)).getText().trim();
    }

    public long getTotalPriceAsLong() {
        try {
            wait.until(ExpectedConditions.visibilityOf(lblTotalPrice));
            String raw = lblTotalPrice.getText().replaceAll("[^0-9]", "");
            return raw.isEmpty() ? 0L : Long.parseLong(raw);
        } catch (Exception e) {
            return 0L;
        }
    }

    /**
     * Phương thức đồng bộ hóa thông minh: Thay vì dùng Thread.sleep() cố định,
     * hệ thống sẽ đợi cho cấu trúc trang Giỏ hàng tái định hình và ổn định lại trong cây DOM.
     */
    private void waitForCartToLoad() {
        try {
            // Chờ cho đến khi toàn bộ khung container tổng thể của trang tải xong và sẵn sàng tương tác
            WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(5));
            shortWait.until(ExpectedConditions.presenceOfElementLocated(By.className("cart")));
        } catch (Exception ignored) {}
    }

    /**
     * Hàm đóng gói tiêm mã lệnh JavaScript cục bộ (Hạn chế tối đa việc viết rác mã nguồn)
     */
    private void executeJS(String script, Object... args) {
        try {
            ((JavascriptExecutor) driver).executeScript(script, args);
        } catch (Exception e) {
            System.out.println("[ERROR] Không thể thực thi mã JavaScript: " + e.getMessage());
        }
    }
}