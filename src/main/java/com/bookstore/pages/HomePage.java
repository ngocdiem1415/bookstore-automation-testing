package com.bookstore.pages;

import com.bookstore.base.BaseSetup;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.List;

public class HomePage extends BasePage {
    private final String PAGE_URL = "/home";

    public void open() {
        driver.get(BaseSetup.baseUrl + PAGE_URL);
    }

    @FindBy(id = "carouselExampleIndicators")
    private WebElement mainBanner;

    @FindBy(css = ".carousel-control-next")
    private WebElement nextBannerButton;

    @FindBy(css = ".owl-item .active")
    private List<WebElement> activeNewBooks;

    @FindBy(xpath = "//h2[contains(.,'Sách văn học')]")
    private WebElement literatureSectionTitle;

    @FindBy(css = ".literary-books .product-wrapper")
    private List<WebElement> literatureBookList;

    @FindBy(id = "cart-notification-success")
    private WebElement cartSuccessNotify;

    private By bookTitleLink = By.cssSelector(".price-most-product h5 a");

    public HomePage(WebDriver driver) {
        super(driver);
    }

    public boolean isBannerDisplayed() {
        return wait.until(ExpectedConditions.visibilityOf(mainBanner)).isDisplayed();
    }

    public int getNewBooksCount() {
        return activeNewBooks.size();
    }

    public int getLiteratureBooksCount() {
        try {
            wait.until(ExpectedConditions.visibilityOfAllElements(literatureBookList));
            return literatureBookList.size();
        } catch (Exception e) {
            return 0;
        }
    }

    public void clickFirstLiteratureBook() {
        if (!literatureBookList.isEmpty()) {
            WebElement firstBook = literatureBookList.get(0);
            WebElement titleLink = firstBook.findElement(bookTitleLink);

            org.openqa.selenium.JavascriptExecutor js = (org.openqa.selenium.JavascriptExecutor) driver;
            js.executeScript("arguments[0].click();", titleLink);
        }
    }

    public String getFirstBookTitle() {
        if (literatureBookList.isEmpty()) return "";
        WebElement firstBook = literatureBookList.get(0);
        return firstBook.findElement(bookTitleLink).getText();
    }

    public boolean isCartSuccessMessageDisplayed() {
        try {
            return wait.until(ExpectedConditions.visibilityOf(cartSuccessNotify)).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
}
