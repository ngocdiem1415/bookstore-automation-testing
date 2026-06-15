package com.bookstore.tests.admin;

import com.bookstore.factory.PageFactoryManager;
import com.bookstore.pages.*;
import com.bookstore.utils.LoggerHelper;
import org.testng.Assert;
import org.testng.annotations.Test;


public class AdminOrderTest extends AdminBaseTest {
    private void prepareShippingOrder(AdminOrderPage page) {
        LoggerHelper.info("[ADMIN][ORDER] Chuẩn bị đơn hàng trạng thái Shipping");
        page.open();
        int index = page.searchByStatus("Pending");
        Assert.assertTrue(
                index >= 0,
                "Không tìm thấy đơn hàng trạng thái Pending để setup."
        );

        LoggerHelper.info(
                "[ADMIN][ORDER] Tìm thấy đơn Pending tại index: " + index);
        page.clickEditAt(index);
        AdminOrderEditPage editPage =
                PageFactoryManager.getAdminOrderEditPage(getDriver(), baseUrl);

        editPage.selectOrderStatus("Shipping");
        String notification = editPage.clickSaveAndGetNotification();
        LoggerHelper.info(
                "[ADMIN][ORDER] Notification sau khi chuyển Pending -> Shipping: "
                        + notification);
    }

    @Test(
            priority = 1,
            description = "ADM-ORD-01: Admin hoàn tất đơn hàng đang vận chuyển."
    )
    public void ADM_ORD_01_CompleteShippingOrder() {
        LoggerHelper.info(
                "[ADMIN][ORDER] Bắt đầu kiểm thử Shipping -> Completed");
        loginAsAdmin();
        AdminOrderPage page =
                PageFactoryManager.getAdminOrderPage(getDriver(), baseUrl);

        // Setup dữ liệu
        prepareShippingOrder(page);
        page.open();

        int index = page.searchByStatus("Shipping");
        Assert.assertTrue(
                index >= 0,
                "Không tìm thấy đơn hàng trạng thái Shipping.");

        LoggerHelper.info(
                "[ADMIN][ORDER] Mở đơn Shipping tại index: " + index);
        page.clickEditAt(index);
        AdminOrderEditPage editPage =
                PageFactoryManager.getAdminOrderEditPage(getDriver(), baseUrl);

        LoggerHelper.info(
                "[ADMIN][ORDER] Chuyển trạng thái Shipping -> Completed");
        editPage.selectOrderStatus("Completed");
        String notification = editPage.clickSaveAndGetNotification();

        LoggerHelper.info(
                "[ADMIN][ORDER] Notification: " + notification);
        page.open();
        int completedIndex = page.searchByStatus("Completed");

        Assert.assertTrue(
                completedIndex >= 0,
                "Không tìm thấy đơn hàng Completed sau khi cập nhật.");
        String orderStatus = page.getStatusAt(completedIndex);
        Assert.assertTrue(
                orderStatus.contains("Completed")
                        || orderStatus.contains("Đã nhận")
                        || notification.contains("thành công"),
                "Order status chưa chuyển sang Completed. Hiện tại: "
                        + orderStatus
        );
        LoggerHelper.info(
                "[ADMIN][ORDER] Kết thúc kiểm thử: PASS");
    }

    @Test(
            priority = 2,
            description = "ADM-ORD-02: Kiểm thử quản trị viên không thể thay đổi trạng thái đơn hàng từ Completed → Pending."
    )
    public void ADM_ORD_02_UpdateStatusBackward() {
        LoggerHelper.info("[ADMIN][ORDER] Bắt đầu kiểm thử trạng thái Completed không thể chỉnh sửa");
        loginAsAdmin();
        AdminOrderPage page = PageFactoryManager.getAdminOrderPage(getDriver(), baseUrl);
        LoggerHelper.info("[ADMIN][ORDER] Mở trang quản lý đơn hàng");
        page.open();
        int index = page.searchByStatus("Completed");
        Assert.assertTrue(
                index >= 0,
                "Không tìm thấy đơn hàng Completed để kiểm thử."
        );
        LoggerHelper.info("[ADMIN][ORDER] Mở đơn hàng Completed tại index: " + index);
        page.clickEditAt(index);
        AdminOrderEditPage editPage =
                PageFactoryManager.getAdminOrderEditPage(getDriver(), baseUrl);
        boolean disabled = editPage.isOrderStatusSelectDisabled();
        LoggerHelper.info("[ADMIN][ORDER] Dropdown trạng thái đơn hàng disabled: " + disabled);
        Assert.assertTrue(
                disabled,
                "Dropdown trạng thái phải bị khóa khi đơn hàng đã Completed."
        );
        LoggerHelper.info("[ADMIN][ORDER] Kết thúc kiểm thử: PASS");
    }

    @Test(
            priority = 3,
            description = "ADM-ORD-03: Quản trị viên hủy đơn hàng đang xử lý."
    )
    public void ADM_ORD_03_CancelPendingOrder() {
        LoggerHelper.info("[ADMIN][ORDER] Bắt đầu kiểm thử Pending -> Cancelled");
        loginAsAdmin();
        AdminOrderPage page = PageFactoryManager.getAdminOrderPage(getDriver(), baseUrl);
        page.open();
        int index = page.searchByStatus("Pending");

        Assert.assertTrue(
                index >= 0,
                "Không tìm thấy đơn hàng Pending để kiểm thử hủy đơn."
        );
        LoggerHelper.info("[ADMIN][ORDER] Mở đơn hàng Pending tại index: " + index);
        page.clickEditAt(index);
        AdminOrderEditPage editPage =
                PageFactoryManager.getAdminOrderEditPage(getDriver(), baseUrl);
        LoggerHelper.info("[ADMIN][ORDER] Chuyển trạng thái đơn hàng sang Cancelled");

        editPage.selectOrderStatus("Cancelled");
        String notification = editPage.clickSaveAndGetNotification();
        LoggerHelper.info("[ADMIN][ORDER] Notification sau khi hủy đơn: " + notification);
        page.open();

        int cancelledIndex = page.searchByStatus("Cancelled");
        Assert.assertTrue(
                cancelledIndex >= 0,
                "Không tìm thấy đơn hàng Cancelled sau khi cập nhật."
        );
        String orderStatus = page.getStatusAt(cancelledIndex);
        Assert.assertTrue(
                orderStatus.contains("Cancelled")
                        || orderStatus.contains("Hủy")
                        || notification.contains("thành công"),
                "Order status chưa chuyển sang Cancelled. Hiện tại: " + orderStatus
        );

        LoggerHelper.info("[ADMIN][ORDER] Kết thúc kiểm thử: PASS");
    }
}
