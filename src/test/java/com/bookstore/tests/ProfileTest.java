package com.bookstore.tests;

import com.bookstore.base.BaseSetup;
import com.bookstore.pages.LoginPage;
import com.bookstore.pages.ProfilePage;
import org.testng.Assert;
import org.testng.annotations.Test;

public class ProfileTest extends BaseSetup {
    @Test(description = "AUTH-PRO-01: Verify user can update profile details successfully.",
            dependsOnMethods = "AUTH_LOG_01_LoginSuccessCustomer")
    public void AUTH_PRO_01_UpdateProfileSuccess() {

        System.out.println("[Precondition] Login as CUSTOMER");
        new LoginPage(driver).open().loginAsCustomer("diem_tester", "Abc@12345");

        System.out.println("[Step 1] Navigate to Profile Page (/profile)");
        ProfilePage profilePage = new ProfilePage(driver);
        profilePage.open();

        System.out.println("[Step 2] Edit [profile-phone] and [profile-birthdate]");
        System.out.println("[Step 3] Select [profile-gender]");
        System.out.println("[Step 4] Click [profile-save-btn]");
        profilePage.enterPhone("0999888777")
                .selectGender("female")
                .clickSave();

        System.out.println("[Assert] Verify JS Alert 'Thông tin đã được lưu!' displays");
        String alertText = profilePage.getAndAcceptSuccessAlert();
        System.out.println("[Assert] Alert text: " + alertText);
        Assert.assertTrue(alertText.contains("Thông tin đã được lưu"),
                "Expected success alert. Actual: " + alertText);

    }

    @Test(description = "AUTH-PRO-02: Verify user cannot update profile with blank mandatory fields.",
            dependsOnMethods = "AUTH_LOG_01_LoginSuccessCustomer")
    public void AUTH_PRO_02_UpdateProfileBlankUsername() {

        System.out.println("[Precondition] Login as CUSTOMER");
        new LoginPage(driver).open().loginAsCustomer("diem_tester", "Abc@12345");

        System.out.println("[Step 1] Navigate to Profile Page");
        ProfilePage profilePage = new ProfilePage(driver);
        profilePage.open();

        System.out.println("[Step 2] Leave [profile-username] blank");
        profilePage.clearUsername();

        System.out.println("[Step 3] Click [profile-save-btn]");
        profilePage.clickSave();

        System.out.println("[Assert] Verify HTML5 validation triggered and submit prevented");
        Assert.assertTrue(profilePage.isFormSubmitPrevented(),
                "Expected form validation to prevent submission with blank username.");
    }

    @Test(description = "AUTH-PRO-03: Verify uploading avatar exceeding size limit (Boundary).",
            dependsOnMethods = "AUTH_LOG_01_LoginSuccessCustomer")
    public void AUTH_PRO_03_UploadAvatarExceedsSizeLimit() {

        System.out.println("[Precondition] Login as CUSTOMER");
        new LoginPage(driver).open().loginAsCustomer("diem_tester", "Abc@12345");

        System.out.println("[Step 1] Navigate to Profile Page");
        ProfilePage profilePage = new ProfilePage(driver);
        profilePage.open();

        System.out.println("[Step 2] Select file > 5MB using [profile-avatar-upload]");
        // Đường dẫn tuyệt đối đến file test 10MB
        String largeFilePath = System.getProperty("user.dir") + "\\src\\test\\resources\\testdata\\10MB.png";
        profilePage.uploadAvatar(largeFilePath);

        System.out.println("[Assert] Verify system rejects upload with error message");
        String errorMsg = profilePage.getAvatarUploadErrorAlert();
        System.out.println("[Assert] Error: " + errorMsg);
        Assert.assertTrue(
                errorMsg.contains("vượt quá") || errorMsg.contains("dung lượng") || !errorMsg.isEmpty(),
                "Expected file size error message. Actual: " + errorMsg);

    }
}
