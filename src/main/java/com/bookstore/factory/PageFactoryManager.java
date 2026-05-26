package com.bookstore.factory;

import com.bookstore.pages.*;
import org.openqa.selenium.WebDriver;

public class PageFactoryManager {
    public static LoginPage getLoginPage(WebDriver driver, String baseUrl) {
        return new LoginPage(driver, baseUrl);
    }

    public static SignupPage getSignupPage(WebDriver driver, String baseUrl) {
        return new SignupPage(driver, baseUrl);
    }

    public static AdminDashboardPage getAdminDashboardPage(WebDriver driver, String baseUrl) {
        return new AdminDashboardPage(driver, baseUrl);
    }

    public static ProfilePage getProfilePage(WebDriver driver, String baseUrl) {
        return new ProfilePage(driver, baseUrl);
    }

    public static AdminCategoryPage getAdminCategoryPage(WebDriver driver, String baseUrl) {
        return new AdminCategoryPage(driver, baseUrl);
    }

    public static AdminOrderPage getAdminOrderPage(WebDriver driver, String baseUrl) {
        return new AdminOrderPage(driver, baseUrl);
    }

    public static AdminProductPage getAdminProductPage(WebDriver driver, String baseUrl) {
        return new AdminProductPage(driver, baseUrl);
    }

    public static AdminUserPage getAdminUserPage(WebDriver driver, String baseUrl) {
        return new AdminUserPage(driver, baseUrl);
    }

    public static ProductListPage getProductListPage(WebDriver driver, String baseUrl) {
        return new ProductListPage(driver, baseUrl);
    }

    public static CartPage getCartPage(WebDriver driver, String baseUrl) {
        return new CartPage(driver, baseUrl);
    }

    public static ProductDetailPage getProductDetailPage(WebDriver driver, String baseUrl) {
        return new ProductDetailPage(driver, baseUrl);
    }

    public static CheckoutPage getCheckoutPage(WebDriver driver, String baseUrl) {
        return new CheckoutPage(driver, baseUrl);
    }

    public static InvoicePage getInvoicePage(WebDriver driver, String baseUrl) {
        return new InvoicePage(driver, baseUrl);
    }

    public static OrderHistoryPage getOrderHistoryPage(WebDriver driver, String baseUrl) {
        return new OrderHistoryPage(driver, baseUrl);
    }

}
