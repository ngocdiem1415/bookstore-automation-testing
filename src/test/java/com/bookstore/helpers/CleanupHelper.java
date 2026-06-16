package com.bookstore.helpers;

import com.bookstore.factory.PageFactoryManager;
import com.bookstore.pages.AdminCategoryPage;
import com.bookstore.pages.AdminOrderEditPage;
import com.bookstore.pages.AdminOrderPage;
import com.bookstore.pages.AdminProductPage;
import com.bookstore.pages.AdminUserPage;
import com.bookstore.pages.CartPage;
import com.bookstore.pages.LoginPage;
import com.bookstore.pages.InvoicePage;
import com.bookstore.pages.ProfilePage;
import com.bookstore.pages.components.HeaderComponent;
import com.bookstore.utils.DataHelper;
import com.bookstore.utils.LoggerHelper;
import org.openqa.selenium.WebDriver;

public class CleanupHelper {

    private CleanupHelper() {
    }

    public static void cleanupCreatedUsers(WebDriver driver, String baseUrl) {
        if (CleanupRegistry.createdUsers.isEmpty()) {
            return;
        }

        try {
            LoginPage loginPage = PageFactoryManager.getLoginPage(driver, baseUrl);
            loginPage.open();
            loginPage.loginAsAdmin(
                    DataHelper.getValue("existing.admin"),
                    DataHelper.getValue("existing.password")
            );
            waitForSessionCookie("[CLEANUP][USER]");

            AdminUserPage userPage = PageFactoryManager.getAdminUserPage(driver, baseUrl);
            userPage.open();

            for (String username : CleanupRegistry.createdUsers) {
                deleteUserIfExists(userPage, username);
            }
        } catch (Exception e) {
            LoggerHelper.warn("[CLEANUP][USER] Không thể dọn user đã tạo: " + e.getMessage());
        } finally {
            CleanupRegistry.createdUsers.clear();
        }
    }

    public static void restoreCustomerProfile(WebDriver driver, String baseUrl) {
        if (CleanupRegistry.profileSnapshot == null) {
            return;
        }

        try {
            LoginPage loginPage = PageFactoryManager.getLoginPage(driver, baseUrl);
            loginPage.open();
            loginPage.loginAsCustomer(
                    DataHelper.getValue("existing.username"),
                    DataHelper.getValue("existing.password")
            );

            HeaderComponent header = new HeaderComponent(driver, baseUrl);
            ProfilePage profilePage = header.navigateToProfile();
            profilePage.enterPhone(CleanupRegistry.profileSnapshot.phone)
                    .enterBirthdate(CleanupRegistry.profileSnapshot.birthdate)
                    .selectGender(CleanupRegistry.profileSnapshot.gender)
                    .clickSave();

            String message = profilePage.getSuccessMessage();
            LoggerHelper.info("[CLEANUP][PROFILE] Đã khôi phục profile khách hàng. Thông báo: " + message);
        } catch (Exception e) {
            LoggerHelper.warn("[CLEANUP][PROFILE] Không thể khôi phục profile khách hàng: " + e.getMessage());
        } finally {
            CleanupRegistry.profileSnapshot = null;
        }
    }

    public static void cleanupCreatedProducts(WebDriver driver, String baseUrl) {
        if (CleanupRegistry.createdProducts.isEmpty()) {
            return;
        }

        try {
            LoginPage loginPage = PageFactoryManager.getLoginPage(driver, baseUrl);
            loginPage.open();
            loginPage.loginAsAdmin(
                    DataHelper.getValue("existing.admin"),
                    DataHelper.getValue("existing.password")
            );
            waitForSessionCookie("[CLEANUP][PRODUCT]");

            AdminProductPage productPage = PageFactoryManager.getAdminProductPage(driver, baseUrl);
            productPage.open();

            for (String title : CleanupRegistry.createdProducts) {
                deleteProductIfExists(productPage, title);
            }
        } catch (Exception e) {
            LoggerHelper.warn("[CLEANUP][PRODUCT] Không thể dọn product đã tạo: " + e.getMessage());
        } finally {
            CleanupRegistry.createdProducts.clear();
        }
    }

