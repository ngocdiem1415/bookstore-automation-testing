package com.bookstore.tests.order;

import com.bookstore.factory.PageFactoryManager;
import com.bookstore.pages.*;
import com.bookstore.utils.JsonDataProvider;
import com.bookstore.utils.LoggerHelper;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Map;

public class OrderCancelTest extends OrderBaseTest {

    @Test(
            priority = 1,
            description = "ORD-CAN-01: Hủy đơn hàng trạng thái chờ xử lý"
    )
    public void ORD_CAN_01_CancelPendingOrder() {
        LoggerHelper.info("[ORDER][CANCEL] Bắt đầu kiểm thử hủy đơn hàng trạng thái chờ xử lý");

        OrderHistoryPage ordPage = loginAsCustomerAndOpenOrderHistoryPage();

        try {
            LoggerHelper.info("[ORDER][CANCEL] Lọc đơn hàng trạng thái Chờ thanh toán");
            ordPage.filterByStatus("Chờ thanh toán");
        } catch (Exception e) {
            LoggerHelper.warn("[ORDER][CANCEL] Không lọc được tab Chờ thanh toán, tiếp tục không lọc");
        }

        int cancelBtnsBefore = ordPage.getCancelButtonCount();
        LoggerHelper.info("[ORDER][CANCEL] Số nút hủy trước khi hủy: " + cancelBtnsBefore);

        Assert.assertTrue(cancelBtnsBefore > 0,
                "Precondition: Cần ít nhất 1 đơn hàng ở trạng thái Pending để test hủy đơn!");

        String statusBefore = ordPage.getOrderStatusAt(0);
        LoggerHelper.info("[ORDER][CANCEL] Trạng thái đơn hàng trước khi hủy: " + statusBefore);

        LoggerHelper.info("[ORDER][CANCEL] Click hủy đơn hàng đầu tiên");
        ordPage.clickCancelAt(0);

        String statusAfter = ordPage.getOrderStatusAt(0);
        LoggerHelper.info("[ORDER][CANCEL] Trạng thái đơn hàng sau khi hủy: " + statusAfter);

        Assert.assertTrue(
                statusAfter.toLowerCase().contains("hủy") || statusAfter.toLowerCase().contains("cancel"),
                "Trạng thái đơn hàng phải chuyển thành Đã hủy. Thực tế: " + statusAfter);

        int cancelBtnsAfter = ordPage.getCancelButtonCount();
        LoggerHelper.info("[ORDER][CANCEL] Số nút hủy sau khi hủy: " + cancelBtnsAfter);

        Assert.assertTrue(cancelBtnsAfter < cancelBtnsBefore,
                "Nút Hủy đơn hàng của đơn này phải biến mất sau khi hủy thành công.");
    }

    @Test(
            priority = 2,
            description = "ORD-CAN-02: Kiểm tra không thể hủy đơn hàng đang giao hoặc đã hoàn thành"
    )
    public void ORD_CAN_02_CannotCancelShippedOrder() {
        LoggerHelper.info("[ORDER][CANCEL] Kiểm thử hông cho phép hủy đơn hàng đang giao hoặc đã hoàn thành...");
        OrderHistoryPage ordPage = loginAsCustomerAndOpenOrderHistoryPage();

        // Thử lọc theo tab Hoàn thành
        try {
            ordPage.filterByStatus("Hoàn thành");
        } catch (Exception e) {
            LoggerHelper.error("[ORDER][CANCEL] Không lọc được tab Hoàn thành.");
        }

        int completedOrderCount = ordPage.getOrderCount();
        LoggerHelper.info("[ORDER][CANCEL] Tìm thấy " + completedOrderCount + " đơn hàng hoàn thành.");

        if (completedOrderCount > 0) {
            int cancelBtnsCount = ordPage.getCancelButtonCount();
            LoggerHelper.info("[ORDER][CANCEL] Số nút hủy đơn hàng hiển thị: " + cancelBtnsCount);
            Assert.assertEquals(cancelBtnsCount, 0,
                    "Không được xuất hiện nút Hủy đơn hàng đối với các đơn hàng đã Hoàn thành!");
        } else {
            LoggerHelper.warn("[ORDER][CANCEL] Không có đơn hàng hoàn thành nào trên tài khoản này để test. Bỏ qua kiểm tra nút hủy.");
        }
    }

