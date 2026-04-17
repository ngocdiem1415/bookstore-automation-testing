package com.bookstore.tests;

import com.bookstore.base.BaseSetup;
import com.bookstore.pages.LoginPage;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.Test;

public class LoginTest extends BaseSetup {

    @Test
    public void testLoginSuccess() {
        // Khởi tạo trang Login
        LoginPage loginPage = new LoginPage(driver);

        // Thực hiện các bước (Steps)
        loginPage.enterUsername("admin");
        loginPage.enterPassword("123456");
        loginPage.clickLogin();

        // Kiểm tra kết quả (Assert)
        String expectedUrl = "http://localhost:3000/home";
        Assert.assertEquals(driver.getCurrentUrl(), expectedUrl, "Đăng nhập không thành công!");
    }

}
