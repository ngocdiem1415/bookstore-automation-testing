package com.bookstore.tests.admin;

import com.bookstore.factory.PageFactoryManager;
import com.bookstore.helpers.CleanupHelper;
import com.bookstore.helpers.CleanupRegistry;
import com.bookstore.helpers.OrderFixtureHelper;
import com.bookstore.pages.AdminOrderEditPage;
import com.bookstore.pages.AdminOrderPage;
import com.bookstore.utils.LoggerHelper;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

public class AdminOrderTest extends AdminBaseTest {

    @AfterMethod(alwaysRun = true)
    public void cleanupAdminOrderFixtures() {
        CleanupHelper.completeAdminOrders(getDriver(), baseUrl);
        CleanupHelper.cancelAdminOrders(getDriver(), baseUrl);
    }

    @Test(
            priority = 1,
            description = "ADM-ORD-01: Admin chuyen don hang Pending -> Shipping -> Completed"
    )
    public void ADM_ORD_01_CompleteShippingOrder() {
        String orderId = getOrderIdForCompleteFlow();
        LoggerHelper.info("[ADMIN][ORDER] Hoàn tất đơn hàng dữ liệu nền, mã đơn: " + orderId);

        loginAsAdmin();
        AdminOrderPage page = PageFactoryManager.getAdminOrderPage(getDriver(), baseUrl);
        CleanupHelper.completeAdminOrder(page, getDriver(), baseUrl, orderId);

        page.open();
        int completedIndex = page.searchByOrderId(orderId);
        Assert.assertTrue(completedIndex >= 0,
                "Cannot find order after completing fixture order ID: " + orderId);

        String orderStatus = page.getStatusAt(completedIndex);
        Assert.assertTrue(isCompletedStatus(orderStatus),
                "Order status must be Completed. Order ID: " + orderId + ". Actual: " + orderStatus);

        CleanupRegistry.adminCompleteOrderIds.remove(orderId);
        LoggerHelper.info("[ADMIN][ORDER] Hoàn tất đơn hàng dữ liệu nền thành công: " + orderId);
    }

    @Test(
            priority = 2,
            description = "ADM-ORD-02: Admin khong the doi trang thai tu Completed ve Pending"
    )
    public void ADM_ORD_02_UpdateStatusBackward() {
        LoggerHelper.info("[ADMIN][ORDER] Kiểm tra đơn đã hoàn thành không cho đổi ngược trạng thái");
        loginAsAdmin();

        AdminOrderPage page = PageFactoryManager.getAdminOrderPage(getDriver(), baseUrl);
        page.open();
        int index = page.searchByStatus("Completed");
        Assert.assertTrue(index >= 0,
                "Precondition: need at least one Completed order to verify backward status update.");

        page.clickEditAt(index);
        AdminOrderEditPage editPage = PageFactoryManager.getAdminOrderEditPage(getDriver(), baseUrl);
        Assert.assertTrue(editPage.isOrderStatusSelectDisabled(),
                "Order status select must be disabled for Completed orders.");

        LoggerHelper.info("[ADMIN][ORDER] Đơn đã hoàn thành không cho đổi ngược trạng thái: THÀNH CÔNG");
    }

    @Test(
            priority = 3,
            description = "ADM-ORD-03: Admin huy don hang dang Pending"
    )
    public void ADM_ORD_03_CancelPendingOrder() {
        String orderId = getOrderIdForCancelFlow();
        LoggerHelper.info("[ADMIN][ORDER] Hủy đơn hàng dữ liệu nền, mã đơn: " + orderId);

        loginAsAdmin();
        AdminOrderPage page = PageFactoryManager.getAdminOrderPage(getDriver(), baseUrl);
        CleanupHelper.cancelAdminOrder(page, getDriver(), baseUrl, orderId);

        page.open();
        int cancelledIndex = page.searchByOrderId(orderId);
        Assert.assertTrue(cancelledIndex >= 0,
                "Cannot find order after cancelling fixture order ID: " + orderId);

        String orderStatus = page.getStatusAt(cancelledIndex);
        Assert.assertTrue(isCancelledStatus(orderStatus),
                "Order status must be Cancelled. Order ID: " + orderId + ". Actual: " + orderStatus);

        CleanupRegistry.adminCancelOrderIds.remove(orderId);
        LoggerHelper.info("[ADMIN][ORDER] Hủy đơn hàng dữ liệu nền thành công: " + orderId);
    }

    private String getOrderIdForCompleteFlow() {
        if (!CleanupRegistry.adminCompleteOrderIds.isEmpty()) {
            return CleanupRegistry.adminCompleteOrderIds.get(0);
        }

        try {
            String orderId = OrderFixtureHelper.createCodOrder(getDriver(), baseUrl);
            CleanupRegistry.adminCompleteOrderIds.add(orderId);
            return orderId;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Cannot create COD order fixture for complete flow", e);
        }
    }

    private String getOrderIdForCancelFlow() {
        if (!CleanupRegistry.adminCancelOrderIds.isEmpty()) {
            return CleanupRegistry.adminCancelOrderIds.get(0);
        }

        try {
            String orderId = OrderFixtureHelper.createCodOrder(getDriver(), baseUrl);
            CleanupRegistry.adminCancelOrderIds.add(orderId);
            return orderId;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Cannot create COD order fixture for cancel flow", e);
        }
    }

    private boolean isCompletedStatus(String status) {
        String normalized = status == null ? "" : status.toLowerCase();
        return normalized.contains("completed")
                || normalized.contains("hoàn thành")
                || normalized.contains("da nhan")
                || normalized.contains("đã nhận");
    }

    private boolean isCancelledStatus(String status) {
        String normalized = status == null ? "" : status.toLowerCase();
        return normalized.contains("cancelled")
                || normalized.contains("cancel")
                || normalized.contains("hủy")
                || normalized.contains("huy");
    }
}
