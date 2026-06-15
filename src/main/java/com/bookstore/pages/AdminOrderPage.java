package com.bookstore.pages;

import com.bookstore.utils.LoggerHelper;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public class AdminOrderPage extends BasePage {

    private static final String PAGE_URL = "/admin/orders";

    @FindBy(css = "[data-testid='admin-order-table'] tbody tr")
    private List<WebElement> tableRows;

    @FindBy(css = "[data-testid='admin-order-id']")
    private List<WebElement> orderIds;

    @FindBy(css = "[data-testid='admin-order-date']")
    private List<WebElement> orderDates;

    @FindBy(css = "[data-testid='admin-order-customer']")
    private List<WebElement> orderCustomers;

    @FindBy(css = "[data-testid='admin-order-phone']")
    private List<WebElement> orderPhones;

    @FindBy(css = "[data-testid='admin-order-total']")
    private List<WebElement> orderTotals;

    @FindBy(css = "[data-testid='admin-order-payment']")
    private List<WebElement> orderPayments;

    @FindBy(css = "[data-testid='admin-order-status']")
    private List<WebElement> orderStatuses;

    @FindBy(css = "[data-testid^='admin-edit-order-']")
    private List<WebElement> editOrderButtons;

    @FindBy(css = "[data-testid='admin-order-search-input']")
    private WebElement txtSearch;

    public AdminOrderPage(WebDriver driver, String baseUrl) {
        super(driver, baseUrl);
    }

    public AdminOrderPage open() {
        LoggerHelper.info("[ADMIN][ORDER_PAGE] Mở trang quản lý đơn hàng");
        driver.get(baseUrl + PAGE_URL);
        return this;
    }

    public int getOrderCount() {
        LoggerHelper.info("[ADMIN][ORDER_PAGE] Đếm số đơn hàng trong bảng");

        try {
            int count = tableRows.size();
            LoggerHelper.info("[ADMIN][ORDER_PAGE] Số đơn hàng hiện tại: " + count);
            return count;
        } catch (Exception e) {
            LoggerHelper.warn("[ADMIN][ORDER_PAGE] Không đếm được số đơn hàng: " + e.getMessage());
            return 0;
        }
    }

    public String getOrderIdAt(int index) {
        LoggerHelper.info("[ADMIN][ORDER_PAGE] Lấy mã đơn hàng tại index: " + index);

        try {
            String value = getTextOf(orderIds.get(index));
            LoggerHelper.info("[ADMIN][ORDER_PAGE] Mã đơn hàng: " + value);
            return value;
        } catch (Exception e) {
            LoggerHelper.warn("[ADMIN][ORDER_PAGE] Không lấy được mã đơn hàng: " + e.getMessage());
            return "";
        }
    }

    public String getOrderDateAt(int index) {
        LoggerHelper.info("[ADMIN][ORDER_PAGE] Lấy ngày đặt hàng tại index: " + index);

        try {
            String value = getTextOf(orderDates.get(index));
            LoggerHelper.info("[ADMIN][ORDER_PAGE] Ngày đặt hàng: " + value);
            return value;
        } catch (Exception e) {
            LoggerHelper.warn("[ADMIN][ORDER_PAGE] Không lấy được ngày đặt hàng: " + e.getMessage());
            return "";
        }
    }

    public String getCustomerAt(int index) {
        LoggerHelper.info("[ADMIN][ORDER_PAGE] Lấy tên khách hàng tại index: " + index);

        try {
            String value = getTextOf(orderCustomers.get(index));
            LoggerHelper.info("[ADMIN][ORDER_PAGE] Khách hàng: " + value);
            return value;
        } catch (Exception e) {
            LoggerHelper.warn("[ADMIN][ORDER_PAGE] Không lấy được tên khách hàng: " + e.getMessage());
            return "";
        }
    }

    public String getPhoneAt(int index) {
        LoggerHelper.info("[ADMIN][ORDER_PAGE] Lấy số điện thoại tại index: " + index);

        try {
            String value = getTextOf(orderPhones.get(index));
            LoggerHelper.info("[ADMIN][ORDER_PAGE] Số điện thoại: " + value);
            return value;
        } catch (Exception e) {
            LoggerHelper.warn("[ADMIN][ORDER_PAGE] Không lấy được số điện thoại: " + e.getMessage());
            return "";
        }
    }

    public String getTotalAt(int index) {
        LoggerHelper.info("[ADMIN][ORDER_PAGE] Lấy tổng tiền tại index: " + index);

        try {
            String value = getTextOf(orderTotals.get(index));
            LoggerHelper.info("[ADMIN][ORDER_PAGE] Tổng tiền: " + value);
            return value;
        } catch (Exception e) {
            LoggerHelper.warn("[ADMIN][ORDER_PAGE] Không lấy được tổng tiền: " + e.getMessage());
            return "";
        }
    }

    public String getPaymentStatusAt(int index) {
        LoggerHelper.info("[ADMIN][ORDER_PAGE] Lấy trạng thái thanh toán tại index: " + index);

        try {
            String value = getTextOf(orderPayments.get(index));
            LoggerHelper.info("[ADMIN][ORDER_PAGE] Trạng thái thanh toán: " + value);
            return value;
        } catch (Exception e) {
            LoggerHelper.warn("[ADMIN][ORDER_PAGE] Không lấy được trạng thái thanh toán: " + e.getMessage());
            return "";
        }
    }

    public String getStatusAt(int index) {
        LoggerHelper.info("[ADMIN][ORDER_PAGE] Lấy trạng thái đơn hàng tại index: " + index);

        try {
            String value = getTextOf(orderStatuses.get(index));
            LoggerHelper.info("[ADMIN][ORDER_PAGE] Trạng thái đơn hàng: " + value);
            return value;
        } catch (Exception e) {
            LoggerHelper.warn("[ADMIN][ORDER_PAGE] Không lấy được trạng thái đơn hàng: " + e.getMessage());
            return "";
        }
    }

    public AdminOrderPage clickEditAt(int index) {
        LoggerHelper.info("[ADMIN][ORDER_PAGE] Click nút sửa đơn hàng tại index: " + index);

        WebElement editBtn = waitUntilClickable(editOrderButtons.get(index));

        clickElement(editBtn);
        return this;
    }

    public boolean isOrderTableDisplayed() {
        LoggerHelper.info("[ADMIN][ORDER_PAGE] Kiểm tra bảng đơn hàng hiển thị");

        try {
            boolean displayed = !tableRows.isEmpty();
            LoggerHelper.info("[ADMIN][ORDER_PAGE] Bảng đơn hàng hiển thị: " + displayed);
            return displayed;
        } catch (Exception e) {
            LoggerHelper.warn("[ADMIN][ORDER_PAGE] Không kiểm tra được bảng đơn hàng: " + e.getMessage());
            return false;
        }
    }

    public int findFirstOrderIndexByStatus(String expectedStatus) {
        LoggerHelper.info("[ADMIN][ORDER_PAGE] Tìm đơn hàng có trạng thái: " + expectedStatus);
        for (int i = 0; i < orderStatuses.size(); i++) {
            String status = getStatusAt(i);

            if (status.equalsIgnoreCase(expectedStatus)
                    || status.toLowerCase().contains(expectedStatus.toLowerCase())) {

                LoggerHelper.info("[ADMIN][ORDER_PAGE] Tìm thấy đơn hàng tại index: " + i);
                return i;
            }
        }
        LoggerHelper.warn("[ADMIN][ORDER_PAGE] Không tìm thấy đơn hàng có trạng thái: " + expectedStatus);
        return -1;
    }

    public int searchByStatus(String expectedStatus) {
        LoggerHelper.info("[ADMIN][ORDER_PAGE] Tìm kiếm đơn hàng có trạng thái: " + expectedStatus);
        clearAndSendText(txtSearch, expectedStatus);
        waitForPageLoaded();
        for (int i = 0; i < orderStatuses.size(); i++) {
            String actualStatus = orderStatuses.get(i).getText().trim();

            LoggerHelper.info(
                    "[ADMIN][ORDER_PAGE] Index " + i +
                            " | Status = " + actualStatus);
            if (actualStatus.equalsIgnoreCase(expectedStatus)) {
                LoggerHelper.info(
                        "[ADMIN][ORDER_PAGE] Tìm thấy đơn hàng trạng thái "
                                + expectedStatus +
                                " tại index " + i);
                return i;
            }
        }
        LoggerHelper.warn(
                "[ADMIN][ORDER_PAGE] Không tìm thấy đơn hàng trạng thái "
                        + expectedStatus);
        return -1;
    }

    protected void waitForPageLoaded() {
        try {
            WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(5));
            shortWait.until(ExpectedConditions.visibilityOfAllElements(tableRows));
        } catch (Exception ignored) {
        }
    }
}