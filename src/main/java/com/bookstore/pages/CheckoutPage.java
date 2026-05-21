package com.bookstore.pages;

import com.bookstore.base.BaseSetup;
import com.bookstore.factory.PageFactoryManager;
import org.openqa.selenium.*;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;

/**
 * Page Object: Trang Thanh toán (/checkout).
 * Covers: CHECKOUT-INFO-01/02/03, CHECKOUT-COD-01/02/03, CHK-VNP-01/02/03
 */
public class CheckoutPage extends BasePage {

    private static final String PAGE_URL = "/checkout";

    // ===========================
    // LOCATORS — Shipping Info
    // ===========================

    @FindBy(css = "[data-testid='checkout-name']")
    private WebElement txtName;

    @FindBy(css = "[data-testid='checkout-phone']")
    private WebElement txtPhone;

    @FindBy(css = "[data-testid='checkout-address']")
    private WebElement txtAddress;

    @FindBy(css = "[data-testid='checkout-city']")
    private WebElement selCity;

    @FindBy(css = "[data-testid='checkout-district']")
    private WebElement selDistrict;

    @FindBy(css = "[data-testid='checkout-ward']")
    private WebElement selWard;

    @FindBy(css = "[data-testid='checkout-save-btn']")
    private WebElement btnSave;

    @FindBy(css = "[data-testid='checkout-payment-method']")
    private WebElement selPaymentMethod;

    @FindBy(css = "[data-testid='checkout-buy-btn']")
    private WebElement btnBuy;

    @FindBy(css = "[data-testid='checkout-shipping-fee']")
    private WebElement lblShippingFee;

    // ===========================
    // CONSTRUCTOR
    // ===========================

    public CheckoutPage(WebDriver driver, String baseUrl) {
        super(driver, baseUrl);
    }


    public CheckoutPage open() {
        driver.get(getCurrentUrl() + PAGE_URL);
        return this;
    }

    // ===========================
    // SHIPPING INFO ACTIONS
    // ===========================

    public CheckoutPage enterName(String name) {
        System.out.println("[CheckoutPage] Entering name: " + name);
        clearAndSendText(txtName, name);
        return this;
    }

    public CheckoutPage enterPhone(String phone) {
        System.out.println("[CheckoutPage] Entering phone: " + phone);
        clearAndSendText(txtPhone, phone);
        return this;
    }

    public CheckoutPage enterAddress(String address) {
        System.out.println("[CheckoutPage] Entering address: " + address);
        clearAndSendText(txtAddress, address);
        return this;
    }

    public CheckoutPage selectCity(String cityValue) {
        System.out.println("[CheckoutPage] Selecting city: " + cityValue);
        new Select(wait.until(ExpectedConditions.elementToBeClickable(selCity)))
                .selectByVisibleText(cityValue);
        try { Thread.sleep(600); } catch (InterruptedException ignored) {}
        return this;
    }

    public CheckoutPage selectDistrict(String districtValue) {
        System.out.println("[CheckoutPage] Selecting district: " + districtValue);
        wait.until(ExpectedConditions.elementToBeClickable(selDistrict));
        new Select(selDistrict).selectByVisibleText(districtValue);
        try { Thread.sleep(600); } catch (InterruptedException ignored) {}
        return this;
    }

    public CheckoutPage selectWard(String wardValue) {
        System.out.println("[CheckoutPage] Selecting ward: " + wardValue);
        wait.until(ExpectedConditions.elementToBeClickable(selWard));
        new Select(selWard).selectByVisibleText(wardValue);
        return this;
    }

    /**
     * Shortcut: Điền đầy đủ thông tin giao hàng hợp lệ.
     */
    public CheckoutPage fillShippingInfo(String name, String phone, String address,
                                         String city, String district, String ward) {
        return enterName(name)
                .enterPhone(phone)
                .enterAddress(address)
                .selectCity(city)
                .selectDistrict(district)
                .selectWard(ward);
    }

