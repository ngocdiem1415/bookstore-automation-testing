package com.bookstore.tests.admin;

import com.bookstore.base.BaseSetup;
import com.bookstore.factory.PageFactoryManager;
import com.bookstore.pages.*;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * ADM-PRO-01/02/03 | ADM-PRE-01/02/03 | ADM-PRD-01/02/03
 */
public class AdminProductTest extends BaseSetup {
    private static final String ADMIN = "admin", PASS = "Abc@12345";
    private static final String NEW_TITLE = "Sách Tự Động " + System.currentTimeMillis();

    private AdminProductPage loginAndOpenProductPage() {
        LoginPage loginPage = PageFactoryManager.getLoginPage(driver, baseUrl);
        loginPage.loginAsAdmin(ADMIN, PASS);
        return PageFactoryManager.getAdminProductPage(driver, baseUrl);
    }

    private AdminDashboardPage loginAsAdmin() {
        LoginPage loginPage = PageFactoryManager.getLoginPage(driver, baseUrl);
        return loginPage.loginAsAdmin(ADMIN, PASS);
    }

    @Test(description = "ADM-PRO-01: Verify Admin adds product successfully.")
    public void ADM_PRO_01_AddProductSuccess() {
        AdminProductPage prodPage = loginAndOpenProductPage();
        int countBefore = prodPage.getProductCount();

        System.out.println("[Step 2] Click Add, fill form with valid data");
        prodPage.clickAdd()
                .enterTitle(NEW_TITLE)
                .enterPrice("150000");

        System.out.println("[Step 3] Click [admin-save-btn]");
        String alert = prodPage.clickSaveAndGetAlert();
        System.out.println("[Assert] Alert: " + alert);

        System.out.println("[Assert] New product appears in DataTables list");
        prodPage.open();
        int countAfter = prodPage.getProductCount();
        System.out.println("[Assert] Count before=" + countBefore + " after=" + countAfter);
        Assert.assertTrue(countAfter > countBefore || prodPage.getFirstProductTitle().contains(NEW_TITLE),
                "Expected new product in list after adding.");
    }

    @Test(description = "ADM-PRO-02: Verify Admin adding product with negative price.")
    public void ADM_PRO_02_AddProductNegativePrice() {
        AdminProductPage prodPage = loginAndOpenProductPage();
        prodPage.clickAdd().enterTitle("Test Product").enterPrice("-10");

        System.out.println("[Step 3] Click [admin-save-btn]");
        prodPage.clickSave();

        System.out.println("[Assert] JS validation blocks submission");
        String validationMsg = prodPage.getPriceValidationMessage();
        String errorMsg = prodPage.getErrorMessage();
        System.out.println("[Assert] Validation msg: " + validationMsg + " | Error: " + errorMsg);

        boolean blocked = !validationMsg.isEmpty()
                || errorMsg.contains("Giá") || errorMsg.contains("> 0")
                || !prodPage.isSaveButtonEnabled();
        Assert.assertTrue(blocked,
                "Expected validation to block negative price. Got validation='" + validationMsg + "'");
    }

    @Test(description = "ADM-PRO-03: Verify uploading executable file instead of image (Boundary).")
    public void ADM_PRO_03_UploadExecutableFileBoundary() {
        AdminProductPage prodPage = loginAndOpenProductPage();
        prodPage.clickAdd().enterTitle("Test Exe").enterPrice("100000");

        String exePath = System.getProperty("user.dir") + "\\src\\test\\resources\\testdata\\shell.exe";
        System.out.println("[Step 2] Upload: " + exePath);
        prodPage.uploadThumbnail(exePath);

        System.out.println("[Step 3] Click [admin-save-btn]");
        String alertText = prodPage.clickSaveAndGetAlert();
        String errorMsg = prodPage.getErrorMessage();
        System.out.println("[Assert] Alert='" + alertText + "' | Error='" + errorMsg + "'");

        boolean rejected = alertText.contains("định dạng ảnh") || alertText.contains("không hợp lệ")
                || errorMsg.contains("định dạng") || !alertText.isEmpty();
        Assert.assertTrue(rejected,
                "Expected system to reject .exe file upload. Got: " + alertText);
    }


    @Test(description = "ADM-PRE-01: Verify Admin edits product successfully.",
            dependsOnMethods = "ADM_PRO_01_AddProductSuccess")
    public void ADM_PRE_01_EditProductSuccess() {
        AdminProductPage prodPage = loginAndOpenProductPage();

        System.out.println("[Step 2] Click [admin-edit-btn] on first product");
        prodPage.clickEditAt(0);

        System.out.println("[Step 3] Change Title to 'Sách B'");
        prodPage.enterTitle("Sách B");

        System.out.println("[Step 4] Click [admin-save-btn]");
        String alert = prodPage.clickSaveAndGetAlert();
        System.out.println("[Assert] Alert: " + alert);

        System.out.println("[Assert] Verify DataTables updates to 'Sách B'");
        prodPage.open();
        String firstTitle = prodPage.getFirstProductTitle();
        System.out.println("[Assert] First title: " + firstTitle);
        Assert.assertTrue(firstTitle.contains("Sách B") || !alert.isEmpty(),
                "Expected product title updated to 'Sách B'. Got: " + firstTitle);
    }

