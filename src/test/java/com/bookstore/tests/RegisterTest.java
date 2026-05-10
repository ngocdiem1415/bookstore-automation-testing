package com.bookstore.tests;

import com.bookstore.base.BaseSetup;
import com.bookstore.factory.PageFactoryManager;
import com.bookstore.pages.SignupPage;
import org.testng.Assert;
import org.testng.annotations.Test;

public class RegisterTest extends BaseSetup {

    @Test(description = "AUTH-REG-01: Registration successful with valid information.")
    public void AUTH_REG_01_RegistrationSuccess() {
        //Buoc1
        SignupPage signupPage = PageFactoryManager.getSignupPage(driver);
        signupPage.open();

        //Buoc2
        signupPage.fillRegistrationForm(
                "diem_tester",
                "Abc@12345",
                "ngocdiem31072004@gmail.com",
                "female",
                "01/05/2002",
                "0987654321"
        );

        //Buoc3
        signupPage.clickSubmitExpectingSuccess();
        Assert.assertTrue(signupPage.isOnEmailVerifyPage(),
                "Expected redirect to email-verify page. URL: "
                        + signupPage.getCurrentUrl());
    }

    @Test(description = "AUTH-REG-02: Registration failed due to existing email/username.")
    public void AUTH_REG_02_RegistrationFailExistingUser() {
        SignupPage signupPage = new SignupPage(driver);
        signupPage.open();

        signupPage.fillRegistrationForm(
                "diem_tester",
                "Abc@12345",
                "ngocdiem31072004@gmail.com",
                "female",
                "01/05/2002",
                "0987654321"
        );

        signupPage.clickSubmitExpectingFailure();
        String errorMsg = signupPage.getErrorMessage();
        boolean isExpectedMsg = errorMsg.contains("Username đã tồn tại")
                || errorMsg.contains("Email đã được sử dụng");
        Assert.assertTrue(isExpectedMsg,
                "Expected duplicate user/email error. Actual: " + errorMsg);
        Assert.assertTrue(signupPage.isOnSignupPage(),
                "Expected to remain on /signup page. URL: " + signupPage.getCurrentUrl());

    }

    @Test(description = "AUTH-REG-03: Registration failed - invalid password format (Boundary).")
    public void AUTH_REG_03_RegistrationFailInvalidPassword() {
        SignupPage signupPage = new SignupPage(driver);
        signupPage.open();
        signupPage.enterUsername("test_user_boundary")
                .enterPassword("abc~123")
                .enterEmail("boundary@gmail.com")
                .selectGender("male")
                .enterBirthdate("01/01/2000")
                .enterPhone("0900000000");
        boolean btnDisabled = !signupPage.isSubmitButtonEnabled();
        String validationMsg = signupPage.getPasswordValidationMessage();
        boolean isBlocked = btnDisabled
                || (validationMsg != null && !validationMsg.isEmpty());
        Assert.assertTrue(isBlocked,
                "Expected submit to be blocked for invalid password 'abc~123'");
    }
}
