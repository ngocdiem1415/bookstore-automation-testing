package com.bookstore.pages;

import com.bookstore.utils.LoggerHelper;
import org.openqa.selenium.*;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.List;

public class AdminCategoryPage extends BasePage {

    private static final String PAGE_URL = "/admin/categories";

    @FindBy(css = "[data-testid='admin-category-search-input']")
    private WebElement txtCategoryName;

    @FindBy(css = "[data-testid='admin-save-category-btn']")
    private WebElement btnSave;

    @FindBy(css = "[data-testid='admin-category-table'] tbody tr")
    private List<WebElement> tableRows;

    @FindBy(css = "[data-testid='admin-add-btn']")
    private WebElement btnAdd;

    @FindBy(css = "[data-testid='admin-notification']")
    private WebElement notification;

    @FindBy(css = "[data-testid='admin-cat-error']")
    private WebElement lblError;

    @FindBy(css = "[data-testid='admin-edit-cat-name']")
    private WebElement txtEditCategoryName;

    @FindBy(css = "[data-testid='admin-update-category-btn']")
    private WebElement btnUpdate;

    @FindBy(css = "[data-testid^='admin-edit-category-']")
    private List<WebElement> btnEditRows;

    @FindBy(css = "[data-testid='admin-delete-btn']")
    private List<WebElement> btnDeleteRows;


    public AdminCategoryPage(WebDriver driver, String baseUrl) {
        super(driver, baseUrl);
    }

    public AdminCategoryPage open() {
        LoggerHelper.info("[ADMIN][CATEGORY_PAGE] Mở trang quản lý danh mục");
        driver.get(baseUrl + PAGE_URL);
        return this;
    }

    public AdminCategoryPage clickAdd() {
        LoggerHelper.info("[ADMIN][CATEGORY_PAGE] Click nút thêm danh mục");
        clickElement(btnAdd);
        return this;
    }

    public AdminCategoryPage enterName(String name) {
        if (name.length() < 255) {
            LoggerHelper.info("[ADMIN][CATEGORY_PAGE] Nhập tên danh mục: " + name);
            clearAndSendText(txtCategoryName, name);
        }
        return this;
    }

    public AdminCategoryPage clickSave() {
        LoggerHelper.info("[ADMIN][CATEGORY_PAGE] Click nút lưu danh mục");
        clickElement(btnSave);
        return this;
    }

    public String clickSaveAndGetAlert() {
        LoggerHelper.info("[ADMIN][CATEGORY_PAGE] Click lưu và chờ alert");

        clickElement(btnSave);

        try {
            Alert alert = wait.until(ExpectedConditions.alertIsPresent());
            String text = alert.getText().trim();

            LoggerHelper.info("[ADMIN][CATEGORY_PAGE] Alert sau khi lưu: " + text);

            alert.accept();
            return text;
        } catch (Exception e) {
            LoggerHelper.warn("[ADMIN][CATEGORY_PAGE] Không có alert sau khi lưu: " + e.getMessage());
            return "";
        }
    }

    public String getNotificationMessage() {
        LoggerHelper.info("[ADMIN][CATEGORY_PAGE] Lấy notification");

        try {
            String message = getTextOf(notification);
            LoggerHelper.info("[ADMIN][CATEGORY_PAGE] Notification: " + message);

            return message;
        } catch (Exception e) {
            LoggerHelper.warn("[ADMIN][CATEGORY_PAGE] Không tìm thấy notification: " + e.getMessage());
            return "";
        }
    }

    public String getErrorMessage() {
        LoggerHelper.info("[ADMIN][CATEGORY_PAGE] Lấy error message");

        try {
            String error = getTextOf(lblError);

            LoggerHelper.info("[ADMIN][CATEGORY_PAGE] Error message: " + error);

            return error;
        } catch (Exception e) {
            LoggerHelper.warn("[ADMIN][CATEGORY_PAGE] Không tìm thấy error message");
            return "";
        }
    }

    public int getCategoryCount() {
        LoggerHelper.info("[ADMIN][CATEGORY_PAGE] Đếm số danh mục trong bảng");

        try {
            int count = tableRows.size();
            LoggerHelper.info("[ADMIN][CATEGORY_PAGE] Số danh mục hiện tại: " + count);
            return count;
        } catch (Exception e) {
            LoggerHelper.warn("[ADMIN][CATEGORY_PAGE] Không đếm được danh mục: " + e.getMessage());
            return 0;
        }
    }

    public boolean isCategoryInList(String name) {
        LoggerHelper.info("[ADMIN][CATEGORY_PAGE] Kiểm tra danh mục có trong danh sách: " + name);

        try {
            boolean exists = tableRows.stream().anyMatch(row -> row.getText().contains(name));

            LoggerHelper.info("[ADMIN][CATEGORY_PAGE] Danh mục tồn tại trong danh sách: " + exists);

            return exists;
        } catch (Exception e) {
            LoggerHelper.warn("[ADMIN][CATEGORY_PAGE] Không kiểm tra được danh mục trong danh sách: " + e.getMessage());
            return false;
        }
    }

    public boolean isNoServerError() {
        LoggerHelper.info("[ADMIN][CATEGORY_PAGE] Kiểm tra trang không bị lỗi server");

        String title = driver.getTitle().toLowerCase();
        String body = driver.getPageSource().toLowerCase();

        boolean safe = !title.contains("500")
                && !body.contains("internal server error")
                && !body.contains("whitelabel error page");

        LoggerHelper.info("[ADMIN][CATEGORY_PAGE] Trang không bị lỗi server: " + safe);

        return safe;
    }

    public AdminCategoryPage enterCategoryName(String name) {
        LoggerHelper.info("[ADMIN][CATEGORY_PAGE] Nhập tên danh mục: " + name);
        isElementVisible(txtCategoryName);
        txtCategoryName.clear();
        txtCategoryName.sendKeys(name);

        return this;
    }

}