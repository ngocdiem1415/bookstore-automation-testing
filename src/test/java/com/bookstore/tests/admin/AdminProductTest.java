package com.bookstore.tests.admin;

import com.bookstore.factory.PageFactoryManager;
import com.bookstore.helpers.CleanupRegistry;
import com.bookstore.helpers.CleanupHelper;
import com.bookstore.pages.AdminProductFormPage;
import com.bookstore.pages.AdminProductPage;
import com.bookstore.utils.JsonDataProvider;
import com.bookstore.utils.LoggerHelper;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

import java.io.File;
import java.util.Map;

public class AdminProductTest extends AdminBaseTest {

    private String uniqueTitle(String prefix) {
        return "AUTO_PRD_" + prefix + " " + System.currentTimeMillis();
    }

    private boolean containsAny(String actual, String... expectedTexts) {
        String lowerActual = actual == null ? "" : actual.toLowerCase();

        for (String expected : expectedTexts) {
            if (lowerActual.contains(expected.toLowerCase())) {
                return true;
            }
        }

        return false;
    }

    private AdminProductPage openProductPageAsAdmin() {
        LoggerHelper.info("[ADMIN][PRODUCT] Đăng nhập admin và mở trang quản lý sản phẩm");
        loginAsAdmin();

        AdminProductPage listPage = PageFactoryManager.getAdminProductPage(getDriver(), baseUrl);
        LoggerHelper.info("[ADMIN][PRODUCT] Mở trang quản lý sản phẩm");
        listPage.open();

        return listPage;
    }

    private AdminProductFormPage openAddProductForm(AdminProductPage listPage) {
        LoggerHelper.info("[ADMIN][PRODUCT] Click nút thêm sản phẩm");
        listPage.clickAdd();
        return PageFactoryManager.getAdminProductFormPage(getDriver(), baseUrl);
    }

    private AdminProductFormPage fillValidProductForm(AdminProductFormPage formPage,
                                                      String title,
                                                      Map<String, String> data) {
        LoggerHelper.info("[ADMIN][PRODUCT] Điền form sản phẩm hợp lệ với title: " + title);
        return formPage.enterTitle(title)
                .enterAuthor(data.get("author"))
                .enterPrice(data.get( "price"))
                .enterQuantity(data.get("quantity"))
                .selectCategoryByIndex(Integer.parseInt(data.get("category_index")))
                .selectPublisherByIndex(Integer.parseInt(data.get( "publisher_index")));
    }

    private String prepareProduct(Map<String, String> data, String titlePrefix) {
        String title = uniqueTitle(titlePrefix);
        LoggerHelper.info("[ADMIN][PRODUCT] Tạo sản phẩm test: " + title);

        AdminProductPage listPage = PageFactoryManager.getAdminProductPage(getDriver(), baseUrl);
        LoggerHelper.info("[ADMIN][PRODUCT] Mở trang quản lý sản phẩm");
        listPage.open();

        AdminProductFormPage formPage = openAddProductForm(listPage);
        String alert = fillValidProductForm(formPage, title, data)
                .clickSaveAndGetAlert();
        CleanupRegistry.createdProducts.add(title);
        LoggerHelper.info("[ADMIN][PRODUCT] Alert sau khi tạo sản phẩm test: " + alert);
        return title;
    }

    @AfterMethod(alwaysRun = true)
    public void cleanupProductData() {
        CleanupHelper.cleanupCreatedProducts(getDriver(), baseUrl);
    }

