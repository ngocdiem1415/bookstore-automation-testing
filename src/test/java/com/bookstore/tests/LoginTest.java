package com.bookstore.tests;

import com.bookstore.base.BaseSetup;
import com.bookstore.factory.PageFactoryManager;
import com.bookstore.pages.AdminDashboardPage;
import com.bookstore.pages.HomePage;
import com.bookstore.pages.LoginPage;
import com.bookstore.pages.components.HeaderComponent;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class LoginTest extends BaseSetup {

    @Test(description = "AUTH-LOG-01: Login successful with CUSTOMER role.")
    public void AUTH_LOG_01_LoginSuccessCustomer() {
        LoginPage loginPage = PageFactoryManager.getLoginPage(driver);
        loginPage.open();
        HomePage homePage = loginPage.loginAsCustomer("diem_tester", "Abc@12345");
        boolean redirectedToHome = homePage.getCurrentUrl().contains("/home")
                || homePage.getCurrentUrl().equals(baseUrl + "/")
                || homePage.getCurrentUrl().equals(baseUrl + "/home");
        Assert.assertTrue(redirectedToHome,
                "Expected redirect to /home. Current URL: " + homePage.getCurrentUrl());
    }

    @Test(description = "AUTH-LOG-02: Login successful with ADMIN role.")
    public void AUTH_LOG_02_LoginSuccessAdmin() {
        LoginPage loginPage = PageFactoryManager.getLoginPage(driver);
        loginPage.open();
        AdminDashboardPage dashPage = loginPage.loginAsAdmin("admin", "Abc@12345");
        Assert.assertTrue(dashPage.isOnAdminDashboard(),
                "Expected redirect to /admin/dashboard. URL: " + dashPage.getCurrentUrl());
        Assert.assertTrue(dashPage.getSidebar().isDashboardVisible(), "[sidebar-dashboard] not visible");
        Assert.assertTrue(dashPage.getSidebar().isUsersVisible(), "[sidebar-users] not visible");
        Assert.assertTrue(dashPage.getSidebar().isOrdersVisible(), "[sidebar-orders] not visible");
        Assert.assertTrue(dashPage.getSidebar().isCategoriesVisible(), "[sidebar-categories] not visible");
        Assert.assertTrue(dashPage.getSidebar().isProductsVisible(), "[sidebar-products] not visible");
        Assert.assertTrue(dashPage.getSidebar().isSuppliersVisible(), "[sidebar-suppliers] not visible");
        Assert.assertTrue(dashPage.getSidebar().isDiscountsVisible(), "[sidebar-discounts] not visible");
        Assert.assertTrue(dashPage.getSidebar().isAdminsVisible(), "[sidebar-admins] not visible");
        Assert.assertTrue(dashPage.getSidebar().isSettingsVisible(), "[sidebar-settings] not visible");
    }

    @Test(description = "AUTH-LOG-03: Login successful with ORDER_STAFF role.")
    public void AUTH_LOG_03_LoginSuccessOrderStaff() {
        LoginPage loginPage = new LoginPage(driver);
        loginPage.open();
        AdminDashboardPage dashPage = loginPage.loginAsAdmin("order_staff", "Abc@12345");
        Assert.assertTrue(dashPage.isOnAdminDashboard(),
                "Expected redirect to /admin/dashboard. URL: " + dashPage.getCurrentUrl());
        Assert.assertTrue(dashPage.getSidebar().isDashboardVisible(), "[sidebar-dashboard] not visible");
        Assert.assertTrue(dashPage.getSidebar().isOrdersVisible(), "[sidebar-orders] not visible");
        Assert.assertTrue(dashPage.getSidebar().isSettingsVisible(), "[sidebar-settings] not visible");
        Assert.assertFalse(dashPage.getSidebar().isCategoriesVisible(), "[sidebar-categories] should be hidden");
        Assert.assertFalse(dashPage.getSidebar().isProductsVisible(), "[sidebar-products] should be hidden");
        Assert.assertFalse(dashPage.getSidebar().isSuppliersVisible(), "[sidebar-suppliers] should be hidden");
        Assert.assertFalse(dashPage.getSidebar().isDiscountsVisible(), "[sidebar-discounts] should be hidden");
        Assert.assertFalse(dashPage.getSidebar().isAdminsVisible(), "[sidebar-admins] should be hidden");
    }

    @Test(description = "AUTH-LOG-04: Login failed due to invalid password.")
    public void AUTH_LOG_04_LoginFailInvalidPassword() {
        LoginPage loginPage = PageFactoryManager.getLoginPage(driver);
        loginPage.open();
        loginPage.enterUsername("admin")
                .enterPassword("Abc")
                .clickLoginExpectingFailure();
        Assert.assertTrue(loginPage.isOnLoginPage(),
                "Expected redirect back to /login. URL: " + loginPage.getCurrentUrl());

        String errorMsg = loginPage.getErrorMessage();
        Assert.assertEquals(errorMsg, "Tài khoản hoặc mật khẩu không chính xác",
                "Unexpected error message for invalid password");
    }

    @Test(description = "AUTH-LOG-05: Verify system prevents login when SQL Injection is attempted via Username.")
    public void AUTH_LOG_05_LoginFailSQLInjection() {
        LoginPage loginPage = PageFactoryManager.getLoginPage(driver);
        loginPage.open();

        loginPage.enterUsername("admin' --")
                .enterPassword("123456")
                .clickLoginExpectingFailure();

        System.out.println("[Assert] Verify no server error 500 (page title check)");
        String pageTitle = loginPage.getPageTitle();
        String currentUrl = loginPage.getCurrentUrl();
        System.out.println("[Assert] Page title: " + pageTitle);
        System.out.println("[Assert] Current URL: " + currentUrl);

        Assert.assertFalse(pageTitle.toLowerCase().contains("500"),
                "Server error 500 detected! SQL Injection may have succeeded.");
        Assert.assertFalse(currentUrl.contains("/admin/dashboard"),
                "Admin dashboard accessible via SQL injection! Security vulnerability.");

        boolean hasErrorMsg;
        try {
            String errorMsg = loginPage.getErrorMessage();
            System.out.println("[Assert] Error message: " + errorMsg);
            hasErrorMsg = !errorMsg.isEmpty();
        } catch (Exception e) {
            hasErrorMsg = loginPage.isOnLoginPage();
        }
        Assert.assertTrue(hasErrorMsg || loginPage.isOnLoginPage(),
                "Expected login to fail safely against SQL injection.");

    }

    @Test(description = "AUTH-OUT-01: Logout successful.",
            dependsOnMethods = "AUTH_LOG_01_LoginSuccessCustomer")
    public void AUTH_OUT_01_LogoutSuccess() {
        LoginPage loginPage = new LoginPage(driver);
        loginPage.open();
        loginPage.loginAsCustomer("diem_tester", "Abc@12345");

        System.out.println("[Step 1] Navigate to Header menu and click Logout (/logout)");
        HeaderComponent header = new HeaderComponent(driver);
        HomePage homePage = header.navigateToLogout(baseUrl);

        System.out.println("[Assert] Verify session invalidated - redirect to /home or /login");
        String currentUrl = homePage.getCurrentUrl();
        System.out.println("[Assert] Current URL after logout: " + currentUrl);
        boolean redirectedCorrectly = currentUrl.contains("/home")
                || currentUrl.contains("/login")
                || currentUrl.equals(baseUrl + "/");
        Assert.assertTrue(redirectedCorrectly,
                "Expected redirect to /home or /login after logout. URL: " + currentUrl);

        System.out.println("[Assert] Verify login-requiring menus are hidden");
        Assert.assertTrue(header.isLoginButtonVisible(),
                "Expected login button to be visible after logout (user is logged out).");

    }
}
