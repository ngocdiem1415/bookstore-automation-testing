package com.bookstore.tests.product;

import com.bookstore.base.BaseSetup;
import com.bookstore.pages.ProductListPage;
import com.bookstore.utils.JsonDataProvider;
import com.bookstore.utils.LoggerHelper;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Map;


public class ProductFilterTest extends BaseSetup {

    @Test(
            priority = 1,
            dataProvider = "GlobalJsonFeeder",
            dataProviderClass = JsonDataProvider.class,
            description = "PROD-FIL-01: Kiểm thử lọc sản phẩm theo khoảng giá hợp lệ."
    )
    public void PROD_FIL_01_FilterByValidPriceRange(Map<String, String> data) {
        String priceRange = data.get("price_range");
        long minPrice = Long.parseLong(data.get("min_price"));
        long maxPrice = Long.parseLong(data.get("max_price"));

        LoggerHelper.info("[PRODUCT][FILTER] Bắt đầu kiểm thử lọc sản phẩm theo khoảng giá hợp lệ");
        ProductListPage listPage = new ProductListPage(getDriver(), baseUrl);
        LoggerHelper.info("[PRODUCT][FILTER] Mở trang danh sách sản phẩm");
        listPage.open();

        LoggerHelper.info("[PRODUCT][FILTER] Chọn khoảng giá: " + priceRange);
        listPage.filterByPrice(priceRange);

        LoggerHelper.info("[PRODUCT][FILTER] Kiểm tra sản phẩm hiển thị sau khi lọc");
        int count = listPage.getBookCount();

        LoggerHelper.info("[PRODUCT][FILTER] Số lượng sản phẩm sau khi lọc: " + count);
        Assert.assertTrue(count > 0,
                "Expected books in price range " + priceRange + ". Got: " + count);

        for (int i = 0; i < count; i++) {
            long price = listPage.getBookPriceAsLong(i);
            LoggerHelper.info("[PRODUCT][FILTER] Book[" + i + "] price = " + price);

            Assert.assertTrue(price >= 100_000 && price <= 200_000,
                    "Book price is outside range 100k-200k: " + price);
        }
        LoggerHelper.info("[PRODUCT][FILTER] Lọc sản phẩm theo khoảng giá thành công");
    }

    @Test(priority = 2, description = "PROD-FIL-02: Kiểm thử sắp xếp sản phẩm theo giá tăng dần.")
    public void PROD_FIL_02_SortByPriceAscending() {
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

    @Test(priority = 3, description = "PROD-FIL-03: Kiểm thử sắp xếp sản phẩm theo giá giảm dần.")
    public void PROD_FIL_03_SortByPriceDescending() {
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
