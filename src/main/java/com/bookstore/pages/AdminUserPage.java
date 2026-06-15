package com.bookstore.pages;

import com.bookstore.utils.LoggerHelper;
import org.openqa.selenium.*;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.List;

public class AdminUserPage extends BasePage {

    private static final String PAGE_URL = "/admin/users";

    @FindBy(css = "[data-testid='admin-user-table'] tbody tr")
    private List<WebElement> tableRows;

    @FindBy(css = "[data-testid='admin-user-id']")
    private List<WebElement> userIds;

    @FindBy(css = "[data-testid='admin-user-name']")
    private List<WebElement> userNames;

    @FindBy(css = "[data-testid='admin-user-phone']")
    private List<WebElement> userPhones;

    @FindBy(css = "[data-testid='admin-user-email']")
    private List<WebElement> userEmails;

    @FindBy(css = "[data-testid='admin-user-status']")
    private List<WebElement> userStatuses;

    @FindBy(css = "[data-testid^='admin-edit-user-']")
    private List<WebElement> editButtons;

    @FindBy(css = "[data-testid^='admin-delete-user-']")
    private List<WebElement> deleteButtons;

    @FindBy(css = "[data-testid='admin-add-user-btn']")
    private WebElement btnAddUser;

    @FindBy(id = "confirmDeleteModal")
    private WebElement confirmDeleteModal;

    @FindBy(id = "confirmDeleteBtn")
    private WebElement btnConfirmDelete;

    @FindBy(id = "notification")
    private WebElement notification;

    @FindBy(css = "[data-testid='admin-user-name']")
    private List<WebElement> listUserNames;

    @FindBy(css = "[data-testid^='admin-delete-user-']")
    private List<WebElement> listDeleteButtons;

    public AdminUserPage(WebDriver driver, String baseUrl) {
        super(driver, baseUrl);
    }

    public AdminUserPage open() {
        LoggerHelper.info("[AUTH][USER_PAGE] Mở trang quản lý tài khoản");
        driver.get(baseUrl + PAGE_URL);
        return this;
    }

    public int getUserCount() {
        LoggerHelper.info("[AUTH][USER_PAGE] Đếm số tài khoản trong bảng");

        try {
            int count = userIds.size();
            LoggerHelper.info("[AUTH][USER_PAGE] Số tài khoản hiện tại: " + count);
            return count;
        } catch (Exception e) {
            LoggerHelper.warn("[AUTH][USER_PAGE] Không đếm được tài khoản: " + e.getMessage());
            return 0;
        }
    }

    public int searchByUsername(String username) {
        LoggerHelper.info("[AUTH][USER_PAGE] Tìm tài khoản theo username: " + username);

        for (int i = 0; i < userNames.size(); i++) {
            String actualUsername = userNames.get(i).getText().trim();

            LoggerHelper.info("[AUTH][USER_PAGE] Index " + i + " | Username = " + actualUsername);

            if (actualUsername.equalsIgnoreCase(username)
                    || actualUsername.contains(username)) {
                LoggerHelper.info("[AUTH][USER_PAGE] Tìm thấy user tại index: " + i);
                return i;
            }
        }

        LoggerHelper.warn("[AUTH][USER_PAGE] Không tìm thấy user: " + username);
        return -1;
    }

    public String getUsernameAt(int index) {
        LoggerHelper.info("[AUTH][USER_PAGE] Lấy username tại index: " + index);

        try {
            String username = getTextOf(userNames.get(index));

            LoggerHelper.info("[AUTH][USER_PAGE] Username tại index " + index + ": " + username);
            return username;
        } catch (Exception e) {
            LoggerHelper.warn("[AUTH][USER_PAGE] Không lấy được username: " + e.getMessage());
            return "";
        }
    }

    public String getUserStatusAt(int index) {
        LoggerHelper.info("[AUTH][USER_PAGE] Lấy trạng thái user tại index: " + index);

        try {
            String status = getTextOf(userStatuses.get(index));
            LoggerHelper.info("[AUTH][USER_PAGE] Trạng thái user tại index " + index + ": " + status);
            return status;
        } catch (Exception e) {
            LoggerHelper.warn("[AUTH][USER_PAGE] Không lấy được trạng thái user: " + e.getMessage());
            return "";
        }
    }

    public AdminUserPage clickEditAt(int index) {
        LoggerHelper.info("[AUTH][USER_PAGE] Click nút sửa user tại index: " + index);
        WebElement btnEdit = waitUntilClickable(editButtons.get(index));
        scrollToElement(btnEdit);
        jsClick(btnEdit);
        return this;
    }

    public AdminUserPage clickAddUser() {
        LoggerHelper.info("[AUTH][USER_PAGE] Click nút thêm tài khoản");
        scrollToElement(btnAddUser);
        jsClick(btnAddUser);
        return this;
    }

    public AdminUserPage clickDeleteAt(int index) {
        LoggerHelper.info("[AUTH][USER] Click xóa user tại index: " + index);
        jsClick(listDeleteButtons.get(index));
        return this;
    }

    public String confirmDeleteAndGetNotification() {
        LoggerHelper.info("[AUTH][USER] Xác nhận xóa");
        clickElement(btnConfirmDelete);
        try {
            return getTextOf(notification);
        } catch (Exception e) {
            return "";
        }
    }

    public String getNotificationMessage() {
        LoggerHelper.info("[AUTH][USER_PAGE] Lấy thông báo notification");

        try {
            String message = getTextOf(notification);
            LoggerHelper.info("[AUTH][USER_PAGE] Notification: " + message);
            return message;
        } catch (Exception e) {
            LoggerHelper.warn("[AUTH][USER_PAGE] Không lấy được notification: " + e.getMessage());
            return "";
        }
    }

    public boolean isUserInList(String username) {
        LoggerHelper.info("[AUTH][USER_PAGE] Kiểm tra user có trong danh sách: " + username);

        boolean exists = searchByUsername(username) >= 0;

        LoggerHelper.info("[AUTH][USER_PAGE] User tồn tại trong danh sách: " + exists);

        return exists;
    }

    public boolean isNoServerError() {
        LoggerHelper.info("[AUTH][USER_PAGE] Kiểm tra trang không bị lỗi server");

        String title = driver.getTitle().toLowerCase();
        String body = driver.getPageSource().toLowerCase();

        boolean safe = !title.contains("500")
                && !body.contains("internal server error")
                && !body.contains("whitelabel error page");

        LoggerHelper.info("[AUTH][USER_PAGE] Trang không bị lỗi server: " + safe);
        return safe;
    }
}