    public static void cleanupCreatedCategories(WebDriver driver, String baseUrl) {
        if (CleanupRegistry.createdCategories.isEmpty()) {
            return;
        }

        try {
            LoginPage loginPage = PageFactoryManager.getLoginPage(driver, baseUrl);
            loginPage.open();
            loginPage.loginAsAdmin(
                    DataHelper.getValue("existing.admin"),
                    DataHelper.getValue("existing.password")
            );
            waitForSessionCookie("[CLEANUP][CATEGORY]");

            AdminCategoryPage categoryPage = PageFactoryManager.getAdminCategoryPage(driver, baseUrl);
            categoryPage.open();

            for (String name : CleanupRegistry.createdCategories) {
                deleteCategoryIfExists(categoryPage, name);
            }
        } catch (Exception e) {
            LoggerHelper.warn("[CLEANUP][CATEGORY] Không thể dọn category đã tạo: " + e.getMessage());
        } finally {
            CleanupRegistry.createdCategories.clear();
        }
    }

    public static void cleanupCustomerCart(WebDriver driver, String baseUrl) {
        try {
            LoginPage loginPage = PageFactoryManager.getLoginPage(driver, baseUrl);
            loginPage.open();
            loginPage.loginAsCustomer(
                    DataHelper.getValue("existing.username"),
                    DataHelper.getValue("existing.password")
            );
            waitForSessionCookie("[CLEANUP][CART]");

            CartPage cartPage = PageFactoryManager.getCartPage(driver, baseUrl);
            cartPage.open();

            int itemCount = cartPage.getCartItemCount();
            if (itemCount > 0) {
                LoggerHelper.info("[CLEANUP][CART] Giỏ hàng còn " + itemCount + " sản phẩm, tiến hành xóa tất cả");
                cartPage.deleteAllItems();
            }
        } catch (Exception e) {
            LoggerHelper.warn("[CLEANUP][CART] Không thể dọn giỏ hàng khách hàng: " + e.getMessage());
        }
    }

    public static void cancelAdminOrders(WebDriver driver, String baseUrl) {
        if (CleanupRegistry.adminCancelOrderIds.isEmpty()) {
            return;
        }

        try {
            loginAsAdmin(driver, baseUrl, "[CLEANUP][ORDER][ADMIN_CANCEL]");
            AdminOrderPage orderPage = PageFactoryManager.getAdminOrderPage(driver, baseUrl);

            for (String orderId : CleanupRegistry.adminCancelOrderIds) {
                cancelAdminOrder(orderPage, driver, baseUrl, orderId);
            }
        } catch (Exception e) {
            LoggerHelper.warn("[CLEANUP][ORDER][ADMIN_CANCEL] Không thể hủy order bằng admin: " + e.getMessage());
        } finally {
            CleanupRegistry.adminCancelOrderIds.clear();
        }
    }

    public static void cancelCustomerOrders(WebDriver driver, String baseUrl) {
        if (CleanupRegistry.customerCancelOrderIds.isEmpty()) {
            return;
        }

        try {
            LoginPage loginPage = PageFactoryManager.getLoginPage(driver, baseUrl);
            loginPage.open();
            loginPage.loginAsCustomer(
                    DataHelper.getValue("existing.username"),
                    DataHelper.getValue("existing.password")
            );
            waitForSessionCookie("[CLEANUP][ORDER][CUSTOMER_CANCEL]");

            InvoicePage orderHistoryPage = PageFactoryManager.getInvoicePage(driver, baseUrl);
            orderHistoryPage.open();
            for (String orderId : CleanupRegistry.customerCancelOrderIds) {
                orderHistoryPage.cancelOrderById(orderId);
            }
        } catch (Exception e) {
            LoggerHelper.warn("[CLEANUP][ORDER][CUSTOMER_CANCEL] Không thể hủy order bằng khách hàng: " + e.getMessage());
        } finally {
            CleanupRegistry.customerCancelOrderIds.clear();
        }
    }