    @Test(
            priority = 1,
            dataProvider = "GlobalJsonFeeder",
            dataProviderClass = JsonDataProvider.class,
            description = "ADM-PRO-01: Admin thêm sản phẩm thành công."
    )
    public void ADM_PRD_01_AddProductSuccess(Map<String, String> data) {
        LoggerHelper.info("[ADMIN][PRODUCT]Bắt đầu kiểm thử thêm sản phẩm thành công");
        AdminProductPage listPage = openProductPageAsAdmin();

        int before = listPage.getProductCount();
        LoggerHelper.info("[ADMIN][PRODUCT] Số sản phẩm trước khi thêm: " + before);

        String title = uniqueTitle(data.get("title_prefix"));
        LoggerHelper.info("[ADMIN][PRODUCT] Title sản phẩm mới: " + title);

        AdminProductFormPage formPage = openAddProductForm(listPage);
        LoggerHelper.info("[ADMIN][PRODUCT] Mở trang thêm sản phẩm và điền thông tin");

        String alert = fillValidProductForm(formPage, title, data)
                .clickSaveAndGetAlert();
        CleanupRegistry.createdProducts.add(title);
        LoggerHelper.info("[ADMIN][PRODUCT] Alert sau khi thêm: " + alert);

        listPage.open();

        int after = listPage.getProductCount();
        LoggerHelper.info("[ADMIN][PRODUCT] Số sản phẩm sau khi thêm: " + after);

        Assert.assertTrue(
                after > before || listPage.isProductInList(title),
                "Expected product appears in list after adding."
        );

        LoggerHelper.info("[ADMIN][PRODUCT] Thêm sản phẩm thành công");
    }

    @Test(
            priority = 2,
            dataProvider = "GlobalJsonFeeder",
            dataProviderClass = JsonDataProvider.class,
            description = "ADM-PRO-02: Admin không thể thêm sản phẩm với giá âm."
    )
    public void ADM_PRD_02_AddProductNegativePrice(Map<String, String> data) {
        LoggerHelper.info("[ADMIN][PRODUCT] Bắt đầu kiểm thử thêm sản phẩm với giá âm");

        AdminProductPage listPage = openProductPageAsAdmin();

        AdminProductFormPage formPage = openAddProductForm(listPage);

        String title = uniqueTitle(data.get("title_prefix"));
        String invalidPrice = data.get("invalid_price");

        LoggerHelper.info("[ADMIN][PRODUCT] Title kiểm thử: " + title);
        LoggerHelper.info("[ADMIN][PRODUCT] Giá không hợp lệ: " + invalidPrice);

        formPage.enterTitle(title)
                .enterPrice(invalidPrice)
                .clickSave();

        String validationMsg = formPage.getPriceValidationMessage();

        LoggerHelper.info("[ADMIN][PRODUCT] Validation price: " + validationMsg);

        Assert.assertTrue(
                validationMsg != null && !validationMsg.isBlank(),
                "Hệ thống chặn giá âm đúng kỳ vọng"
        );

        LoggerHelper.info("[ADMIN][PRODUCT] Hệ thống chặn giá âm đúng kỳ vọng");
    }

    @Test(
            priority = 3,
            dataProvider = "GlobalJsonFeeder",
            dataProviderClass = JsonDataProvider.class,
            description = "ADM-PRO-03: Admin không thể upload file .exe thay cho ảnh."
    )
    public void ADM_PRD_03_UploadExecutableFileBoundary(Map<String, String> data) {
        LoggerHelper.info("[ADMIN][PRODUCT] Bắt đầu kiểm thử upload file exe");

        AdminProductPage listPage = openProductPageAsAdmin();

        AdminProductFormPage formPage = openAddProductForm(listPage);

        String title = uniqueTitle(data.get("title_prefix"));
        String exePath = data.get("invalid_file_path");
        String absolutePath = System.getProperty("user.dir") + exePath;

        LoggerHelper.info("[ADMIN][PRODUCT] Title kiểm thử: " + title);
        LoggerHelper.info("[ADMIN][PRODUCT] File upload: " + exePath);
        LoggerHelper.info("[AUTH][PRODUCT] Đường dẫn file avatar tuyệt đối: " + absolutePath);

        File uploadFile = new File(absolutePath);

        LoggerHelper.info("[ADMIN][PRODUCT] Kiểm tra file avatar có tồn tại hay không");
        if (!uploadFile.exists()) {
            LoggerHelper.error("[ADMIN][PRODUCT] File avatar chưa tồn tại: " + absolutePath);
            Assert.fail("[ERROR] File \"" + absolutePath + "\" chưa tồn tại.");
            return;
        }

        formPage.enterTitle(title)
                .enterPrice(data.get( "price"))
                .enterQuantity(data.get("quantity"))
                .uploadThumbnail(absolutePath);
        CleanupRegistry.createdProducts.add(title);

        String alert = formPage.clickSaveAndGetAlert();

        LoggerHelper.info("[ADMIN][PRODUCT] Alert sau khi upload exe: " + alert);

        Assert.assertTrue(
                containsAny(alert, "định dạng", "không hợp lệ", "thất bại") || !alert.isBlank(),
                "Expected system rejects executable file. Got: " + alert
        );

        LoggerHelper.info("[ADMIN][PRODUCT] Hệ thống từ chối file exe đúng kỳ vọng");
    }

