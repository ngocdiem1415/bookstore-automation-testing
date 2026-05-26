package com.bookstore.pages.components;

import com.bookstore.pages.BasePage;
import com.bookstore.pages.HomePage;
import com.bookstore.pages.ProfilePage;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class HeaderComponent extends BasePage {

    @FindBy(css = "[data-testid='search-input']")
    private WebElement txtSearchInput;

    @FindBy(css = "[data-testid='search-btn']")
    private WebElement btnSearch;

    @FindBy(css = "[data-testid='username-menu']")
    private WebElement lblUsernameMenu;

    // 2. Định vị nút Hồ sơ bên trong Menu thả xuống
    @FindBy(css = "[data-testid='nav-profile']")
    private WebElement lnkProfile;

    // 3. Định vị nút Đăng xuất bên trong Menu thả xuống
    @FindBy(css = "[data-testid='nav-logout']")
    private WebElement lnkLogout;

    public HeaderComponent(WebDriver driver, String baseUrl) {
        super(driver, baseUrl);
    }

    public HeaderComponent hoverUserMenu() {
        wait.until(ExpectedConditions.visibilityOf(lblUsernameMenu));
        Actions actions = new Actions(driver);
        actions.moveToElement(lblUsernameMenu).perform();
        return this;
    }

    /**
     * Luồng điều hướng đến trang cá nhân (AUTH-PRO-01)
     */
    public ProfilePage navigateToProfile() {
        hoverUserMenu();
        wait.until(ExpectedConditions.visibilityOf(lnkProfile));
        wait.until(ExpectedConditions.elementToBeClickable(lnkProfile)).click();
        return new ProfilePage(driver, baseUrl);
    }

    /**
     * Luồng xử lý bấm Đăng xuất (AUTH-OUT-01)
     */
    public HomePage clickLogout() {
        // Bước 1: Di chuột vào menu cha
        hoverUserMenu();

        // Bước 2: Đợi liên kết xuất hiện và click đăng xuất
        System.out.println("[Header] Đợi và click chọn nút [Đăng xuất]...");
        wait.until(ExpectedConditions.visibilityOf(lnkLogout));
        wait.until(ExpectedConditions.elementToBeClickable(lnkLogout)).click();

        return new HomePage(driver, baseUrl);
    }

    public HomePage navigateToLogout() {
        driver.get(baseUrl + "/logout");
        return new HomePage(driver, baseUrl);
    }

    public boolean isLogoutButtonVisible() {
        try {
            return lnkLogout.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
}