package com.bookstore.tests.auth;

import com.bookstore.base.BaseSetup;
import com.bookstore.factory.PageFactoryManager;
import com.bookstore.helpers.CleanupRegistry;
import com.bookstore.helpers.CleanupHelper;
import com.bookstore.pages.SignupPage;
import com.bookstore.utils.DataHelper;
import com.bookstore.utils.JsonDataProvider;
import com.bookstore.utils.LoggerHelper;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

import java.util.Map;

public class RegisterTest extends BaseSetup {

    public String generateRandomPhoneNumber() {
        String[] prefixes = {"09", "03", "08", "07"};
        java.util.Random random = new java.util.Random();
        String prefix = prefixes[random.nextInt(prefixes.length)];
        StringBuilder phone = new StringBuilder(prefix);
        for (int i = 0; i < 8; i++) {
            phone.append(random.nextInt(10));
        }
        return phone.toString();
    }

    @Test(
            priority = 1,
            description = "AUTH-REG-01: Registration successful with valid dynamic information."
    )
    public void AUTH_REG_01_RegistrationAndVerifySuccess() {
        String suffix = String.valueOf(System.currentTimeMillis()).substring(8);
        String randomUser = "auto_reg_" + suffix;
        String randomEmail = "auto_reg_" + suffix + "@gmail.com";
        String randomPhone = generateRandomPhoneNumber();
        CleanupRegistry.createdUsers.add(randomUser);

        SignupPage signupPage = PageFactoryManager.getSignupPage(driver, baseUrl);
        LoggerHelper.info("[AUTH][REGISTER] Mở trang đăng ký");
        signupPage.open();

        LoggerHelper.info("[AUTH][REGISTER] Nhập thông tin đăng ký hợp lệ");
        signupPage.fillRegistrationForm(
                randomUser,
                "Abc12345",
                randomEmail,
                "female",
                "01/05/2002",
                randomPhone
        );
        LoggerHelper.info("[AUTH][REGISTER] Click nút đăng ký");
        signupPage.clickSubmitExpectingSuccess();
        String alert = signupPage.getMessage();
        Assert.assertTrue(alert.contains("Đăng ký thành công! Vui lòng kiểm tra email để xác nhận tài khoản"),
                "Lỗi: Hệ thống không hiển thị thông báo đăng kí thành công sau khi gửi thông tin hợp lệ!");
        LoggerHelper.info("[AUTH][REGISTER] Thông báo đăng kí thành công");

        LoggerHelper.info("[AUTH][REGISTER] Kiểm tra chuyển sang trang xác thực OTP");
        Assert.assertTrue(signupPage.isOnEmailVerifyPage(),
                "Lỗi: Hệ thống không hiển thị trang xác thực OTP sau khi gửi thông tin hợp lệ!");

        LoggerHelper.info("[AUTH][REGISTER] Nhập mã OTP");
        signupPage.enterVerifyCode(DataHelper.getValue("fixed.otp.code"));
        signupPage.clickVerify();

        LoggerHelper.info("[AUTH][REGISTER] Kiểm tra đăng ký thành công");
        Assert.assertTrue(signupPage.isRedirectedToSuccess(),
                "Lỗi: Không chuyển hướng về trang /success sau khi xác thực OTP thành công.");
    }

    @AfterMethod(alwaysRun = true)
    public void cleanupRegisterData() {
        CleanupHelper.cleanupCreatedUsers(driver, baseUrl);
    }

    @Test(
            priority = 2,
            dataProvider = "GlobalJsonFeeder",
            dataProviderClass = JsonDataProvider.class,
            description = "AUTH-REG-02: Registration failed due to existing email/username."
    )
    public void AUTH_REG_02_RegistrationFailExistingUser(Map<String, String> data) {
        SignupPage signupPage = PageFactoryManager.getSignupPage(driver, baseUrl);
        LoggerHelper.info("[AUTH][REGISTER] Mở trang đăng ký");
        signupPage.open();

        LoggerHelper.info("[AUTH][REGISTER] Nhập username/email đã tồn tại");
        signupPage.fillRegistrationForm(
                data.get("username"),
                data.get("password"),
                data.get("email"),
                "female",
                "01/05/2002",
                generateRandomPhoneNumber()
        );
        LoggerHelper.info("[AUTH][REGISTER] Click nút đăng ký");
        signupPage.clickSubmitExpectingFailure();

        LoggerHelper.info("[AUTH][REGISTER] Kiểm tra thông báo lỗi trùng dữ liệu");
        String errorMsg = signupPage.getErrorMessage();
        Assert.assertTrue(errorMsg.contains("Username đã tồn tại"),
                "Lỗi: Thông báo báo trùng dữ liệu hiển thị không đúng! Actual: " + errorMsg);

        LoggerHelper.info("[AUTH][REGISTER] Kiểm tra người dùng vẫn ở trang đăng ký");
        Assert.assertTrue(signupPage.isOnSignupPage(),
                "Lỗi: Hệ thống điều hướng sai, không giữ người dùng lại trang đăng ký.");
    }

    @Test(
            priority = 3,
            dataProvider = "GlobalJsonFeeder",
            dataProviderClass = JsonDataProvider.class,
            description = "AUTH-REG-03: Registration failed - invalid password formats (Boundary/Regex)."
    )
    public void AUTH_REG_03_RegistrationFailInvalidPassword(Map<String, String> data) {
        SignupPage signupPage = PageFactoryManager.getSignupPage(driver, baseUrl);
        LoggerHelper.info("[AUTH][REGISTER] Mở trang đăng ký");
        signupPage.open();

        LoggerHelper.info("[AUTH][REGISTER] Nhập thông tin lỗi");
        signupPage.fillRegistrationForm(
                "user_loi_bien",
                data.get("password"),
                "invalid_pwd@gmail.com",
                "female",
                "01/05/2002",
                generateRandomPhoneNumber()
        );

        boolean isBtnDisabled = !signupPage.isSubmitButtonEnabled();
        LoggerHelper.info("[AUTH][REGISTER] Kiểm tra nút Submat bị vô hiệu hóa");
        Assert.assertTrue(isBtnDisabled, "Lỗi: Nút Submit phải bị vô hiệu hóa khi mật khẩu sai định dạng!");

        String actualError = signupPage.getCheckPwdWarning();
        String expectedError = "Mật khẩu phải có ít nhất 6 ký tự, bao gồm cả chữ cái và số!";
        LoggerHelper.info("[AUTH][REGISTER] Kiểm tra thông báo lỗi trùng dữ liệu");
        Assert.assertEquals(actualError, expectedError);
    }
}
