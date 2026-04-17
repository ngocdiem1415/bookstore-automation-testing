package com.bookstore.tests;

import com.bookstore.base.BaseSetup;
import com.bookstore.pages.LoginPage;
import com.bookstore.pages.SignupPage;
import org.testng.Assert;
import org.testng.annotations.Test;

public class AuthTest extends BaseSetup {
    @Test(description = "Kiểm tra đăng nhập thất bại")
    public void testLoginFail() {
        LoginPage loginPage = new LoginPage(driver);
        loginPage.open();
        loginPage.login("wronguser", "wrongpass");
        Assert.assertEquals(loginPage.getErrorMessage(), "Sai tên đăng nhập hoặc mật khẩu");
    }

    @Test(description = "Kiểm tra validate mật khẩu realtime (Javascript)")
    public void testSignupPasswordValidation() {
        SignupPage signupPage = new SignupPage(driver);
        signupPage.open();
        signupPage.enterSignupInfo("", "123", "", "male", "", ""); // Pass yếu
        Assert.assertEquals(signupPage.getPasswordValidationMsg(),
                "Mật khẩu phải có ít nhất 6 ký tự, bao gồm cả chữ cái và số!");
    }

}
