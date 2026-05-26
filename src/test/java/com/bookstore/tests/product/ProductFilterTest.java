package com.bookstore.tests.product;

import com.bookstore.base.BaseSetup;
import com.bookstore.pages.ProductListPage;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Test Class: Product - Filter (PROD-FIL)
 */
public class ProductFilterTest extends BaseSetup {

//
//    @Test(description = "PROD-FIL-01: Verify filtering products by valid price range.")
//    public void PROD_FIL_01_FilterByValidPriceRange() {
//        long t = startTimer("PROD-FIL-01", "Filter by valid price range 100k-200k");
//
//        System.out.println("[Step 1] Navigate to Products page");
//        ProductListPage listPage = new ProductListPage(driver).open();
//
//        System.out.println("[Step 2] Select [filter-price] = '100000-200000'");
//        listPage.filterByPrice("100000-200000");
//
//        System.out.println("[Assert] Verify products are displayed");
//        int count = listPage.getBookCount();
//        System.out.println("[Assert] Book count: " + count);
//        Assert.assertTrue(count > 0,
//                "Expected books in price range 100k-200k. Got: " + count);
//
//        System.out.println("[Assert] Verify all displayed prices are within 100k-200k");
//        boolean allInRange = true;
//        for (int i = 0; i < count; i++) {
//            long price = listPage.getBookPriceAsLong(i);
//            System.out.printf("  Book[%d] price = %d%n", i, price);
//            if (price < 100_000 || price > 200_000) {
//                allInRange = false;
//                System.out.println("  ⚠ Out of range: " + price);
//            }
//        }
//        Assert.assertTrue(allInRange,
//                "Some books displayed are outside the 100k-200k price range.");
//
//        endTimer("PROD-FIL-01", t);
//    }
//
//    @Test(description = "PROD-FIL-02: Verify filtering with Min Price > Max Price (Boundary).")
//    public void PROD_FIL_02_FilterMinGreaterThanMaxBoundary() {
//        long t = startTimer("PROD-FIL-02", "Min Price > Max Price via URL (Boundary)");
//
//        String url = BaseSetup.baseUrl + "/api/books/filter?firstPrice=200000&secondPrice=50000";
//        System.out.println("[Step 1] Navigate to URL: " + url);
//        ProductListPage listPage = new ProductListPage(driver);
//        listPage.navigateTo(url);
//
//        System.out.println("[Assert] Verify page does not crash (no DB error)");
//        Assert.assertTrue(listPage.isPageSafe(),
//                "Server crashed with invalid price range (min>max). Possible DB error.");
//
//        System.out.println("[Assert] Verify system handles gracefully: empty list or ignores filter");
//        boolean isEmpty  = listPage.getBookCount() == 0;
//        boolean hasSafe  = listPage.isPageSafe();
//        System.out.println("[Assert] isEmpty=" + isEmpty + " | isPageSafe=" + hasSafe);
//        Assert.assertTrue(hasSafe,
//                "Expected graceful handling for Min > Max price filter.");
//
//        endTimer("PROD-FIL-02", t);
//    }
//
//    @Test(description = "PROD-FIL-03: Verify applying filters with negative values via URL (Boundary).")
//    public void PROD_FIL_03_FilterNegativePriceBoundary() {
//        long t = startTimer("PROD-FIL-03", "Negative price value via URL (Boundary)");
//
//        String url = BaseSetup.baseUrl + "/api/books/filter?firstPrice=-100000";
//        System.out.println("[Step 1] Navigate to URL: " + url);
//        ProductListPage listPage = new ProductListPage(driver);
//        listPage.navigateTo(url);
//
//        System.out.println("[Assert] Verify no server error 500");
//        Assert.assertTrue(listPage.isPageSafe(),
//                "Server crashed for negative price parameter.");
//
//        System.out.println("[Assert] Verify system returns normal list or ignores negative price");
//        boolean safe = listPage.isPageSafe();
//        System.out.println("[Assert] isPageSafe=" + safe
//                + " | bookCount=" + listPage.getBookCount());
//        Assert.assertTrue(safe,
//                "Expected graceful handling for negative firstPrice=-100000.");
//
//        endTimer("PROD-FIL-03", t);
//    }
}
