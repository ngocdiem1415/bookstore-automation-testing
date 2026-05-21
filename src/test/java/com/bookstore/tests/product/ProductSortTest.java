package com.bookstore.tests.product;

import com.bookstore.base.BaseSetup;
import com.bookstore.pages.ProductListPage;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Test Class: Product - Sort (PROD-SRT)
 */
public class ProductSortTest extends BaseSetup {

//    @Test(description = "PROD-SRT-01: Verify sorting products by Price Ascending.")
//    public void PROD_SRT_01_SortByPriceAscending() {
//        long t = startTimer("PROD-SRT-01", "Sort by Price Ascending");
//
//        System.out.println("[Step 1] Navigate to Products page");
//        ProductListPage listPage = new ProductListPage(driver).open();
//
//        System.out.println("[Step 2] Select 'Giá tăng dần' in [filter-sort]");
//        listPage.sortBy("Giá tăng dần");
//
//        System.out.println("[Assert] Verify products sorted in ascending price order");
//        int count = listPage.getBookCount();
//        System.out.println("[Assert] Book count: " + count);
//        Assert.assertTrue(count > 0, "Expected books to be displayed after sorting.");
//
//        List<Long> prices = new ArrayList<>();
//        for (int i = 0; i < count; i++) {
//            long price = listPage.getBookPriceAsLong(i);
//            prices.add(price);
//            System.out.printf("  Book[%d] price = %d%n", i, price);
//        }
//
//        boolean isAscending = true;
//        for (int i = 0; i < prices.size() - 1; i++) {
//            if (prices.get(i) > prices.get(i + 1)) {
//                isAscending = false;
//                System.out.printf("  ⚠ Not ascending: prices[%d]=%d > prices[%d]=%d%n",
//                        i, prices.get(i), i + 1, prices.get(i + 1));
//            }
//        }
//        Assert.assertTrue(isAscending,
//                "Products are NOT in ascending price order after selecting 'Giá tăng dần'.");
//
//        endTimer("PROD-SRT-01", t);
//    }
//
//    @Test(description = "PROD-SRT-02: Verify sorting products by Newest.")
//    public void PROD_SRT_02_SortByNewest() {
//        long t = startTimer("PROD-SRT-02", "Sort by Newest");
//
//        System.out.println("[Step 1] Navigate to Products page");
//        ProductListPage listPage = new ProductListPage(driver).open();
//
//        System.out.println("[Step 2] Select 'Hàng mới nhất' in [filter-sort]");
//        listPage.sortBy("Hàng mới nhất");
//
//        System.out.println("[Assert] Verify books are rendered after sort");
//        int count = listPage.getBookCount();
//        System.out.println("[Assert] Book count: " + count);
//        Assert.assertTrue(count > 0,
//                "Expected books to be displayed after sorting by newest.");
//
//        System.out.println("[Assert] Verify selected sort option is 'Hàng mới nhất'");
//        String selected = listPage.getSelectedSortOption();
//        System.out.println("[Assert] Selected sort: " + selected);
//        Assert.assertTrue(selected.contains("mới nhất") || selected.contains("Hàng mới nhất"),
//                "Expected sort option 'Hàng mới nhất' to be selected. Actual: " + selected);
//
//        endTimer("PROD-SRT-02", t);
//    }
//
//    @Test(description = "PROD-SRT-03: Verify invalid sort parameter in URL (Boundary).")
//    public void PROD_SRT_03_InvalidSortParamBoundary() {
//        long t = startTimer("PROD-SRT-03", "Invalid sort parameter via URL (Boundary)");
//
//        String url = BaseSetup.baseUrl + "/api/books/filter?sort=xyz";
//        System.out.println("[Step 1] Navigate to URL: " + url);
//        ProductListPage listPage = new ProductListPage(driver);
//        listPage.navigateTo(url);
//
//        System.out.println("[Assert] Verify no server error (page safe)");
//        Assert.assertTrue(listPage.isPageSafe(),
//                "Server crashed with invalid sort parameter 'xyz'.");
//
//        System.out.println("[Assert] Verify page falls back to default (no crash)");
//        int count = listPage.getBookCount();
//        System.out.println("[Assert] Book count (fallback default): " + count);
//        // Falls back to default sort: any result ≥ 0 is acceptable
//        Assert.assertTrue(count >= 0,
//                "Expected graceful fallback with invalid sort param 'xyz'.");
//
//    }
}
