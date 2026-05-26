package com.bookstore.pages;

import com.bookstore.base.BaseSetup;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

/**
 * Page Object: Trang Đăng nhập (/login)
 * Covers: AUTH-LOG-01, AUTH-LOG-02, AUTH-LOG-03, AUTH-LOG-04, AUTH-LOG-05
 */
public class LoginPage extends BasePage{
    private final String PAGE_URL = "/login";

    @FindBy(css = "[data-testid='login-username']")
    private WebElement txtUsername;

    @FindBy(css = "[data-testid='login-password']")
    private WebElement txtPassword;

    @FindBy(css = "[data-testid='login-submit-btn']")
    private WebElement btnLoginSubmit;

    @FindBy(css = "[data-testid='login-error-message']")
    private WebElement lblErrorMessage;

    @FindBy(css = "[data-testid='link-to-signup']")
    private WebElement lnkToSignup;

    @FindBy(css = "[data-testid='link-to-forgot-password']")
    private WebElement lnkToForgotPassword;

    public LoginPage(WebDriver driver, String baseUrl) {
        super(driver, baseUrl);
    }

    public LoginPage open() {
        driver.get(baseUrl + PAGE_URL);
        return this;
    }

    public LoginPage enterUsername(String username) {
        clearAndSendText(txtUsername, username);
        return this;
    }

    public LoginPage enterPassword(String password) {
        clearAndSendText(txtPassword, password);
        return this;
    }

    public LoginPage clickLoginExpectingFailure() {
        clickElement(btnLoginSubmit);
        return this;
    }

    public HomePage clickLoginAsCustomer() {
        clickElement(btnLoginSubmit);
        return new HomePage(driver, baseUrl);
    }

    public AdminDashboardPage clickLoginAsAdmin() {
        clickElement(btnLoginSubmit);
        return new AdminDashboardPage(driver, baseUrl);
    }


    public HomePage loginAsCustomer(String username, String password) {
        return enterUsername(username)
                .enterPassword(password)
                .clickLoginAsCustomer();
    }


    public AdminDashboardPage loginAsAdmin(String username, String password) {
        return enterUsername(username)
                .enterPassword(password)
                .clickLoginAsAdmin();
    }

    public SignupPage clickLinkToSignup() {
        clickElement(lnkToSignup);
        return new SignupPage(driver, baseUrl);
    }

    public ForgotPasswordPage clickLinkToForgotPassword() {
        clickElement(lnkToForgotPassword);
        return new ForgotPasswordPage(driver, baseUrl);
    }

    public String getErrorMessage() {
        return wait.until(ExpectedConditions.visibilityOf(lblErrorMessage)).getText().trim();
    }

    public boolean isOnLoginPage() {
        return driver.getCurrentUrl().contains("/login");
    }

    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }
}
