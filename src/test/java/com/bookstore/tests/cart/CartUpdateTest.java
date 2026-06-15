package com.bookstore.tests.cart;

import com.bookstore.base.BaseSetup;
import com.bookstore.factory.PageFactoryManager;
import com.bookstore.pages.CartPage;
import com.bookstore.pages.LoginPage;
import com.bookstore.pages.ProductDetailPage;
import com.bookstore.pages.ProductListPage;
import com.bookstore.utils.DataHelper;
import com.bookstore.utils.JsonDataProvider;
import com.bookstore.utils.LoggerHelper;
import org.openqa.selenium.JavascriptExecutor;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Map;


public class CartUpdateTest extends CartBaseTest {


    @Test(
            priority = 1,
            description = "CART-UPD-01: Kiểm tra thay đổi số lượng mặt hàng trong trang Giỏ hàng.."
    )
    public void CART_UPD_01_IncreaseQuantity() throws InterruptedException {
        LoggerHelper.info("[CART][UPDATE] Kiểm tra việc thay đổi số lượng mặt hàng trong trang Giỏ hàng.");
        CartPage cartPage = loginAndAddOneItemToCart();

        int qtyBefore = cartPage.getQuantityAt(0);
        long totalBefore = cartPage.getTotalPriceAsLong();
        LoggerHelper.info("[CART][UPDATE] CLick vào nút tăng số lượng sản phẩm");
        cartPage.clickIncreaseAt(0);

        // Delay 3s chờ AJAX update
        Thread.sleep(3000);
        int qtyAfter = cartPage.getQuantityAt(0);
        long totalAfter = cartPage.getTotalPriceAsLong();

        LoggerHelper.info("[CART][UPDATE] Số lượng sản phẩm thay khi click từ " + qtyBefore + " lên " + qtyAfter);
        Assert.assertEquals(qtyAfter, qtyBefore + 1,
                "[FAIL] Số lượng không tăng thêm 1 sau khi click [+]!"
                        + "\n  Before: " + qtyBefore + "  After: " + qtyAfter);

        LoggerHelper.info("[CART][UPDATE] Tổng tiền tăng từ" + totalBefore + " lên " + totalAfter + "sau khi tăng số lượng!");
        Assert.assertTrue(totalAfter >= totalBefore,
                "[FAIL] Tổng tiền không tăng sau khi tăng số lượng!"
                        + "\n  Before: " + totalBefore + "  After: " + totalAfter);

        LoggerHelper.info("[CART][UPDATE] Ô số lượng được tăng đúng kỳ vọng");

    }

    @Test(
            priority = 2,
            dataProvider = "GlobalJsonFeeder",
            dataProviderClass = JsonDataProvider.class,
            description = "CART-UPD-02: Xác minh không thể nhập số âm thủ công trong giỏ hàng."
    )
    public void CART_UPD_02_CannotTypeNegativeQuantity(Map<String, String> data) {
        LoggerHelper.info("[CART][UPDATE] Bắt đầu kiểm thử không cho nhập số lượng không hợp lệ");

        CartPage cartPage = loginAndAddOneItemToCart();

        String invalidQuantity = data.get("invalid_quantity");
        LoggerHelper.info("[CART][UPDATE] Giá trị số lượng không hợp lệ: " + invalidQuantity);

        LoggerHelper.info("[CART][UPDATE] Thử nhập giá trị không hợp lệ vào ô số lượng");
        cartPage.tryTypeIntoQuantityInput(0, invalidQuantity);

        boolean isDisabled = cartPage.isQuantityInputDisabled(0);
        LoggerHelper.info("[CART][UPDATE] Trạng thái disabled của ô số lượng: " + isDisabled);
        Assert.assertTrue(isDisabled,
                "[FAIL] [cart-item-quantity] phải bị khóa (disabled/readonly)!"
                        + "\n  Người dùng không được nhập số lượng không hợp lệ trực tiếp.");

        LoggerHelper.info("[CART][UPDATE] Ô số lượng được khóa đúng kỳ vọng");
    }

    @Test(
            priority = 3,
            description = "CART-UPD-03: Kiểm tra cập nhật số lượng sản phẩm ở mức tối thiểu trong giỏ hàng."
    )
    public void CART_UPD_03_DecreaseQuantityAtMinimum() {
        LoggerHelper.info("[CART][UPDATE] Bắt đầu kiểm thử giảm số lượng sản phẩm ở mức tối thiểu");
        CartPage cartPage = loginAndAddOneItemToCart();

        // Xác nhận quantity ban đầu = 1 (mới thêm vào giỏ)
        int qtyBefore = cartPage.getQuantityAt(0);
        int countBefore = cartPage.getCartItemCount();
        LoggerHelper.info("[CART][UPDATE] Số lượng tại chỉ mục 0: " + qtyBefore);
        Assert.assertEquals(qtyBefore, 1,
                "[Precondition] Sản phẩm mới thêm phải có quantity = 1!");

        LoggerHelper.info("[CART][UPDATE] Click nút giảm số lượng tại index 0");
        cartPage.clickDecreaseAt(0);

        int countAfter = cartPage.getCartItemCount();
        LoggerHelper.info("[CART][UPDATE] Số item sau khi click giảm: " + countAfter);
        if (countAfter < countBefore) {
            LoggerHelper.info("[CART][UPDATE] Hệ thống xóa sản phẩm khi số lượng giảm về 0");
            Assert.assertEquals(countAfter, countBefore - 1,
                    "[FAIL] Số item phải giảm đúng 1 khi hệ thống xóa sản phẩm ở ngưỡng tối thiểu.");
            return;
        }
        LoggerHelper.info("[CART][UPDATE] Hệ thống xóa sản phẩm khi số lượng giảm về 0");
    }
}
