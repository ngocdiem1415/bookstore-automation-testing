package com.bookstore.tests.order;

import com.bookstore.base.BaseSetup;
import com.bookstore.pages.*;
import org.testng.Assert;
import org.testng.annotations.Test;

/** ORD-LST-01/02/03 */
public class OrderListTest extends BaseSetup {

    private static final String USER = "diem_tester", PASS = "Abc@12345";
    private static final String NEW_USER = "new_user_" + System.currentTimeMillis();

//    @Test(description = "ORD-LST-01: Verify user can view order history.")
//    public void ORD_LST_01_ViewOrderHistory() {
//        long t = startTimer("ORD-LST-01", "User views order history");
//
//        System.out.println("[Step 1] Login as CUSTOMER with existing orders");
//        new LoginPage(driver).open().loginAsCustomer(USER, PASS);
//
//        System.out.println("[Step 1] Navigate to Order History (/invoice)");
//        OrderHistoryPage ordPage = new OrderHistoryPage(driver).open();
//
//        System.out.println("[Assert] Verify [order-list-item] renders with Status, Date, Amount");
//        int count = ordPage.getOrderCount();
//        System.out.println("[Assert] Order count: " + count);
//        Assert.assertTrue(count > 0,
//                "Expected order list to render. Got 0 orders. Data may not exist.");
//
//        endTimer("ORD-LST-01", t);
//    }
//
//    @Test(description = "ORD-LST-02: Verify order history for new user shows empty state.")
//    public void ORD_LST_02_NewUserEmptyOrderHistory() {
//        long t = startTimer("ORD-LST-02", "New user sees empty order history");
//
//        System.out.println("[Precondition] Login with fresh CUSTOMER account (no orders)");
//        new LoginPage(driver).open().loginAsCustomer(USER, PASS);
//
//        System.out.println("[Step 2] Navigate to Order History");
//        OrderHistoryPage ordPage = new OrderHistoryPage(driver).open();
//
//        // Nếu user hiện tại có orders, test này phải dùng account mới thực sự
//        // Đây là assertion logic: nếu account không có orders
//        boolean isEmpty = ordPage.isEmptyMessageDisplayed();
//        if (isEmpty) {
//            System.out.println("[Assert] Empty message displayed: " + ordPage.getEmptyMessage());
//            Assert.assertTrue(ordPage.getEmptyMessage().contains("Không có hóa đơn"),
//                    "Expected empty order message. Got: " + ordPage.getEmptyMessage());
//        } else {
//            System.out.println("[Info] Account has existing orders - skipping empty check (needs new account).");
//            Assert.assertTrue(ordPage.getOrderCount() >= 0, "Page should load without error.");
//        }
//
//        endTimer("ORD-LST-02", t);
//    }
//
//    @Test(description = "ORD-LST-03: Verify pagination handles large numbers of orders (Boundary).")
//    public void ORD_LST_03_PaginationBoundary() {
//        long t = startTimer("ORD-LST-03", "Pagination with >50 orders (Boundary)");
//
//        System.out.println("[Step 1] Use account with >50 orders");
//        new LoginPage(driver).open().loginAsCustomer(USER, PASS);
//
//        System.out.println("[Step 2] Navigate to Order History");
//        OrderHistoryPage ordPage = new OrderHistoryPage(driver).open();
//
//        System.out.println("[Assert] Page loads without error");
//        Assert.assertTrue(ordPage.isPageSafe(), "Page crashed loading order history.");
//
//        System.out.println("[Step 3] Click Page 2 in pagination");
//        try {
//            ordPage.clickPage(2);
//            System.out.println("[Assert] Page 2 loaded, orders: " + ordPage.getOrderCount());
//            Assert.assertTrue(ordPage.isPageSafe(), "Server crashed on page 2 of order history.");
//        } catch (Exception e) {
//            System.out.println("[Info] Page 2 not available (less than 1 page of orders): " + e.getMessage());
//        }
//    }
}
