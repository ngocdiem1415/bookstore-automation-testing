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

    // ===========================
    // LOCATORS (data-testid)
    // ===========================

    @FindBy(css = "[data-testid='reset-new-password']")
    private WebElement txtResetNewPassword;

    @FindBy(css = "[data-testid='reset-confirm-password']")
    private WebElement txtResetConfirmPassword;

    @FindBy(css = "[data-testid='reset-submit-btn']")
    private WebElement btnResetSubmit;

    @FindBy(css = "[data-testid='reset-error-message']")
    private WebElement lblResetErrorMessage;

    // ===========================
    // CONSTRUCTOR
    // ===========================

    public ResetPasswordPage(WebDriver driver) {
        super(driver);
    }

    // ===========================
    // NAVIGATION
    // ===========================

    /**
     * Mở trang Reset Password với token cụ thể.
     * @param token Mã token lấy từ email (hợp lệ, hết hạn, v.v.)
     */
    public ResetPasswordPage openWithToken(String token) {
        String url = getCurrentUrl() + PAGE_URL + "?code=" + token;
        System.out.println("[ResetPasswordPage] Navigating to: " + url);
        driver.get(url);
        return this;
    }

    /**
     * AUTH-RST-03: Mở trang với token đã hết hạn (Boundary).
     * @param expiredToken Token đã hết hạn.
     */
    public ResetPasswordPage openWithExpiredToken(String expiredToken) {
        System.out.println("[ResetPasswordPage] Opening with EXPIRED token: " + expiredToken);
        return openWithToken(expiredToken);
    }

    // ===========================
    // ACTION METHODS
    // ===========================

    /** Step: Input [reset-new-password] */
    public ResetPasswordPage enterNewPassword(String password) {
        System.out.println("[ResetPasswordPage] Entering new password...");
        clearAndSendText(txtResetNewPassword, password);
        return this;
    }

    /** Step: Input [reset-confirm-password] */
    public ResetPasswordPage enterConfirmPassword(String confirmPassword) {
        System.out.println("[ResetPasswordPage] Entering confirm password...");
        clearAndSendText(txtResetConfirmPassword, confirmPassword);
        return this;
    }

    /**
     * Step: Click [reset-submit-btn] → Kỳ vọng thành công → Redirect đến /login.
     * AUTH-RST-01.
     */
    public LoginPage clickSubmitExpectingSuccess() {
        System.out.println("[ResetPasswordPage] Clicking reset submit (expecting success → /login)...");
        clickElement(btnResetSubmit);
        return new LoginPage(driver);
    }

    /**
     * Step: Click [reset-submit-btn] → Kỳ vọng thất bại, ở lại trang.
     * AUTH-RST-02, AUTH-RST-03.
     */
    public ResetPasswordPage clickSubmitExpectingFailure() {
        System.out.println("[ResetPasswordPage] Clicking reset submit (expecting failure)...");
        clickElement(btnResetSubmit);
        return this;
    }

    // ===========================
    // GETTER METHODS (Assertions)
    // ===========================

    /** Lấy text của [reset-error-message] */
    public String getErrorMessage() {
        System.out.println("[ResetPasswordPage] Getting error message...");
        return wait.until(ExpectedConditions.visibilityOf(lblResetErrorMessage)).getText().trim();
    }

    /**
     * AUTH-RST-03: Kiểm tra thông báo lỗi token hết hạn hiển thị ngay khi mở trang.
     * (Có thể là lỗi server trả về, không cần click submit)
     */
    public boolean isExpiredTokenErrorDisplayed() {
        try {
            String msg = getErrorMessage();
            return msg.contains("hết hạn") || msg.contains("không hợp lệ");
        } catch (Exception e) {
            return false;
        }
    }

    /** Kiểm tra có đang ở trang /login sau khi reset thành công. */
    public boolean isRedirectedToLogin() {
        return waitForUrlContains("/login");
    }
}
