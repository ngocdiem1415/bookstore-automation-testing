package com.bookstore.tests.auth;

import com.bookstore.base.BaseSetup;
import com.bookstore.factory.PageFactoryManager;
import com.bookstore.pages.AdminDashboardPage;
import com.bookstore.pages.HomePage;
import com.bookstore.pages.LoginPage;
import com.bookstore.pages.components.HeaderComponent;
import com.bookstore.pages.components.SidebarComponent;
import com.bookstore.utils.DataHelper;
import com.bookstore.utils.JsonDataProvider;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Map;

public class LoginTest extends BaseSetup {
    @Test(priority = 1, description = "AUTH-LOG-01: Login successful with CUSTOMER role.")
    public void AUTH_LOG_01_LoginSuccessCustomer() {
        LoginPage loginPage = PageFactoryManager.getLoginPage(driver, baseUrl);
        loginPage.open();

        HomePage homePage = loginPage.loginAsCustomer(
                DataHelper.getValue("existing.username"),
                DataHelper.getValue("existing.password")
        );

        boolean redirectedToHome = homePage.getCurrentUrl().contains("/home")
                || homePage.getCurrentUrl().equals(baseUrl + "/")
                || homePage.getCurrentUrl().equals(baseUrl + "/home");
        Assert.assertTrue(redirectedToHome, "Expected redirect to /home. Current URL: " + homePage.getCurrentUrl());
    }

    @Test(
            priority = 2,
            dataProvider = "GlobalJsonFeeder",
            dataProviderClass = JsonDataProvider.class,
            description = "AUTH-LOG-02: Login successful with ADMIN role."
    )
    public void AUTH_LOG_02_LoginSuccessAdmin(Map<String, String> data) {
        LoginPage loginPage = PageFactoryManager.getLoginPage(driver, baseUrl);
        loginPage.open();

        AdminDashboardPage dashPage = loginPage.loginAsAdmin(
                data.get("username"),
                data.get("password")
        );

        Assert.assertTrue(dashPage.isOnAdminDashboard(), "Expected redirect to /admin/dashboard. URL: " + dashPage.getCurrentUrl());
        dashPage.openSidebarMenu();
        SidebarComponent sidebar = dashPage.getSidebar();
        Assert.assertTrue(sidebar.isDashboardVisible(), "[sidebar-dashboard] không hiển thị");
        Assert.assertTrue(sidebar.isUsersVisible(), "[sidebar-users] không hiển thị");
        Assert.assertTrue(sidebar.isOrdersVisible(), "[sidebar-orders] không hiển thị");
        Assert.assertTrue(sidebar.isCategoriesVisible(), "[sidebar-categories] không hiển thị");
        Assert.assertTrue(sidebar.isProductsVisible(), "[sidebar-products] không hiển thị");
        Assert.assertTrue(sidebar.isSuppliersVisible(), "[sidebar-suppliers] không hiển thị");
        Assert.assertTrue(sidebar.isDiscountsVisible(), "[sidebar-discounts] không hiển thị");
        Assert.assertTrue(sidebar.isAdminsVisible(), "[sidebar-admins] không hiển thị");
        Assert.assertTrue(sidebar.isSettingsVisible(), "[sidebar-settings] không hiển thị");
    }

    @Test(
            priority = 3,
            dataProvider = "GlobalJsonFeeder",
            dataProviderClass = JsonDataProvider.class,
            description = "AUTH-LOG-03: Login failed due to invalid password."
    )
    public void AUTH_LOG_03_LoginFailInvalidPassword(Map<String, String> data) {
        LoginPage loginPage = PageFactoryManager.getLoginPage(driver, baseUrl);
        loginPage.open();

        loginPage.enterUsername(data.get("username"))
                .enterPassword(data.get("password"))
                .clickLoginExpectingFailure();

        Assert.assertTrue(loginPage.isOnLoginPage(), "Expected redirect back to /login. URL: " + loginPage.getCurrentUrl());
        String errorMsg = loginPage.getErrorMessage();
        Assert.assertEquals(errorMsg, data.get("expected_error"), "Unexpected error message for invalid password");
    }

    @Test(
            priority = 4,
            dataProvider = "GlobalJsonFeeder",
            dataProviderClass = JsonDataProvider.class,
            description = "AUTH-LOG-04: Verify system prevents login when SQL Injection is attempted via Username."
    )
    public void AUTH_LOG_04_LoginFailSQLInjection(Map<String, String> data) {
        LoginPage loginPage = PageFactoryManager.getLoginPage(driver, baseUrl);
        loginPage.open();

        loginPage.enterUsername(data.get("username"))
                .enterPassword(data.get("password"))
                .clickLoginExpectingFailure();

        String pageTitle = loginPage.getPageTitle();
        String currentUrl = loginPage.getCurrentUrl();

        // Kiểm tra an toàn bảo mật (Security Gate)
        Assert.assertFalse(pageTitle.toLowerCase().contains("500"), "Server error 500 detected! SQL Injection may have succeeded.");
        Assert.assertFalse(currentUrl.contains("/admin/dashboard"), "Admin dashboard accessible via SQL injection! Security vulnerability.");

        boolean hasErrorMsg;
        try {
            String errorMsg = loginPage.getErrorMessage();
            hasErrorMsg = !errorMsg.isEmpty();
        } catch (Exception e) {
            hasErrorMsg = loginPage.isOnLoginPage();
        }
        Assert.assertTrue(hasErrorMsg || loginPage.isOnLoginPage(), "Expected login to fail safely against SQL injection.");
    }

    @Test(priority = 5, description = "AUTH-OUT-01: Logout successful.")
    public void AUTH_OUT_01_LogoutSuccess() {
        LoginPage loginPage = PageFactoryManager.getLoginPage(driver, baseUrl);
        loginPage.open();
        loginPage.loginAsCustomer(DataHelper.getValue("existing.username"), DataHelper.getValue("existing.password"));

        HeaderComponent header = new HeaderComponent(driver, baseUrl);
        HomePage homePage = header.navigateToLogout();

        String currentUrl = homePage.getCurrentUrl();
        boolean redirectedCorrectly = currentUrl.contains("/home")
                || currentUrl.contains("/login")
                || currentUrl.equals(baseUrl + "/");

        Assert.assertTrue(redirectedCorrectly, "Expected redirect to /home or /login after logout. URL: " + currentUrl);
        //thêm xét giỏ hàng kh bấm được
    }
}
