package com.bookstore.tests.product;

import com.bookstore.base.BaseSetup;
import com.bookstore.factory.PageFactoryManager;
import com.bookstore.pages.ProductDetailPage;
import com.bookstore.pages.ProductListPage;
import com.bookstore.utils.JsonDataProvider;
import com.bookstore.utils.LoggerHelper;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Map;


public class ProductDetailTest extends BaseSetup {

    @Test(priority = 1,
            description = "PROD-DET-01: Xác minh thông tin hiển thị của sản phẩm hợp lệ.")
    public void PROD_DET_01_ValidProductDetail() {
        LoggerHelper.info("[PRODUCT][DETAIL] Bắt đầu kiểm thử xem chi tiết sản phẩm hợp lệ");
        ProductListPage listPage = PageFactoryManager.getProductListPage(getDriver(), baseUrl);
        LoggerHelper.info("[PRODUCT][DETAIL] Mở trang danh sách sản phẩm");
        listPage.open();

        LoggerHelper.info("[PRODUCT][DETAIL] Click sản phẩm đầu tiên trong danh sách");
        ProductDetailPage detailPage = listPage.clickBookAt(0);

        String currentUrl = detailPage.getCurrentUrl();
        LoggerHelper.info("[PRODUCT][DETAIL] URL hiện tại: " + currentUrl);
        Assert.assertTrue(currentUrl.contains("/books/"),
                "Expected URL to contain /books/{id}. Actual: " + currentUrl);

        LoggerHelper.info("[PRODUCT][DETAIL] Kiểm tra thumbnail hiển thị");
        Assert.assertTrue(detailPage.isThumbnailDisplayed(), "Thumbnail not displayed");

        LoggerHelper.info("[PRODUCT][DETAIL] Kiểm tra title không rỗng");
        Assert.assertFalse(detailPage.getTitle().isEmpty(), "Title is empty");

        LoggerHelper.info("[PRODUCT][DETAIL] Kiểm tra price không rỗng");
        Assert.assertFalse(detailPage.getPrice().isEmpty(), "Price is empty");

        LoggerHelper.info("[PRODUCT][DETAIL] Kiểm tra description không rỗng");
        Assert.assertFalse(detailPage.getDescription().isEmpty(), "Description is empty");
    }

    @Test(priority = 2,
            dataProvider = "GlobalJsonFeeder",
            dataProviderClass = JsonDataProvider.class,
            description = "PROD-DET-02:Kiểm tra truy cập sản phẩm không tồn tại.")
    public void PROD_DET_02_HiddenProductReturns404(Map<String, String> data) {
        String hiddenProductId = data.get("hidden_product_id");

        LoggerHelper.info("[PRODUCT][DETAIL] Bắt đầu kiểm thử truy cập sản phẩm ẩn hoặc không tồn tại");
        LoggerHelper.info("[PRODUCT][DETAIL] Product ID kiểm thử: " + hiddenProductId);

        ProductDetailPage detailPage = PageFactoryManager.getProductDetailPage(getDriver(), baseUrl);
        LoggerHelper.info("[PRODUCT][DETAIL] Mở trang chi tiết sản phẩm với ID: " + hiddenProductId);
        detailPage.openById(hiddenProductId);

        boolean is404 = detailPage.is404Page();
        boolean isSafe = detailPage.isPageSafe();
        String title = detailPage.getPageTitle();

        LoggerHelper.info("[PRODUCT][DETAIL] Page title: " + title);
        LoggerHelper.info("[PRODUCT][DETAIL] is404Page = " + is404);
        LoggerHelper.info("[PRODUCT][DETAIL] isPageSafe = " + isSafe);

        Assert.assertTrue(is404 || isSafe,
                "Expected 404 page for hidden product ID " + data.get("HIDDEN_PRODUCT_ID")
                        + ". Got title: " + title);
        LoggerHelper.info("[PRODUCT][DETAIL] Hệ thống xử lý sản phẩm ẩn/không tồn tại an toàn");

    }

    @Test(
            priority = 3,
            dataProvider = "GlobalJsonFeeder",
            dataProviderClass = JsonDataProvider.class,
            description = "PROD-DET-03: Kiểm thử SQL Injection trên tham số ID sản phẩm"
    )
    public void PROD_DET_03_SqlInjectionIdBoundary(Map<String, String> data) {
        String rawUrl = getBaseUrl() + data.get("sqli_url");

        LoggerHelper.info("[PRODUCT][DETAIL] Bắt đầu kiểm thử SQL Injection trên tham số ID sản phẩm");
        LoggerHelper.info("[PRODUCT][DETAIL] URL SQL Injection: " + rawUrl);
        ProductDetailPage detailPage = PageFactoryManager.getProductDetailPage(driver, baseUrl);

        LoggerHelper.info("[PRODUCT][DETAIL] Điều hướng tới URL SQL Injection");
        detailPage.navigateTo(rawUrl);

        LoggerHelper.info("[PRODUCT][DETAIL] Kiểm tra hệ thống không crash hoặc trả lỗi 500");
        Assert.assertTrue(detailPage.isPageSafe(),
                "Server crashed or exposed DB for SQLi ID parameter. Critical security issue.");

        String pageTitle = detailPage.getPageTitle();
        String currentUrl = detailPage.getCurrentUrl();
        LoggerHelper.info("[PRODUCT][DETAIL] Page title: " + pageTitle);
        LoggerHelper.info("[PRODUCT][DETAIL] Current URL: " + currentUrl);

        Assert.assertFalse(pageTitle.toLowerCase().contains("500"),
                "HTTP 500 returned for SQLi attempt - possible vulnerability.");
        LoggerHelper.info("[PRODUCT][DETAIL] Hệ thống xử lý SQL Injection ID an toàn");

    }
}
