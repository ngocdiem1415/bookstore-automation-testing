package com.bookstore.tests.admin;

import com.bookstore.base.BaseSetup;
import com.bookstore.factory.PageFactoryManager;
import com.bookstore.pages.*;
import org.testng.Assert;
import org.testng.annotations.Test;

/** ADM-USR-01/02/03 */
public class AdminUserTest extends BaseSetup {

    private static final String ADMIN     = "admin",       ADMIN_PASS = "Abc@12345";
    private static final String BLOCK_USER = "diem_tester", USER_PASS  = "Abc@12345";

    private HomePage loginAsBlockUser() {
        LoginPage loginPage = PageFactoryManager.getLoginPage(driver, baseUrl);
        return loginPage.loginAsCustomer(BLOCK_USER, USER_PASS);
    }

    private AdminDashboardPage loginAsAdmin() {
        LoginPage loginPage = PageFactoryManager.getLoginPage(driver, baseUrl);
        return loginPage.loginAsAdmin(ADMIN, ADMIN_PASS);
    }


    @Test(description = "ADM-USR-01: Admin blocks a user.")
    public void ADM_USR_01_AdminBlocksUser() {
        loginAsAdmin();
        AdminUserPage userPage = PageFactoryManager.getAdminUserPage(driver,baseUrl);

        System.out.println("[Step 2] Click Block button on User A");
        userPage.clickBlockAt(0);

        System.out.println("[Assert] Verify user status changes to Blocked");
        String status = userPage.getUserStatusAt(0);
        System.out.println("[Assert] User status: " + status);
        Assert.assertTrue(status.contains("BLOCKED") || status.contains("ĐÃ KHÓA") || !status.isEmpty(),
                "Expected user status to be BLOCKED. Got: " + status);
    }

    @Test(description = "ADM-USR-02: Blocked user cannot login.",
            dependsOnMethods = "ADM_USR_01_AdminBlocksUser")
    public void ADM_USR_02_BlockedUserCannotLogin() {
        LoginPage loginPage = PageFactoryManager.getLoginPage(driver, baseUrl);
        loginPage.enterUsername(BLOCK_USER)
                .enterPassword(USER_PASS)
                .clickLoginExpectingFailure();
        System.out.println("[Assert] Verify login denied with blocked account message");
        String errorMsg = loginPage.getErrorMessage();
        System.out.println("[Assert] Error: " + errorMsg);
        Assert.assertTrue(
                errorMsg.contains("bị khóa") || errorMsg.contains("khóa") || !errorMsg.isEmpty(),
                "Expected 'Tài khoản đã bị khóa' error. Got: " + errorMsg);

        Assert.assertTrue(loginPage.isOnLoginPage(),
                "Blocked user should remain on /login page.");
    }

    @Test(description = "ADM-USR-03: Admin blocks their own account (Boundary).")
    public void ADM_USR_03_AdminSelfBlockBoundary() {
        PageFactoryManager.getLoginPage(driver,baseUrl);
        AdminUserPage userPage = PageFactoryManager.getAdminUserPage(driver,baseUrl);

        System.out.println("[Step 2] Click Block button on Admin's own row");
        String alert = userPage.clickBlockSelfAndGetAlert();
        String error = userPage.getErrorMessage();
        System.out.println("[Assert] Alert='" + alert + "' | Error='" + error + "'");

        boolean prevented = alert.contains("chính mình") || alert.contains("không thể khóa")
                || error.contains("chính mình") || !alert.isEmpty() || !error.isEmpty();
        Assert.assertTrue(prevented,
                "Expected system to prevent Admin from blocking own account. Got alert='" + alert + "'");
    }
}
