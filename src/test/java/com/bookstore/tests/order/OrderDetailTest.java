package com.bookstore.tests.order;

import com.bookstore.base.BaseSetup;
import com.bookstore.pages.*;
import org.testng.Assert;
import org.testng.annotations.Test;

/** ORD-DET-01/02/03 */
public class OrderDetailTest extends BaseSetup {

    private static final String USER = "diem_tester", PASS = "Abc@12345";
    private static final String INVALID_ORDER_ID = "99999";
    private static final String USER_B_ORDER_ID  = "USER_B_ORDER_ID"; // Thay bằng ID thực

    private long startTimer(String id, String desc) {
        long t = System.currentTimeMillis();
        System.out.printf("%n========================================%n[START] %s - %s%n========================================%n", id, desc);
        return t;
    }
    private void endTimer(String id, long t) {
        long ms = System.currentTimeMillis() - t;
        System.out.printf("[END] %s | %d ms (%.2f s)%n", id, ms, ms / 1000.0);
    }

//    @Test(description = "ORD-DET-01: Verify viewing order details.")
//    public void ORD_DET_01_ViewOrderDetail() {
//        long t = startTimer("ORD-DET-01", "View valid order detail");
//
//        new LoginPage(driver).open().loginAsCustomer(USER, PASS);
//
//        System.out.println("[Step 1] Navigate to Order History");
//        OrderHistoryPage ordPage = new OrderHistoryPage(driver).open();
//
//        Assert.assertTrue(ordPage.getOrderCount() > 0,
//                "Precondition: User must have at least 1 order.");
//
//        System.out.println("[Step 2] Click [order-detail-link] on first order");
//        ordPage.clickDetailAt(0);
//
//        System.out.println("[Assert] Verify detail page shows Recipient info");
//        Assert.assertTrue(ordPage.isDetailInfoDisplayed(),
//                "Expected recipient info to display on order detail.");
//
//        System.out.println("[Assert] Verify item list displayed");
//        Assert.assertTrue(ordPage.getDetailItemCount() > 0,
//                "Expected at least 1 item in order detail.");
//
//        System.out.println("[Assert] Verify status is not empty");
//        String status = ordPage.getDetailStatus();
//        System.out.println("[Assert] Status: " + status);
//        Assert.assertFalse(status.isEmpty(), "Expected order status to be displayed.");
//
//        endTimer("ORD-DET-01", t);
//    }
//
//    @Test(description = "ORD-DET-02: Verify viewing invalid order ID.")
//    public void ORD_DET_02_InvalidOrderId() {
//        long t = startTimer("ORD-DET-02", "Access invalid order ID 99999");
//
//        new LoginPage(driver).open().loginAsCustomer(USER, PASS);
//
//        System.out.println("[Step 1] Navigate to /invoice?id=99999");
//        OrderHistoryPage ordPage = new OrderHistoryPage(driver).openWithId(INVALID_ORDER_ID);
//
//        System.out.println("[Assert] Verify 404 or 'Không tìm thấy đơn hàng' (no crash)");
//        boolean safe = ordPage.isPageSafe();
//        boolean is404 = ordPage.is403Or404();
//        System.out.println("[Assert] isPageSafe=" + safe + " | is404=" + is404);
//
//        Assert.assertTrue(safe, "Server crashed for invalid order ID " + INVALID_ORDER_ID);
//        Assert.assertTrue(is404 || ordPage.isEmptyMessageDisplayed(),
//                "Expected 404 or not-found message for invalid order ID.");
//
//        endTimer("ORD-DET-02", t);
//    }
//
//    @Test(description = "ORD-DET-03: Verify viewing other user's order ID (Boundary/IDOR).")
//    public void ORD_DET_03_IdroBoundary() {
//        long t = startTimer("ORD-DET-03", "IDOR - access User B's order while logged as User A");
//
//        System.out.println("[Step 1] Login as User A");
//        new LoginPage(driver).open().loginAsCustomer(USER, PASS);
//
//        System.out.println("[Step 2] Navigate to URL of User B's order ID: " + USER_B_ORDER_ID);
//        OrderHistoryPage ordPage = new OrderHistoryPage(driver).openWithId(USER_B_ORDER_ID);
//
//        String url = ordPage.getCurrentUrl();
//        System.out.println("[Assert] Current URL: " + url);
//        System.out.println("[Assert] Verify 403 Forbidden or redirect (IDOR protected)");
//
//        boolean isProtected = ordPage.is403Or404()
//                || url.contains("/invoice") && !url.contains(USER_B_ORDER_ID)
//                || url.contains("/home") || url.contains("/login");
//        Assert.assertTrue(isProtected,
//                "SECURITY: User A accessed User B's order. IDOR vulnerability detected!");
//
//        endTimer("ORD-DET-03", t);
//    }
}
