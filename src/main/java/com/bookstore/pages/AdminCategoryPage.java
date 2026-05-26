package com.bookstore.pages;

import com.bookstore.base.BaseSetup;
import org.openqa.selenium.*;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.List;

/**
 * Page Object: Admin - Quản lý Danh mục (/admin/categories).
 * Covers: ADM-CAT-01/02/03
 */
public class AdminCategoryPage extends BasePage {

    private static final String PAGE_URL = "/admin/categories";

    @FindBy(css = "[data-testid='admin-cat-name']")
    private WebElement txtCategoryName;

    @FindBy(css = "[data-testid='admin-save-btn']")
    private WebElement btnSave;

    @FindBy(css = "[data-testid='admin-cat-error']")
    private WebElement lblError;

    @FindBy(css = "[data-testid='admin-cat-list'] tr")
    private List<WebElement> tableRows;

    @FindBy(css = "[data-testid='admin-add-btn']")
    private WebElement btnAdd;

    public AdminCategoryPage(WebDriver driver,String baseUrl) {
        super(driver, baseUrl);
    }

    public AdminCategoryPage open() {
        driver.get(getCurrentUrl() + PAGE_URL);
        return this;
    }

    public AdminCategoryPage clickAdd() {
        clickElement(btnAdd);
        return this;
    }

    public AdminCategoryPage enterName(String name) {
        clearAndSendText(txtCategoryName, name);
        return this;
    }

    public String clickSaveAndGetAlert() {
        clickElement(btnSave);
        try {
            Alert alert = wait.until(ExpectedConditions.alertIsPresent());
            String text = alert.getText().trim();
            alert.accept();
            return text;
        } catch (Exception e) { return ""; }
    }

    public AdminCategoryPage clickSave() {
        clickElement(btnSave);
        return this;
    }

    public String getErrorMessage() {
        try { return wait.until(ExpectedConditions.visibilityOf(lblError)).getText().trim(); }
        catch (Exception e) { return ""; }
    }

    public int getCategoryCount() {
        try { return Math.max(0, tableRows.size() - 1); } catch (Exception e) { return 0; }
    }

    public boolean isCategoryInList(String name) {
        try {
            return tableRows.stream().anyMatch(r -> r.getText().contains(name));
        } catch (Exception e) { return false; }
    }
}
