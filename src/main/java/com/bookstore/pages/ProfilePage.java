package com.bookstore.pages;

import org.openqa.selenium.*;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;

public class ProfilePage extends BasePage {
    private static final String PAGE_URL = "/profile";
    private static final long MAX_AVATAR_SIZE_BYTES = 5L * 1024 * 1024; // 5MB

    @FindBy(css = "[data-testid='profile-phone']")
    private WebElement txtProfilePhone;

    @FindBy(css = "[data-testid='profile-birthdate']")
    private WebElement txtProfileBirthdate;

    @FindBy(css = "[data-testid='profile-gender']")
    private WebElement selProfileGender;

    @FindBy(css = "[data-testid='profile-avatar-upload']")
    private WebElement inputProfileAvatarUpload;

    @FindBy(css = "[data-testid='profile-save-btn']")
    private WebElement btnProfileSave;

    @FindBy(css = "[data-testid='profile-error-message']")
    private WebElement lblProfileErrorMessage;

    @FindBy(css = "[data-testid='profile-success-message']")
    private WebElement lblProfileSuccessMessage;

    public ProfilePage(WebDriver driver,String baseUrl) {
        super(driver, baseUrl);
    }

    public ProfilePage open() {
        driver.get(getCurrentUrl() + PAGE_URL);
        return this;
    }

    public ProfilePage enterPhone(String phone) {
        clearAndSendText(txtProfilePhone, phone);
        return this;
    }

    public ProfilePage enterBirthdate(String birthdate) {
        clearAndSendText(txtProfileBirthdate, birthdate);
        return this;
    }

    public ProfilePage selectGender(String genderValue) {
        Select select = new Select(wait.until(ExpectedConditions.elementToBeClickable(selProfileGender)));
        select.selectByValue(genderValue);
        return this;
    }

    public ProfilePage uploadAvatar(String filePath) {
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].style.display = 'block'; arguments[0].style.visibility = 'visible';",
                inputProfileAvatarUpload
        );
        inputProfileAvatarUpload.sendKeys(filePath);
        return this;
    }

    public ProfilePage clickSave() {
        clickElement(btnProfileSave);
        return this;
    }

    public String getAndAcceptSuccessAlert() {
        try {
            Alert alert = wait.until(ExpectedConditions.alertIsPresent());
            String alertText = alert.getText();
            alert.accept();
            return alertText;
        } catch (Exception e) {
            return "";
        }
    }

    public ProfilePage clearPhone() {
        wait.until(ExpectedConditions.visibilityOf(txtProfilePhone)).clear();
        return this;
    }

    public String getErrorMessage() {
        try {
            return wait.until(ExpectedConditions.visibilityOf(lblProfileErrorMessage)).getText().trim();
        } catch (Exception e) {
            return getAndAcceptSuccessAlert();
        }
    }


    public String getSuccessMessage() {
        try {
            return wait.until(ExpectedConditions.visibilityOf(lblProfileSuccessMessage)).getText().trim();
        } catch (Exception e) {
            return getAndAcceptSuccessAlert();
        }
    }

    public String getAvatarUploadErrorAlert() {
        try {
            Alert alert = wait.until(ExpectedConditions.alertIsPresent());
            String text = alert.getText();
            alert.dismiss();
            return text;
        } catch (Exception e) {
            return getErrorMessage();
        }
    }
}
