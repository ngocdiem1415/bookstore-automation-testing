package com.bookstore.tests.admin;

import com.bookstore.factory.PageFactoryManager;
import com.bookstore.pages.AdminUserEditPage;
import com.bookstore.pages.AdminUserPage;
import com.bookstore.pages.LoginPage;
import com.bookstore.pages.SignupPage;
import com.bookstore.utils.DataHelper;
import com.bookstore.utils.JsonDataProvider;
import com.bookstore.utils.LoggerHelper;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Map;

public class AdminUserTest extends AdminBaseTest {

    private String generateRandomPhoneNumber() {
        String[] prefixes = {"09", "03", "08", "07"};
        java.util.Random random = new java.util.Random();
        String prefix = prefixes[random.nextInt(prefixes.length)];

        StringBuilder phone = new StringBuilder(prefix);
        for (int i = 0; i < 8; i++) {
            phone.append(random.nextInt(10));
        }

        return phone.toString();
    }

    private Map<String, String> createUserForBlockTest() {
        String suffix = String.valueOf(System.currentTimeMillis()).substring(7);
        String username = "block_user_" + suffix;
        String password = "Abc12345";
        String email = "blockuser" + suffix + "@gmail.com";
        String phone = generateRandomPhoneNumber();
        LoggerHelper.info("[AUTH][USER][SETUP] Tạo tài khoản test: " + username);
        SignupPage signupPage = PageFactoryManager.getSignupPage(getDriver(), baseUrl);
        signupPage.open();

        signupPage.fillRegistrationForm(
                username,
                password,
                email,
                "female",
                "01/05/2002",
                phone
        );

        signupPage.clickSubmitExpectingSuccess();
        String alert = signupPage.getMessageAndAccept();
        Assert.assertTrue(alert.contains("Đăng ký thành công! Vui lòng kiểm tra email để xác nhận tài khoản"),
                "Lỗi: Hệ thống không hiển thị thông báo đăng kí thành công sau khi gửi thông tin hợp lệ!");
        LoggerHelper.info("[AUTH][REGISTER] Thông báo đăng kí thành công");

        Assert.assertTrue(signupPage.isOnEmailVerifyPage(), "Không chuyển sang trang xác thực OTP."
        );

        signupPage.enterVerifyCode(DataHelper.getValue("fixed.otp.code"));
        signupPage.clickVerify();
        Assert.assertTrue(
                signupPage.isRedirectedToSuccess(),
                "Tạo tài khoản test thất bại."
        );

        LoggerHelper.info("[AUTH][USER][SETUP] Tạo tài khoản thành công: " + username);
        return Map.of(
                "username", username,
                "password", password,
                "email", email,
                "phone", phone
        );
    }

    private void blockUserByUsername(String username) {
        LoggerHelper.info("[AUTH][USER][SETUP] Khóa user: " + username);
        loginAsAdmin();

        AdminUserPage userPage = PageFactoryManager.getAdminUserPage(getDriver(), baseUrl);
        userPage.open();

        int index = userPage.searchByUsername(username);
        Assert.assertTrue(index >= 0, "Không tìm thấy user để khóa: " + username);
        userPage.clickEditAt(index);

        AdminUserEditPage editPage = PageFactoryManager.getAdminUserEditPage(getDriver(), baseUrl);
        String alert = editPage.selectStatus("2")
                .clickUpdateAndGetAlert();
        LoggerHelper.info("[AUTH][USER][SETUP] Alert sau khi khóa user: " + alert);
    }

    private void deleteUserByUsername(String username) {
        LoggerHelper.info("[AUTH][USER] Xóa user test: " + username);
        loginAsAdmin();

        AdminUserPage page =
                PageFactoryManager.getAdminUserPage(getDriver(), baseUrl);
        page.open();

        int index = page.searchByUsername(username);
        if (index < 0) {
            LoggerHelper.warn(
                    "[AUTH][USER] Không tìm thấy user cần xóa: "
                            + username);
            return;
        }

        page.clickDeleteAt(index);
        String notification =
                page.confirmDeleteAndGetNotification();
        LoggerHelper.info(
                "[AUTH][USER] Kết quả xóa user: "
                        + notification);
    }

