package com.bookstore.pages;

import com.bookstore.factory.PageFactoryManager;
import com.bookstore.utils.LoggerHelper;
import org.openqa.selenium.*;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static java.lang.Thread.sleep;

public class CheckoutPage extends BasePage {

    private static final String PAGE_URL = "/checkout";

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

    public CheckoutPage(WebDriver driver, String baseUrl) {
        super(driver, baseUrl);
    }

    public CheckoutPage open() {
        driver.get(baseUrl + PAGE_URL);
        return this;
    }

    public CheckoutPage enterName(String name) {
        LoggerHelper.info("[CHECKOUT_PAGE] Nhập họ tên: " + name);
        clearAndSendText(txtName, name);
        return this;
    }

    public CheckoutPage enterPhone(String phone) {
        System.out.println("[CheckoutPage] Entering phone: " + phone);
        clearAndSendText(txtPhone, phone);
        return this;
    }

    public CheckoutPage enterAddress(String address) {
        LoggerHelper.info("[CHECKOUT_PAGE] Nhập địa chỉ giao hàng");
        clearAndSendText(txtAddress, address);
        return this;
    }

    public CheckoutPage selectCity(String cityValue) {
        LoggerHelper.info("[CHECKOUT_PAGE] Chọn tỉnh/thành phố: " + cityValue);
        new Select(waitUntilClickable(selCity))
                .selectByVisibleText(cityValue);
        try {
            sleep(600);
        } catch (InterruptedException ignored) {
        }
        return this;
    }

    public CheckoutPage selectDistrict(String districtValue) {
        LoggerHelper.info("[CHECKOUT_PAGE] Chọn quận/huyện: " + districtValue);
        clickElement(selDistrict);
        new Select(selDistrict).selectByVisibleText(districtValue);
        try {
            sleep(600);
        } catch (InterruptedException ignored) {
        }
        return this;
    }

    public CheckoutPage selectWard(String wardValue) {
        LoggerHelper.info("[CHECKOUT_PAGE] Chọn phường/xã: " + wardValue);
        clickElement(selWard);
        new Select(selWard).selectByVisibleText(wardValue);
        return this;
    }

    public CheckoutPage fillShippingInfo(String name, String phone, String address,
                                         String city, String district, String ward) {
        return enterName(name)
                .enterPhone(phone)
                .enterAddress(address)
                .selectCity(city)
                .selectDistrict(district)
                .selectWard(ward);
    }

    public String clickSaveAndGetAlert() {
        LoggerHelper.info("[CHECKOUT_PAGE] Click nút lưu thông tin giao hàng");
        clickElement(btnSave);
        return acceptAlert();
    }

    public CheckoutPage selectPaymentMethod(String visibleText) {
        LoggerHelper.info("[CHECKOUT_PAGE] Chọn phương thức thanh toán: " + visibleText);
        scrollToElement(selPaymentMethod);
        jsClick(selPaymentMethod);
        new Select(selPaymentMethod).selectByVisibleText(visibleText);
        return this;
    }

    public InvoicePage clickBuyExpectingSuccess() throws InterruptedException {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        // scroll xuống nút
        scrollToElement(btnBuy);
        wait.until(ExpectedConditions.elementToBeClickable(btnBuy));
        sleep(500);
        try {
            btnBuy.click();
        } catch (Exception e) {
            jsClick(btnBuy);
        }
        return PageFactoryManager.getInvoicePage(driver, baseUrl);
    }

    public InvoicePage clickBuyExpectingFailure() throws InterruptedException {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        // scroll xuống nút
        scrollToElement(btnBuy);
        clickElement(btnBuy);
        sleep(500);
        try {
            btnBuy.click();
        } catch (Exception e) {
            jsClick(btnBuy);
        }
        return PageFactoryManager.getInvoicePage(driver, baseUrl);
    }


    public boolean isBuyButtonEnabled() {
        try {
            String disabled = btnBuy.getAttribute("disabled");
            return disabled == null;
        } catch (Exception e) {
            return false;
        }
    }

    public String getShippingFeeText() {
        try {
            return getTextOf(lblShippingFee);
        } catch (Exception e) {
            return "";
        }
    }

    public boolean isShippingFeeDisplayed() {
        String fee = getShippingFeeText().replaceAll("[^0-9]", "");
        return !fee.isEmpty() && Long.parseLong(fee) > 0;
    }

    public String acceptAlert() {
        try {
            Alert alert = wait.until(ExpectedConditions.alertIsPresent());
            String text = alert.getText().trim();
            LoggerHelper.info("[CHECKOUT_PAGE] Alert text: " + text);
            alert.accept();
            return text;
        } catch (Exception e) {
            LoggerHelper.warn("[CHECKOUT_PAGE] Không tìm thấy alert: " + e.getMessage());
            return "";
        }
    }

    public String getAlertText() {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
            Alert alert = wait.until(ExpectedConditions.alertIsPresent());
            String alertText = alert.getText().trim();
            alert.accept();
            return alertText;
        } catch (Exception e) {
            return "";
        }
    }
}
