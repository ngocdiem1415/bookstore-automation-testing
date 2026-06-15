package com.bookstore.pages.components;

import com.bookstore.pages.BasePage;
import com.bookstore.utils.LoggerHelper;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class SidebarComponent extends BasePage {
    @FindBy(css = "[data-testid='sidebar-dashboard']")
    private WebElement menuDashboard;

    @FindBy(css = "[data-testid='sidebar-users']")
    private WebElement menuUsers;

    @FindBy(css = "[data-testid='sidebar-orders']")
    private WebElement menuOrders;

    @FindBy(css = "[data-testid='sidebar-categories']")
    private WebElement menuCategories;

    @FindBy(css = "[data-testid='sidebar-products']")
    private WebElement menuProducts;

    @FindBy(css = "[data-testid='sidebar-suppliers']")
    private WebElement menuSuppliers;

    @FindBy(css = "[data-testid='sidebar-discounts']")
    private WebElement menuDiscounts;

    @FindBy(css = "[data-testid='sidebar-admins']")
    private WebElement menuAdmins;

    @FindBy(css = "[data-testid='sidebar-settings']")
    private WebElement menuSettings;

    public SidebarComponent(WebDriver driver, String baseUrl) {
        super(driver, baseUrl);
    }

    private boolean isElementDisplayed(WebElement element) {
        try {
            isElementVisible(element);
            return element.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isDashboardVisible() {
        LoggerHelper.info("[ADMIN][SIDEBAR] Kiểm tra menu Dashboard hiển thị");
        return isElementDisplayed(menuDashboard);
    }

    public boolean isUsersVisible() {
        LoggerHelper.info("[ADMIN][SIDEBAR] Kiểm tra menu Users hiển thị");
        return isElementDisplayed(menuUsers);
    }

    public boolean isOrdersVisible() {
        LoggerHelper.info("[ADMIN][SIDEBAR] Kiểm tra menu Orders hiển thị");
        return isElementDisplayed(menuOrders);
    }

    public boolean isCategoriesVisible() {
        LoggerHelper.info("[ADMIN][SIDEBAR] Kiểm tra menu Categories hiển thị");
        return isElementDisplayed(menuCategories);
    }

    public boolean isProductsVisible() {
        LoggerHelper.info("[ADMIN][SIDEBAR] Kiểm tra menu Products hiển thị");
        return isElementDisplayed(menuProducts);
    }

    public boolean isSuppliersVisible() {
        LoggerHelper.info("[ADMIN][SIDEBAR] Kiểm tra menu Suppliers hiển thị");
        return isElementDisplayed(menuSuppliers);
    }

    public boolean isDiscountsVisible() {
        LoggerHelper.info("[ADMIN][SIDEBAR] Kiểm tra menu Discounts hiển thị");
        return isElementDisplayed(menuDiscounts);
    }

    public boolean isAdminsVisible() {
        LoggerHelper.info("[ADMIN][SIDEBAR] Kiểm tra menu Admins hiển thị");
        return isElementDisplayed(menuAdmins);
    }

    public boolean isSettingsVisible() {
        LoggerHelper.info("[ADMIN][SIDEBAR] Kiểm tra menu Settings hiển thị");
        return isElementDisplayed(menuSettings);
    }
}