    private static void deleteUserIfExists(AdminUserPage userPage, String username) {
        int index = userPage.searchByUsername(username);

        if (index < 0) {
            LoggerHelper.warn("[CLEANUP][USER] Không tìm thấy user đã tạo: " + username);
            return;
        }
        userPage.clickDeleteAt(index);
        String message = userPage.confirmDeleteAndGetNotification();
        LoggerHelper.info("[CLEANUP][USER] Đã xóa user đã tạo: " + username + " | " + message);
        userPage.open();
    }

    private static void deleteProductIfExists(AdminProductPage productPage, String title) {
        int index = productPage.findProductIndexByTitle(title);

        if (index < 0) {
            LoggerHelper.warn("[CLEANUP][PRODUCT] Không tìm thấy product đã tạo: " + title);
            return;
        }

        productPage.clickDeleteAt(index);
        String message = productPage.getNotificationMessage();
        LoggerHelper.info("[CLEANUP][PRODUCT] Đã xóa product đã tạo: " + title + " | " + message);
        productPage.open();
    }

    private static void deleteCategoryIfExists(AdminCategoryPage categoryPage, String name) {
        int index = categoryPage.findCategoryIndexByName(name);

        if (index < 0) {
            LoggerHelper.warn("[CLEANUP][CATEGORY] Không tìm thấy category đã tạo: " + name);
            return;
        }

        categoryPage.clickDeleteAt(index);
        String message = categoryPage.getNotificationMessage();
        LoggerHelper.info("[CLEANUP][CATEGORY] Đã xóa category đã tạo: " + name + " | " + message);
        categoryPage.open();
    }

    public static void shipAdminOrder(AdminOrderPage orderPage, WebDriver driver, String baseUrl, String orderId) {
        LoggerHelper.info("[CLEANUP][ORDER][SHIPPING] Chuyển đơn hàng sang Shipping, mã đơn: " + orderId);
        orderPage.open();
        orderPage.clickEditByOrderId(orderId);

        AdminOrderEditPage editPage = PageFactoryManager.getAdminOrderEditPage(driver, baseUrl);
        String notification = editPage.selectOrderStatus("Shipping").clickSaveAndGetNotification();
        LoggerHelper.info("[CLEANUP][ORDER][SHIPPING] Đã chuyển đơn hàng sang Shipping, mã đơn: "
                + orderId + " | " + notification);
    }

    public static void cancelAdminOrder(AdminOrderPage orderPage, WebDriver driver, String baseUrl, String orderId) {
        LoggerHelper.info("[CLEANUP][ORDER][ADMIN_CANCEL] Hủy mã đơn: " + orderId);
        orderPage.open();
        orderPage.clickEditByOrderId(orderId);

        AdminOrderEditPage editPage = PageFactoryManager.getAdminOrderEditPage(driver, baseUrl);
        String notification = editPage.selectOrderStatus("Cancelled").clickSaveAndGetNotification();
        LoggerHelper.info("[CLEANUP][ORDER][ADMIN_CANCEL] Đã hủy mã đơn: " + orderId + " | " + notification);
    }

    private static void loginAsAdmin(WebDriver driver, String baseUrl, String logPrefix) {
        LoginPage loginPage = PageFactoryManager.getLoginPage(driver, baseUrl);
        loginPage.open();
        loginPage.loginAsAdmin(
                DataHelper.getValue("existing.admin"),
                DataHelper.getValue("existing.password")
        );
        waitForSessionCookie(logPrefix);
    }

    private static void waitForSessionCookie(String logPrefix) {
        try {
            Thread.sleep(500);
            LoggerHelper.info(logPrefix + " Đăng nhập hoàn tất, đang chờ cookie ");
        } catch (InterruptedException e) {
            LoggerHelper.warn(logPrefix + " Bị gián đoạn trong khi chờ sau khi đăng nhập admin");
            Thread.currentThread().interrupt();
        }
    }
}
