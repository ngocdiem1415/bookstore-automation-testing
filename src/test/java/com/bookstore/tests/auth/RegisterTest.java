package com.bookstore.tests.auth;

import com.bookstore.base.BaseSetup;
import com.bookstore.factory.PageFactoryManager;
import com.bookstore.pages.SignupPage;
import com.bookstore.utils.DataHelper;
import com.bookstore.utils.JsonDataProvider;
import org.testng.Assert;
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
        String randomUser = "tester" + suffix;
        String randomEmail = "diem" + suffix + "@gmail.com";
        String randomPhone = generateRandomPhoneNumber();

        SignupPage signupPage = PageFactoryManager.getSignupPage(driver, baseUrl);
        signupPage.open();

        signupPage.fillRegistrationForm(
                randomUser,
                "Abc12345",
                randomEmail,
                "female",
                "01/05/2002",
                randomPhone
        );
        signupPage.clickSubmitExpectingSuccess();

        Assert.assertTrue(signupPage.isOnEmailVerifyPage(),
                "Lỗi: Hệ thống không hiển thị trang xác thực OTP sau khi gửi thông tin hợp lệ!");

        signupPage.enterVerifyCode(DataHelper.getValue("fixed.otp.code"));
        signupPage.clickVerify();

        Assert.assertTrue(signupPage.isRedirectedToSuccess(),
                "Lỗi: Không chuyển hướng về trang /success sau khi xác thực OTP thành công.");
    }

    @Test(
            priority = 2,
            dataProvider = "GlobalJsonFeeder",
            dataProviderClass = JsonDataProvider.class,
            description = "AUTH-REG-02: Registration failed due to existing email/username."
    )
    public void AUTH_REG_02_RegistrationFailExistingUser(Map<String, String> data) {
        SignupPage signupPage = PageFactoryManager.getSignupPage(driver, baseUrl);
        signupPage.open();
        signupPage.fillRegistrationForm(
                data.get("username"),
                data.get("password"),
                data.get("email"),
                "female",
                "01/05/2002",
                generateRandomPhoneNumber()
        );

        signupPage.clickSubmitExpectingFailure();
        String errorMsg = signupPage.getErrorMessage();
        Assert.assertTrue(errorMsg.contains(data.get("expected_error")),
                "Lỗi: Thông báo báo trùng dữ liệu hiển thị không đúng! Actual: " + errorMsg);
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
        signupPage.open();
        signupPage.fillRegistrationForm(
                "user_loi_bien",
                data.get("password"),
                "invalid_pwd@gmail.com",
                "female",
                "01/05/2002",
                generateRandomPhoneNumber()
        );
        boolean isBtnDisabled = !signupPage.isSubmitButtonEnabled();

        Assert.assertTrue(isBtnDisabled, "Lỗi: Nút Submit phải bị vô hiệu hóa khi mật khẩu sai định dạng!");
        String actualError = signupPage.getCheckPwdWarning();
        String expectedError = "Mật khẩu phải có ít nhất 6 ký tự, bao gồm cả chữ cái và số!";
        Assert.assertEquals(actualError, expectedError);

    }
}
