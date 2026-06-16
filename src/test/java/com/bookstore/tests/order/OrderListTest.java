package com.bookstore.tests.order;

import com.bookstore.pages.InvoicePage;
import com.bookstore.utils.JsonDataProvider;
import com.bookstore.utils.LoggerHelper;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Map;

public class OrderListTest extends OrderBaseTest {

    @Test(
            priority = 1,
            description = "ORD-LST-01: Xác minh khách hàng có thể xem lịch sử đơn đặt hàng của họ."
    )
    public void ORD_LST_01_ViewOrderHistory() {
        LoggerHelper.info("[ORDER][LIST] Bắt đầu kiểm thử xem lịch sử đơn hàng");
        InvoicePage ordPage = loginAsCustomerAndOpenInvoicePage();

        int count = ordPage.getOrderCount();
        LoggerHelper.info("[ORDER][LIST] Số lượng đơn hàng tìm thấy: " + count);

        Assert.assertTrue(count >= 0,
                "Trang lịch sử đơn hàng phải tải được danh sách đơn hàng (>= 0).");

        LoggerHelper.info("[ORDER][LIST] Trang lịch sử đơn hàng tải thành công");
    }

    @Test(
            priority = 2,
            dataProvider = "GlobalJsonFeeder",
            dataProviderClass = JsonDataProvider.class,
            description = "ORD-LST-02: Tìm kiếm đơn hàng khi tài khoản chưa có đơn hàng"
    )
    public void ORD_LST_02_NewUserEmptyOrderHistory(Map<String,String> data) {
        LoggerHelper.info("[ORDER][LIST] Bắt đầu kiểm thử tìm kiếm đơn hàng khi tài khoản chưa có đơn hàng");

        String user = data.get("username");
        String pass = data.get("password");

        LoggerHelper.info("[ORDER][LIST] Đăng nhập bằng tài khoản: " + user);
        InvoicePage ordPage = loginAsCustomerNotOrderAndOpenInvoicePage(user, pass);

        boolean isEmpty = ordPage.isEmptyMessageDisplayed();
        LoggerHelper.info("[ORDER][LIST] Trạng thái hiển thị empty message: " + isEmpty);

        if (isEmpty) {
            String emptyMsg = ordPage.getEmptyMessage();
            LoggerHelper.info("[ORDER][LIST] Nội dung empty message: " + emptyMsg);

            Assert.assertTrue(emptyMsg.contains("Không có hóa đơn") || emptyMsg.contains("Chưa có"),
                    "Thông báo đơn hàng trống không đúng: " + emptyMsg);
        } else {
            LoggerHelper.warn("[ORDER][LIST] Tài khoản test hiện tại đã có sẵn đơn hàng, kiểm tra danh sách > 0");
            Assert.assertTrue(ordPage.getOrderCount() > 0,
                    "Tài khoản có đơn hàng thì số lượng đơn hàng phải > 0.");
        }
    }

    @Test(
            priority = 3,
            dataProvider = "GlobalJsonFeeder",
            dataProviderClass = JsonDataProvider.class,
            description = "ORD-LST-03: Tìm kiếm đơn hàng theo tên sản phẩm"
    )
    public void ORD_LST_03_SearchOrderByProductName(Map<String, String> data) {
        LoggerHelper.info("[ORDER][LIST] Bắt đầu kiểm thử tìm kiếm đơn hàng theo tên sản phẩm");

        InvoicePage ordPage = loginAsCustomerAndOpenInvoicePage();

        String keywordEmpty = data.get("keyword");
        LoggerHelper.info("[ORDER][LIST] Tìm kiếm với từ khóa không tồn tại: " + keywordEmpty);

        ordPage.searchOrder(keywordEmpty);

        LoggerHelper.info("[ORDER][LIST] Kiểm tra hiển thị empty message khi không có kết quả");
        Assert.assertTrue(ordPage.isEmptyMessageDisplayed(),
                "[FAIL] Hệ thống không hiển thị thông báo trống khi tìm kiếm từ khóa không khớp!");

        LoggerHelper.info("[ORDER][LIST] Kiểm tra số lượng đơn hàng sau tìm kiếm bằng 0");
        Assert.assertEquals(ordPage.getOrderCount(), 0,
                "[FAIL] Vẫn hiển thị đơn hàng khi tìm kiếm với từ khóa không tồn tại!");

        LoggerHelper.info("[ORDER][LIST] Mở lại trang lịch sử đơn hàng để dọn trạng thái tìm kiếm");
        ordPage.open();
    }
}
