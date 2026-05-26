package com.bookstore.pages;

import com.bookstore.base.BaseSetup;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

/**
 * Page Object: Trang Đặt lại mật khẩu (/reset-password?code=...).
 * Covers: AUTH-RST-01, AUTH-RST-02, AUTH-RST-03
 */
public class ResetPasswordPage extends BasePage {
    private static final String PAGE_URL = "/reset-password";

    @FindBy(css = "[data-testid='reset-new-password']")
    private WebElement txtResetNewPassword;

    @FindBy(css = "[data-testid='reset-confirm-password']")
    private WebElement txtResetConfirmPassword;

    @FindBy(css = "[data-testid='reset-submit-btn']")
    private WebElement btnResetSubmit;

    @FindBy(css = "[data-testid='reset-error-message']")
    private WebElement lblResetErrorMessage;

    public ResetPasswordPage(WebDriver driver,String baseUrl) {
        super(driver, baseUrl);
    }

    public ResetPasswordPage openWithToken(String token) {
        String url = getCurrentUrl() + PAGE_URL + "?code=" + token;
        driver.get(url);
        return this;
    }

    public ResetPasswordPage openWithExpiredToken(String expiredToken) {
        return openWithToken(expiredToken);
    }

    public ResetPasswordPage enterNewPassword(String password) {
        clearAndSendText(txtResetNewPassword, password);
        return this;
    }

    public ResetPasswordPage enterConfirmPassword(String confirmPassword) {
        clearAndSendText(txtResetConfirmPassword, confirmPassword);
        return this;
    }

    public LoginPage clickSubmitExpectingSuccess() {
        clickElement(btnResetSubmit);
        return new LoginPage(driver, baseUrl);
    }

    public ResetPasswordPage clickSubmitExpectingFailure() {
        clickElement(btnResetSubmit);
        return this;
    }

    public String getErrorMessage() {
        return wait.until(ExpectedConditions.visibilityOf(lblResetErrorMessage)).getText().trim();
    }

    public boolean isExpiredTokenErrorDisplayed() {
        try {
            String msg = getErrorMessage();
            return msg.contains("hết hạn") || msg.contains("không hợp lệ");
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isRedirectedToLogin() {
        return waitForUrlContains("/login");
    }
}
