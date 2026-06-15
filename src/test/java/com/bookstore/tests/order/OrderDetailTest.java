package com.bookstore.tests.order;

import com.bookstore.base.BaseSetup;
import com.bookstore.factory.PageFactoryManager;
import com.bookstore.pages.*;
import com.bookstore.utils.JsonDataProvider;
import com.bookstore.utils.LoggerHelper;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Map;

/**
 * ORD-DET-01/02/03
 */
public class OrderDetailTest extends OrderBaseTest {

    @Test(
            priority = 1,
            description = "ORD-DET-01: Verify customer can view detailed information of an order."
    )
    public void ORD_DET_01_ViewOrderDetail() {
        LoggerHelper.info("[ORDER][DETAIL] Bắt đầu kiểm thử xem chi tiết đơn hàng");
        OrderHistoryPage ordPage = loginAsCustomerAndOpenOrderHistoryPage();

        int orderCount = ordPage.getOrderCount();
        LoggerHelper.info("[ORDER][DETAIL] Số lượng đơn hàng tìm thấy: " + orderCount);
        Assert.assertTrue(orderCount > 0, "Precondition: Tài khoản phải có ít nhất 1 đơn hàng để xem chi tiết!");

        // Ghi nhận trạng thái đơn hàng ở danh sách trước khi click xem chi tiết
        String expectedStatus = ordPage.getOrderStatusAt(0);
        LoggerHelper.info("[ORDER][DETAIL] Trạng thái đơn hàng trước khi xem chi tiết: " + expectedStatus);

        LoggerHelper.info("[ORDER][DETAIL] Click xem chi tiết đơn hàng đầu tiên");
        ordPage.clickDetailAt(0);

        LoggerHelper.info("[ORDER][DETAIL] Kiểm tra thông tin người nhận hiển thị");
        Assert.assertTrue(ordPage.isDetailInfoDisplayed(),
                "[FAIL] Không hiển thị thông tin người nhận trong chi tiết đơn hàng!");

        String recipientName = ordPage.getDetailRecipientName();
        String recipientPhone = ordPage.getDetailRecipientPhone();
        String recipientAddress = ordPage.getDetailRecipientAddress();

        LoggerHelper.info("[ORDER][DETAIL] Người nhận: " + recipientName);
        LoggerHelper.info("[ORDER][DETAIL] Số điện thoại người nhận: " + recipientPhone);
        LoggerHelper.info("[ORDER][DETAIL] Địa chỉ người nhận: " + recipientAddress);
        Assert.assertFalse(recipientName.isEmpty(), "Tên người nhận không được trống.");

        int itemCount = ordPage.getDetailItemCount();
        LoggerHelper.info("[ORDER][DETAIL] Số lượng sản phẩm trong chi tiết đơn hàng: " + itemCount);
        Assert.assertTrue(itemCount > 0, "Đơn hàng chi tiết phải có ít nhất 1 sản phẩm.");

        String actualStatus = ordPage.getDetailStatus();
        LoggerHelper.info("[ORDER][DETAIL] Trạng thái chi tiết: " + actualStatus);
        Assert.assertFalse(actualStatus.isEmpty(), "Trạng thái đơn hàng không hiển thị trong chi tiết.");

        LoggerHelper.info("[ORDER][DETAIL] Quay lại danh sách đơn hàng");
        ordPage.clickBackToList();
    }

    @Test(
            priority = 2,
            dataProvider = "GlobalJsonFeeder",
            dataProviderClass = JsonDataProvider.class,
            description = "ORD-DET-02: Verify accessing order detail with non-existent ID shows empty state or safe page."
    )
    public void ORD_DET_02_InvalidOrderId(Map<String, String> data) {
        LoggerHelper.info("[ORDER][DETAIL] Bắt đầu kiểm thử truy cập ID đơn hàng không tồn tại");

        loginAsCustomer();

        String invalidId = data.get("INVALID_ORDER_ID");
        LoggerHelper.info("[ORDER][DETAIL] Invalid order ID: " + invalidId);

        OrderHistoryPage ordPage = PageFactoryManager.getOrderHistoryPage(getDriver(), baseUrl);
        ordPage.openWithId(invalidId);

        LoggerHelper.info("[ORDER][DETAIL] Kiểm tra trang không lỗi 500");
        Assert.assertTrue(ordPage.isPageSafe(),
                "[FAIL] Hệ thống lỗi 500 khi truy cập ID đơn hàng không tồn tại!");

        boolean is404 = ordPage.is403Or404();
        boolean isEmpty = ordPage.isEmptyMessageDisplayed();

        LoggerHelper.info("[ORDER][DETAIL] is403Or404 = " + is404);
        LoggerHelper.info("[ORDER][DETAIL] isEmptyMessageDisplayed = " + isEmpty);

        Assert.assertTrue(is404 || isEmpty || ordPage.getOrderCount() == 0,
                "Truy cập đơn hàng không tồn tại phải báo lỗi 404, hiển thị trống hoặc không có đơn hàng.");
    }

    @Test(
            priority = 3,
            dataProvider = "GlobalJsonFeeder",
            dataProviderClass = JsonDataProvider.class,
            description = "ORD-DET-03: Verify accessing other user's order ID is blocked (Boundary/IDOR Protection)."
    )
    public void ORD_DET_03_IdorBoundary(Map<String, String> data) {
        LoggerHelper.info("[ORDER][DETAIL] Bắt đầu kiểm tra bảo mật IDOR truy cập ID đơn hàng của người dùng khác");
        loginAsCustomer();

        String otherUserOrderId = data.get("USER_B_ORDER_ID");
        OrderHistoryPage ordPage = PageFactoryManager.getOrderHistoryPage(getDriver(), baseUrl);
        LoggerHelper.info("[ORDER][DETAIL] Chuyển hướng truy cập ID đơn hàng của người dùng khác: " + otherUserOrderId);
        ordPage.openWithId(otherUserOrderId);

        String currentUrl = ordPage.getCurrentUrl();
        LoggerHelper.info("[ORDER][DETAIL] URL hiện tại: " + currentUrl);

        boolean isProtected = ordPage.is403Or404()
                || (currentUrl.contains("/invoice") && !currentUrl.contains("id=" + otherUserOrderId))
                || currentUrl.contains("/home")
                || currentUrl.contains("/login");

        Assert.assertTrue(isProtected,
                "[SECURITY VIOLATION] Người dùng có thể truy cập trái phép xem thông tin đơn hàng của người khác!");
        LoggerHelper.info("[ORDER][DETAIL] Người dùng không thể truy cập trái phép xem thông tin đơn hàng của người khác!");
    }
}
