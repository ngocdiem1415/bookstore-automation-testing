package com.bookstore.tests.admin;

import com.bookstore.base.BaseSetup;
import com.bookstore.factory.PageFactoryManager;
import com.bookstore.pages.AdminDashboardPage;
import com.bookstore.pages.HomePage;
import com.bookstore.pages.LoginPage;
import com.bookstore.utils.DataHelper;
import com.bookstore.utils.LoggerHelper;

public class AdminBaseTest extends BaseSetup {

    protected void loginAsCustomer() {
        LoggerHelper.info("[ADMIN][BASE] Mở trang đăng nhập");
        LoginPage loginPage = PageFactoryManager.getLoginPage(getDriver(), baseUrl);
        loginPage.open();

        LoggerHelper.info("[ADMIN][BASE] Đăng nhập bằng tài khoản CUSTOMER: "
                + DataHelper.getValue("existing.username"));

        loginPage.loginAsCustomer(
                DataHelper.getValue("existing.username"),
                DataHelper.getValue("existing.password")
        );

        try {
            Thread.sleep(500);
            LoggerHelper.info("[ADMIN][BASE] Đăng nhập CUSTOMER xong, chờ 500ms để ổn định cookie");
        } catch (InterruptedException e) {
            LoggerHelper.warn("[ADMIN][BASE] Luồng chờ sau khi đăng nhập CUSTOMER bị gián đoạn");
            Thread.currentThread().interrupt();
        }
    }

    protected void loginAsAdmin() {
        LoggerHelper.info("[ADMIN][BASE] Mở trang đăng nhập");
        LoginPage loginPage = PageFactoryManager.getLoginPage(getDriver(), baseUrl);
        loginPage.open();

        LoggerHelper.info("[ADMIN][BASE] Đăng nhập bằng tài khoản ADMIN: "
                + DataHelper.getValue("existing.admin"));

        loginPage.loginAsAdmin(
                DataHelper.getValue("existing.admin"),
                DataHelper.getValue("existing.password")
        );

        try {
            Thread.sleep(500);
            LoggerHelper.info("[ADMIN][BASE] Đăng nhập ADMIN xong, chờ 500ms để ổn định cookie");
        } catch (InterruptedException e) {
            LoggerHelper.warn("[ADMIN][BASE] Luồng chờ sau khi đăng nhập ADMIN bị gián đoạn");
            Thread.currentThread().interrupt();
        }
    }
}
