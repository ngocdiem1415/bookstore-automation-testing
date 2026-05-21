package com.bookstore.pages.components;

import com.bookstore.pages.BasePage;
import com.bookstore.pages.HomePage;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static com.bookstore.pages.BasePage.EXPLICIT_WAIT_SECONDS;

/**
 * Component: Header (thanh điều hướng trên cùng).
 * Covers: AUTH-OUT-01
 */
public class HeaderComponent extends BasePage {

    @FindBy(css = "[data-testid='header-search-input']")
    private WebElement txtSearchInput;

    @FindBy(css = "[data-testid='header-search-btn']")
    private WebElement btnSearch;

    @FindBy(css = "[data-testid='header-cart-icon']")
    private WebElement lnkCartIcon;

    @FindBy(css = "[data-testid='header-user-menu']")
    private WebElement lnkUserMenu;

    @FindBy(css = "[data-testid='header-logout-btn']")
    private WebElement lnkLogout;

    @FindBy(css = "[data-testid='header-login-btn']")
    private WebElement lnkLogin;

    @FindBy(css = "[data-testid='header-signup-btn']")
    private WebElement lnkSignup;

    public HeaderComponent(WebDriver driver, String baseUrl) {
        super(driver, baseUrl);
    }

    public HeaderComponent enterSearchKeyword(String keyword) {
        wait.until(ExpectedConditions.visibilityOf(txtSearchInput)).clear();
        txtSearchInput.sendKeys(keyword);
        return this;
    }

    public void clickSearch() {
        wait.until(ExpectedConditions.elementToBeClickable(btnSearch)).click();
    }

    public HeaderComponent clickUserMenu() {
        wait.until(ExpectedConditions.elementToBeClickable(lnkUserMenu)).click();
        return this;
    }

    public HomePage clickLogout() {
        try {
            clickUserMenu();
        } catch (Exception e) {
        }
        wait.until(ExpectedConditions.elementToBeClickable(lnkLogout)).click();
        return new HomePage(driver,baseUrl);
    }


    public HomePage navigateToLogout() {
        driver.get(baseUrl + "/logout");
        return new HomePage(driver, baseUrl);
    }

//    public boolean isLoginButtonVisible() {
//        try {
//            return wait.until(ExpectedConditions.visibilityOf(lnkLogin)).isDisplayed();
//        } catch (Exception e) {
//            return false;
//        }
//    }

    public boolean isLogoutButtonVisible() {
        try {
            return lnkLogout.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isUserMenuVisible() {
        try {
            return wait.until(ExpectedConditions.visibilityOf(lnkUserMenu)).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
}