    @Test(
            priority = 1,
            description = "ADM-USR-01: Quản trị viên khóa tài khoản người dùng."
    )
    public void ADM_USR_01_AdminBlocksUser() {
        LoggerHelper.info("[AUTH][USER][ADM-USR-01] Bắt đầu kiểm thử khóa tài khoản người dùng");
        Map<String, String> user = createUserForBlockTest();
        String username = user.get("username");

        blockUserByUsername(username);
        AdminUserPage userPage = PageFactoryManager.getAdminUserPage(getDriver(), baseUrl);
        userPage.open();

        int index = userPage.searchByUsername(username);
        Assert.assertTrue(index >= 0, "Không tìm thấy user sau khi khóa: " + username);

        String status = userPage.getUserStatusAt(index);
        LoggerHelper.info("[AUTH][USER][ADM-USR-01] Trạng thái sau khi khóa: " + status);

        Assert.assertTrue(
                status.contains("Khóa"),
                "Expected user status is locked. Got: " + status
        );
        LoggerHelper.info("[AUTH][USER][ADM-USR-01] Kết thúc kiểm thử: PASS");
    }

    @Test(
            priority = 2,
            description = "ADM-USR-02: Người dùng bị khóa không thể đăng nhập."
    )
    public void ADM_USR_02_BlockedUserCannotLogin() {
        LoggerHelper.info("[AUTH][USER][ADM-USR-02] Bắt đầu kiểm thử user bị khóa không thể đăng nhập");

        Map<String, String> user = createUserForBlockTest();

        String username = user.get("username");
        String password = user.get("password");
        LoggerHelper.info("[AUTH][USER][ADM-USR-02] Khóa tài khoản test vừa tạo: " + username);
        blockUserByUsername(username);

        LoggerHelper.info("[AUTH][USER][ADM-USR-02] Mở trang login để kiểm thử đăng nhập user bị khóa");
        getDriver().get(baseUrl + "/login");

        LoginPage loginPage = PageFactoryManager.getLoginPage(getDriver(), baseUrl);

        LoggerHelper.info("[AUTH][USER][ADM-USR-02] Nhập username: " + username);
        loginPage.enterUsername(username)
                .enterPassword(password)
                .clickLoginExpectingFailure();

        String error = loginPage.getErrorMessage();

        LoggerHelper.info("[AUTH][USER][ADM-USR-02] Error message khi login: " + error);

        Assert.assertTrue(
                error.contains("Tài khoản đã bị khóa")
                        || error.contains("khóa"),
                "Expected blocked user login error. Got: " + error
        );

        Assert.assertTrue(
                loginPage.isOnLoginPage(),
                "User bị khóa phải ở lại trang login."
        );

        LoggerHelper.info("[AUTH][USER][ADM-USR-02] Kết thúc kiểm thử: PASS");
    }

    @Test(
            priority = 3,
            dataProvider = "GlobalJsonFeeder",
            dataProviderClass = JsonDataProvider.class,
            description = "ADM-USR-03: Quản trị viên không thể khóa chính tài khoản của mình."
    )
    public void ADM_USR_03_AdminSelfBlockBoundary(Map<String, String> data) {
        LoggerHelper.info("[AUTH][USER][ADM-USR-03] Bắt đầu kiểm thử admin tự khóa chính mình");
        loginAsAdmin();

        String adminUsername = data.get("username");
        AdminUserPage userPage = PageFactoryManager.getAdminUserPage(getDriver(), baseUrl);
        LoggerHelper.info("[AUTH][USER] Mở trang quản lí tài khoản");
        userPage.open();

        int index = userPage.searchByUsername(adminUsername);
        Assert.assertFalse(index >= 0, "Tìm thấy tài khoản admin hiện tại: " + adminUsername);
        LoggerHelper.info("[AUTH][USER][ADM-USR-03] Mở trang chỉnh sửa tài khoản admin đang đăng nhập");

        LoggerHelper.info("[AUTH][USER][ADM-USR-03] Kết thúc kiểm thử: PASS");
    }
}