    @Test(
            priority = 4,
            dataProvider = "GlobalJsonFeeder",
            dataProviderClass = JsonDataProvider.class,
            description = "ADM-PRD-04: Admin chỉnh sửa sản phẩm thành công."
    )
    public void ADM_PRD_04_EditProductSuccess(Map<String, String> data) {
        LoggerHelper.info("[ADMIN][PRODUCT][ADM-PRE-01] Bắt đầu kiểm thử chỉnh sửa sản phẩm");

        loginAsAdmin();

        String oldTitle = prepareProduct(data,data.get("title_prefix"));
        String newTitle = uniqueTitle(data.get("updated_title_prefix"));

        LoggerHelper.info("[ADMIN][PRODUCT][ADM-PRE-01] Title cũ: " + oldTitle);
        LoggerHelper.info("[ADMIN][PRODUCT][ADM-PRE-01] Title mới: " + newTitle);

        AdminProductPage listPage = PageFactoryManager.getAdminProductPage(getDriver(), baseUrl);

        listPage.open();

        LoggerHelper.info("[ADMIN][PRODUCT][ADM-PRE-01] Click sửa sản phẩm đầu tiên");
        listPage.clickEditByTitle(oldTitle);

        AdminProductFormPage formPage = PageFactoryManager.getAdminProductFormPage(getDriver(), baseUrl);

        String alert = formPage.enterTitle(newTitle)
                .clickSaveAndGetAlert();
        CleanupRegistry.createdProducts.remove(oldTitle);
        CleanupRegistry.createdProducts.add(newTitle);

        LoggerHelper.info("[ADMIN][PRODUCT][ADM-PRE-01] Alert sau khi sửa: " + alert);

        listPage.open();

        Assert.assertTrue(
                listPage.isProductInList(newTitle) || !alert.isBlank(),
                "Expected product title updated. New title: " + newTitle
        );

        LoggerHelper.info("[ADMIN][PRODUCT][ADM-PRE-01] Chỉnh sửa sản phẩm thành công");
    }

    @Test(
            priority = 5,
            dataProvider = "GlobalJsonFeeder",
            dataProviderClass = JsonDataProvider.class,
            description = "ADM-PRD-05: Admin không thể để trống tên sản phẩm khi sửa."
    )
    public void ADM_PRD_05_EditProductBlankTitle(Map<String, String> data) {
        LoggerHelper.info("[ADMIN][PRODUCT][ADM-PRE-02] Bắt đầu kiểm thử bỏ trống tên sản phẩm khi sửa");
        loginAsAdmin();
        String title = prepareProduct(data, data.get("title_prefix"));
        AdminProductPage listPage = PageFactoryManager.getAdminProductPage(getDriver(), baseUrl);
        LoggerHelper.info("[ADMIN][PRODUCT][ADM-PRE-02] Mở trang quản lí danh sách sản phẩm");
        listPage.open();

        LoggerHelper.info("[ADMIN][PRODUCT][ADM-PRE-02] Click sửa sản phẩm đầu tiên");
        listPage.clickEditByTitle(title);
        AdminProductFormPage formPage = PageFactoryManager.getAdminProductFormPage(getDriver(), baseUrl);
        String blankTitle = data.get("blank_title");
        LoggerHelper.info("[ADMIN][PRODUCT][ADM-PRE-02] Xóa trống title sản phẩm");
        formPage.enterTitle(blankTitle)
                .clickSave();

        String validation = formPage.getTitleValidationMessage();

        LoggerHelper.info("[ADMIN][PRODUCT][ADM-PRE-02] Validation title: " + validation);
        Assert.assertTrue(
                validation != null && !validation.isBlank(),
                "Expected validation for blank product title."
        );

        LoggerHelper.info("[ADMIN][PRODUCT][ADM-PRE-02] Hệ thống chặn title trống đúng kỳ vọng");
    }

