package com.bookstore.tests.cart;

import com.bookstore.factory.PageFactoryManager;
import com.bookstore.pages.*;
import com.bookstore.utils.JsonDataProvider;
import com.bookstore.utils.LoggerHelper;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Map;

public class CartAddTest extends CartBaseTest {

    @Test(priority = 1, description = "CART-ADD-01: Xác minh người dùng có thể thêm sản phẩm còn hàng vào giỏ hàng.")
    public void CART_ADD_01_AddInStockProduct() {
        LoggerHelper.info("[CART][ADD] Bắt đầu kiểm thử thêm sản phẩm còn hàng vào giỏ hàng");
        loginAsCustomer();

        ProductListPage listPage = PageFactoryManager.getProductListPage(getDriver(), baseUrl);
        LoggerHelper.info("[CART][ADD] Mở trang danh sách sản phẩm");
        listPage.open();

        Assert.assertTrue(listPage.getBookCount() > 0, "[FAIL] Không tìm thấy quyển sách nào hiển thị trên UI!");

        LoggerHelper.info("[CART][ADD] Mở chi tiết sản phẩm đầu tiên");
        ProductDetailPage detailPage = listPage.clickBookAt(0);

        int stock = detailPage.getStockQuantity();
        LoggerHelper.info("[CART][ADD] Quyển sách được chọn phải có số lượng kho là" + stock);
        Assert.assertTrue(stock > 0, "[FAIL] Quyển sách được chọn phải còn hàng (Stock > 0).");

        LoggerHelper.info("[CART][ADD] Click nút thêm vào giỏ hàng");
        detailPage.clickAddToCart();
        String alertText = detailPage.getSuccessMessage();

        LoggerHelper.info("[CART][ADD] Thông báo thực tế: " + alertText);
        Assert.assertTrue(
                alertText.contains("Sản phẩm đã được thêm vào giỏ hàng!"),
                "[FAIL] Không xuất hiện thông báo thêm vào giỏ hàng thành công! Actual: " + alertText);

        LoggerHelper.info("[CART][ADD] Thêm sản phẩm còn hàng vào giỏ hàng thành công");
    }

    @Test(
            priority = 2,
            dataProvider = "GlobalJsonFeeder",
            dataProviderClass = JsonDataProvider.class,
            description = "CART-ADD-02: Xác minh người dùng không thể thêm sản phẩm đã hết hàng."
    )
    public void CART_ADD_02_OutOfStockButtonDisabled(Map<String, String> data) {
        LoggerHelper.info("[CART][ADD] Bắt đầu kiểm thử không cho thêm sản phẩm hết hàng");
        loginAsCustomer();

        String outOfStockId = data.get("out_of_stock_id");
        LoggerHelper.info("[CART][ADD] Product ID hết hàng: " + outOfStockId);

        ProductDetailPage detailPage = PageFactoryManager.getProductDetailPage(getDriver(), baseUrl);
        LoggerHelper.info("[CART][ADD] Mở trang chi tiết sản phẩm hết hàng");
        detailPage.openById(outOfStockId);

        boolean isDisabled = detailPage.isAddToCartButtonDisabled();
        LoggerHelper.info("[CART][ADD] Trạng thái disabled của nút thêm giỏ hàng: " + isDisabled);

        Assert.assertTrue(isDisabled, "[FAIL] Nút 'Thêm vào giỏ hàng' phải bị khóa cứng khi sản phẩm đã hết hàng!");
        LoggerHelper.info("[CART][ADD] Hệ thống khóa nút thêm giỏ hàng đúng kỳ vọng");
    }

    @Test(priority = 3, description = "CART-ADD-03: Kiểm tra số lượng thêm vào giỏ hàng vượt quá số lượng tồn kho (Giới hạn).")
    public void CART_ADD_03_ExceedStockQuantityBoundary() throws InterruptedException {
        LoggerHelper.info("[CART][UPDATE] Bắt đầu kiểm thử không cho nhập số lượng vượt quá số lượng kho hàng");
        loginAsCustomer();

        ProductListPage listPage = PageFactoryManager.getProductListPage(getDriver(), baseUrl);
        LoggerHelper.info("[CART][ADD] Mở trang danh sách sản phẩm");
        listPage.open();

        Assert.assertTrue(listPage.getBookCount() > 0, "[FAIL] Không tìm thấy quyển sách nào hiển thị trên UI!");
        LoggerHelper.info("[CART][ADD] Mở chi tiết sản phẩm đầu tiên");
        ProductDetailPage detailPage = listPage.clickBookAt(0);

        int stock = detailPage.getStockQuantity();
        int attemptQty = stock + 5;
        detailPage.forceSetQuantity(attemptQty);
        int actualQtyInInput = detailPage.getCurrentQuantityValue();
        Thread.sleep(300);

        LoggerHelper.info("[CART][ADD] Hệ thống điều chỉnh giá trị ô nhập liệu về ngưỡng Stock tối đa!"
                + "\n  Số lượng kho (Stock)       : " + stock
                + "\n  Số lượng cố tình nhập      : " + attemptQty
                + "\n  Số lượng thực tế trong ô ô Input: " + actualQtyInInput);
        Assert.assertEquals(actualQtyInInput, stock,
                "[FAIL] Hệ thống không tự động điều chỉnh giá trị ô nhập liệu về ngưỡng Stock tối đa!");

        LoggerHelper.info("[CART][ADD] Hệ thống tự động điều chỉnh giá trị ô nhập liệu về ngưỡng Stock tối đa!");
    }
}