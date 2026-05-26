package com.bookstore.pages.components;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

import static com.bookstore.pages.BasePage.EXPLICIT_WAIT_SECONDS;

public class SidebarComponent {
    private final WebDriver driver;
    private final WebDriverWait wait;
//
//    @FindBy(css = "[data-testid='sidebar-dashboard']")
//    private WebElement itemDashboard;
//
//    @FindBy(css = "[data-testid='sidebar-users']")
//    private WebElement itemUsers;
//
//    @FindBy(css = "[data-testid='sidebar-orders']")
//    private WebElement itemOrders;
//
//    @FindBy(css = "[data-testid='sidebar-categories']")
//    private WebElement itemCategories;
//
//    @FindBy(css = "[data-testid='sidebar-products']")
//    private WebElement itemProducts;
//
//    @FindBy(css = "[data-testid='sidebar-suppliers']")
//    private WebElement itemSuppliers;
//
//    @FindBy(css = "[data-testid='sidebar-discounts']")
//    private WebElement itemDiscounts;
//
//    @FindBy(css = "[data-testid='sidebar-admins']")
//    private WebElement itemAdmins;
//
//    @FindBy(css = "[data-testid='sidebar-settings']")
//    private WebElement itemSettings;

    public SidebarComponent(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(EXPLICIT_WAIT_SECONDS));
        PageFactory.initElements(driver, this);
    }

    private boolean isElementVisible(String dataTestId) {
        try {
            By locator = By.cssSelector("[data-testid='" + dataTestId + "']");
            WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
            return element.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    private boolean isElementHidden(String dataTestId) {
        try {
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(0));
            List<WebElement> elements = driver.findElements(By.cssSelector("[data-testid='" + dataTestId + "']"));

            // Nếu danh sách trống trơn -> Thymeleaf xóa thành công -> Trả về true (Ẩn đúng mong đợi)
            boolean isHidden = elements.isEmpty();

            // Khôi phục lại cấu hình đợi mặc định cho hệ thống
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(EXPLICIT_WAIT_SECONDS));
            return isHidden;
        } catch (Exception e) {
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(EXPLICIT_WAIT_SECONDS));
            return true;
        }
    }

    public boolean isDashboardVisible() {
        return isElementVisible("sidebar-dashboard");
    }

    public boolean isUsersVisible() {
        return isElementVisible("sidebar-users");
    }

    public boolean isOrdersVisible() {
        return isElementVisible("sidebar-orders");
    }

    public boolean isCategoriesVisible() {
        return isElementVisible("sidebar-categories");
    }

    public boolean isProductsVisible() {
        return isElementVisible("sidebar-products");
    }

    public boolean isSuppliersVisible() {
        return isElementVisible("sidebar-suppliers");
    }

    public boolean isDiscountsVisible() {
        return isElementVisible("sidebar-discounts");
    }

    public boolean isAdminsVisible() {
        return isElementVisible("sidebar-admins");
    }

    public boolean isSettingsVisible() {
        return isElementVisible("sidebar-settings");
    }

    public boolean isUsersHidden() {
        return isElementHidden("sidebar-users");
    }

    public boolean isCategoriesHidden() {
        return isElementHidden("sidebar-categories");
    }

    public boolean isProductsHidden() {
        return isElementHidden("sidebar-products");
    }

    public boolean isSuppliersHidden() {
        return isElementHidden("sidebar-suppliers");
    }

    public boolean isDiscountsHidden() {
        return isElementHidden("sidebar-discounts");
    }

    public boolean isAdminsHidden() {
        return isElementHidden("sidebar-admins");
    }
}