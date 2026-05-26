package com.bookstore.pages;

import com.bookstore.base.BaseSetup;
import org.openqa.selenium.*;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.List;

/**
 * Page Object: Admin - Quản lý Sản phẩm (/admin/products).
 * Covers: ADM-PRO-01/02/03, ADM-PRE-01/02/03, ADM-PRD-01/02/03
 */
public class AdminProductPage extends BasePage {

    private static final String PAGE_URL = "/admin/products";

    // --- List ---
    @FindBy(css = "[data-testid='admin-product-list'] tr")
    private List<WebElement> tableRows;

    @FindBy(css = "[data-testid='admin-add-btn']")
    private WebElement btnAdd;

    @FindBy(css = "[data-testid='admin-edit-btn']")
    private List<WebElement> listEditBtns;

    @FindBy(css = "[data-testid='admin-delete-btn']")
    private List<WebElement> listDeleteBtns;

    // --- Form fields ---
    @FindBy(css = "[data-testid='admin-prod-title']")
    private WebElement txtTitle;

    @FindBy(css = "[data-testid='admin-prod-price']")
    private WebElement txtPrice;

    @FindBy(css = "[data-testid='admin-prod-thumbnail']")
    private WebElement inputThumbnail;

    @FindBy(css = "[data-testid='admin-prod-description']")
    private WebElement txtDescription;

    @FindBy(css = "[data-testid='admin-save-btn']")
    private WebElement btnSave;

    // --- Messages ---
    @FindBy(css = "[data-testid='admin-prod-error']")
    private WebElement lblError;

    @FindBy(css = "[data-testid='admin-prod-success']")
    private WebElement lblSuccess;

    public AdminProductPage(WebDriver driver,String baseUrl) {
        super(driver, baseUrl);
    }
    public AdminProductPage open() {
        System.out.println("[AdminProductPage] Navigating to: " + getCurrentUrl() + PAGE_URL);
        driver.get(getCurrentUrl() + PAGE_URL);
        return this;
    }

    // --- List actions ---
    public int getProductCount() {
        try { return Math.max(0, tableRows.size() - 1); } catch (Exception e) { return 0; }
    }

    public String getFirstProductTitle() {
        try {
            return tableRows.get(1)
                    .findElement(By.cssSelector("[data-testid='admin-prod-name']"))
                    .getText().trim();
        } catch (Exception e) { return ""; }
    }

    public AdminProductPage clickAdd() {
        clickElement(btnAdd);
        return this;
    }

    public AdminProductPage clickEditAt(int index) {
        System.out.println("[AdminProductPage] Clicking edit at index: " + index);
        clickElement(listEditBtns.get(index));
        return this;
    }

    public AdminProductPage clickDeleteAt(int index) {
        System.out.println("[AdminProductPage] Clicking delete at index: " + index);
        clickElement(listDeleteBtns.get(index));
        try {
            Alert confirm = wait.until(ExpectedConditions.alertIsPresent());
            confirm.accept();
            Thread.sleep(800);
        } catch (Exception e) {
            System.out.println("[AdminProductPage] No confirm dialog: " + e.getMessage());
        }
        return this;
    }

    // --- Form actions ---
    public AdminProductPage enterTitle(String title) {
        clearAndSendText(txtTitle, title);
        return this;
    }

    public AdminProductPage enterPrice(String price) {
        clearAndSendText(txtPrice, price);
        return this;
    }

    public AdminProductPage uploadThumbnail(String filePath) {
        System.out.println("[AdminProductPage] Uploading thumbnail: " + filePath);
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].style.display='block';", inputThumbnail);
        inputThumbnail.sendKeys(filePath);
        return this;
    }

    public AdminProductPage clickSave() {
        clickElement(btnSave);
        return this;
    }

    public String clickSaveAndGetAlert() {
        clickSave();
        try {
            Alert alert = wait.until(ExpectedConditions.alertIsPresent());
            String text = alert.getText().trim();
            alert.accept();
            return text;
        } catch (Exception e) { return ""; }
    }

    // --- Assertions ---
    public String getErrorMessage() {
        try { return wait.until(ExpectedConditions.visibilityOf(lblError)).getText().trim(); }
        catch (Exception e) { return ""; }
    }

    public boolean isSaveButtonEnabled() {
        try {
            return btnSave.getAttribute("disabled") == null;
        } catch (Exception e) { return true; }
    }

    public String getPriceValidationMessage() {
        return (String) ((JavascriptExecutor) driver)
                .executeScript("return arguments[0].validationMessage;", txtPrice);
    }
}
