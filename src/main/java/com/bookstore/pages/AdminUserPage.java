package com.bookstore.pages;

import com.bookstore.base.BaseSetup;
import org.openqa.selenium.*;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.List;

/**
 * Page Object: Admin - Quản lý Người dùng (/admin/users).
 * Covers: ADM-USR-01/02/03
 */
public class AdminUserPage extends BasePage {

    private static final String PAGE_URL = "/admin/users";

    @FindBy(css = "[data-testid='admin-user-block-btn']")
    private List<WebElement> listBlockBtns;

    @FindBy(css = "[data-testid='admin-user-status']")
    private List<WebElement> listUserStatuses;

    @FindBy(css = "[data-testid='admin-user-list'] tr")
    private List<WebElement> tableRows;

    @FindBy(css = "[data-testid='admin-user-error']")
    private WebElement lblError;

    public AdminUserPage(WebDriver driver,String baseUrl) {
        super(driver, baseUrl);
    }
    public AdminUserPage open() {
        driver.get(getCurrentUrl() + PAGE_URL);
        return this;
    }

    /** ADM-USR-01: Click Block button tại index */
    public AdminUserPage clickBlockAt(int index) {
        System.out.println("[AdminUserPage] Clicking block button at index: " + index);
        clickElement(listBlockBtns.get(index));
        try {
            Alert confirm = wait.until(ExpectedConditions.alertIsPresent());
            confirm.accept();
            Thread.sleep(800);
        } catch (Exception e) {
            System.out.println("[AdminUserPage] No confirm dialog: " + e.getMessage());
        }
        return this;
    }

    /** ADM-USR-01: Lấy status text của user tại index */
    public String getUserStatusAt(int index) {
        try {
            return listUserStatuses.get(index).getText().trim().toUpperCase();
        } catch (Exception e) { return ""; }
    }

    /** ADM-USR-03: Click Block button của chính Admin (self-block) */
    public String clickBlockSelfAndGetAlert() {
        System.out.println("[AdminUserPage] Attempting to block own account...");
        // Tìm row của chính admin (thường là row đầu tiên hoặc có data-self="true")
        try {
            WebElement selfRow = driver.findElement(
                    By.cssSelector("[data-testid='admin-user-list'] [data-self='true'] [data-testid='admin-user-block-btn']"));
            selfRow.click();
        } catch (Exception e) {
            // Fallback: click block btn đầu tiên (admin tự block)
            clickElement(listBlockBtns.get(0));
        }
        try {
            Alert alert = wait.until(ExpectedConditions.alertIsPresent());
            String text = alert.getText().trim();
            alert.accept();
            return text;
        } catch (Exception e) { return ""; }
    }

    public String getErrorMessage() {
        try { return wait.until(ExpectedConditions.visibilityOf(lblError)).getText().trim(); }
        catch (Exception e) { return ""; }
    }

    public int getUserCount() {
        try { return Math.max(0, tableRows.size() - 1); } catch (Exception e) { return 0; }
    }
}
