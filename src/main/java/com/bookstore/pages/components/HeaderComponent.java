package com.bookstore.pages.components;

import com.bookstore.pages.BasePage;
import com.bookstore.pages.HomePage;
import com.bookstore.pages.ProfilePage;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;

public class HeaderComponent extends BasePage {

    @FindBy(css = "[data-testid='search-input']")
    private WebElement txtSearchInput;

    @FindBy(css = "[data-testid='search-btn']")
    private WebElement btnSearch;

    @FindBy(css = "[data-testid='username-menu']")
    private WebElement lblUsernameMenu;

    @FindBy(css = "[data-testid='nav-profile']")
    private WebElement lnkProfile;

    @FindBy(css = "[data-testid='nav-logout']")
    private WebElement lnkLogout;

    public HeaderComponent(WebDriver driver, String baseUrl) {
        super(driver, baseUrl);
    }

    public HeaderComponent hoverUserMenu() {
        isElementVisible(lblUsernameMenu);
        Actions actions = new Actions(driver);
        actions.moveToElement(lblUsernameMenu).perform();
        return this;
    }

    public ProfilePage navigateToProfile() {
        hoverUserMenu();
        isElementVisible(lnkProfile);
        clickElement(lnkProfile);
        return new ProfilePage(driver, baseUrl);
    }

    public HomePage navigateToLogout() {
        driver.get(baseUrl + "/logout");
        return new HomePage(driver, baseUrl);
    }

}