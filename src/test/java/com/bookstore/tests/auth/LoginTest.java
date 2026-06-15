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
import com.bookstore.utils.LoggerHelper;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Map;

public class LoginTest extends BaseSetup {
    @Test(priority = 1, description = "AUTH-LOG-01: Login successful with CUSTOMER role.")
    public void AUTH_LOG_01_LoginSuccessCustomer() {
        LoggerHelper.info("[AUTH][LOGIN] Bắt đầu kiểm thử đăng nhập với tài khoản CUSTOMER");
        LoginPage loginPage = PageFactoryManager.getLoginPage(driver, baseUrl);

        LoggerHelper.info("[AUTH][LOGIN] Mở trang đăng nhập");
        loginPage.open();

        LoggerHelper.info("[AUTH][LOGIN] Nhập username CUSTOMER: " + DataHelper.getValue("existing.username"));
        LoggerHelper.info("[AUTH][LOGIN] Nhập password CUSTOMER");
        LoggerHelper.info("[AUTH][LOGIN] Click nút đăng nhập");
        HomePage homePage = loginPage.loginAsCustomer(
                DataHelper.getValue("existing.username"),
                DataHelper.getValue("existing.password")
        );

        LoggerHelper.info("[AUTH][LOGIN] Kiểm tra chuyển hướng sau khi đăng nhập");

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        boolean redirectedToHome = homePage.getCurrentUrl().contains("/home")
                || homePage.getCurrentUrl().equals(baseUrl + "/")
                || homePage.getCurrentUrl().equals(baseUrl + "/home");

        Assert.assertTrue(redirectedToHome, "Expected redirect to /home. Current URL: " + homePage.getCurrentUrl());
        LoggerHelper.info("[AUTH][LOGIN] Đăng nhập CUSTOMER thành công. URL hiện tại: " + homePage.getCurrentUrl());

    }

    @Test(
            priority = 2,
            dataProvider = "GlobalJsonFeeder",
            dataProviderClass = JsonDataProvider.class,
            description = "AUTH-LOG-02: Login successful with ADMIN role."
    )
    public void AUTH_LOG_02_LoginSuccessAdmin(Map<String, String> data) {
        LoggerHelper.info("[AUTH][LOGIN] Bắt đầu kiểm thử đăng nhập với tài khoản ADMIN");

        LoginPage loginPage = PageFactoryManager.getLoginPage(driver, baseUrl);
        LoggerHelper.info("[AUTH][LOGIN] Mở trang đăng nhập");
        loginPage.open();

        LoggerHelper.info("[AUTH][LOGIN] Nhập username ADMIN: " + data.get("username"));
        LoggerHelper.info("[AUTH][LOGIN] Nhập password ADMIN");
        LoggerHelper.info("[AUTH][LOGIN] Click nút đăng nhập");
        AdminDashboardPage dashPage = loginPage.loginAsAdmin(
                data.get("username"),
                data.get("password")
        );

        LoggerHelper.info("[AUTH][LOGIN] Kiểm tra chuyển hướng tới trang Admin Dashboard");
        Assert.assertTrue(dashPage.isOnAdminDashboard(), "Expected redirect to /admin/dashboard. URL: " + dashPage.getCurrentUrl());

        LoggerHelper.info("[AUTH][LOGIN] Mở sidebar admin");
        dashPage.openSidebarMenu();
        SidebarComponent sidebar = dashPage.getSidebar();

        LoggerHelper.info("[AUTH][LOGIN] Kiểm tra các menu quản trị hiển thị");
        Assert.assertTrue(sidebar.isDashboardVisible(), "[sidebar-dashboard] không hiển thị");
        Assert.assertTrue(sidebar.isUsersVisible(), "[sidebar-users] không hiển thị");
        Assert.assertTrue(sidebar.isOrdersVisible(), "[sidebar-orders] không hiển thị");
        Assert.assertTrue(sidebar.isCategoriesVisible(), "[sidebar-categories] không hiển thị");
        Assert.assertTrue(sidebar.isProductsVisible(), "[sidebar-products] không hiển thị");
        Assert.assertTrue(sidebar.isSuppliersVisible(), "[sidebar-suppliers] không hiển thị");
        Assert.assertTrue(sidebar.isDiscountsVisible(), "[sidebar-discounts] không hiển thị");
        Assert.assertTrue(sidebar.isAdminsVisible(), "[sidebar-admins] không hiển thị");
        Assert.assertTrue(sidebar.isSettingsVisible(), "[sidebar-settings] không hiển thị");

        LoggerHelper.info("[AUTH][LOGIN] Đăng nhập ADMIN thành công và sidebar hiển thị đầy đủ");
    }

