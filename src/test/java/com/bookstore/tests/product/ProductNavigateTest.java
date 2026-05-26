package com.bookstore.tests.product;

import com.bookstore.base.BaseSetup;
import com.bookstore.pages.ProductListPage;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Test Class: Product - Navigate (PROD-NAV)
 * Tách riêng theo trang để dễ quản lý.
 */
public class ProductNavigateTest extends BaseSetup {

//    @Test(description = "PROD-NAV-01: Verify navigating to a valid category shows correct books.")
//    public void PROD_NAV_01_ValidCategoryShowsBooks() {
//        long t = startTimer("PROD-NAV-01", "Valid category shows correct books");
//
//        System.out.println("[Step 1] Navigate to Products page (/listBook)");
//        ProductListPage listPage = new ProductListPage(driver).open();
//
//        System.out.println("[Step 2] Select [filter-category] = 'Tiểu thuyết'");
//        listPage.filterByCategory("Tiểu thuyết");
//
//        System.out.println("[Assert] Verify products are rendered in [book-item]");
//        int count = listPage.getBookCount();
//        System.out.println("[Assert] Book count: " + count);
//        Assert.assertTrue(count > 0,
//                "Expected books to be displayed for category 'Tiểu thuyết'. Count was: " + count);
//
//        System.out.println("[Assert] Verify product container visible");
//        Assert.assertTrue(listPage.isProductContainerDisplayed(),
//                "Product container should be visible after category filter.");
//
//        endTimer("PROD-NAV-01", t);
//    }
//
//    @Test(description = "PROD-NAV-02: Verify navigating to an empty category shows empty state.")
//    public void PROD_NAV_02_EmptyCategoryShowsEmptyState() {
//        long t = startTimer("PROD-NAV-02", "Empty category shows empty state");
//
//        System.out.println("[Step 1] Navigate to Products page");
//        ProductListPage listPage = new ProductListPage(driver).open();
//
//        System.out.println("[Step 2] Select [filter-category] = 'Sách test' (no books)");
//        listPage.filterByCategory("Sách test");
//
//        System.out.println("[Assert] Verify empty state displayed - no [book-item] rendered");
//        int count = listPage.getBookCount();
//        System.out.println("[Assert] Book count: " + count);
//        Assert.assertEquals(count, 0,
//                "Expected 0 books for empty category 'Sách test'. Actual: " + count);
//
//        System.out.println("[Assert] Verify 'Không tìm thấy sản phẩm' message displayed");
//        Assert.assertTrue(listPage.isNoResultMessageDisplayed(),
//                "Expected empty state message to be displayed.");
//
//        endTimer("PROD-NAV-02", t);
//    }
//
//    @Test(description = "PROD-NAV-03: Verify accessing invalid category ID via URL (Boundary).")
//    public void PROD_NAV_03_InvalidCategoryIdBoundary() {
//        long t = startTimer("PROD-NAV-03", "Invalid category ID via URL (Boundary)");
//
//        String invalidUrl = BaseSetup.baseUrl + "/api/books/filter?category=99999";
//        System.out.println("[Step 1] Navigate directly to: " + invalidUrl);
//        ProductListPage listPage = new ProductListPage(driver);
//        listPage.navigateTo(invalidUrl);
//
//        System.out.println("[Assert] Verify page does not crash (no Error 500)");
//        Assert.assertTrue(listPage.isPageSafe(),
//                "Page crashed with server error for invalid category ID 99999.");
//
//        System.out.println("[Assert] Verify page renders 404 OR empty list gracefully");
//        boolean is404   = listPage.is404Page();
//        boolean isEmpty = listPage.getBookCount() == 0;
//        System.out.println("[Assert] is404=" + is404 + " | isEmpty=" + isEmpty);
//        Assert.assertTrue(is404 || isEmpty,
//                "Expected 404 page or empty result for invalid category ID 99999.");
//
//    }
}
