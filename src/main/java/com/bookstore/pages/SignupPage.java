package com.bookstore.pages;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

/**
 * Page Object: Trang Đăng ký (/signup)
 * Covers: AUTH-REG-01, AUTH-REG-02, AUTH-REG-03
 */
public class SignupPage extends BasePage {
    private static final String PAGE_URL = "/signup";

    @FindBy(css = "[data-testid='register-username']")
    private WebElement txtRegisterUsername;

    @FindBy(css = "[data-testid='register-password']")
    private WebElement txtRegisterPassword;

    @FindBy(css = "[data-testid='register-email']")
    private WebElement txtRegisterEmail;

    @FindBy(css = "[data-testid='register-gender']")
    private WebElement selRegisterGender;

    @FindBy(css = "[data-testid='register-birthdate']")
    private WebElement txtRegisterBirthdate;

    @FindBy(css = "[data-testid='register-phone']")
    private WebElement txtRegisterPhone;

    @FindBy(css = "[data-testid='register-submit-btn']")
    private WebElement btnRegisterSubmit;

    @FindBy(css = "[data-testid='register-error-message']")
    private WebElement lblRegisterErrorMessage;

    @FindBy(css = "[data-testid='register-pass-warning']")
    private WebElement pRegisterCheckPwdMessage;

    @FindBy(css = "[data-testid='verify-code-input']")
    private WebElement txtVerifyCode;

    @FindBy(css = "[data-testid='verify-submit-btn']")
    private WebElement btnVerify;

    public SignupPage(WebDriver driver, String baseUrl) {
        super(driver, baseUrl);
    }

    public SignupPage open() {
        driver.get(baseUrl + PAGE_URL);
        return this;
    }

    public SignupPage enterUsername(String username) {
        clearAndSendText(txtRegisterUsername, username);
        return this;
    }

    public SignupPage enterPassword(String password) {
        txtRegisterPassword.clear();
        txtRegisterPassword.click();
        txtRegisterPassword.sendKeys(password);
        txtRegisterPassword.sendKeys(Keys.TAB);
        return this;
    }

    public SignupPage enterEmail(String email) {
        clearAndSendText(txtRegisterEmail, email);
        return this;
    }

    public SignupPage selectGender(String genderValue) {
        Select select = new Select(wait.until(ExpectedConditions.elementToBeClickable(selRegisterGender)));
        select.selectByValue(genderValue);
        return this;
    }

    /**
     * format: dd/MM/yyyy
     */
    public SignupPage enterBirthdate(String birthdate) {
        clearAndSendText(txtRegisterBirthdate, birthdate);
        return this;
    }

    public SignupPage enterPhone(String phone) {
        clearAndSendText(txtRegisterPhone, phone);
        return this;
    }

    public SignupPage fillRegistrationForm(String username, String password, String email,
                                           String gender, String birthdate, String phone) {
        return enterUsername(username)
                .enterPassword(password)
                .enterEmail(email)
                .selectGender(gender)
                .enterBirthdate(birthdate)
                .enterPhone(phone);
    }

    public void clickSubmitExpectingSuccess() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.elementToBeClickable(btnRegisterSubmit));
        btnRegisterSubmit.click();
    }

    public SignupPage clickSubmitExpectingFailure() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", btnRegisterSubmit);
        try { Thread.sleep(500); } catch (InterruptedException e) {}
        if (!btnRegisterSubmit.isEnabled()) {
            System.out.println("[DEBUG] Nút đang bị khóa, dùng JS Click để ép gửi form...");
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btnRegisterSubmit);
        } else {
            btnRegisterSubmit.click();
        }
        return this;
    }

    public String getErrorMessage() {
        return wait.until(ExpectedConditions.visibilityOf(lblRegisterErrorMessage)).getText().trim();
    }

    public String getCheckPwdWarning() {
        return wait.until(ExpectedConditions.visibilityOf(pRegisterCheckPwdMessage)).getText().trim();
    }

    public boolean isSubmitButtonEnabled() {
        return btnRegisterSubmit.isDisplayed() && btnRegisterSubmit.isEnabled();    }

    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }

    public boolean isOnEmailVerifyPage() {
        try {
            return wait.until(ExpectedConditions.visibilityOf(txtVerifyCode)).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isRedirectedToSuccess() {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            return wait.until(ExpectedConditions.urlContains("/success"));
        } catch (Exception e) {
            return false;
        }
    }


    public void enterVerifyCode(String code) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement input = wait.until(ExpectedConditions.elementToBeClickable(txtVerifyCode));
        input.clear();
        input.click();
        input.sendKeys(code);
    }

    public void clickVerify() {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("arguments[0].click();", btnVerify);
    }

    public boolean isOnSignupPage() {
        return driver.getCurrentUrl().contains("/signup");
    }

    public String getPasswordValidationMessage() {
        return (String) ((JavascriptExecutor) driver)
                .executeScript("return arguments[0].validationMessage;", txtRegisterPassword);
    }

}