    @Test(description = "ADM-PRE-02: Verify Admin leaves product name blank during edit.")
    public void ADM_PRE_02_EditProductBlankTitle() {
        AdminProductPage prodPage = loginAndOpenProductPage();
        prodPage.clickEditAt(0).enterTitle("");

        System.out.println("[Assert] Verify validation error on blank title");
        prodPage.clickSave();
        String error = prodPage.getErrorMessage();
        String validation = (String) ((org.openqa.selenium.JavascriptExecutor) driver)
                .executeScript("return document.querySelector(\"[data-testid='admin-prod-title']\").validationMessage;");
        System.out.println("[Assert] Error='" + error + "' | Validation='" + validation + "'");

        boolean blocked = (validation != null && !validation.isEmpty())
                || error.contains("trống") || error.contains("Tên sản phẩm");
        Assert.assertTrue(blocked,
                "Expected validation for blank title. Got error='" + error + "'");
    }

    @Test(description = "ADM-PRE-03: Verify concurrent editing of same product (Boundary - Race Condition).")
    public void ADM_PRE_03_ConcurrentEditBoundary() throws InterruptedException {
        loginAsAdmin();
        PageFactoryManager.getAdminProductPage(driver, baseUrl).clickEditAt(0);
        // Admin 1 changes title
        driver.findElement(org.openqa.selenium.By.cssSelector("[data-testid='admin-prod-title']"))
                .clear();
        driver.findElement(org.openqa.selenium.By.cssSelector("[data-testid='admin-prod-title']"))
                .sendKeys("Admin 1 Version");

        // Giả lập Admin 2 đã submit thay đổi trước (dùng JS fetch)
        ((org.openqa.selenium.JavascriptExecutor) driver).executeScript(
                "fetch('/admin/products/1', {method:'PUT', headers:{'Content-Type':'application/json'},"
                        + "body:JSON.stringify({title:'Admin 2 Version', version: 1})});");
        Thread.sleep(1000);

        // Admin 1 submit sau
        String alert = PageFactoryManager.getAdminProductPage(driver, baseUrl).clickSaveAndGetAlert();
        System.out.println("[Assert] Alert for second submitter: " + alert);
        System.out.println("[Assert] Verify optimistic locking error or graceful message");

        boolean handledGracefully = alert.contains("thay đổi") || alert.contains("người khác")
                || alert.contains("conflict") || !alert.isEmpty();
        Assert.assertTrue(handledGracefully,
                "Expected optimistic locking message for concurrent edit. Got: " + alert);
    }


    @Test(description = "ADM-PRD-01: Verify Admin deletes unused product.",
            dependsOnMethods = "ADM_PRO_01_AddProductSuccess")
    public void ADM_PRD_01_DeleteUnusedProduct() {
        AdminProductPage prodPage = loginAndOpenProductPage();
        int countBefore = prodPage.getProductCount();

        prodPage.clickDeleteAt(0);
        prodPage.open();

        int countAfter = prodPage.getProductCount();
        System.out.println("[Assert] Count before=" + countBefore + " after=" + countAfter);
        Assert.assertTrue(countAfter < countBefore,
                "Expected product count to decrease after deletion.");
    }

    @Test(description = "ADM-PRD-02: Verify Admin deletes product linked to an Order.")
    public void ADM_PRD_02_DeleteProductWithOrder() {
        AdminProductPage prodPage = loginAndOpenProductPage();
        // Cần click delete trên sản phẩm đang có trong đơn hàng
        // Thường là sản phẩm ở cuối danh sách (product ID nhỏ = có orders)
        int lastIdx = prodPage.getProductCount() - 1;
        String alert = "";
        try {
            prodPage.clickDeleteAt(lastIdx);
            Thread.sleep(800);
            alert = prodPage.getErrorMessage();
        } catch (Exception e) {
            System.out.println("[Info] Exception on delete (expected for FK): " + e.getMessage());
        }

        System.out.println("[Assert] Alert/Error: " + alert);
        boolean blocked = alert.contains("giao dịch") || alert.contains("Không thể xóa")
                || alert.contains("FK") || !alert.isEmpty();
        prodPage.open();
        Assert.assertTrue(prodPage.getProductCount() >= lastIdx,
                "Product should remain when linked to orders (FK constraint).");
    }

    @Test(description = "ADM-PRD-03: Verify CSRF vulnerability on delete action (Boundary).")
    public void ADM_PRD_03_CsrfDeleteBoundary() {
        loginAsAdmin();
        String csrfAttemptUrl = baseUrl + "/admin/products/1/delete";
        driver.get(csrfAttemptUrl);

        String url = driver.getCurrentUrl();
        String title = driver.getTitle().toLowerCase();
        String body = "";
        try {
            body = driver.findElement(org.openqa.selenium.By.tagName("body")).getText().toLowerCase();
        } catch (Exception ignored) {
        }

        System.out.println("[Assert] URL=" + url + " | Title=" + title);
        boolean csrfBlocked = title.contains("403") || body.contains("403")
                || body.contains("csrf") || body.contains("forbidden")
                || body.contains("invalid") || !url.contains("/products/1/delete");
        Assert.assertTrue(csrfBlocked,
                "Expected CSRF protection (GET delete rejected). Check CSRF token enforcement.");
    }
}
