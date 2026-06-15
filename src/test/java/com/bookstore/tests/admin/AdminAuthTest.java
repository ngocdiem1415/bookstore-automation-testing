package com.bookstore.tests.admin;

import com.bookstore.base.BaseSetup;
import com.bookstore.factory.PageFactoryManager;
import com.bookstore.pages.*;
import com.bookstore.utils.LoggerHelper;
import org.testng.Assert;
import org.testng.annotations.Test;

public class AdminAuthTest extends AdminBaseTest {

    @Test(priority = 1, description = "ADM-AUT-01: Xác minh quản trị viên có thể đăng nhập vào dashboard.")
    public void ADM_AUT_01_AdminLoginDashboard() {
        LoggerHelper.info("[ADMIN][AUTH] Bắt đầu kiểm thử admin đăng nhập vào dashboard");
        loginAsAdmin();

        LoggerHelper.info("[ADMIN][AUTH] Khởi tạo trang Admin Dashboard");
        AdminDashboardPage dash = PageFactoryManager.getAdminDashboardPage(getDriver(), baseUrl);

        LoggerHelper.info("[ADMIN][AUTH] Mở trang Admin Dashboard");
        dash.open();

        LoggerHelper.info("[ADMIN][AUTH] Kiểm tra URL hiện tại là trang dashboard");
        Assert.assertTrue(dash.isOnAdminDashboard(),
                "Expected Admin to access /admin/dashboard. URL: " + dash.getCurrentUrl());

        LoggerHelper.info("[ADMIN][AUTH] Kiểm tra nội dung dashboard render thành công");
        Assert.assertTrue(dash.isDashboardPageLoaded(),
                "Expected Admin Dashboard to render content.");
        LoggerHelper.info("[ADMIN][AUTH] Admin đăng nhập và truy cập dashboard thành công");
    }

    @Test(priority = 2, description = "ADM-AUT-02: Kiểm tra Người dùng thông thường không thể truy cập trang Quản trị.")
    public void ADM_AUT_02_NormalUserCannotAccessAdmin() {
        LoggerHelper.info("[ADMIN][AUTH] Bắt đầu kiểm thử customer không được truy cập trang admin");

        loginAsCustomer();

        LoggerHelper.info("[ADMIN][AUTH] Customer truy cập trực tiếp URL /admin/dashboard");
        driver.get(baseUrl + "/admin/dashboard");

        String url = driver.getCurrentUrl();
        String title = driver.getTitle().toLowerCase();

        LoggerHelper.info("[ADMIN][AUTH] URL hiện tại: " + url);
        LoggerHelper.info("[ADMIN][AUTH] Title hiện tại: " + title);

        boolean blocked = title.contains("403")
                || url.contains("/login")
                || url.contains("/home")
                || !url.contains("/admin/dashboard");

        LoggerHelper.info("[ADMIN][AUTH] Kết quả chặn truy cập admin: " + blocked);

        Assert.assertTrue(blocked,
                "SECURITY: Normal user accessed /admin/dashboard! Expected 403 or redirect.");

        LoggerHelper.info("[ADMIN][AUTH] Customer bị chặn truy cập admin đúng kỳ vọng");
    }

}
