package com.bookstore.pages;

import com.bookstore.utils.LoggerHelper;
import org.openqa.selenium.Alert;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;

public class AdminUserEditPage extends BasePage {

    @FindBy(css = "[data-testid='admin-edit-user-form']")
    private WebElement formEditUser;

    @FindBy(css = "[data-testid='admin-user-username-input']")
    private WebElement txtUsername;

    @FindBy(css = "[data-testid='admin-user-email-input']")
    private WebElement txtEmail;

    @FindBy(css = "[data-testid='admin-user-phone-input']")
    private WebElement txtPhone;

    @FindBy(css = "[data-testid='admin-user-password-input']")
    private WebElement txtPassword;

    @FindBy(css = "[data-testid='admin-user-gender-select']")
    private WebElement selectGender;

    @FindBy(css = "[data-testid='admin-user-dob-input']")
    private WebElement txtDob;

    @FindBy(css = "[data-testid='admin-user-role-1']")
    private WebElement chkAdmin;

    @FindBy(css = "[data-testid='admin-user-role-2']")
    private WebElement chkOrderStaff;

    @FindBy(css = "[data-testid='admin-user-role-3']")
    private WebElement chkStockManager;

    @FindBy(css = "[data-testid='admin-user-role-4']")
    private WebElement chkCustomer;

    @FindBy(css = "[data-testid='admin-user-status-select']")
    private WebElement selectStatus;

    @FindBy(css = "[data-testid='admin-user-update-btn']")
    private WebElement btnUpdate;

    public AdminUserEditPage(WebDriver driver, String baseUrl) {
        super(driver, baseUrl);
    }

    public AdminUserEditPage selectStatus(String value) {
        LoggerHelper.info("[ADMIN][USER_EDIT] Chọn trạng thái: " + value);
        scrollToElement(selectStatus);
        new Select(selectStatus).selectByValue(value);
        return this;
    }

    public AdminUserEditPage clickUpdate() {
        LoggerHelper.info("[ADMIN][USER_EDIT] Click lưu");
        scrollToElement(btnUpdate);
        jsClick(btnUpdate);
        return this;
    }

    public String clickUpdateAndGetAlert() {
        LoggerHelper.info("[ADMIN][USER_EDIT_PAGE] Click lưu và lấy alert");
        scrollToElement(btnUpdate);
        jsClick(btnUpdate);
        try {
            Alert alert = wait.until(ExpectedConditions.alertIsPresent());
            String text = alert.getText().trim();

            LoggerHelper.info("[ADMIN][USER_EDIT_PAGE] Alert sau khi cập nhật: " + text);
            alert.accept();
            return text;
        } catch (Exception e) {
            LoggerHelper.warn("[ADMIN][USER_EDIT_PAGE] Không có alert sau khi cập nhật: " + e.getMessage());
            return "";
        }
    }
}