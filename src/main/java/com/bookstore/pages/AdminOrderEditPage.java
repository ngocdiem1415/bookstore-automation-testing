package com.bookstore.pages;

import com.bookstore.utils.LoggerHelper;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;

public class AdminOrderEditPage extends BasePage {

    @FindBy(css = "[data-testid='admin-order-payment-method-select']")
    private WebElement paymentMethodSelect;

    @FindBy(css = "[data-testid='admin-order-payment-status-select']")
    private WebElement paymentStatusSelect;

    @FindBy(css = "[data-testid='admin-order-status-select']")
    private WebElement orderStatusSelect;

    @FindBy(css = "[data-testid='admin-save-order-btn']")
    private WebElement btnSaveOrder;

    @FindBy(css = "[data-testid='admin-order-notification']")
    private WebElement notification;

    @FindBy(css = "[data-testid='admin-order-cancel-btn']")
    private WebElement btnCancel;

    public AdminOrderEditPage(WebDriver driver, String baseUrl) {
        super(driver, baseUrl);
    }

    public AdminOrderEditPage selectPaymentMethod(String value) {
        LoggerHelper.info("[ADMIN][ORDER_EDIT_PAGE] Chọn phương thức thanh toán: " + value);

        Select select = new Select(waitUntilClickable(paymentMethodSelect));
        select.selectByValue(value);

        return this;
    }

    public AdminOrderEditPage selectPaymentStatus(String value) {
        LoggerHelper.info("[ADMIN][ORDER_EDIT_PAGE] Chọn trạng thái thanh toán: " + value);

        Select select = new Select(waitUntilClickable(paymentStatusSelect));
        select.selectByValue(value);

        return this;
    }

    public AdminOrderEditPage selectOrderStatus(String value) {
        LoggerHelper.info("[ADMIN][ORDER_EDIT_PAGE] Chọn trạng thái đơn hàng: " + value);

        Select select = new Select(waitUntilClickable(orderStatusSelect));
        select.selectByValue(value);

        return this;
    }

    public String getSelectedOrderStatus() {
        LoggerHelper.info("[ADMIN][ORDER_EDIT_PAGE] Lấy trạng thái đơn hàng hiện tại");

        try {
            Select select = new Select(wait.until(
                    ExpectedConditions.visibilityOf(orderStatusSelect)
            ));

            String status = select.getFirstSelectedOption().getText().trim();

            LoggerHelper.info("[ADMIN][ORDER_EDIT_PAGE] Trạng thái đơn hàng hiện tại: " + status);

            return status;
        } catch (Exception e) {
            LoggerHelper.warn("[ADMIN][ORDER_EDIT_PAGE] Không lấy được trạng thái đơn hàng: " + e.getMessage());
            return "";
        }
    }

    public String getSelectedPaymentStatus() {
        LoggerHelper.info("[ADMIN][ORDER_EDIT_PAGE] Lấy trạng thái thanh toán hiện tại");

        try {
            Select select = new Select(wait.until(
                    ExpectedConditions.visibilityOf(paymentStatusSelect)
            ));

            String status = select.getFirstSelectedOption().getText().trim();

            LoggerHelper.info("[ADMIN][ORDER_EDIT_PAGE] Trạng thái thanh toán hiện tại: " + status);

            return status;
        } catch (Exception e) {
            LoggerHelper.warn("[ADMIN][ORDER_EDIT_PAGE] Không lấy được trạng thái thanh toán: " + e.getMessage());
            return "";
        }
    }

    public boolean isOrderStatusSelectDisabled() {
        LoggerHelper.info("[ADMIN][ORDER_EDIT_PAGE] Kiểm tra dropdown trạng thái đơn hàng có bị disabled không");

        boolean disabled = orderStatusSelect.getAttribute("disabled") != null;

        LoggerHelper.info("[ADMIN][ORDER_EDIT_PAGE] Dropdown trạng thái đơn hàng disabled: " + disabled);

        return disabled;
    }

    public AdminOrderEditPage clickSave() {
        LoggerHelper.info("[ADMIN][ORDER_EDIT_PAGE] Click nút lưu thay đổi");
        clickElement(btnSaveOrder);
        return this;
    }

    public String clickSaveAndGetNotification() {
        LoggerHelper.info("[ADMIN][ORDER_EDIT_PAGE] Click lưu và lấy thông báo");
        scrollToElement(btnSaveOrder);
        jsClick(btnSaveOrder);
        try {
            String message = getTextOf(notification);
            LoggerHelper.info("[ADMIN][ORDER_EDIT_PAGE] Notification sau khi lưu: " + message);
            return message;
        } catch (Exception e) {
            LoggerHelper.warn("[ADMIN][ORDER_EDIT_PAGE] Không lấy được notification sau khi lưu: " + e.getMessage());
            return "";
        }
    }

    public AdminOrderEditPage clickCancel() {
        LoggerHelper.info("[ADMIN][ORDER_EDIT_PAGE] Click nút hủy để quay lại danh sách đơn hàng");
        clickElement(btnCancel);
        return this;
    }

    public boolean isNoServerError() {
        LoggerHelper.info("[ADMIN][ORDER_EDIT_PAGE] Kiểm tra trang không bị lỗi server");

        String title = driver.getTitle().toLowerCase();
        String body = driver.getPageSource().toLowerCase();

        boolean safe = !title.contains("500")
                && !body.contains("internal server error")
                && !body.contains("whitelabel error page");

        LoggerHelper.info("[ADMIN][ORDER_EDIT_PAGE] Trang không bị lỗi server: " + safe);

        return safe;
    }


}