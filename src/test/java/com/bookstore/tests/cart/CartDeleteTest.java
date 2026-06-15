package com.bookstore.tests.cart;

import com.bookstore.pages.CartPage;
import com.bookstore.utils.LoggerHelper;
import org.testng.Assert;
import org.testng.annotations.Test;


public class CartDeleteTest extends CartBaseTest {

    @Test(
            priority = 1,
            description = "CART-DEL-01: Kiểm tra xóa 1 mặt hàng khỏi giỏ hàng."
    )
    public void CART_DEL_01_DeleteOneItem() throws InterruptedException {
        LoggerHelper.info("[CART][DELETE] Bắt đầu kiểm thử xóa một sản phẩm khỏi giỏ hàng");
        LoggerHelper.info("[CART][DELETE] Chuẩn bị giỏ hàng có 2 sản phẩm");
        CartPage cartPage = loginAndAddItemsToCart(2);

        int countBefore = cartPage.getCartItemCount();
        LoggerHelper.info("[CART][DELETE] Số item trước khi xóa: " + countBefore);
        Assert.assertTrue(countBefore > 0,
                "[ERROR] Precondition: Giỏ hàng phải có ít nhất 1 sản phẩm!");

        LoggerHelper.info("[CART][DELETE] Click xóa sản phẩm tại index 0");
        cartPage.clickDeleteAt(0);

        int countAfter = cartPage.getCartItemCount();
        LoggerHelper.info("[CART][DELETE] Số item sau khi xóa: " + countAfter);
        Assert.assertEquals(countAfter, countBefore - 1,
                "[FAIL] Số lượng item không giảm đúng 1 sau khi xóa!");

        LoggerHelper.info("[CART][DELETE] Xóa một sản phẩm khỏi giỏ hàng thành công");
    }

    @Test(
            priority = 2,
            description = "CART-DEL-02: Kiểm thử xóa toàn bộ sản phẩm khỏi giỏ hàng."
    )
    public void CART_DEL_02_DeleteAllItemsIndividually() {
        LoggerHelper.info("[CART][DELETE] Bắt đầu kiểm thử xóa toàn bộ sản phẩm khỏi giỏ hàng");
        CartPage cartPage = loginAndAddItemsToCart(2);

        LoggerHelper.info("[CART][DELETE] Xóa tất cả item trong giỏ hàng");
        cartPage.deleteAllItems();

        int finalCount = cartPage.getCartItemCount();
        LoggerHelper.info("[CART][DELETE] Số item cuối cùng: " + finalCount);
        Assert.assertEquals(finalCount, 0,
                "[FAIL] Vẫn còn " + finalCount + " item sau khi xóa hết!");

        boolean isEmptyMsgShown = cartPage.isCartEmptyMessageDisplayed();
        LoggerHelper.info("[CART][DELETE] Trạng thái hiển thị thông báo giỏ hàng trống: " + isEmptyMsgShown);
        Assert.assertTrue(isEmptyMsgShown,
                "[FAIL] [cart-empty-msg] không hiển thị sau khi xóa hết sản phẩm!");

        String emptyMsg = cartPage.getCartEmptyMessage();
        LoggerHelper.info("[CART][DELETE] Nội dung thông báo giỏ hàng trống: " + emptyMsg);
        Assert.assertTrue(emptyMsg.contains("Chưa có sản phẩm nào trong giỏ hàn"),
                "[FAIL] Nội dung [cart-empty-msg] không đúng kỳ vọng!");

        LoggerHelper.info("[CART][DELETE] Xóa toàn bộ sản phẩm khỏi giỏ hàng thành công");
    }


}
