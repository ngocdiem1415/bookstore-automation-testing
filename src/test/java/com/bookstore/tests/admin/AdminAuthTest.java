package com.bookstore.tests.admin;

import com.bookstore.base.BaseSetup;
import com.bookstore.factory.PageFactoryManager;
import com.bookstore.pages.*;
import org.testng.Assert;
import org.testng.annotations.Test;

/** ADM-AUT-01/02/03 */
public class AdminAuthTest extends BaseSetup {
    private static final String ADMIN = "admin",      ADMIN_PASS = "Abc@12345";
    private static final String USER  = "diem_tester", USER_PASS  = "Abc@12345";

    private HomePage loginAsCustomer() {
        LoginPage loginPage = PageFactoryManager.getLoginPage(driver, baseUrl);
        return loginPage.loginAsCustomer(USER, USER_PASS);
    }

    private AdminDashboardPage loginAsAdmin() {
        LoginPage loginPage = PageFactoryManager.getLoginPage(driver, baseUrl);
        return loginPage.loginAsAdmin(ADMIN, ADMIN_PASS);
    }

    @Test(description = "ADM-AUT-01: Verify Admin can login to dashboard.")
    public void ADM_AUT_01_AdminLoginDashboard() {
        AdminDashboardPage dash = loginAsAdmin();
        Assert.assertTrue(dash.isOnAdminDashboard(),
                "Expected Admin to access /admin/dashboard. URL: " + dash.getCurrentUrl());
        Assert.assertTrue(dash.isDashboardPageLoaded(),
                "Expected Admin Dashboard to render content.");
    }

    @Test(description = "ADM-AUT-02: Verify Normal User cannot access Admin pages.")
    public void ADM_AUT_02_NormalUserCannotAccessAdmin() {
        loginAsCustomer();
        driver.get(baseUrl + "/admin/dashboard");
        String url   = driver.getCurrentUrl();
        String title = driver.getTitle().toLowerCase();
        String body  = "";
        try { body = driver.findElement(org.openqa.selenium.By.tagName("body")).getText().toLowerCase(); }
        catch (Exception ignored) {}
        boolean blocked = title.contains("403") || body.contains("403")
                || body.contains("forbidden") || body.contains("access denied")
                || url.contains("/login") || url.contains("/home")
                || !url.contains("/admin/dashboard");
        Assert.assertTrue(blocked,
                "SECURITY: Normal user accessed /admin/dashboard! Expected 403 or redirect.");
    }

    @Test(description = "ADM-AUT-03: Verify accessing Admin API without token (Boundary).")
    public void ADM_AUT_03_AdminApiWithoutToken() {
        driver.manage().deleteAllCookies();
        String adminApiUrl = baseUrl + "/admin/dashboard";
        driver.get(adminApiUrl);

        String url   = driver.getCurrentUrl();
        String title = driver.getTitle().toLowerCase();
        System.out.println("[Assert] URL=" + url + " | Title=" + title);

        boolean isUnauthorized = url.contains("/login")
                || title.contains("401") || title.contains("login")
                || !url.contains("/admin");
        Assert.assertTrue(isUnauthorized,
                "Expected 401 Unauthorized or redirect to /login without session. Got: " + url);
    }
}