    @Test(
            priority = 3,
            dataProvider = "GlobalJsonFeeder",
            dataProviderClass = JsonDataProvider.class,
            description = "AUTH-LOG-03: Login failed due to invalid password."
    )
    public void AUTH_LOG_03_LoginFailInvalidPassword(Map<String, String> data) {
        LoggerHelper.info("[AUTH][LOGIN] Bắt đầu kiểm thử đăng nhập thất bại do sai mật khẩu");

        LoginPage loginPage = PageFactoryManager.getLoginPage(driver, baseUrl);
        LoggerHelper.info("[AUTH][LOGIN] Mở trang đăng nhập");
        loginPage.open();

        LoggerHelper.info("[AUTH][LOGIN] Nhập username: " + data.get("username"));
        LoggerHelper.info("[AUTH][LOGIN] Nhập password không hợp lệ");
        LoggerHelper.info("[AUTH][LOGIN] Click nút đăng nhập");

        loginPage.enterUsername(data.get("username"))
                .enterPassword(data.get("password"))
                .clickLoginExpectingFailure();

        LoggerHelper.info("[AUTH][LOGIN] Kiểm tra hệ thống vẫn ở trang đăng nhập");
        Assert.assertTrue(loginPage.isOnLoginPage(),
                "Expected redirect back to /login. URL: " + loginPage.getCurrentUrl());

        String errorMsg = loginPage.getErrorMessage();

        LoggerHelper.info("[AUTH][LOGIN] Thông báo lỗi thực tế: " + errorMsg);
        Assert.assertEquals(errorMsg, "Tên đăng nhập hoặc mật khẩu không đúng",
                "Unexpected error message for invalid password");

        LoggerHelper.info("[AUTH][LOGIN] Hệ thống từ chối đăng nhập với mật khẩu sai đúng kỳ vọng");
    }

    @Test(
            priority = 4,
            dataProvider = "GlobalJsonFeeder",
            dataProviderClass = JsonDataProvider.class,
            description = "AUTH-LOG-04: Verify system prevents login when SQL Injection is attempted via Username."
    )
    public void AUTH_LOG_04_LoginFailSQLInjection(Map<String, String> data) {
        LoggerHelper.info("[AUTH][LOGIN] Bắt đầu kiểm thử chống SQL Injection ở màn hình đăng nhập");

        LoginPage loginPage = PageFactoryManager.getLoginPage(driver, baseUrl);

        LoggerHelper.info("[AUTH][LOGIN] Mở trang đăng nhập");
        loginPage.open();

        LoggerHelper.info("[AUTH][LOGIN] Nhập payload SQL Injection vào username");
        LoggerHelper.info("[AUTH][LOGIN] Nhập password kiểm thử");
        LoggerHelper.info("[AUTH][LOGIN] Click nút đăng nhập");

        loginPage.enterUsername(data.get("username"))
                .enterPassword(data.get("password"))
                .clickLoginExpectingFailure();

        String pageTitle = loginPage.getPageTitle();
        String url = loginPage.getCurrentUrl();

        LoggerHelper.info("[AUTH][LOGIN] Kiểm tra không xuất hiện lỗi server 500");
        Assert.assertFalse(pageTitle.toLowerCase().contains("500"),
                "Server xuất hiện lỗi server 500");

        LoggerHelper.info("[AUTH][LOGIN] Kiểm tra không truy cập được Admin Dashboard");
        Assert.assertFalse(url.contains("/admin/dashboard"),
                "Có thể truy cập trang quản trị thông qua tấn công SQL injection");

        boolean hasErrorMsg;
        try {
            String errorMsg = loginPage.getErrorMessage();
            LoggerHelper.info("[AUTH][LOGIN] Thông báo lỗi hiển thị: " + errorMsg);
            hasErrorMsg = !errorMsg.isEmpty();
        } catch (Exception e) {
            LoggerHelper.warn("[AUTH][LOGIN] Không lấy được thông báo lỗi, kiểm tra trạng thái trang login");
            hasErrorMsg = loginPage.isOnLoginPage();
        }

        Assert.assertTrue(hasErrorMsg || loginPage.isOnLoginPage(),
                "Expected đăng nhập không thành công với SQL injection");

        LoggerHelper.info("[AUTH][LOGIN] Hệ thống xử lý SQL Injection an toàn");
    }

    @Test(priority = 5, description = "AUTH-OUT-01: Logout successful.")
    public void AUTH_OUT_01_LogoutSuccess() {
        LoggerHelper.info("[AUTH][LOGOUT] Bắt đầu kiểm thử đăng xuất");

        LoginPage loginPage = PageFactoryManager.getLoginPage(driver, baseUrl);
        LoggerHelper.info("[AUTH][LOGOUT] Mở trang đăng nhập");
        loginPage.open();

        LoggerHelper.info("[AUTH][LOGOUT] Đăng nhập bằng tài khoản CUSTOMER");
        loginPage.loginAsCustomer(
                DataHelper.getValue("existing.username"),
                DataHelper.getValue("existing.password")
        );

        LoggerHelper.info("[AUTH][LOGOUT] Click đăng xuất từ header");
        HeaderComponent header = new HeaderComponent(driver, baseUrl);
        HomePage homePage = header.navigateToLogout();

        String currentUrl = homePage.getCurrentUrl();
        LoggerHelper.info("[AUTH][LOGOUT] Kiểm tra URL sau khi đăng xuất: " + currentUrl);
        boolean redirectedCorrectly = currentUrl.contains("/home")
                || currentUrl.contains("/login")
                || currentUrl.equals(baseUrl + "/");
        Assert.assertTrue(redirectedCorrectly,
                "Expected redirect to /home after logout. URL: " + currentUrl);
        LoggerHelper.info("[AUTH][LOGOUT] Đăng xuất thành công");
    }
}
