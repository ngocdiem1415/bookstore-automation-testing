package com.bookstore.pages;

import com.bookstore.base.BaseSetup;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class LoginPage extends BasePage{
    private final String PAGE_URL = "/login";

    public void open() {
        driver.get(BaseSetup.baseUrl + PAGE_URL);
    }

    @FindBy(id = "username")
    private WebElement usernameInput;

    @FindBy(id = "password")
    private WebElement passwordInput;

    @FindBy(css = ".form-submit-sign-in")
    private WebElement loginButton;

    @FindBy(css = ".text-danger")
    private WebElement errorMessage;

    public LoginPage(WebDriver driver) {
        super(driver);
    }

    public void login(String user, String pass) {
        sendText(usernameInput, user);
        sendText(passwordInput, pass);
        clickElement(loginButton);
    }

    public String getErrorMessage() {
        return wait.until(ExpectedConditions.visibilityOf(errorMessage)).getText();
    }
}
