package com.bookstore.pages;

import com.bookstore.base.BaseSetup;
import com.bookstore.pages.components.SidebarComponent;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

/**
 * Page Object: Trang Dashboard Admin (/admin/dashboard).
 * Covers: AUTH-LOG-02 (ADMIN role), AUTH-LOG-03 (ORDER_STAFF role).
 */
public class AdminDashboardPage extends BasePage {

    private static final String PAGE_URL = "/admin/dashboard";

    // ===========================
    // LOCATORS (data-testid)
    // ===========================

    @FindBy(css = "[data-testid='admin-dashboard-title']")
    private WebElement lblDashboardTitle;

    @FindBy(css = "[data-testid='admin-dashboard-container']")
    private WebElement containerDashboard;

    // ===========================
    // COMPONENTS
    // ===========================

    private final SidebarComponent sidebar;

    // ===========================
    // CONSTRUCTOR
    // ===========================

    public AdminDashboardPage(WebDriver driver) {
        super(driver);
        this.sidebar = new SidebarComponent(driver);
    }

    // ===========================
    // NAVIGATION
    // ===========================

    public AdminDashboardPage open() {
        driver.get(getCurrentUrl() + PAGE_URL);
        return this;
    }

    // ===========================
    // GETTER METHODS (Assertions)
    // ===========================

    /**
     * Kiểm tra đang ở trang /admin/dashboard.
     */
    public boolean isOnAdminDashboard() {
        boolean urlOk = waitForUrlContains("/admin/dashboard");
        System.out.println("[AdminDashboardPage] Current URL: " + driver.getCurrentUrl());
        return urlOk;
    }

    /**
     * Lấy SidebarComponent để kiểm tra các mục menu.
     */
    public SidebarComponent getSidebar() {
        return sidebar;
    }

    /**
     * Kiểm tra dashboard container có hiển thị không.
     */
    public boolean isDashboardPageLoaded() {
        try {
            return wait.until(ExpectedConditions.visibilityOf(containerDashboard)).isDisplayed();
        } catch (Exception e) {
            // Fallback: kiểm tra theo URL
            return driver.getCurrentUrl().contains("/admin/dashboard");
        }
    }
}
