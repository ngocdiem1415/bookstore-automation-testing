package com.bookstore.pages;

import com.bookstore.base.BaseSetup;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.Select;

public class SignupPage extends BasePage {
    private final String PAGE_URL = "/signup";

    public void open() {
        driver.get(BaseSetup.baseUrl + PAGE_URL);
    }

    @FindBy(id = "username")
    private WebElement usernameInput;

    @FindBy(id = "password")
    private WebElement passwordInput;

    @FindBy(id = "email")
    private WebElement emailInput;

    @FindBy(id = "gender")
    private WebElement genderSelect;

    @FindBy(id = "birthdate")
    private WebElement birthdateInput;

    @FindBy(id = "tel")
    private WebElement phoneInput;

    @FindBy(css = ".form-submit-sign-in")
    private WebElement signupButton;

    @FindBy(id = "password-msg")
    private WebElement passwordJsMsg;

    public SignupPage(WebDriver driver) {
        super(driver);
    }

    public void enterSignupInfo(String user, String pass, String email, String gender, String dob, String phone) {
        sendText(usernameInput, user);
        sendText(passwordInput, pass);
        sendText(emailInput, email);

        Select select = new Select(genderSelect);
        select.selectByValue(gender);

        sendText(birthdateInput, dob); // Format: dd/mm/yyyy
        sendText(phoneInput, phone);
    }

    public void clickSignup() {
        clickElement(signupButton);
    }

    public String getPasswordValidationMsg() {
        return passwordJsMsg.getText();
    }
}