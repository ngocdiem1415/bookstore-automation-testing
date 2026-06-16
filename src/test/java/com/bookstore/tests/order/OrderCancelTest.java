package com.bookstore.tests.order;

import com.bookstore.factory.PageFactoryManager;
import com.bookstore.helpers.CleanupRegistry;
import com.bookstore.helpers.OrderFixtureHelper;
import com.bookstore.pages.CartPage;
import com.bookstore.pages.CheckoutPage;
import com.bookstore.pages.InvoiceDetailPage;
import com.bookstore.pages.InvoicePage;
import com.bookstore.pages.ProductDetailPage;
import com.bookstore.pages.ProductListPage;
import com.bookstore.utils.JsonDataProvider;
import com.bookstore.utils.LoggerHelper;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Map;

public class OrderCancelTest extends OrderBaseTest {

    @Test(
            priority = 1,
            description = "ORD-CAN-01: Huy don hang trang thai cho xu ly"
    )
    public void ORD_CAN_01_CancelPendingOrder() throws InterruptedException {
        LoggerHelper.info("[ORDER][CANCEL] Bắt đầu kiểm thử khách hàng hủy đơn COD mới tạo");

        String orderId = OrderFixtureHelper.createCodOrder(getDriver(), baseUrl);

        InvoiceDetailPage ordPage = PageFactoryManager.getInvoiceDetailPage(getDriver(), baseUrl);
        LoggerHelper.info("[ORDER][CANCEL] Hủy đơn hàng vừa tạo, mã đơn: " + orderId);
        ordPage.cancelOrderById(orderId);

        ordPage.openWithId(orderId);
        String statusAfter = ordPage.getDetailStatus();
        LoggerHelper.info("[ORDER][CANCEL] Trạng thái sau khi hủy đơn " + orderId + ": " + statusAfter);
        Assert.assertTrue(
                isCancelledStatus(statusAfter),
                "Order status must be Cancelled after customer cancels order ID "
                        + orderId + ". Actual: " + statusAfter);
    }

    @Test(
            priority = 2,
            description = "ORD-CAN-02: Khong the huy don dang giao hoac da hoan thanh"
    )
    public void ORD_CAN_02_CannotCancelShippedOrder() {
        LoggerHelper.info("[ORDER][CANCEL] Kiểm tra đơn đã giao/hoàn thành không hiển thị nút hủy");
        InvoicePage ordPage = loginAsCustomerAndOpenInvoicePage();

        try {
            ordPage.filterByStatus("Hoàn thành");
        } catch (Exception e) {
            LoggerHelper.error("[ORDER][CANCEL] Không lọc được đơn hoàn thành: " + e.getMessage());
        }

        int completedOrderCount = ordPage.getOrderCount();
        LoggerHelper.info("[ORDER][CANCEL] Số đơn hoàn thành tìm thấy: " + completedOrderCount);

        if (completedOrderCount > 0) {
            int cancelBtnsCount = ordPage.getCancelButtonCount();
            LoggerHelper.info("[ORDER][CANCEL] Số nút hủy đang hiển thị: " + cancelBtnsCount);
            Assert.assertEquals(cancelBtnsCount, 0,
                    "Completed orders must not show cancel buttons.");
        } else {
            LoggerHelper.warn("[ORDER][CANCEL] Tài khoản chưa có đơn hoàn thành, bỏ qua kiểm tra nút hủy.");
        }
    }

