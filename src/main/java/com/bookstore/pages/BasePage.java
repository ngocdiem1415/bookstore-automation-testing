package com.bookstore.pages;

import org.openqa.selenium.*;
import org.openqa.selenium.io.FileHandler;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class BasePage {
    protected WebDriver driver;
    protected WebDriverWait wait;

    public static final int EXPLICIT_WAIT_SECONDS = 15;
    private static final String SCREENSHOT_DIR = "test-output/screenshots/";

    public BasePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(EXPLICIT_WAIT_SECONDS));
        PageFactory.initElements(driver, this);
    }

    protected void clickElement(WebElement element) {
        wait.until(ExpectedConditions.elementToBeClickable(element)).click();
    }

    protected void sendText(WebElement element, String text) {
        wait.until(ExpectedConditions.visibilityOf(element)).sendKeys(text);
    }

    protected void clearAndSendText(WebElement element, String text) {
        WebElement el = wait.until(ExpectedConditions.visibilityOf(element));
        el.clear();
        el.sendKeys(text);
    }

    protected String getTextOf(WebElement element) {
        return wait.until(ExpectedConditions.visibilityOf(element)).getText().trim();
    }

    protected boolean isElementVisible(WebElement element) {
        try {
            return wait.until(ExpectedConditions.visibilityOf(element)).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    protected void scrollToElement(WebElement element) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", element);
    }


    protected void jsClick(WebElement element) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
    }

    protected boolean waitForUrlContains(String urlFragment) {
        try {
            return wait.until(ExpectedConditions.urlContains(urlFragment));
        } catch (Exception e) {
            return false;
        }
    }

    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }

    public String getPageTitle() {
        return driver.getTitle();
    }

    /**
     * Chụp screenshot và lưu vào thư mục test-output/screenshots/.
     * @param testName Tên test case (dùng làm tên file).
     * @return Đường dẫn file screenshot.
     */
    public String takeScreenshot(String testName) {
        if (!(driver instanceof TakesScreenshot)) return "";
        try {
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String fileName = SCREENSHOT_DIR + testName + "_" + timestamp + ".png";
            File destFile = new File(fileName);
            destFile.getParentFile().mkdirs();
            File srcFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            FileHandler.copy(srcFile, destFile);
            System.out.println("[BasePage] Screenshot saved: " + fileName);
            return fileName;
        } catch (IOException e) {
            System.err.println("[BasePage] Failed to take screenshot: " + e.getMessage());
            return "";
        }
    }
}
