package com.bookstore.tests.auth;

import com.bookstore.base.BaseSetup;
import com.bookstore.factory.PageFactoryManager;
import com.bookstore.pages.LoginPage;
import com.bookstore.pages.ProfilePage;
import com.bookstore.pages.components.HeaderComponent;
import com.bookstore.utils.DataHelper;
import com.bookstore.utils.JsonDataProvider;
import com.bookstore.utils.LoggerHelper;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.File;
import java.util.Map;

public class ProfileTest extends BaseSetup {

    private ProfilePage loginAsCustomerAndOpenProfile() {
        LoggerHelper.info("[AUTH][PROFILE] Khởi tạo trang đăng nhập");
        LoginPage loginPage = PageFactoryManager.getLoginPage(driver, baseUrl);

        LoggerHelper.info("[AUTH][PROFILE] Mở trang đăng nhập");
        loginPage.open();

        LoggerHelper.info("[AUTH][PROFILE] Đăng nhập bằng tài khoản CUSTOMER");
        loginPage.loginAsCustomer(
                DataHelper.getValue("existing.username"),
                DataHelper.getValue("existing.password")
        );

        LoggerHelper.info("[AUTH][PROFILE] Mở trang hồ sơ cá nhân từ header");
        HeaderComponent header = new HeaderComponent(driver, baseUrl);

        return header.navigateToProfile();
    }

    @Test(
            priority = 1,
            dataProvider = "GlobalJsonFeeder",
            dataProviderClass = JsonDataProvider.class,
            description = "AUTH-PRO-01: Verify user can update profile details successfully."
    )
    public void AUTH_PRO_01_UpdateProfileSuccess(Map<String, String> data) {
        LoggerHelper.info("[AUTH][PROFILE] Bắt đầu kiểm thử cập nhật hồ sơ thành công");

        ProfilePage profilePage = loginAsCustomerAndOpenProfile();

        LoggerHelper.info("[AUTH][PROFILE] Nhập số điện thoại mới: " + data.get("test_phone"));
        LoggerHelper.info("[AUTH][PROFILE] Nhập ngày sinh mới: " + data.get("test_birthdate"));
        LoggerHelper.info("[AUTH][PROFILE] Chọn giới tính: " + data.get("test_gender"));
        profilePage
                .enterPhone(data.get("test_phone"))
                .enterBirthdate(data.get("test_birthdate"))
                .selectGender(data.get("test_gender"));

        LoggerHelper.info("[AUTH][PROFILE] Click nút lưu hồ sơ");
        profilePage.clickSave();


        LoggerHelper.info("[AUTH][PROFILE] Kiểm tra thông báo chứa nội dung kỳ vọng: " + data.get("expected_alert"));
        String alertText = profilePage.getSuccessMessage();
        Assert.assertTrue(
                alertText.contains("Cập nhật hồ sơ thành công!"),
                "[FAIL] Lưu hồ sơ không đúng kỳ vọng"
                        + "\n  Actual            : \"" + alertText + "\""
        );
    }

    @Test(
            priority = 2,
            description = "AUTH-PRO-02: Verify user cannot update profile with blank mandatory fields."
    )
    public void AUTH_PRO_02_UpdateProfileBlankPhone() {
        LoggerHelper.info("[AUTH][PROFILE] Bắt đầu kiểm thử cập nhật hồ sơ thiếu số điện thoại");

        ProfilePage profilePage = loginAsCustomerAndOpenProfile();

        LoggerHelper.info("[AUTH][PROFILE] Xóa số điện thoại bắt buộc");
        profilePage.clearPhone();

        LoggerHelper.info("[AUTH][PROFILE] Click nút lưu hồ sơ");
        profilePage.clickSave();

        LoggerHelper.info("[AUTH][PROFILE] Kiểm tra hệ thống hiển thị lỗi số điện thoại không hợp lệ");
        String alertText = profilePage.getErrorMessage();
        Assert.assertTrue(
                alertText.contains("Số điện thoại không hợp lệ (10 số, bắt đầu bằng 0)"),
                "[FAIL] Hệ thống không báo lỗi khi thiếu thông tin"
        );
    }

    @Test(
            priority = 3,
            dataProvider = "GlobalJsonFeeder",
            dataProviderClass = JsonDataProvider.class,
            description = "AUTH-PRO-03: Verify uploading avatar exceeding size limit (Boundary)."
    )
    public void AUTH_PRO_03_UploadAvatarExceedsSizeLimit(Map<String, String> data) {
        LoggerHelper.info("[AUTH][PROFILE] Bắt đầu kiểm thử upload avatar vượt quá dung lượng cho phép");

        ProfilePage profilePage = loginAsCustomerAndOpenProfile();

        String relativePath = data.get("file_path");
        String absolutePath = System.getProperty("user.dir") + relativePath;
        LoggerHelper.info("[AUTH][PROFILE] Đường dẫn file avatar tương đối: " + relativePath);
        LoggerHelper.info("[AUTH][PROFILE] Đường dẫn file avatar tuyệt đối: " + absolutePath);

        File uploadFile = new File(absolutePath);

        LoggerHelper.info("[AUTH][PROFILE] Kiểm tra file avatar có tồn tại hay không");
        if (!uploadFile.exists()) {
            LoggerHelper.error("[AUTH][PROFILE] File avatar chưa tồn tại: " + absolutePath);
            Assert.fail("[ERROR] File \"" + absolutePath + "\" chưa tồn tại.");
            return;
        }

        LoggerHelper.info("[AUTH][PROFILE] Upload avatar vượt quá dung lượng cho phép");
        profilePage.uploadAvatar(absolutePath);

        LoggerHelper.info("[AUTH][PROFILE] Kiểm tra thông báo lỗi chứa nội dung kỳ vọng: " + data.get("expected_error"));
        String errorMsg = profilePage.getAvatarUploadErrorAlert();
        Assert.assertTrue(
                errorMsg.contains("Ảnh phải nhỏ hơn 3MB"),
                "[FAIL] Hệ thống không từ chối file ảnh vượt 1MB!"
                        + "\n  Actual            : \"" + errorMsg + "\""
        );
    }
}