    @Test(
            priority = 3,
            dataProvider = "GlobalJsonFeeder",
            dataProviderClass = JsonDataProvider.class,
            description = "ORD-CAN-03: Kiểm tra khôi phục tồn kho sau khi hủy đơn"
    )
    public void ORD_CAN_03_StockRestoredOnCancel(Map<String, String> data) throws InterruptedException {
        LoggerHelper.info("[ORDER][CANCEL] Bắt đầu kiểm thử khôi phục tồn kho sau khi hủy đơn");
        LoggerHelper.info("[ORDER][CANCEL] Dọn giỏ hàng trước khi tạo đơn");
        int quantityToOrder = Integer.parseInt(data.get("quantity"));
        CartPage cartPage = loginAndCleanCart();

        ProductListPage listPage = PageFactoryManager.getProductListPage(getDriver(), baseUrl);
        LoggerHelper.info("[ORDER][CANCEL] Mở danh sách sản phẩm");
        listPage.open();

        int availableBooks = listPage.getBookCount();
        Assert.assertTrue(availableBooks > 0,
                "[ERROR] Precondition: Không tìm thấy sản phẩm nào trên trang danh sách!");
        ProductDetailPage detailPage = listPage.clickBookAt(0);
        int stockBefore = detailPage.getStockQuantity();
        LoggerHelper.info("[ORDER][CANCEL] Stock trước khi đặt hàng: " + stockBefore);

        LoggerHelper.info("[ORDER][CANCEL] Số lượng đặt hàng: " + quantityToOrder);
        detailPage.forceSetQuantity(quantityToOrder);

        LoggerHelper.info("[ORDER][CANCEL] Thêm sản phẩm vào giỏ hàng");
        detailPage.clickAddToCart();
        cartPage.open();
        Assert.assertTrue(cartPage.getCartItemCount() > 0,
                "[ERROR] Precondition: Giỏ hàng vẫn trống sau khi thêm sản phẩm!");
        cartPage.checkCheckboxAt(0);

        //checkout
        CheckoutPage checkoutPage = cartPage.clickBuyButton();
        LoggerHelper.info("[ORDER][CANCEL] Điền thông tin checkout");
        checkoutPage.fillShippingInfo(
                data.get("name"),
                data.get("phone"),
                data.get("address"),
                data.get("city"),
                data.get("district"),
                data.get("ward")
        );
        checkoutPage.clickSaveAndGetAlert();

        LoggerHelper.info("[ORDER][CANCEL] Đặt hàng thành công, mở invoice");
        InvoicePage invoice = checkoutPage.clickBuyExpectingSuccess();
        invoice.open();
        Assert.assertTrue(invoice.isOnInvoicePage(),
                "Kỳ vọng chuyển đến trang hóa đơn /invoice. Thực tế: " + invoice.getCurrentUrl());

        LoggerHelper.info("[ORDER][CANCEL] Quay lại trang danh sách và chi tiết sản phẩm để kiểm tra trừ kho");
        listPage.open();
        detailPage = listPage.clickBookAt(0);
        int stockAfterOrder = detailPage.getStockQuantity();
        int expectedStockAfterOrder = stockBefore - quantityToOrder;

        LoggerHelper.info("[ORDER][CANCEL] Tồn kho trước: " + stockBefore + " | Tồn kho sau khi đặt hàng (chưa hủy): " + stockAfterOrder);
        Assert.assertEquals(stockAfterOrder, expectedStockAfterOrder,
                "[FAIL] Hệ thống không trừ bớt số lượng sản phẩm trong kho sau khi đặt hàng thành công!"
                        + "\n  Trước khi mua : " + stockBefore
                        + "\n  Số lượng mua  : " + quantityToOrder
                        + "\n  Kỳ vọng còn lại: " + expectedStockAfterOrder
                        + "\n  Thực tế còn lại: " + stockAfterOrder);

        // cancelled
        LoggerHelper.info("[ORDER][CANCEL] Mở lịch sử đơn hàng để hủy đơn vừa đặt");
        OrderHistoryPage ordPage = PageFactoryManager.getOrderHistoryPage(getDriver(), baseUrl);
        ordPage.open();
        Assert.assertTrue(ordPage.getCancelButtonCount() > 0, "Đơn hàng vừa đặt phải có nút hủy!");
        LoggerHelper.info("[ORDER][CANCEL] Click hủy đơn hàng vừa đặt");
        ordPage.clickCancelAt(0);

        LoggerHelper.info("[ORDER][CANCEL] Mở lại trang lịch sử đơn hàng sau khi hủy để đồng bộ UI");
        ordPage.open();

        LoggerHelper.info("[ORDER][CANCEL] Mở chi tiết đơn hàng đã hủy để kiểm tra sản phẩm");
        ordPage.clickDetailAt(0);
        detailPage = ordPage.clickProductInOrderDetail();

        int stockAfter = detailPage.getStockQuantity();
        LoggerHelper.info("[ORDER][CANCEL] Tồn kho trước: " + stockBefore + " | Tồn kho sau khi hủy: " + stockAfter);
        Assert.assertEquals(stockAfter, stockBefore,
                "[FAIL] Số lượng tồn kho không được khôi phục chính xác về trạng thái ban đầu!"
                        + "\n  Trước khi mua : " + stockBefore
                        + "\n  Sau khi hủy   : " + stockAfter);
        LoggerHelper.info("[ORDER][CANCEL] Stock sau khi hủy: " + stockAfter);
    }
}
