package com.bookstore.tests.product;

import com.bookstore.base.BaseSetup;
import com.bookstore.pages.ProductListPage;
import com.bookstore.utils.LoggerHelper;
import org.testng.Assert;
import org.testng.annotations.Test;

public class ProductSortTest extends BaseSetup {

    @Test(priority = 1, description = "PROD-FIL-02: Kiểm thử sắp xếp sản phẩm theo giá tăng dần.")
    public void PROD_FIL_01_SortByPriceAscending() {
        LoggerHelper.info("[PRODUCT][FILTER] Bắt đầu kiểm thử sắp xếp giá tăng dần");
        ProductListPage listPage = new ProductListPage(getDriver(), baseUrl);
        LoggerHelper.info("[PRODUCT][FILTER] Mở trang danh sách sản phẩm");
        listPage.open();

        LoggerHelper.info("[PRODUCT][FILTER] Chọn sắp xếp: Giá tăng dần");
        listPage.sortBy("Giá tăng dần");
        int count = listPage.getBookCount();

        LoggerHelper.info("[PRODUCT][FILTER] Số lượng sản phẩm sau khi sắp xếp: " + count);
        Assert.assertTrue(count > 1,
                "Expected at least 2 books to verify ascending sort. Got: " + count);

        LoggerHelper.info("[PRODUCT][FILTER] Kiểm tra danh sách sản phẩm được sắp xếp tăng dần");
        Assert.assertTrue(listPage.arePricesAscending(),
                "Products are not sorted by price ascending.");
        LoggerHelper.info("[PRODUCT][FILTER] Sắp xếp giá tăng dần thành công");
    }

    @Test(priority = 2, description = "PROD-FIL-03: Kiểm thử sắp xếp sản phẩm theo giá giảm dần.")
    public void PROD_FIL_02_SortByPriceDescending() {
        LoggerHelper.info("[PRODUCT][FILTER] Bắt đầu kiểm thử sắp xếp giá giảm dần");
        ProductListPage listPage = new ProductListPage(getDriver(), baseUrl);
        LoggerHelper.info("[PRODUCT][FILTER] Mở trang danh sách sản phẩm");
        listPage.open();

        LoggerHelper.info("[PRODUCT][FILTER] Chọn sắp xếp: Giá giảm dần");
        listPage.sortBy("Giá giảm giá");

        int count = listPage.getBookCount();

        LoggerHelper.info("[PRODUCT][FILTER] Số lượng sản phẩm sau khi sắp xếp: " + count);
        Assert.assertTrue(count > 1,
                "Expected at least 2 books to verify descending sort. Got: " + count);

        LoggerHelper.info("[PRODUCT][FILTER] Kiểm tra danh sách sản phẩm được sắp xếp giảm dần");
        Assert.assertTrue(listPage.arePricesDescending(),
                "Products are not sorted by price descending.");

        LoggerHelper.info("[PRODUCT][FILTER] Sắp xếp giá giảm dần thành công");
    }
}
