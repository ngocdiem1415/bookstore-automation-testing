package com.bookstore.tests.product;

import com.bookstore.base.BaseSetup;
import com.bookstore.pages.ProductListPage;
import com.bookstore.utils.JsonDataProvider;
import com.bookstore.utils.LoggerHelper;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Map;

public class ProductNavigateTest extends BaseSetup {

    @Test(
            priority = 1,
            dataProvider = "GlobalJsonFeeder",
            dataProviderClass = JsonDataProvider.class,
            description = "PROD-NAV-01: Verify filtering by valid category shows books."
    )
    public void PROD_NAV_01_ValidCategoryShowsBooks(Map<String, String> data) {
        LoggerHelper.info("[PRODUCT][NAVIGATE] Bắt đầu kiểm thử lọc sản phẩm theo danh mục hợp lệ");

        ProductListPage listPage = new ProductListPage(getDriver(), baseUrl);
        LoggerHelper.info("[PRODUCT][NAVIGATE] Mở trang danh sách sản phẩm");
        listPage.open();

        String category = data.get("category");
        LoggerHelper.info("[PRODUCT][NAVIGATE] Lọc theo danh mục: " + category);
        listPage.filterByCategory(category);

        int count = listPage.getBookCount();
        LoggerHelper.info("[PRODUCT][NAVIGATE] Số lượng sản phẩm sau khi lọc: " + count);

        Assert.assertTrue(count > 0,
                "Expected books to be displayed for category Tiểu thuyết.");

        LoggerHelper.info("[PRODUCT][NAVIGATE] Kiểm tra container sản phẩm hiển thị");
        Assert.assertTrue(listPage.isProductContainerDisplayed(),
                "Product container should be visible.");

        LoggerHelper.info("[PRODUCT][NAVIGATE] Lọc danh mục Tiểu thuyết thành công");
    }

    @Test(
            priority = 2,
            dataProvider = "GlobalJsonFeeder",
            dataProviderClass = JsonDataProvider.class,
            description = "PROD-NAV-03: Verify switching categories updates product list."
    )
    public void PROD_NAV_02_SwitchCategoryUpdatesProductList(Map<String, String> data) {
        LoggerHelper.info("[PRODUCT][NAVIGATE] Bắt đầu kiểm thử chuyển đổi danh mục sản phẩm");

        ProductListPage listPage = new ProductListPage(getDriver(), baseUrl);
        LoggerHelper.info("[PRODUCT][NAVIGATE] Mở trang danh sách sản phẩm");
        listPage.open();

        String firstCategory = data.get("first_category");
        String secondCategory = data.get("second_category");

        LoggerHelper.info("[PRODUCT][NAVIGATE] Bắt đầu kiểm thử chuyển đổi danh mục sản phẩm");
        LoggerHelper.info("[PRODUCT][NAVIGATE] Danh mục đầu tiên: " + firstCategory);
        listPage.filterByCategory(firstCategory);
        int firstCount = listPage.getBookCount();
        String firstSignature = listPage.getVisibleBookSignature();
        Assert.assertTrue(firstCount > 0,
                "Expected books for first category.");

        LoggerHelper.info("[PRODUCT][NAVIGATE] Chuyển sang danh mục thứ hai: Kinh tế");
        listPage.filterByCategory(secondCategory);
        int secondCount = listPage.getBookCount();
        String secondSignature = listPage.getVisibleBookSignature();
        Assert.assertTrue(secondCount > 0,
                "Expected books for second category.");

        LoggerHelper.info("[PRODUCT][NAVIGATE] Kiểm tra danh sách sản phẩm thay đổi sau khi đổi danh mục");
        Assert.assertNotEquals(secondSignature, firstSignature,
                "Product list should change after switching category.");

        LoggerHelper.info("[PRODUCT][NAVIGATE] Chuyển đổi danh mục thành công");
    }
}