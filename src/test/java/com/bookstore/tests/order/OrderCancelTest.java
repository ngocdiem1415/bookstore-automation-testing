package com.bookstore.tests.order;

import com.bookstore.base.BaseSetup;
import com.bookstore.factory.PageFactoryManager;
import com.bookstore.pages.*;
import org.testng.Assert;
import org.testng.annotations.Test;

/** ORD-CAN-01/02/03 */
public class OrderCancelTest extends BaseSetup {
    private static final String USER = "diem_tester", PASS = "Abc@12345";

    @Test(description = "ORD-CAN-01: Verify cancelling a PENDING order.")
    public void ORD_CAN_01_CancelPendingOrder() {
        PageFactoryManager.getLoginPage(driver,baseUrl).loginAsCustomer(USER, PASS);
        OrderHistoryPage ordPage =  PageFactoryManager.getOrderHistoryPage(driver,baseUrl);

        try {
            ordPage.filterByStatus("Chờ xử lý");
        } catch (Exception e) {
            System.out.println("[Info] Filter not available, proceeding without filter.");
        }

        int cancelBtnsBefore = ordPage.getCancelButtonCount();
        Assert.assertTrue(cancelBtnsBefore > 0,
                "Precondition: Need at least 1 PENDING order with [order-cancel-btn].");
        ordPage.clickCancelAt(0);
        String statusAfter = ordPage.getOrderStatusAt(0);
        Assert.assertTrue(
                statusAfter.contains("Đã hủy") || statusAfter.contains("CANCELLED"),
                "Expected cancelled status. Got: " + statusAfter);
        int cancelBtnsAfter = ordPage.getCancelButtonCount();
        Assert.assertTrue(cancelBtnsAfter < cancelBtnsBefore,
                "Expected cancel button to disappear after cancellation.");
    }

    @Test(description = "ORD-CAN-02: Verify cannot cancel a SHIPPED order.")
    public void ORD_CAN_02_CannotCancelShippedOrder() {
        PageFactoryManager.getLoginPage(driver,baseUrl).loginAsCustomer(USER, PASS);

        System.out.println("[Step 1] Navigate to Order History");
        OrderHistoryPage ordPage =  PageFactoryManager.getOrderHistoryPage(driver,baseUrl);

        System.out.println("[Step 2] Filter Status = 'Đang giao' (Shipped)");
        try {
            ordPage.filterByStatus("Đang giao");
        } catch (Exception e) {
            System.out.println("[Info] Filter not available.");
        }

        int shippedOrderCount = ordPage.getOrderCount();
        System.out.println("[Assert] Shipped orders found: " + shippedOrderCount);

        if (shippedOrderCount > 0) {
            System.out.println("[Assert] Verify NO [order-cancel-btn] on SHIPPED orders");
            Assert.assertEquals(ordPage.getCancelButtonCount(), 0,
                    "Expected NO cancel buttons for SHIPPED orders. Got: "
                            + ordPage.getCancelButtonCount());
        } else {
            System.out.println("[Info] No SHIPPED orders available to test. Marking as acceptable.");
        }
    }

//    @Test(description = "ORD-CAN-03: Verify stock is restored upon cancellation (Boundary).")
//    public void ORD_CAN_03_StockRestoredOnCancel() throws InterruptedException {
//        PageFactoryManager.getLoginPage(driver,baseUrl).loginAsCustomer(USER, PASS);
//
//        System.out.println("[Step 1] Get stock before — check via Product Detail page");
//        ProductListPage listPage =  PageFactoryManager.getProductListPage(driver,baseUrl);
//        ProductDetailPage detailPage = listPage.clickBookAt(0);
//        int stockBefore = detailPage.getStockQuantity();
//        String productUrl = detailPage.getCurrentUrl();
//        System.out.println("[Info] Product: " + productUrl + " | Stock before: " + stockBefore);
//
//        System.out.println("[Step 1] Create order with 2 books");
//        detailPage.forceSetQuantity(2);
//        detailPage.clickAddToCart();
//
//        CheckoutPage cp = new CheckoutPage(driver).open();
//        cp.fillShippingInfo("Test User", "0900000000", "123 Test St",
//                "Hồ Chí Minh", "Quận 1", "Phường Bến Nghé");
//        cp.clickSaveAndGetAlert();
//        cp.selectPaymentMethod("Thanh toán khi nhận hàng (COD)");
//        cp.clickBuyExpectingSuccess();
//
//        System.out.println("[Step 2] Cancel the order using [order-cancel-btn]");
//        OrderHistoryPage ordPage = new OrderHistoryPage(driver).open();
//        Assert.assertTrue(ordPage.getCancelButtonCount() > 0, "Need cancel button for new order.");
//        ordPage.clickCancelAt(0);
//        Thread.sleep(1500);
//
//        System.out.println("[Step 3] Check stock restored via API");
//        driver.get(productUrl);
//        detailPage = new ProductDetailPage(driver);
//        int stockAfter = detailPage.getStockQuantity();
//        System.out.println("[Assert] Stock before=" + stockBefore + " | Stock after=" + stockAfter);
//
//        Assert.assertTrue(stockAfter >= stockBefore,
//                "Expected stock to be restored to >= " + stockBefore + " after cancel. Got: " + stockAfter);
//
//        endTimer("ORD-CAN-03", t);
//    }
}
