package com.bookstore.pages;

import com.bookstore.utils.LoggerHelper;
import org.openqa.selenium.*;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;

public class AdminProductFormPage extends BasePage {

    @FindBy(css = "[data-testid='admin-prod-form'], [data-testid='admin-edit-product-form']")
    private WebElement productForm;

    @FindBy(css = "[data-testid='admin-save-btn']")
    private WebElement btnSave;

    @FindBy(css = "[data-testid='admin-product-title-input']")
    private WebElement txtTitle;

    @FindBy(css = "[data-testid='admin-product-author-input']")
    private WebElement txtAuthor;

    @FindBy(css = "[data-testid='admin-product-publisher-select']")
    private WebElement selectPublisher;

    @FindBy(css = "[data-testid='admin-product-year-input']")
    private WebElement txtPublishYear;

    @FindBy(css = "[data-testid='admin-product-dimensions-input']")
    private WebElement txtDimensions;

    @FindBy(css = "[data-testid='admin-product-weight-input']")
    private WebElement txtWeight;

    @FindBy(css = "[data-testid='admin-product-pages-input']")
    private WebElement txtPages;

    @FindBy(css = "[data-testid='admin-product-cover-input']")
    private WebElement txtCover;

    @FindBy(css = "[data-testid='admin-product-description-input']")
    private WebElement txtDescription;

    @FindBy(css = "[data-testid='admin-product-thumbnail-input']")
    private WebElement inputThumbnail;

    @FindBy(css = "[data-testid='admin-product-images-input']")
    private WebElement inputImages;

    @FindBy(css = "[data-testid='admin-product-price-input']")
    private WebElement txtPrice;

    @FindBy(css = "[data-testid='admin-product-qty-input']")
    private WebElement txtQuantity;

    @FindBy(css = "[data-testid='admin-product-status-select']")
    private WebElement selectStatus;

    @FindBy(css = "[data-testid='admin-product-category-select']")
    private WebElement selectCategory;

    @FindBy(css = "[data-testid='admin-product-promotion-select']")
    private WebElement selectPromotion;

    public AdminProductFormPage(WebDriver driver, String baseUrl) {
        super(driver, baseUrl);
    }

    public AdminProductFormPage enterTitle(String title) {
        LoggerHelper.info("[ADMIN][PRODUCT_FORM_PAGE] Nhập tên sản phẩm: " + title);
        clearAndSendText(txtTitle, title);
        return this;
    }

    public AdminProductFormPage enterAuthor(String author) {
        LoggerHelper.info("[ADMIN][PRODUCT_FORM_PAGE] Nhập tác giả: " + author);
        clearAndSendText(txtAuthor, author);
        return this;
    }

    public AdminProductFormPage enterPrice(String price) {
        LoggerHelper.info("[ADMIN][PRODUCT_FORM_PAGE] Nhập giá bán: " + price);
        clearAndSendText(txtPrice, price);
        return this;
    }

    public AdminProductFormPage enterQuantity(String quantity) {
        LoggerHelper.info("[ADMIN][PRODUCT_FORM_PAGE] Nhập tồn kho: " + quantity);
        clearAndSendText(txtQuantity, quantity);
        return this;
    }

    public AdminProductFormPage selectPublisherByIndex(int index) {
        LoggerHelper.info("[ADMIN][PRODUCT_FORM_PAGE] Chọn NXB index: " + index);
        new Select(waitUntilClickable(selectPublisher)).selectByIndex(index);
        return this;
    }

    public AdminProductFormPage selectCategoryByIndex(int index) {
        LoggerHelper.info("[ADMIN][PRODUCT_FORM_PAGE] Chọn danh mục index: " + index);
        new Select(waitUntilClickable(selectCategory))
                .selectByIndex(index);
        return this;
    }

    public AdminProductFormPage selectStatusByValue(String value) {
        LoggerHelper.info("[ADMIN][PRODUCT_FORM_PAGE] Chọn trạng thái: " + value);
        new Select(waitUntilClickable(selectStatus))
                .selectByValue(value);
        return this;
    }

    public AdminProductFormPage uploadThumbnail(String filePath) {
        LoggerHelper.info("[ADMIN][PRODUCT_FORM_PAGE] Upload thumbnail: " + filePath);
        inputThumbnail.sendKeys(filePath);
        return this;
    }

    public AdminProductFormPage clickSave() {
        LoggerHelper.info("[ADMIN][PRODUCT_FORM_PAGE] Click lưu sản phẩm");
        scrollToElement(btnSave);
        jsClick(btnSave);
        return this;
    }

    public String clickSaveAndGetAlert() {
        LoggerHelper.info("[ADMIN][PRODUCT_FORM_PAGE] Click lưu và lấy alert");
        scrollToElement(btnSave);
        jsClick(btnSave);
        try {
            Alert alert = wait.until(ExpectedConditions.alertIsPresent());
            String text = alert.getText().trim();
            LoggerHelper.info("[ADMIN][PRODUCT_FORM_PAGE] Alert: " + text);
            alert.accept();
            return text;
        } catch (Exception e) {
            LoggerHelper.warn("[ADMIN][PRODUCT_FORM_PAGE] Không có alert: " + e.getMessage());
            return "";
        }
    }

    public String getTitleValidationMessage() {
        return (String) ((JavascriptExecutor) driver)
                .executeScript("return arguments[0].validationMessage;", txtTitle);
    }

    public String getPriceValidationMessage() {
        return (String) ((JavascriptExecutor) driver)
                .executeScript("return arguments[0].validationMessage;", txtPrice);
    }

}