package com.bookstore.tests.order;

import com.bookstore.factory.PageFactoryManager;
import com.bookstore.pages.InvoiceDetailPage;
import com.bookstore.pages.InvoicePage;
import com.bookstore.utils.JsonDataProvider;
import com.bookstore.utils.LoggerHelper;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Map;

public class OrderDetailTest extends OrderBaseTest {

    @Test(
            priority = 1,
            description = "ORD-DET-01: Xác minh khách hàng có thể xem thông tin chi tiết của đơn hàng."
    )
    public void ORD_DET_01_ViewOrderDetail() {
        LoggerHelper.info("[ORDER][DETAIL] Bắt đầu kiểm thử xem chi tiết đơn hàng");
        InvoicePage invoicePage = loginAsCustomerAndOpenInvoicePage();

        int orderCount = invoicePage.getOrderCount();
        LoggerHelper.info("[ORDER][DETAIL] Số lượng đơn hàng tìm thấy: " + orderCount);
        Assert.assertTrue(orderCount > 0,
                "Precondition: Tài khoản phải có ít nhất 1 đơn hàng để xem chi tiết!");

        String expectedStatus = invoicePage.getOrderStatusAt(0);
        LoggerHelper.info("[ORDER][DETAIL] Trạng thái đơn hàng trước khi xem chi tiết: " + expectedStatus);

        LoggerHelper.info("[ORDER][DETAIL] Click xem chi tiết đơn hàng đầu tiên");
        InvoiceDetailPage detailPage = invoicePage.clickDetailAt(0);

        LoggerHelper.info("[ORDER][DETAIL] Kiểm tra thông tin người nhận hiển thị");
        Assert.assertTrue(detailPage.isDetailInfoDisplayed(),
                "[FAIL] Không hiển thị thông tin người nhận trong chi tiết đơn hàng!");

        String recipientName = detailPage.getDetailRecipientName();
        String recipientPhone = detailPage.getDetailRecipientPhone();
        String recipientAddress = detailPage.getDetailRecipientAddress();

        LoggerHelper.info("[ORDER][DETAIL] Người nhận: " + recipientName);
        LoggerHelper.info("[ORDER][DETAIL] Số điện thoại người nhận: " + recipientPhone);
        LoggerHelper.info("[ORDER][DETAIL] Địa chỉ người nhận: " + recipientAddress);
        Assert.assertFalse(recipientName.isEmpty(), "Tên người nhận không được trống.");

        int itemCount = detailPage.getDetailItemCount();
        LoggerHelper.info("[ORDER][DETAIL] Số lượng sản phẩm trong chi tiết đơn hàng: " + itemCount);
        Assert.assertTrue(itemCount > 0, "Đơn hàng chi tiết phải có ít nhất 1 sản phẩm.");

        String actualStatus = detailPage.getDetailStatus();
        LoggerHelper.info("[ORDER][DETAIL] Trạng thái chi tiết: " + actualStatus);
        Assert.assertFalse(actualStatus.isEmpty(), "Trạng thái đơn hàng không hiển thị trong chi tiết.");

        LoggerHelper.info("[ORDER][DETAIL] Quay lại danh sách đơn hàng");
        detailPage.clickBackToList();
    }

    @Test(
            priority = 2,
            dataProvider = "GlobalJsonFeeder",
            dataProviderClass = JsonDataProvider.class,
            description = "ORD-DET-02: Kiểm thử truy cập ID đơn hàng không tồn tại."
    )
    public void ORD_DET_02_InvalidOrderId(Map<String, String> data) {
        LoggerHelper.info("[ORDER][DETAIL] Bắt đầu kiểm thử truy cập ID đơn hàng không tồn tại");

        loginAsCustomer();

        String invalidId = data.get("INVALID_ORDER_ID");
        LoggerHelper.info("[ORDER][DETAIL] Invalid order ID: " + invalidId);

        InvoiceDetailPage detailPage = PageFactoryManager.getInvoiceDetailPage(getDriver(), baseUrl);
        detailPage.openWithId(invalidId);

        LoggerHelper.info("[ORDER][DETAIL] Kiểm tra trang không lỗi 500");
        Assert.assertTrue(detailPage.isPageSafe(),
                "[FAIL] Hệ thống lỗi 500 khi truy cập ID đơn hàng không tồn tại!");

        boolean is404 = detailPage.is403Or404();
        LoggerHelper.info("[ORDER][DETAIL] is403Or404 = " + is404);

        Assert.assertTrue(is404,
                "Truy cập đơn hàng không tồn tại phải báo lỗi 404 hoặc chặn truy cập.");
    }

    @Test(
            priority = 3,
            dataProvider = "GlobalJsonFeeder",
            dataProviderClass = JsonDataProvider.class,
            description = "ORD-DET-03: Kiểm tra bảo mật IDOR truy cập ID đơn hàng của người dùng khác."
    )
    public void ORD_DET_03_IdorBoundary(Map<String, String> data) {
        LoggerHelper.info("[ORDER][DETAIL] Bắt đầu kiểm tra bảo mật IDOR truy cập ID đơn hàng của người dùng khác");
        loginAsCustomer();

        String otherUserOrderId = data.get("USER_B_ORDER_ID");
        InvoiceDetailPage detailPage = PageFactoryManager.getInvoiceDetailPage(getDriver(), baseUrl);
        LoggerHelper.info("[ORDER][DETAIL] Chuyển hướng truy cập ID đơn hàng của người dùng khác: " + otherUserOrderId);
        detailPage.openWithId(otherUserOrderId);

        String currentUrl = detailPage.getCurrentUrl();
        LoggerHelper.info("[ORDER][DETAIL] URL hiện tại: " + currentUrl);

        boolean isProtected = detailPage.is403Or404()
                || (currentUrl.contains("/invoice") && !currentUrl.contains("id=" + otherUserOrderId))
                || currentUrl.contains("/home")
                || currentUrl.contains("/login");

        Assert.assertTrue(isProtected,
                "[SECURITY VIOLATION] Người dùng có thể truy cập trái phép xem thông tin đơn hàng của người khác!");
        LoggerHelper.info("[ORDER][DETAIL] Người dùng không thể truy cập trái phép xem thông tin đơn hàng của người khác!");
    }
}
