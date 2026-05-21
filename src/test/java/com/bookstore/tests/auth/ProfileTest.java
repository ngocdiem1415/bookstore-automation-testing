package com.bookstore.tests.auth;

import com.bookstore.base.BaseSetup;
import com.bookstore.factory.PageFactoryManager;
import com.bookstore.pages.LoginPage;
import com.bookstore.pages.ProfilePage;
import com.bookstore.utils.DataHelper;
import com.bookstore.utils.JsonDataProvider;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Map;

public class ProfileTest extends BaseSetup {

    @Test(
            priority = 1,
            dataProvider = "GlobalJsonFeeder",
            dataProviderClass = JsonDataProvider.class,
            description = "AUTH-PRO-01: Verify user can update profile details successfully."
    )
    public void AUTH_PRO_01_UpdateProfileSuccess(Map<String, String> data) {
        LoginPage loginPage = PageFactoryManager.getLoginPage(driver, baseUrl);
        loginPage.open();
        loginPage.loginAsCustomer(
                DataHelper.getValue("existing.username"),
                DataHelper.getValue("existing.password"));

        ProfilePage profilePage = new ProfilePage(driver, baseUrl);
        profilePage.open();

        profilePage.enterPhone(data.get("test_phone"))
                .enterBirthdate(data.get("test_birthdate"))
                .selectGender(data.get("test_gender"));

        profilePage.clickSave();
        String alertText = profilePage.getAndAcceptSuccessAlert();
        Assert.assertTrue(alertText.contains(data.get("expected_alert")),
                "[FAIL] Thông điệp lưu hồ sơ không đúng như mong đợi! Thực tế: " + alertText);
    }

    @Test(
            priority = 2,
            dataProvider = "GlobalJsonFeeder",
            dataProviderClass = JsonDataProvider.class,
            description = "AUTH-PRO-02: Verify user cannot update profile with blank mandatory fields."
    )
    public void AUTH_PRO_02_UpdateProfileBlankUsername(Map<String, String> data) {
        LoginPage loginPage = PageFactoryManager.getLoginPage(driver, baseUrl);
        loginPage.open();
        loginPage.loginAsCustomer(data.get("username"), data.get("password"));
        ProfilePage profilePage = new ProfilePage(driver, baseUrl);
        profilePage.open();

        profilePage.clearUsername();
        profilePage.clickSave();
        Assert.assertTrue(profilePage.isFormSubmitPrevented(),
                "[FAIL] Form vẫn được gửi đi thành công hoặc HTML5 validation không kích hoạt khi username trống!");
    }

    @Test(
            priority = 3,
            dataProvider = "GlobalJsonFeeder",
            dataProviderClass = JsonDataProvider.class,
            description = "AUTH-PRO-03: Verify uploading avatar exceeding size limit (Boundary)."
    )
    public void AUTH_PRO_03_UploadAvatarExceedsSizeLimit(Map<String, String> data) {
        LoginPage loginPage = PageFactoryManager.getLoginPage(driver, baseUrl);
        loginPage.open();
        loginPage.loginAsCustomer(data.get("username"), data.get("password"));
        ProfilePage profilePage = new ProfilePage(driver, baseUrl);
        profilePage.open();

        String relativePath = data.get("file_path"); // Đường dẫn lấy từ file JSON
        String absoluteLargeFilePath = System.getProperty("user.dir") + relativePath;
        profilePage.uploadAvatar(absoluteLargeFilePath);
        profilePage.clickSave();
        String errorMsg = profilePage.getAvatarUploadErrorAlert();
        Assert.assertTrue(errorMsg.contains(data.get("expected_error")),
                "[FAIL] Hệ thống không hiển thị cảnh báo lỗi upload ảnh quá dung lượng quy định! Thực tế: " + errorMsg);
    }
}