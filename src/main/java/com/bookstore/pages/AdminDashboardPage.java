package com.bookstore.pages;

import com.bookstore.base.BaseSetup;
import com.bookstore.factory.PageFactoryManager;
import com.bookstore.pages.components.SidebarComponent;
import com.bookstore.utils.LoggerHelper;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.List;

public class AdminDashboardPage extends BasePage {
    private static final String PAGE_URL = "/admin/dashboard";

    @FindBy(css = "[data-testid='admin-dashboard-title']")
    private WebElement lblDashboardTitle;

    @FindBy(css = "[data-testid='admin-dashboard-container']")
    private WebElement containerDashboard;

    @FindBy(css = "[data-testid='menu-toggle-button']")
    private WebElement menuToggleButton;
    @FindBy(css = "[data-testid='admin-recent-orders-section']")
    private WebElement recentOrdersSection;

    @FindBy(css = "[data-testid='admin-recent-orders-table']")
    private WebElement recentOrdersTable;

    @FindBy(css = "[data-testid='admin-recent-order-row']")
    private List<WebElement> recentOrderRows;

    @FindBy(css = "[data-testid='admin-recent-order-id']")
    private List<WebElement> recentOrderIds;

    @FindBy(css = "[data-testid='admin-recent-order-status']")
    private List<WebElement> recentOrderStatuses;

    @FindBy(css = "[data-testid='admin-recent-order-payment-status']")
    private List<WebElement> recentOrderPaymentStatuses;

    @FindBy(css = "[data-testid='admin-recent-order-edit-link']")
    private List<WebElement> recentOrderEditLinks;

    @FindBy(css = "[data-testid='admin-recent-orders-empty']")
    private WebElement recentOrdersEmptyMessage;

    private final SidebarComponent sidebar;

    public AdminDashboardPage(WebDriver driver, String baseUrl) {
        super(driver, baseUrl);
        this.sidebar = new SidebarComponent(driver,baseUrl);
    }


    public AdminDashboardPage open() {
        LoggerHelper.info("[ADMIN][DASHBOARD] Mở trang Admin Dashboard");
        driver.get(baseUrl + PAGE_URL);
        return this;
    }

    public boolean isOnAdminDashboard() {
        LoggerHelper.info("[ADMIN][DASHBOARD] Kiểm tra URL có chứa /admin/dashboard");
        return waitForUrlContains("/admin/dashboard");
    }

    public SidebarComponent getSidebar() {
        return sidebar;
    }

    public boolean isDashboardPageLoaded() {
        LoggerHelper.info("[ADMIN][DASHBOARD] Kiểm tra nội dung dashboard đã hiển thị");
        try {
            boolean displayed = isElementVisible(containerDashboard);
            LoggerHelper.info("[ADMIN][DASHBOARD] Container dashboard hiển thị: " + displayed);
            return displayed;
        } catch (Exception e) {
            LoggerHelper.warn("[ADMIN][DASHBOARD] Không tìm thấy container dashboard, kiểm tra fallback bằng URL");
            return waitForUrlContains("/admin/dashboard");
        }
    }

    public void openSidebarMenu() {
        LoggerHelper.info("[ADMIN][DASHBOARD] Click nút mở sidebar menu");
        clickElement(menuToggleButton);

        try {
            Thread.sleep(500);
            LoggerHelper.info("[ADMIN][DASHBOARD] Chờ sidebar ổn định sau khi mở menu");
        } catch (InterruptedException e) {
            LoggerHelper.warn("[ADMIN][DASHBOARD] Luồng chờ mở sidebar bị gián đoạn");
            Thread.currentThread().interrupt();
        }
    }

    public boolean isRecentOrdersSectionDisplayed() {
        LoggerHelper.info("[ADMIN][DASHBOARD] Kiểm tra khu vực Đơn hàng mới hiển thị");

        try {
            return isElementVisible(recentOrdersSection);
        } catch (Exception e) {
            LoggerHelper.warn("[ADMIN][DASHBOARD] Không tìm thấy khu vực Đơn hàng mới");
            return false;
        }
    }

    public int getRecentOrderCount() {
        LoggerHelper.info("[ADMIN][DASHBOARD] Đếm số đơn hàng mới trên dashboard");

        try {
            isElementVisible(recentOrdersTable);
            int count = recentOrderRows.size();
            LoggerHelper.info("[ADMIN][DASHBOARD] Số đơn hàng mới: " + count);
            return count;
        } catch (Exception e) {
            LoggerHelper.warn("[ADMIN][DASHBOARD] Không đọc được bảng đơn hàng mới: " + e.getMessage());
            return 0;
        }
    }

    public String getLatestOrderId() {
        LoggerHelper.info("[ADMIN][DASHBOARD] Lấy Order ID của đơn hàng mới nhất");
        wait.until(ExpectedConditions.visibilityOfAllElements(recentOrderIds));
        String orderId = recentOrderIds.get(0).getText().trim();
        LoggerHelper.info("[ADMIN][DASHBOARD] Order ID mới nhất: " + orderId);
        return orderId;
    }

    public AdminOrderPage clickLatestOrderEdit() {
        LoggerHelper.info("[ADMIN][DASHBOARD] Click edit đơn hàng mới nhất");
        wait.until(ExpectedConditions.visibilityOfAllElements(recentOrderEditLinks));
        clickElement(recentOrderEditLinks.get(0));
        return PageFactoryManager.getAdminOrderPage(driver, baseUrl);
    }

    public AdminOrderPage clickLatestOrderEditSafely() {
        LoggerHelper.info("[ADMIN][DASHBOARD] Kiểm tra bảng đơn hàng mới trước khi click edit");
        int count = getRecentOrderCount();
        if (count == 0) {
            throw new RuntimeException("Không có đơn hàng mới nào trên dashboard để click edit.");
        }
        LoggerHelper.info("[ADMIN][DASHBOARD] Số đơn hàng mới trên dashboard: " + count);
        LoggerHelper.info("[ADMIN][DASHBOARD] Order ID mới nhất: " + getLatestOrderId());
        return clickLatestOrderEdit();
    }
}
