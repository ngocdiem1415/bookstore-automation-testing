package com.bookstore.pages;

import com.bookstore.base.BaseSetup;
import org.openqa.selenium.*;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;

/**
 * Page Object: Trang Hồ sơ người dùng (/profile).
 * Covers: AUTH-PRO-01, AUTH-PRO-02, AUTH-PRO-03
 */
public class ProfilePage extends BasePage {

    private static final String PAGE_URL = "/profile";
    private static final long MAX_AVATAR_SIZE_BYTES = 5L * 1024 * 1024; // 5MB

    // ===========================
    // LOCATORS (data-testid)
    // ===========================

    @FindBy(css = "[data-testid='profile-username']")
    private WebElement txtProfileUsername;

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

    // ===========================
    // CONSTRUCTOR
    // ===========================

    public ProfilePage(WebDriver driver,String baseUrl) {
        super(driver, baseUrl);
    }

    // ===========================
    // NAVIGATION
    // ===========================

    /** Mở trang Profile trực tiếp. Yêu cầu đã đăng nhập. */
    public ProfilePage open() {
        driver.get(getCurrentUrl() + PAGE_URL);
        return this;
    }

    // ===========================
    // ACTION METHODS
    // ===========================

    /** Step: Edit [profile-phone] */
    public ProfilePage enterPhone(String phone) {
        System.out.println("[ProfilePage] Entering phone: " + phone);
        clearAndSendText(txtProfilePhone, phone);
        return this;
    }

    /** Step: Edit [profile-birthdate] */
    public ProfilePage enterBirthdate(String birthdate) {
        System.out.println("[ProfilePage] Entering birthdate: " + birthdate);
        clearAndSendText(txtProfileBirthdate, birthdate);
        return this;
    }

    /** Step: Select [profile-gender] */
    public ProfilePage selectGender(String genderValue) {
        System.out.println("[ProfilePage] Selecting gender: " + genderValue);
        Select select = new Select(wait.until(ExpectedConditions.elementToBeClickable(selProfileGender)));
        select.selectByValue(genderValue);
        return this;
    }

    /**
     * Step: Xóa [profile-username] để để trống.
     * AUTH-PRO-02: Verify user cannot update profile with blank mandatory fields.
     */
    public ProfilePage clearUsername() {
        System.out.println("[ProfilePage] Clearing username field...");
        wait.until(ExpectedConditions.visibilityOf(txtProfileUsername)).clear();
        return this;
    }

    /**
     * Step: Chọn file ảnh avatar vượt quá 5MB bằng [profile-avatar-upload].
     * AUTH-PRO-03: Verify uploading avatar exceeding size limit.
     * @param filePath Đường dẫn tuyệt đối đến file ảnh > 5MB.
     */
    public ProfilePage uploadAvatar(String filePath) {
        System.out.println("[ProfilePage] Uploading avatar file: " + filePath);
        // Đảm bảo input file visible (có thể ẩn bởi CSS)
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].style.display = 'block'; arguments[0].style.visibility = 'visible';",
                inputProfileAvatarUpload
        );
        inputProfileAvatarUpload.sendKeys(filePath);
        return this;
    }

    /**
     * Step: Click [profile-save-btn].
     */
    public ProfilePage clickSave() {
        System.out.println("[ProfilePage] Clicking save button...");
        clickElement(btnProfileSave);
        return this;
    }

    // ===========================
    // GETTER METHODS (Assertions)
    // ===========================

    /**
     * AUTH-PRO-01: Đọc JavaScript Alert "Thông tin đã được lưu!" và chấp nhận nó.
     * @return Text của alert.
     */
    public String getAndAcceptSuccessAlert() {
        System.out.println("[ProfilePage] Waiting for JS alert...");
        try {
            Alert alert = wait.until(ExpectedConditions.alertIsPresent());
            String alertText = alert.getText();
            System.out.println("[ProfilePage] Alert text: " + alertText);
            alert.accept();
            return alertText;
        } catch (Exception e) {
            System.out.println("[ProfilePage] No JS alert found: " + e.getMessage());
            return "";
        }
    }

    /**
     * AUTH-PRO-02: Lấy validation message từ HTML5 constraint API của username field.
     */
    public String getUsernameValidationMessage() {
        return (String) ((JavascriptExecutor) driver)
                .executeScript("return arguments[0].validationMessage;", txtProfileUsername);
    }

    /**
     * AUTH-PRO-02: Kiểm tra form submit có bị HTML5 validation chặn không.
     * @return true nếu submit bị ngăn (username rỗng → validation triggered).
     */
    public boolean isFormSubmitPrevented() {
        String validationMsg = getUsernameValidationMessage();
        System.out.println("[ProfilePage] Username validation message: " + validationMsg);
        return validationMsg != null && !validationMsg.isEmpty();
    }

    /**
     * AUTH-PRO-03: Lấy text error message sau khi upload file quá lớn.
     */
    public String getErrorMessage() {
        System.out.println("[ProfilePage] Getting error message...");
        try {
            return wait.until(ExpectedConditions.visibilityOf(lblProfileErrorMessage)).getText().trim();
        } catch (Exception e) {
            // Có thể là JS alert thay vì element
            return getAndAcceptSuccessAlert();
        }
    }

    /**
     * AUTH-PRO-03: Kiểm tra alert / error message liên quan đến kích thước file.
     */
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
