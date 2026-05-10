package com.bookstore.pages.components;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static com.bookstore.pages.BasePage.EXPLICIT_WAIT_SECONDS;

/**
 * Component: Admin Sidebar (thanh điều hướng bên trái của trang Admin).
 * Covers: AUTH-LOG-02, AUTH-LOG-03
 */
public class SidebarComponent {

    private final WebDriver driver;
    private final WebDriverWait wait;

    @FindBy(css = "[data-testid='sidebar-dashboard']")
    private WebElement itemDashboard;

    @FindBy(css = "[data-testid='sidebar-users']")
    private WebElement itemUsers;

    @FindBy(css = "[data-testid='sidebar-orders']")
    private WebElement itemOrders;

    @FindBy(css = "[data-testid='sidebar-categories']")
    private WebElement itemCategories;

    @FindBy(css = "[data-testid='sidebar-products']")
    private WebElement itemProducts;

    @FindBy(css = "[data-testid='sidebar-suppliers']")
    private WebElement itemSuppliers;

    @FindBy(css = "[data-testid='sidebar-discounts']")
    private WebElement itemDiscounts;

    @FindBy(css = "[data-testid='sidebar-admins']")
    private WebElement itemAdmins;

    @FindBy(css = "[data-testid='sidebar-settings']")
    private WebElement itemSettings;

    public SidebarComponent(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(EXPLICIT_WAIT_SECONDS));
        PageFactory.initElements(driver, this);
    }

    private boolean isVisible(WebElement element) {
        try {
            return wait.until(ExpectedConditions.visibilityOf(element)).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isDashboardVisible() {
        return isVisible(itemDashboard);
    }

    public boolean isUsersVisible() {
        return isVisible(itemUsers);
    }

    public boolean isOrdersVisible() {
        return isVisible(itemOrders);
    }

    public boolean isCategoriesVisible() {
        return isVisible(itemCategories);
    }

    public boolean isProductsVisible() {
        return isVisible(itemProducts);
    }

    public boolean isSuppliersVisible() {
        return isVisible(itemSuppliers);
    }

    public boolean isDiscountsVisible() {
        return isVisible(itemDiscounts);
    }

    public boolean isAdminsVisible() {
        return isVisible(itemAdmins);
    }

    public boolean isSettingsVisible() {
        return isVisible(itemSettings);
    }

    /**
     * AUTH-LOG-02: Kiểm tra ADMIN thấy đầy đủ 9 mục sidebar.
     * Expected: dashboard, users, orders, categories, products, suppliers, discounts, admins, settings
     */
    public boolean hasFullAdminSidebar() {
        System.out.println("[SidebarComponent] Verifying full ADMIN sidebar...");
        return isDashboardVisible()
                && isUsersVisible()
                && isOrdersVisible()
                && isCategoriesVisible()
                && isProductsVisible()
                && isSuppliersVisible()
                && isDiscountsVisible()
                && isAdminsVisible()
                && isSettingsVisible();
    }

    /**
     * AUTH-LOG-03: Kiểm tra ORDER_STAFF thấy đúng 3 mục sidebar.
     * Expected: dashboard, orders, settings (KHÔNG có: users, categories, products, suppliers, discounts, admins)
     */
    public boolean hasOrderStaffSidebar() {
        System.out.println("[SidebarComponent] Verifying ORDER_STAFF sidebar...");
        boolean hasRequired = isDashboardVisible() && isOrdersVisible() && isSettingsVisible();
        boolean noUnauthorized = !isUsersVisible()
                && !isCategoriesVisible()
                && !isProductsVisible()
                && !isSuppliersVisible()
                && !isDiscountsVisible()
                && !isAdminsVisible();
        return hasRequired && noUnauthorized;
    }
}
