package com.bookstore.tests.admin;

import com.bookstore.base.BaseSetup;
import com.bookstore.factory.PageFactoryManager;
import com.bookstore.pages.AdminDashboardPage;
import com.bookstore.pages.HomePage;
import com.bookstore.pages.LoginPage;
import com.bookstore.utils.DataHelper;

public class AdminBase extends BaseSetup {

    protected HomePage loginAsCustomer() {
        LoginPage loginPage = PageFactoryManager.getLoginPage(driver, baseUrl);
        return loginPage.loginAsCustomer(
                DataHelper.getValue("existing.username"),
                DataHelper.getValue("existing.password")
        );
    }

    protected AdminDashboardPage loginAsAdmin() {
        LoginPage loginPage = PageFactoryManager.getLoginPage(driver, baseUrl);
        return loginPage.loginAsAdmin(ADMIN, ADMIN_PASS);
    }
}
