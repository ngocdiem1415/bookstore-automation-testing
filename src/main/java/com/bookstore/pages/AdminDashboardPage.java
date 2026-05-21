package com.bookstore.pages;

import com.bookstore.base.BaseSetup;
import com.bookstore.pages.components.SidebarComponent;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class AdminDashboardPage extends BasePage {
    private static final String PAGE_URL = "/admin/dashboard";

    @FindBy(css = "[data-testid='admin-dashboard-title']")
    private WebElement lblDashboardTitle;

    @FindBy(css = "[data-testid='admin-dashboard-container']")
    private WebElement containerDashboard;

    @FindBy(css = "[data-testid='menu-toggle-button']")
    private WebElement menuToggleButton;

    private final SidebarComponent sidebar;

    public AdminDashboardPage(WebDriver driver, String baseUrl) {
        super(driver, baseUrl);
        this.sidebar = new SidebarComponent(driver);
    }


    public AdminDashboardPage open() {
        driver.get(baseUrl + PAGE_URL);
        return this;
    }

    public boolean isOnAdminDashboard() {
        return waitForUrlContains("/admin/dashboard");
    }

    public SidebarComponent getSidebar() {
        return sidebar;
    }

    public boolean isDashboardPageLoaded() {
        try {
            return wait.until(ExpectedConditions.visibilityOf(containerDashboard)).isDisplayed();
        } catch (Exception e) {
            return driver.getCurrentUrl().contains("/admin/dashboard");
        }
    }

    public void openSidebarMenu() {
        wait.until(ExpectedConditions.elementToBeClickable(menuToggleButton)).click();
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
