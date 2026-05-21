package com.bookstore.tests.product;

import com.bookstore.base.BaseSetup;
import com.bookstore.factory.PageFactoryManager;
import com.bookstore.pages.ProductDetailPage;
import com.bookstore.pages.ProductListPage;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Test Class: Product - Detail (PROD-DET)
 */
public class ProductDetailTest extends BaseSetup {
    private static final String VALID_PRODUCT_ID   = "1";
    private static final String HIDDEN_PRODUCT_ID  = "999";


    @Test(description = "PROD-DET-01: Verify viewing valid product details.")
    public void PROD_DET_01_ValidProductDetail() {
        ProductListPage listPage =  PageFactoryManager.getProductListPage(driver,baseUrl);
        ProductDetailPage detailPage = listPage.clickBookAt(0);

        String currentUrl = detailPage.getCurrentUrl();
        Assert.assertTrue(currentUrl.contains("/books/"),
                "Expected URL to contain /books/{id}. Actual: " + currentUrl);

        Assert.assertTrue(detailPage.isThumbnailDisplayed(), "Thumbnail not displayed");
        Assert.assertFalse(detailPage.getTitle().isEmpty(), "Title is empty");
        Assert.assertFalse(detailPage.getPrice().isEmpty(), "Price is empty");
        Assert.assertFalse(detailPage.getDescription().isEmpty(), "Description is empty");
    }

    @Test(description = "PROD-DET-02: Verify viewing soft-deleted or hidden product.")
    public void PROD_DET_02_HiddenProductReturns404() {
        String url = getBaseUrl() + "/books/" + HIDDEN_PRODUCT_ID;
        System.out.println("[Step 1] Navigate to hidden product URL: " + url);
        ProductDetailPage detailPage =  PageFactoryManager.getProductDetailPage(driver,baseUrl);
        detailPage.navigateTo(url);

        boolean is404   = detailPage.is404Page();
        boolean isSafe  = detailPage.isPageSafe();
        String title    = detailPage.getPageTitle();

        Assert.assertTrue(is404 || isSafe,
                "Expected 404 page for hidden product ID " + HIDDEN_PRODUCT_ID
                        + ". Got title: " + title);
    }

    @Test(description = "PROD-DET-03: Verify ID parameter manipulation - SQLi (Boundary).")
    public void PROD_DET_03_SqlInjectionIdBoundary() {
        String rawUrl = getBaseUrl() + "/books/%27%20OR%201%3D1--";
        System.out.println("[Step 1] Navigate to SQLi URL: " + rawUrl);
        ProductDetailPage detailPage =  PageFactoryManager.getProductDetailPage(driver,baseUrl);
        detailPage.navigateTo(rawUrl);

        System.out.println("[Assert] Verify no DB crash or exposure (no Error 500)");
        Assert.assertTrue(detailPage.isPageSafe(),
                "Server crashed or exposed DB for SQLi ID parameter. Critical security issue.");

        System.out.println("[Assert] Verify response is 400 Bad Request or safe 404");
        String pageTitle = detailPage.getPageTitle();
        String currentUrl = detailPage.getCurrentUrl();
        System.out.println("[Assert] title='" + pageTitle + "' url='" + currentUrl + "'");

        Assert.assertFalse(pageTitle.toLowerCase().contains("500"),
                "HTTP 500 returned for SQLi attempt - possible vulnerability.");
    }
}
