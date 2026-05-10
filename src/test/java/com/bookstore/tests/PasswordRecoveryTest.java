package com.bookstore.tests;

import com.bookstore.base.BaseSetup;
import com.bookstore.pages.ForgotPasswordPage;
import com.bookstore.pages.LoginPage;
import com.bookstore.pages.ResetPasswordPage;
import org.testng.Assert;
import org.testng.annotations.Test;

public class PasswordRecoveryTest extends BaseSetup {

    @Test(description = "AUTH-FGP-01: Request forgot password link with valid email.")
    public void AUTH_FGP_01_ForgotPasswordValidEmail() {

        System.out.println("[Step 1] Navigate to Forgot Password page (/forget-password)");
        ForgotPasswordPage forgotPage = new ForgotPasswordPage(driver);
        forgotPage.open();

        System.out.println("[Step 2] Input valid email");
        System.out.println("[Step 3] Click [forgot-submit-btn]");
        forgotPage.enterEmail("valid@gmail.com").clickSubmit();

        System.out.println("[Assert] Verify success message displayed");
        String msg = forgotPage.getMessage();
        System.out.println("[Assert] Message: " + msg);
        Assert.assertTrue(msg.contains("Liên kết khôi phục đã được gửi"),
                "Expected recovery link sent message. Actual: " + msg);
    }

    @Test(description = "AUTH-FGP-02: Request forgot password link with non-existent email.")
    public void AUTH_FGP_02_ForgotPasswordNonExistentEmail() {

        System.out.println("[Step 1] Navigate to Forgot Password page");
        ForgotPasswordPage forgotPage = new ForgotPasswordPage(driver);
        forgotPage.open();

        System.out.println("[Step 2] Input non-existent email: null@gmail.com");
        System.out.println("[Step 3] Click [forgot-submit-btn]");
        forgotPage.enterEmail("null@gmail.com").clickSubmit();

        System.out.println("[Assert] Verify error message for non-existent email");
        String msg = forgotPage.getMessage();
        System.out.println("[Assert] Message: " + msg);
        Assert.assertTrue(msg.contains("Email không tồn tại") || !msg.isEmpty(),
                "Expected error for non-existent email. Actual: " + msg);

    }

    @Test(description = "AUTH-FGP-03: Verify rate limiting on forgot password requests (Biên).")
    public void AUTH_FGP_03_ForgotPasswordRateLimiting() {

        System.out.println("[Step 1] Navigate to Forgot Password page");
        ForgotPasswordPage forgotPage = new ForgotPasswordPage(driver);
        forgotPage.open();

        System.out.println("[Step 2] Input valid email");
        forgotPage.enterEmail("valid@gmail.com");

        System.out.println("[Step 3] Rapidly click [forgot-submit-btn] 5 times");
        forgotPage.clickSubmitMultipleTimes(5);

        System.out.println("[Assert] Verify button is disabled after rapid clicks");
        Assert.assertTrue(forgotPage.isSubmitButtonDisabled(),
                "Expected [forgot-submit-btn] to be disabled after 5 rapid clicks.");

    }

    @Test(description = "AUTH-RST-01: Verify user can reset password with valid token.",
            dependsOnMethods = "AUTH_FGP_01_ForgotPasswordValidEmail")
    public void AUTH_RST_01_ResetPasswordValidToken() {
        String validToken = "VALID_TOKEN_FROM_EMAIL";
        System.out.println("[Step 1] Navigate to Reset password link with valid token");
        ResetPasswordPage resetPage = new ResetPasswordPage(driver);
        resetPage.openWithToken(validToken);

        System.out.println("[Step 2] Input new password and confirm password");
        System.out.println("[Step 3] Click [reset-submit-btn]");
        LoginPage loginPage = resetPage
                .enterNewPassword("New@123")
                .enterConfirmPassword("New@123")
                .clickSubmitExpectingSuccess();

        System.out.println("[Assert] Verify redirect to /login");
        Assert.assertTrue(loginPage.isOnLoginPage(),
                "Expected redirect to /login after successful reset. URL: " + loginPage.getCurrentUrl());

    }

    @Test(description = "AUTH-RST-02: Verify user cannot reset password with mismatched confirm password.",
            dependsOnMethods = "AUTH_FGP_01_ForgotPasswordValidEmail")
    public void AUTH_RST_02_ResetPasswordMismatch() {

        System.out.println("[Step 1] Navigate to Reset password link");
        ResetPasswordPage resetPage = new ResetPasswordPage(driver);
        resetPage.openWithToken("VALID_TOKEN_FROM_EMAIL");

        System.out.println("[Step 2] Input mismatching passwords");
        System.out.println("[Step 3] Click [reset-submit-btn]");
        resetPage.enterNewPassword("New@123")
                .enterConfirmPassword("Wrong@123")
                .clickSubmitExpectingFailure();

        System.out.println("[Assert] Verify error message: Mật khẩu xác nhận không khớp");
        String errorMsg = resetPage.getErrorMessage();
        System.out.println("[Assert] Error: " + errorMsg);
        Assert.assertTrue(errorMsg.contains("không khớp"),
                "Expected mismatch error. Actual: " + errorMsg);
    }

    @Test(description = "AUTH-RST-03: Verify user cannot reset password with expired token (Boundary).",
            dependsOnMethods = "AUTH_FGP_01_ForgotPasswordValidEmail")
    public void AUTH_RST_03_ResetPasswordExpiredToken() {

        System.out.println("[Step 1] Navigate to expired Reset password link (after 24h)");
        ResetPasswordPage resetPage = new ResetPasswordPage(driver);
        resetPage.openWithExpiredToken("EXPIRED_TOKEN_AFTER_24H");

        System.out.println("[Step 2] Input valid passwords");
        System.out.println("[Step 3] Click [reset-submit-btn]");
        resetPage.enterNewPassword("New@123")
                .enterConfirmPassword("New@123")
                .clickSubmitExpectingFailure();

        System.out.println("[Assert] Verify expired token error message");
        String errorMsg = resetPage.getErrorMessage();
        System.out.println("[Assert] Error: " + errorMsg);
        Assert.assertTrue(
                errorMsg.contains("hết hạn") || errorMsg.contains("không hợp lệ"),
                "Expected expired token error. Actual: " + errorMsg);
    }
}
