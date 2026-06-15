package com.bookstore.pages;

import com.bookstore.factory.PageFactoryManager;
import com.bookstore.utils.LoggerHelper;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;

import java.time.Duration;
import java.util.List;

public class ProductListPage extends BasePage {

    private static final String PAGE_URL = "/books";

    private static final By PRODUCT_CONTAINER = By.cssSelector("[data-testid='product-container']");
    private static final By BOOK_ITEM = By.cssSelector("[data-testid='book-item']");
    private static final By BOOK_LINK = By.cssSelector("[data-testid='book-link']");
    private static final By BOOK_NAME = By.cssSelector("[data-testid='book-name']");
    private static final By BOOK_PRICE = By.cssSelector("[data-testid='book-price']");

    @FindBy(css = "[data-testid='category-section']")
    private WebElement categorySection;

    @FindBy(css = "[data-testid='publisher-section']")
    private WebElement publisherSection;

    @FindBy(css = "[data-testid='category-label']")
    private List<WebElement> categoryLabels;

    @FindBy(css = "[data-testid='filter-category']")
    private List<WebElement> radioFilterCategory;

    @FindBy(css = "[data-testid='filter-price']")
    private List<WebElement> radioFilterPrice;

    @FindBy(css = "[data-testid='filter-sort']")
    private WebElement selFilterSort;

    public ProductListPage(WebDriver driver, String baseUrl) {
        super(driver, baseUrl);
    }

    public ProductListPage open() {
        driver.get(baseUrl + PAGE_URL);
        waitForProductsToLoad();
        return this;
    }

    public ProductListPage navigateTo(String url) {
        if (!url.startsWith("http")) {
            driver.get(baseUrl + url);
        } else {
            driver.get(url);
        }

        waitForProductsToLoad();
        return this;
    }

    private void clickRadioByValueOrLabel(List<WebElement> radios,
                                          String expected,
                                          String filterName) {
        if (radios == null || radios.isEmpty()) {
            throw new RuntimeException("Không tìm thấy danh sách radio: " + filterName);
        }

        for (int i = 0; i < radios.size(); i++) {
            WebElement radio = radios.get(i);
            String actualValue = radio.getAttribute("value");

            String actualLabel = "";
            if (i < categoryLabels.size()) {
                actualLabel = categoryLabels.get(i).getText().trim();
            }

            LoggerHelper.info("[PRODUCT_LIST_PAGE] " + filterName
                    + " item[" + i + "] value='" + actualValue + "', label='" + actualLabel + "'");

            boolean matchedByValue = actualValue != null && actualValue.equalsIgnoreCase(expected);
            boolean matchedByLabel = actualLabel.equalsIgnoreCase(expected);

            if (matchedByValue || matchedByLabel) {
                LoggerHelper.info("[PRODUCT_LIST_PAGE] Chọn " + filterName + ": " + expected);
                scrollToElement(radio);
                jsClick(radio);
                waitForProductsToLoad();
                return;
            }
        }
        throw new IllegalArgumentException("Không tìm thấy " + filterName + " trên UI: " + expected);
    }

    public ProductListPage filterByCategory(String categoryLabelOrValue) {
        clickRadioByValueOrLabel(radioFilterCategory, categoryLabelOrValue, "Danh mục");
        waitForProductsToLoad();
        return this;
    }

    public ProductListPage filterByPrice(String priceRangeValue) {
        for (WebElement radio : radioFilterPrice) {
            String actualValue = radio.getAttribute("value");
            if (priceRangeValue.equalsIgnoreCase(actualValue)) {
                scrollToElement(radio);
                jsClick(radio);
                waitForProductsToLoad();
                return this;
            }
        }
        throw new IllegalArgumentException(
                "Không tìm thấy khoảng giá  trên UI: " + priceRangeValue
        );
    }

    public ProductListPage sortBy(String visibleText) {
        clickElement(selFilterSort);
        new Select(selFilterSort).selectByVisibleText(visibleText);
        waitForProductsToLoad();
        return this;
    }

    public List<WebElement> getDisplayedBooks() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(PRODUCT_CONTAINER));
        return driver.findElements(BOOK_ITEM);
    }

    public int getBookCount() {
        return driver.findElements(BOOK_ITEM).size();
    }

    public String getBookNameAt(int index) {
        return getDisplayedBooks()
                .get(index)
                .findElement(BOOK_NAME)
                .getText()
                .trim();
    }

    public String getBookPriceAt(int index) {
        return getDisplayedBooks()
                .get(index)
                .findElement(BOOK_PRICE)
                .getText()
                .trim();
    }

    public long getBookPriceAsLong(int index) {
        String raw = getBookPriceAt(index).replaceAll("[^0-9]", "");

        if (raw.isEmpty()) {
            throw new RuntimeException("Không đọc được giá sách tại index: " + index);
        }

        return Long.parseLong(raw);
    }

    public ProductDetailPage clickBookAt(int index) {
        WebElement book = getDisplayedBooks().get(index);
        WebElement link = book.findElement(BOOK_LINK);
        scrollToElement(link);
        clickElement(link);
        return PageFactoryManager.getProductDetailPage(driver, baseUrl);
    }


    private void waitForProductsToLoad() {
        wait.until(ExpectedConditions.presenceOfElementLocated(PRODUCT_CONTAINER));
        wait.until(ExpectedConditions.visibilityOfElementLocated(PRODUCT_CONTAINER));
        try {
            Thread.sleep(800);
        } catch (InterruptedException ignored) {
        }
    }

    public boolean arePricesAscending() {
        int count = getBookCount();
        for (int i = 0; i < count - 1; i++) {
            long current = getBookPriceAsLong(i);
            long next = getBookPriceAsLong(i + 1);
            System.out.printf("Compare ASC: %d <= %d%n", current, next);
            if (current > next) {
                return false;
            }
        }
        return true;
    }

    public boolean arePricesDescending() {
        int count = getBookCount();
        for (int i = 0; i < count - 1; i++) {
            long current = getBookPriceAsLong(i);
            long next = getBookPriceAsLong(i + 1);
            LoggerHelper.info("[PRODUCT_LIST_PAGE] So sánh giá tăng dần: " + current + " <= " + next);
            if (current < next) {
                return false;
            }
        }
        return true;
    }

    public String getVisibleBookSignature() {
        StringBuilder signature = new StringBuilder();
        int count = getBookCount();

        for (int i = 0; i < count; i++) {
            signature.append(getBookNameAt(i))
                    .append("|")
                    .append(getBookPriceAsLong(i))
                    .append(";");
        }

        return signature.toString();
    }

    public boolean isProductContainerDisplayed() {
        try {
            return driver.findElement(PRODUCT_CONTAINER).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

}
