package com.bookstore.pages;

import com.bookstore.base.BaseSetup;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;

/**
 * Page Object: Trang Đăng ký (/signup)
 * Covers: AUTH-REG-01, AUTH-REG-02, AUTH-REG-03
 */
public class SignupPage extends BasePage {
    private static final String PAGE_URL = "signup";

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

    public SignupPage(WebDriver driver) {
        super(driver);
    }

    public SignupPage open() {
        driver.get(getCurrentUrl() + PAGE_URL);
        return this;
    }

    public SignupPage enterUsername(String username) {
        clearAndSendText(txtRegisterUsername, username);
        return this;
    }

    public SignupPage enterPassword(String password) {
        clearAndSendText(txtRegisterPassword, password);
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

    /** format: dd/MM/yyyy */
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
        clickElement(btnRegisterSubmit);
    }

    public SignupPage clickSubmitExpectingFailure() {
        System.out.println("[SignupPage] Clicking register submit (expecting failure)...");
        clickElement(btnRegisterSubmit);
        return this;
    }

    public String getErrorMessage() {
        System.out.println("[SignupPage] Getting error/success message...");
        return wait.until(ExpectedConditions.visibilityOf(lblRegisterErrorMessage)).getText().trim();
    }

    public boolean isSubmitButtonEnabled() {
        return btnRegisterSubmit.isEnabled();
    }

    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }

    public boolean isOnEmailVerifyPage() {
        return driver.getCurrentUrl().contains("email-verify");
    }

    public boolean isOnSignupPage() {
        return driver.getCurrentUrl().contains("/signup");
    }

    /**
     * Lấy validation message từ HTML5 browser (constraint validation API).
     * Dùng JS để lấy validationMessage của input password.
     */
    public String getPasswordValidationMessage() {
        return (String) ((org.openqa.selenium.JavascriptExecutor) driver)
                .executeScript("return arguments[0].validationMessage;", txtRegisterPassword);
    }
}