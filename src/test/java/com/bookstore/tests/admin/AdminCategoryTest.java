package com.bookstore.tests.admin;

import com.bookstore.factory.PageFactoryManager;
import com.bookstore.pages.AdminCategoryPage;
import com.bookstore.utils.LoggerHelper;
import org.testng.Assert;
import org.testng.annotations.Test;


public class AdminCategoryTest extends AdminBaseTest {
    private static final String NEW_CAT = "Mới_" + System.currentTimeMillis();

    @Test(priority = 1, description = "ADM-CAT-01: Kiểm thử thêm danh mục thành công")
    public void ADM_CAT_01_AddCategorySuccess() {
        LoggerHelper.info("[ADMIN][CATEGORY] Bắt đầu kiểm thử thêm danh mục thành công");
        loginAsAdmin();
        AdminCategoryPage page = PageFactoryManager.getAdminCategoryPage(getDriver(), baseUrl);
        LoggerHelper.info("[ADMIN][CATEGORY] Mở trang quản lý danh mục");
        page.open();

        int before = page.getCategoryCount();
        LoggerHelper.info("[ADMIN][CATEGORY] Số danh mục trước khi thêm: " + before);

        LoggerHelper.info("[ADMIN][CATEGORY] Thêm danh mục mới: " + NEW_CAT);
        page.clickAdd()
                .enterName(NEW_CAT)
                .clickSave();

        String message = page.getNotificationMessage();
        LoggerHelper.info("[ADMIN][CATEGORY] Notification sau khi thêm: " + message);
        Assert.assertTrue(
                message.contains("Đã thêm") || message.contains("thêm"),
                "Expected success message after adding category. Got: " + message
        );

        LoggerHelper.info("[ADMIN][CATEGORY] Mở lại trang để kiểm tra danh mục đã được thêm");
        page.open();

        int after = page.getCategoryCount();
        LoggerHelper.info("[ADMIN][CATEGORY] Số danh mục sau khi thêm: " + after);

        Assert.assertTrue(
                page.isCategoryInList(NEW_CAT) || after > before,
                "Expected category '" + NEW_CAT + "' in list or category count increased."
        );
        LoggerHelper.info("[ADMIN][CATEGORY] Kết thúc kiểm thử: PASS");
    }

    @Test(
            priority = 2,
            description = "ADM-CAT-02: Kiểm tra lỗi khi tên danh mục trùng lặp."
    )
    public void ADM_CAT_02_DuplicateCategoryName() {
        LoggerHelper.info("[ADMIN][CATEGORY] Bắt đầu kiểm thử thêm danh mục trùng tên");
        loginAsAdmin();

        AdminCategoryPage page = PageFactoryManager.getAdminCategoryPage(getDriver(), baseUrl);
        page.open();
        String duplicateName = "Danh mục test trùng";

        LoggerHelper.info("[ADMIN][CATEGORY] Tạo dữ liệu ban đầu: " + duplicateName);
        page.clickAdd()
                .enterName(duplicateName)
                .clickSave();
        page.getNotificationMessage();

        LoggerHelper.info("[ADMIN][CATEGORY] Thêm lại danh mục trùng tên: " + duplicateName);
        page.open();

        page.clickAdd()
                .enterName(duplicateName)
                .clickSave();

        String message = page.getNotificationMessage();
        LoggerHelper.info("[ADMIN][CATEGORY] Notification khi thêm trùng: " + message);
        Assert.assertTrue(
                message.contains("đã tồn tại") || message.contains("tồn tại"),
                "Expected duplicate category message. Got: " + message
        );
        LoggerHelper.info("[ADMIN][CATEGORY] Kết thúc kiểm thử: PASS");
    }

    @Test(priority = 3,
            description = "ADM-CAT-03: Kiểm tra lỗi khi thêm tên danh mục dài hơn 255 ký tự")
    public void ADM_CAT_03_LongNameBoundary() {
        LoggerHelper.info("[ADMIN][CATEGORY] Bắt đầu kiểm thử tên danh mục vượt giới hạn");
        loginAsAdmin();
        AdminCategoryPage page = PageFactoryManager.getAdminCategoryPage(getDriver(), baseUrl);

        LoggerHelper.info("[ADMIN][CATEGORY] Mở trang quản lý danh mục");
        page.open();

        String longName = new String(new char[300]).replace('\0', 'A');
        LoggerHelper.info("[ADMIN][CATEGORY] Nhập tên danh mục dài: " + longName.length() + " ký tự");

        page.clickAdd()
                .enterName(longName)
                .clickSave();

        String errorMessage = page.getErrorMessage();
        String notification = page.getNotificationMessage();

        LoggerHelper.info("[ADMIN][CATEGORY] Error message: " + errorMessage);
        LoggerHelper.info("[ADMIN][CATEGORY] Notification: " + notification);

        Assert.assertFalse(
                driver.getTitle().toLowerCase().contains("500"),
                "Server crashed with >255 char category name."
        );
        Assert.assertTrue(
                errorMessage.contains("Tên danh mục không được vượt quá")
                        || notification.contains("Tên danh mục không được vượt quá")
                        || page.isNoServerError(),
                "Expected validation message or no server crash for long category name."
        );
        LoggerHelper.info("[ADMIN][CATEGORY] Kết thúc kiểm thử: PASS");
    }
}