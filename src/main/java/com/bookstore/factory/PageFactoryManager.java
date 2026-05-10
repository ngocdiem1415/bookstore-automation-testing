package com.bookstore.factory;

import com.bookstore.pages.AdminDashboardPage;
import com.bookstore.pages.LoginPage;
import com.bookstore.pages.ProfilePage;
import com.bookstore.pages.SignupPage;
import org.openqa.selenium.WebDriver;

public class PageFactoryManager {
    public static LoginPage getLoginPage(WebDriver driver) {
        return new LoginPage(driver);
    }

    public static SignupPage getSignupPage(WebDriver driver) {
        return new SignupPage(driver);
    }

    public static AdminDashboardPage getAdminDashboardPage(WebDriver driver) {
        return new AdminDashboardPage(driver);
    }

    public static ProfilePage getProfilePage(WebDriver driver) {
        return new ProfilePage(driver);
    }

}
