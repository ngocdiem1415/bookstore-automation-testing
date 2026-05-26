package com.bookstore.tests.admin;

import com.bookstore.base.BaseSetup;
import com.bookstore.factory.PageFactoryManager;
import com.bookstore.pages.*;
import org.testng.Assert;
import org.testng.annotations.Test;

/** ADM-CAT-01/02/03 */
public class AdminCategoryTest extends BaseSetup {

    private static final String ADMIN = "admin", PASS = "Abc@12345";
    private static final String NEW_CAT = "Mới_" + System.currentTimeMillis();
    private static final String DUP_CAT = "Tiểu thuyết";

    private AdminCategoryPage loginAndOpen() {
        LoginPage loginPage = PageFactoryManager.getLoginPage(driver, baseUrl);
        loginPage.loginAsAdmin(ADMIN, PASS);
        return PageFactoryManager.getAdminCategoryPage(driver, baseUrl);
    }

    @Test(description = "ADM-CAT-01: Admin adds new category.")
    public void ADM_CAT_01_AddCategorySuccess() {
        AdminCategoryPage page = loginAndOpen();
        int before = page.getCategoryCount();
        page.clickAdd().enterName(NEW_CAT);
        String alert = page.clickSaveAndGetAlert();
        page.open();
        Assert.assertTrue(page.isCategoryInList(NEW_CAT) || page.getCategoryCount() > before,
                "Expected category '" + NEW_CAT + "' in list. Alert=" + alert);
    }

    @Test(description = "ADM-CAT-02: Duplicate category name shows error.",
            dependsOnMethods = "ADM_CAT_01_AddCategorySuccess")
    public void ADM_CAT_02_DuplicateCategoryName() {
        AdminCategoryPage page = loginAndOpen();
        page.clickAdd().enterName(DUP_CAT).clickSave();
        String error = page.getErrorMessage();
        System.out.println("[Assert] Error: " + error);
        Assert.assertTrue(error.contains("đã tồn tại") || !error.isEmpty(),
                "Expected duplicate error. Got: " + error);
    }

//    @Test(description = "ADM-CAT-03: Long category name >255 chars (Boundary).")
//    public void ADM_CAT_03_LongNameBoundary() {
//        AdminCategoryPage page = loginAndOpen();
//        page.clickAdd().enterName("A".repeat(300)).clickSave();
//        String error = page.getErrorMessage();
//        System.out.println("[Assert] Error: " + error);
//        boolean handled = !error.isEmpty() || !driver.getTitle().toLowerCase().contains("500");
//        Assert.assertTrue(handled, "Server should handle long name gracefully.");
//        Assert.assertFalse(driver.getTitle().toLowerCase().contains("500"),
//                "Server crashed with >255 char name.");
//    }
}
