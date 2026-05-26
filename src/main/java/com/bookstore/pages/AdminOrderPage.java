package com.bookstore.pages;

import com.bookstore.base.BaseSetup;
import org.openqa.selenium.*;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;

import java.util.List;

/**
 * Page Object: Admin - Quản lý Đơn hàng (/admin/orders).
 * Covers: ADM-ORD-01/02/03
 */
public class AdminOrderPage extends BasePage {

    private static final String PAGE_URL = "/admin/orders";

    @FindBy(css = "[data-testid='admin-order-status-select']")
    private List<WebElement> listStatusSelects;

    @FindBy(css = "[data-testid='admin-order-save-btn']")
    private List<WebElement> listSaveBtns;

    @FindBy(css = "[data-testid='admin-order-list'] tr")
    private List<WebElement> tableRows;

    @FindBy(css = "[data-testid='admin-order-error']")
    private WebElement lblError;

    public AdminOrderPage(WebDriver driver,String baseUrl) {
        super(driver, baseUrl);
    }
    public AdminOrderPage open() {
        driver.get(getCurrentUrl() + PAGE_URL);
        return this;
    }

    /** ADM-ORD-01/02: Thay đổi status của order tại index */
    public AdminOrderPage changeStatusAt(int index, String statusValue) {
        System.out.println("[AdminOrderPage] Changing status at index " + index + " to: " + statusValue);
        Select sel = new Select(wait.until(
                ExpectedConditions.elementToBeClickable(listStatusSelects.get(index))));
        sel.selectByValue(statusValue);
        return this;
    }

    public String clickSaveAndGetResult(int index) {
        clickElement(listSaveBtns.get(index));
        try {
            Alert alert = wait.until(ExpectedConditions.alertIsPresent());
            String text = alert.getText().trim();
            alert.accept();
            return text;
        } catch (Exception e) { return ""; }
    }

    /** ADM-ORD-02: Kiểm tra dropdown status có bị disabled không */
    public boolean isStatusSelectDisabled(int index) {
        String disabled = listStatusSelects.get(index).getAttribute("disabled");
        return disabled != null;
    }

    public String getErrorMessage() {
        try { return wait.until(ExpectedConditions.visibilityOf(lblError)).getText().trim(); }
        catch (Exception e) { return ""; }
    }

    public String getStatusAt(int index) {
        try {
            return new Select(listStatusSelects.get(index)).getFirstSelectedOption().getText().trim();
        } catch (Exception e) { return ""; }
    }
}
