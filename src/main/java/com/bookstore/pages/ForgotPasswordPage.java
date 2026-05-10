package com.bookstore.pages;

import com.bookstore.base.BaseSetup;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

/**
 * Page Object: Trang Quên mật khẩu (/forget-password).
 * Covers: AUTH-FGP-01, AUTH-FGP-02, AUTH-FGP-03
 */
public class ForgotPasswordPage extends BasePage {

    private static final String PAGE_URL = "/forget-password";

    // ===========================
    // LOCATORS (data-testid)
    // ===========================

    @FindBy(css = "[data-testid='forgot-email']")
    private WebElement txtForgotEmail;

    @FindBy(css = "[data-testid='forgot-submit-btn']")
    private WebElement btnForgotSubmit;

    @FindBy(css = "[data-testid='forgot-error-message']")
    private WebElement lblForgotMessage;

    // ===========================
    // CONSTRUCTOR
    // ===========================

    public ForgotPasswordPage(WebDriver driver) {
        super(driver);
    }

    // ===========================
    // NAVIGATION
    // ===========================

    /** Mở trang Forgot Password trực tiếp. */
    public ForgotPasswordPage open() {
        driver.get(getCurrentUrl() + PAGE_URL);
        return this;
    }

    // ===========================
    // ACTION METHODS
    // ===========================

    /** Step: Input [forgot-email] */
    public ForgotPasswordPage enterEmail(String email) {
        System.out.println("[ForgotPasswordPage] Entering email: " + email);
        clearAndSendText(txtForgotEmail, email);
        return this;
    }

    /**
     * Step: Click [forgot-submit-btn].
     * Trả về chính page này (vì kết quả hiển thị ngay trên cùng trang).
     */
    public ForgotPasswordPage clickSubmit() {
        System.out.println("[ForgotPasswordPage] Clicking submit button...");
        clickElement(btnForgotSubmit);
        return this;
    }

    /**
     * AUTH-FGP-03: Click nhanh nút submit nhiều lần để kiểm tra rate limiting.
     * @param times Số lần click.
     */
    public ForgotPasswordPage clickSubmitMultipleTimes(int times) {
        System.out.println("[ForgotPasswordPage] Clicking submit button " + times + " times rapidly...");
        for (int i = 1; i <= times; i++) {
            try {
                System.out.println("[ForgotPasswordPage] Click #" + i);
                btnForgotSubmit.click();
                Thread.sleep(300); // Delay nhỏ giữa các lần click
            } catch (Exception e) {
                System.out.println("[ForgotPasswordPage] Click #" + i + " failed: " + e.getMessage());
            }
        }
        return this;
    }

    // ===========================
    // GETTER METHODS (Assertions)
    // ===========================

    /** Lấy text của [forgot-error-message] (dùng cho cả success & error message). */
    public String getMessage() {
        System.out.println("[ForgotPasswordPage] Getting response message...");
        return wait.until(ExpectedConditions.visibilityOf(lblForgotMessage)).getText().trim();
    }

    /**
     * AUTH-FGP-03: Kiểm tra button submit bị disabled sau khi click nhiều lần.
     * @return true nếu button bị disabled.
     */
    public boolean isSubmitButtonDisabled() {
        System.out.println("[ForgotPasswordPage] Checking if submit button is disabled...");
        try {
            // Kiểm tra attribute disabled
            String disabled = btnForgotSubmit.getAttribute("disabled");
            if (disabled != null) return true;
            // Kiểm tra class disabled
            String classes = btnForgotSubmit.getAttribute("class");
            return classes != null && classes.contains("disabled");
        } catch (Exception e) {
            return false;
        }
    }
}