    /**
     * CHECKOUT-INFO-01: Click [checkout-save-btn] — đọc JS Alert và accept.
     * @return text của JS Alert
     */
    public String clickSaveAndGetAlert() {
        System.out.println("[CheckoutPage] Clicking [checkout-save-btn]...");
        clickElement(btnSave);
        return acceptAlert();
    }

    // ===========================
    // PAYMENT ACTIONS
    // ===========================

    /**
     * Chọn phương thức thanh toán trong [checkout-payment-method].
     * @param visibleText "Thanh toán khi nhận hàng (COD)" | "Chuyển khoản ngân hàng (VNPay)"
     */
    public CheckoutPage selectPaymentMethod(String visibleText) {
        System.out.println("[CheckoutPage] Selecting payment method: " + visibleText);
        wait.until(ExpectedConditions.elementToBeClickable(selPaymentMethod));
        new Select(selPaymentMethod).selectByVisibleText(visibleText);
        return this;
    }

    /**
     * CHECKOUT-COD-01: Click [checkout-buy-btn] → Kỳ vọng thành công → InvoicePage.
     */
    public InvoicePage clickBuyExpectingSuccess() {
        clickElement(btnBuy);
        return  PageFactoryManager.getInvoicePage(driver,baseUrl);
    }

    /**
     * CHECKOUT-COD-02: Click [checkout-buy-btn] → Kỳ vọng thất bại (out-of-stock / error).
     * @return JS Alert text hoặc error message
     */
    public String clickBuyExpectingFailure() {
        System.out.println("[CheckoutPage] Clicking [checkout-buy-btn] (expecting failure)...");
        clickElement(btnBuy);
        return acceptAlert();
    }

    /**
     * CHK-VNP-01: Click [checkout-buy-btn] → Redirect sang VNPay gateway.
     */
    public CheckoutPage clickBuyToVnpay() {
        System.out.println("[CheckoutPage] Clicking [checkout-buy-btn] (expecting VNPAY redirect)...");
        clickElement(btnBuy);
        return this;
    }

    // ===========================
    // GETTER METHODS (Assertions)
    // ===========================

    /**
     * Kiểm tra [checkout-buy-btn] có available (enabled) không.
     * CHECKOUT-INFO-01: "becomes available" sau khi save thành công.
     */
    public boolean isBuyButtonEnabled() {
        try {
            String disabled = btnBuy.getAttribute("disabled");
            String classes  = btnBuy.getAttribute("class");
            return disabled == null && (classes == null || !classes.contains("disabled"));
        } catch (Exception e) { return false; }
    }

    /** Lấy text phí giao hàng */
    public String getShippingFeeText() {
        try {
            return wait.until(ExpectedConditions.visibilityOf(lblShippingFee)).getText().trim();
        } catch (Exception e) { return ""; }
    }

    /** Kiểm tra phí giao hàng đã hiển thị (số > 0) */
    public boolean isShippingFeeDisplayed() {
        String fee = getShippingFeeText().replaceAll("[^0-9]", "");
        return !fee.isEmpty() && Long.parseLong(fee) > 0;
    }

    /** Kiểm tra đang ở trang /checkout */
    public boolean isOnCheckoutPage() {
        return driver.getCurrentUrl().contains("/checkout");
    }

    /** Kiểm tra đã bị redirect sang VNPAY sandbox */
    public boolean isRedirectedToVnpay() {
        try {
            wait.until(ExpectedConditions.urlContains("sandbox.vnpayment.vn"));
            return driver.getCurrentUrl().contains("sandbox.vnpayment.vn")
                    || driver.getCurrentUrl().contains("vnpayment");
        } catch (Exception e) { return false; }
    }

    // ===========================
    // INTERNAL
    // ===========================

    private String acceptAlert() {
        try {
            Alert alert = wait.until(ExpectedConditions.alertIsPresent());
            String text = alert.getText().trim();
            System.out.println("[CheckoutPage] Alert text: " + text);
            alert.accept();
            return text;
        } catch (Exception e) {
            System.out.println("[CheckoutPage] No alert found: " + e.getMessage());
            return "";
        }
    }
}