    @Test(
            priority = 3,
            dataProvider = "GlobalJsonFeeder",
            dataProviderClass = JsonDataProvider.class,
            description = "ORD-CAN-03: Kiem tra khoi phuc ton kho sau khi huy don"
    )
    public void ORD_CAN_03_StockRestoredOnCancel(Map<String, String> data) throws InterruptedException {
        LoggerHelper.info("[ORDER][CANCEL] Bắt đầu kiểm thử khôi phục tồn kho sau khi hủy đơn");
        int quantityToOrder = Integer.parseInt(data.get("quantity"));
        CartPage cartPage = loginAndCleanCart();

        ProductListPage listPage = PageFactoryManager.getProductListPage(getDriver(), baseUrl);
        listPage.open();

        int availableBooks = listPage.getBookCount();
        Assert.assertTrue(availableBooks > 0,
                "[ERROR] Precondition: No product found on product list.");

        ProductDetailPage detailPage = listPage.clickBookAt(0);
        int stockBefore = detailPage.getStockQuantity();
        LoggerHelper.info("[ORDER][CANCEL] Tồn kho trước khi đặt hàng: " + stockBefore);

        detailPage.forceSetQuantity(quantityToOrder);
        detailPage.clickAddToCart();
        detailPage.getSuccessMessage();

        cartPage.open();
        Assert.assertTrue(cartPage.getCartItemCount() > 0,
                "[ERROR] Precondition: Cart is empty after adding product.");
        cartPage.checkCheckboxAt(0);

        CheckoutPage checkoutPage = cartPage.clickBuyButton();
        checkoutPage.fillShippingInfo(
                data.get("name"),
                data.get("phone"),
                data.get("address"),
                data.get("city"),
                data.get("district"),
                data.get("ward")
        );
        checkoutPage.clickSaveAndGetAlert();
        checkoutPage.selectPaymentMethod(data.get("paymentMethod"));

        InvoiceDetailPage invoice = checkoutPage.clickBuyExpectingSuccess();
        invoice.open();
        Assert.assertTrue(invoice.isOnInvoicePage(),
                "Expected invoice page. Actual: " + invoice.getCurrentUrl());

        String orderId = invoice.getOrderId();
        Assert.assertTrue(orderId != null && !orderId.isBlank(),
                "Cannot capture created order ID for stock restore test.");
        CleanupRegistry.customerCancelOrderIds.add(orderId);
        LoggerHelper.info("[ORDER][CANCEL] Ghi nhận order để hủy dự phòng: " + orderId);

        listPage.open();
        detailPage = listPage.clickBookAt(0);
        int stockAfterOrder = detailPage.getStockQuantity();
        int expectedStockAfterOrder = stockBefore - quantityToOrder;

        LoggerHelper.info("[ORDER][CANCEL] Tồn kho trước: " + stockBefore
                + " | sau khi đặt hàng: " + stockAfterOrder);
        Assert.assertEquals(stockAfterOrder, expectedStockAfterOrder,
                "[FAIL] Stock was not reduced after placing order."
                        + "\n  Before : " + stockBefore
                        + "\n  Ordered: " + quantityToOrder
                        + "\n  Expect : " + expectedStockAfterOrder
                        + "\n  Actual : " + stockAfterOrder);

        InvoiceDetailPage ordPage = PageFactoryManager.getInvoiceDetailPage(getDriver(), baseUrl);
        LoggerHelper.info("[ORDER][CANCEL] Hủy đơn hàng theo mã đơn: " + orderId);
        ordPage.cancelOrderById(orderId);
        CleanupRegistry.customerCancelOrderIds.remove(orderId);

        ordPage.openWithId(orderId);
        detailPage = ordPage.clickProductInOrderDetail();

        int stockAfter = detailPage.getStockQuantity();
        LoggerHelper.info("[ORDER][CANCEL] Tồn kho trước: " + stockBefore + " | sau khi hủy: " + stockAfter);
        Assert.assertEquals(stockAfter, stockBefore,
                "[FAIL] Stock was not restored after cancelling order."
                        + "\n  Before       : " + stockBefore
                        + "\n  After cancel : " + stockAfter);
    }

    private boolean isCancelledStatus(String status) {
        String normalized = status == null ? "" : status.toLowerCase();
        return normalized.contains("hủy") || normalized.contains("huy") || normalized.contains("cancel");
    }
}