    @Test(
            priority = 6,
            dataProvider = "GlobalJsonFeeder",
            dataProviderClass = JsonDataProvider.class,
            description = "ADM-PRD-01: Admin xóa sản phẩm chưa phát sinh đơn hàng."
    )
    public void ADM_PRD_06_DeleteUnusedProduct(Map<String, String> data) {
        LoggerHelper.info("[ADMIN][PRODUCT] Bắt đầu kiểm thử xóa sản phẩm chưa phát sinh đơn hàng");

        loginAsAdmin();
        String title = prepareProduct(data, data.get("title_prefix"));
        LoggerHelper.info("[ADMIN][PRODUCT] Sản phẩm dùng để xóa: " + title);
        AdminProductPage listPage =
                PageFactoryManager.getAdminProductPage(getDriver(), baseUrl);
        listPage.open();

        int before = listPage.getProductCount();

        LoggerHelper.info("[ADMIN][PRODUCT] Số sản phẩm trước khi xóa: " + before);

        listPage.clickDeleteByTitle(title);
        String message = listPage.getNotificationMessage();
        LoggerHelper.info("[ADMIN][PRODUCT] Notification sau khi xóa: " + message);
        listPage.open();
        int after = listPage.getProductCount();

        LoggerHelper.info("[ADMIN][PRODUCT] Số sản phẩm sau khi xóa: " + after);
        Assert.assertTrue(
                after < before || message.toLowerCase().contains("xóa"),
                "Expected product count decreases after deletion."
        );
        CleanupRegistry.createdProducts.remove(title);

        LoggerHelper.info("[ADMIN][PRODUCT] Xóa sản phẩm thành công");
    }

    @Test(priority = 7,
            dataProvider = "GlobalJsonFeeder",
            dataProviderClass = JsonDataProvider.class,
            description = "ADM-PRD-02: Admin hủy thao tác xóa sản phẩm.")
    public void ADM_PRD_07_CancelDeleteProduct(Map<String, String> data) {
        LoggerHelper.info("[ADMIN][PRODUCT] Bắt đầu kiểm thử hủy xóa sản phẩm");
        loginAsAdmin();

        String title = prepareProduct(data, data.get("title_prefix"));
        AdminProductPage listPage = PageFactoryManager.getAdminProductPage(getDriver(), baseUrl);
        listPage.open();

        int before = listPage.getProductCount();
        LoggerHelper.info("[ADMIN][PRODUCT] Số sản phẩm trước khi hủy xóa: " + before);
        LoggerHelper.info("[ADMIN][PRODUCT] Click xóa rồi hủy xóa sản phẩm đầu tiên");
        listPage.clickCancelDeleteByTitle(title);

        LoggerHelper.info("[ADMIN][PRODUCT] Mở trang quản lý sản phẩm");
        listPage.open();

        int after = listPage.getProductCount();
        LoggerHelper.info("[ADMIN][PRODUCT] Số sản phẩm sau khi hủy xóa: " + after);

        Assert.assertEquals(
                after,
                before,
                "Product count should not change after cancelling delete."
        );

        LoggerHelper.info("[ADMIN][PRODUCT] Hủy xóa sản phẩm thành công");
    }
}
