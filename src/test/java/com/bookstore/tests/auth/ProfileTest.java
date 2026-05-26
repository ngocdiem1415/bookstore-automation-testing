package com.bookstore.tests.auth;

import com.bookstore.base.BaseSetup;
import com.bookstore.factory.PageFactoryManager;
import com.bookstore.pages.LoginPage;
import com.bookstore.pages.ProfilePage;
import com.bookstore.pages.components.HeaderComponent;
import com.bookstore.utils.DataHelper;
import com.bookstore.utils.JsonDataProvider;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Map;

public class ProfileTest extends BaseSetup {

    private ProfilePage loginAsCustomerAndOpenProfile() {
        LoginPage loginPage = PageFactoryManager.getLoginPage(driver, baseUrl);
        loginPage.open();
        loginPage.loginAsCustomer(
                DataHelper.getValue("existing.username"),
                DataHelper.getValue("existing.password")
        );
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
        ProfilePage profilePage = loginAsCustomerAndOpenProfile();
        profilePage
                .enterPhone(data.get("test_phone"))
                .enterBirthdate(data.get("test_birthdate"))
                .selectGender(data.get("test_gender"));

        profilePage.clickSave();
        String alertText = profilePage.getSuccessMessage();
        Assert.assertTrue(
                alertText.contains(data.get("expected_alert")),
                "[FAIL] Thông điệp lưu hồ sơ không đúng kỳ vọng!"
                        + "\n  Expected contains : \"" + data.get("expected_alert") + "\""
                        + "\n  Actual            : \"" + alertText + "\""
        );
    }

    @Test(
            priority = 2,
            description = "AUTH-PRO-02: Verify user cannot update profile with blank mandatory fields."
    )
    public void AUTH_PRO_02_UpdateProfileBlankUsername() {
        ProfilePage profilePage = loginAsCustomerAndOpenProfile();
        profilePage.clearPhone();
        profilePage.clickSave();
        String alertText = profilePage.getErrorMessage();
        Assert.assertTrue(
                alertText.contains("Số điện thoại không hợp lệ (10 số, bắt đầu bằng 0)"),
                "[FAIL] Hệ thông không báo lỗi khi thiếu thông tin");
    }

    @Test(
            priority = 3,
            dataProvider = "GlobalJsonFeeder",
            dataProviderClass = JsonDataProvider.class,
            description = "AUTH-PRO-03: Verify uploading avatar exceeding size limit (Boundary)."
    )
    public void AUTH_PRO_03_UploadAvatarExceedsSizeLimit(Map<String, String> data) {
        ProfilePage profilePage = loginAsCustomerAndOpenProfile();
        String relativePath = data.get("file_path");
        String absolutePath = System.getProperty("user.dir") + relativePath;
        java.io.File uploadFile = new java.io.File(absolutePath);
        if (!uploadFile.exists()) {
            Assert.fail("[SKIP] File \"" + absolutePath + "\" chưa tồn tại.");
            return;
        }
        profilePage.uploadAvatar(absolutePath);
        String errorMsg = profilePage.getAvatarUploadErrorAlert();
        Assert.assertTrue(
                errorMsg.contains(data.get("expected_error")),
                "[FAIL] Hệ thống không từ chối file ảnh vượt 1MB!"
                        + "\n  Expected contains : \"" + data.get("expected_error") + "\""
                        + "\n  Actual            : \"" + errorMsg + "\""
        );
    }
}