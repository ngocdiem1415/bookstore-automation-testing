package com.bookstore.pages;

import com.bookstore.factory.PageFactoryManager;
import com.bookstore.utils.LoggerHelper;
import org.openqa.selenium.*;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public class AdminProductPage extends BasePage {

    private static final String PAGE_URL = "/admin/products";

    @FindBy(css = "[data-testid='admin-product-table'] tbody tr")
    private List<WebElement> tableRows;

    @FindBy(css = "[data-testid='admin-add-product-btn']")
    private WebElement btnAdd;

    @FindBy(css = "[data-testid^='admin-edit-product-']")
    private List<WebElement> listEditBtns;

    @FindBy(css = "[data-testid^='admin-delete-product-']")
    private List<WebElement> listDeleteBtns;

    @FindBy(css = "[data-testid='admin-product-name']")
    private List<WebElement> productNames;

    @FindBy(css = "[data-testid='admin-product-id']")
    private List<WebElement> productIds;

    @FindBy(css = "[data-testid='admin-product-category']")
    private List<WebElement> productCategories;

    @FindBy(css = "[data-testid='admin-product-author']")
    private List<WebElement> productAuthors;

    @FindBy(css = "[data-testid='admin-product-year']")
    private List<WebElement> productYears;

    @FindBy(css = "[data-testid='admin-product-qty']")
    private List<WebElement> productQuantities;

    @FindBy(css = "[data-testid='admin-product-price']")
    private List<WebElement> productPrices;

    @FindBy(css = "[data-testid='admin-product-promo']")
    private List<WebElement> productPromotions;

    @FindBy(css = "[data-testid='admin-product-updated']")
    private List<WebElement> productUpdatedDates;

    @FindBy(id = "confirm-modal")
    private WebElement confirmModal;

    @FindBy(id = "confirm-delete")
    private WebElement btnConfirmDelete;

    @FindBy(id = "cancel-delete")
    private WebElement btnCancelDelete;

    @FindBy(id = "notification")
    private WebElement notification;

    public AdminProductPage(WebDriver driver, String baseUrl) {
        super(driver, baseUrl);
    }

    public AdminProductPage open() {
        LoggerHelper.info("[ADMIN][PRODUCT_PAGE] Mở trang quản lý sản phẩm");
        driver.get(baseUrl + PAGE_URL);
        waitForLoadPage();
        return this;
    }

    private void waitForLoadPage() {
        try {
            WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(10));
            shortWait.until(ExpectedConditions.visibilityOfAllElements(tableRows));
        } catch (Exception ignored) {
        }
    }

    public int getProductCount() {
        LoggerHelper.info("[ADMIN][PRODUCT_PAGE] Đếm số sản phẩm trong bảng");

        try {
            int count = productIds.size();
            LoggerHelper.info("[ADMIN][PRODUCT_PAGE] Số sản phẩm hiện tại: " + count);
            return count;
        } catch (Exception e) {
            LoggerHelper.warn("[ADMIN][PRODUCT_PAGE] Không đếm được sản phẩm: " + e.getMessage());
            return 0;
        }
    }

    public String getFirstProductTitle() {
        LoggerHelper.info("[ADMIN][PRODUCT_PAGE] Lấy tên sản phẩm đầu tiên");

        try {
            String title = getTextOf(productNames.get(0));
            LoggerHelper.info("[ADMIN][PRODUCT_PAGE] Tên sản phẩm đầu tiên: " + title);
            return title;
        } catch (Exception e) {
            LoggerHelper.warn("[ADMIN][PRODUCT_PAGE] Không lấy được tên sản phẩm đầu tiên: " + e.getMessage());
            return "";
        }
    }

    public String getProductTitleAt(int index) {
        LoggerHelper.info("[ADMIN][PRODUCT_PAGE] Lấy tên sản phẩm tại index: " + index);

        try {
            String title = getTextOf(productNames.get(index));
            LoggerHelper.info("[ADMIN][PRODUCT_PAGE] Tên sản phẩm tại index " + index + ": " + title);
            return title;
        } catch (Exception e) {
            LoggerHelper.warn("[ADMIN][PRODUCT_PAGE] Không lấy được tên sản phẩm: " + e.getMessage());
            return "";
        }
    }

    public AdminProductPage clickAdd() {
        LoggerHelper.info("[ADMIN][PRODUCT_PAGE] Click nút thêm sản phẩm");
        clickElement(btnAdd);
        return this;
    }

    public AdminProductPage clickEditAt(int index) {
        LoggerHelper.info("[ADMIN][PRODUCT_PAGE] Click nút sửa sản phẩm tại index: " + index);
        clickElement(listEditBtns.get(index));
        return this;
    }

    public int findProductIndexByTitle(String title) {
        LoggerHelper.info("[ADMIN][PRODUCT_PAGE] Tìm sản phẩm theo title: " + title);

        try {
            for (int i = 0; i < productNames.size(); i++) {
                String actualTitle = productNames.get(i).getText().trim();
                LoggerHelper.info("[ADMIN][PRODUCT_PAGE] Index " + i + " | Title = " + actualTitle);

                if (actualTitle.equalsIgnoreCase(title) || actualTitle.contains(title)) {
                    LoggerHelper.info("[ADMIN][PRODUCT_PAGE] Tìm thấy sản phẩm tại index: " + i);
                    return i;
                }
            }
        } catch (Exception e) {
            LoggerHelper.warn("[ADMIN][PRODUCT_PAGE] Không tìm được sản phẩm: " + e.getMessage());
        }

        LoggerHelper.warn("[ADMIN][PRODUCT_PAGE] Không tìm thấy sản phẩm: " + title);
        return -1;
    }

    public AdminProductPage clickEditByTitle(String title) {
        int index = findProductIndexByTitle(title);
        if (index < 0) {
            throw new NoSuchElementException("Cannot find product to edit: " + title);
        }
        return clickEditAt(index);
    }

    public AdminProductPage clickDeleteAt(int index) {
        LoggerHelper.info("[ADMIN][PRODUCT_PAGE] Click nút xóa sản phẩm tại index: " + index);
        clickElement(listDeleteBtns.get(index));
        wait.until(ExpectedConditions.visibilityOf(confirmModal));
        LoggerHelper.info("[ADMIN][PRODUCT_PAGE] Xác nhận xóa sản phẩm");
        clickElement(btnConfirmDelete);

        return this;
    }

    public AdminProductPage clickDeleteByTitle(String title) {
        int index = findProductIndexByTitle(title);
        if (index < 0) {
            throw new NoSuchElementException("Cannot find product to delete: " + title);
        }
        return clickDeleteAt(index);
    }

    public AdminProductPage clickCancelDeleteAt(int index) {
        LoggerHelper.info("[ADMIN][PRODUCT_PAGE] Click xóa rồi hủy xóa sản phẩm tại index: " + index);
        clickElement(listDeleteBtns.get(index));
        wait.until(ExpectedConditions.visibilityOf(confirmModal));
        LoggerHelper.info("[ADMIN][PRODUCT_PAGE] Click hủy xóa");
        clickElement(btnCancelDelete);

        return this;
    }

    public AdminProductPage clickCancelDeleteByTitle(String title) {
        int index = findProductIndexByTitle(title);
        if (index < 0) {
            throw new NoSuchElementException("Cannot find product to cancel delete: " + title);
        }
        return clickCancelDeleteAt(index);
    }

    public String getNotificationMessage() {
        LoggerHelper.info("[ADMIN][PRODUCT_PAGE] Lấy thông báo notification");

        try {
            String message = wait.until(ExpectedConditions.visibilityOf(notification))
                    .getText()
                    .trim();

            LoggerHelper.info("[ADMIN][PRODUCT_PAGE] Notification: " + message);
            return message;
        } catch (Exception e) {
            LoggerHelper.warn("[ADMIN][PRODUCT_PAGE] Không lấy được notification: " + e.getMessage());
            return "";
        }
    }

    public boolean isProductInList(String title) {
        LoggerHelper.info("[ADMIN][PRODUCT_PAGE] Kiểm tra sản phẩm có trong danh sách: " + title);

        try {
            boolean exists = productNames.stream()
                    .anyMatch(item -> item.getText().trim().contains(title));

            LoggerHelper.info("[ADMIN][PRODUCT_PAGE] Sản phẩm tồn tại: " + exists);
            return exists;
        } catch (Exception e) {
            LoggerHelper.warn("[ADMIN][PRODUCT_PAGE] Không kiểm tra được sản phẩm: " + e.getMessage());
            return false;
        }
    }

    public boolean isNoServerError() {
        LoggerHelper.info("[ADMIN][PRODUCT_PAGE] Kiểm tra trang không bị lỗi server");

        String title = driver.getTitle().toLowerCase();
        String body = driver.getPageSource().toLowerCase();

        boolean safe = !title.contains("500")
                && !body.contains("internal server error")
                && !body.contains("whitelabel error page");

        LoggerHelper.info("[ADMIN][PRODUCT_PAGE] Trang không bị lỗi server: " + safe);
        return safe;
    }

}
