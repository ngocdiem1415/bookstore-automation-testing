package com.bookstore.pages;

import com.bookstore.base.BaseSetup;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;


public class ForgotPasswordPage extends BasePage {

    private static final String PAGE_URL = "/forget-password";

    @FindBy(css = "[data-testid='forgot-email']")
    private WebElement txtForgotEmail;

    @FindBy(css = "[data-testid='forgot-submit-btn']")
    private WebElement btnForgotSubmit;

    @FindBy(css = "[data-testid='forgot-error-message']")
    private WebElement lblForgotMessage;

    public ForgotPasswordPage(WebDriver driver,String baseUrl) {
        super(driver, baseUrl);
    }

    /** Mở trang Forgot Password trực tiếp. */
    public ForgotPasswordPage open() {
        driver.get(getCurrentUrl() + PAGE_URL);
        return this;
    }

    public ForgotPasswordPage enterEmail(String email) {
        System.out.println("[ForgotPasswordPage] Entering email: " + email);
        clearAndSendText(txtForgotEmail, email);
        return this;
    }

    public ForgotPasswordPage clickSubmit() {
        System.out.println("[ForgotPasswordPage] Clicking submit button...");
        clickElement(btnForgotSubmit);
        return this;
    }

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

    public String getMessage() {
        System.out.println("[ForgotPasswordPage] Getting response message...");
        return wait.until(ExpectedConditions.visibilityOf(lblForgotMessage)).getText().trim();
    }

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
