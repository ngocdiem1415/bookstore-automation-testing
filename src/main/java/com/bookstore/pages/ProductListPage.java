package com.bookstore.pages;

import com.bookstore.base.BaseSetup;
import com.bookstore.factory.PageFactoryManager;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;

import java.util.List;

/**
 * Page Object: Trang danh sách sản phẩm (/listBook).
 * Covers: PROD-NAV, PROD-FIL, PROD-SRT
 */
public class ProductListPage extends BasePage {

    private static final String PAGE_URL = "/listBook";
    private static final By BOOK_ITEM_LOCATOR    = By.cssSelector("[data-testid='book-item']");
    private static final By BOOK_NAME_IN_CARD    = By.cssSelector(".product-name");
    private static final By BOOK_PRICE_IN_CARD   = By.cssSelector(".product-price");
    private static final By PAGINATION_ITEMS     = By.cssSelector("#pagination .page-item");
    private static final By NEXT_PAGE_BTN        = By.cssSelector(
            "#pagination .page-item.next .page-link, #pagination .page-item:last-child .page-link");

    // --- Filter: Category ---
    @FindBy(css = "[data-testid='filter-category']")
    private List<WebElement> radioFilterCategory;

    // --- Filter: Price ---
    @FindBy(css = "[data-testid='filter-price']")
    private List<WebElement> radioFilterPrice;

    // --- Filter: Sort ---
    @FindBy(css = "[data-testid='filter-sort']")
    private WebElement selFilterSort;

    // --- Result area ---
    @FindBy(id = "product-container")
    private WebElement productContainer;

    @FindBy(css = ".no-result-message")
    private WebElement noResultMessage;

    @FindBy(id = "pagination")
    private WebElement paginationWrapper;

    @FindBy(id = "searchInput")
    private WebElement searchInput;

    @FindBy(css = "button.search-btn")
    private WebElement searchButton;

    public ProductListPage(WebDriver driver,String baseUrl) {
        super(driver,baseUrl);
    }

    public ProductListPage open() {
        driver.get(getCurrentUrl() + PAGE_URL);
        return this;
    }

    /**
     * Điều hướng thẳng đến URL tùy ý (dùng cho Boundary tests).
     */
    public ProductListPage navigateTo(String url) {
        System.out.println("[ProductListPage] Navigating to URL: " + url);
        driver.get(url);
        return this;
    }

    /**
     * PROD-NAV-01/02: Chọn [filter-category] radio button theo label text.
     * @param categoryLabel Ví dụ: "Tiểu thuyết", "Sách test"
     */
    public ProductListPage filterByCategory(String categoryLabel) {
        System.out.println("[ProductListPage] Filtering by category: " + categoryLabel);
        boolean found = false;
        for (WebElement radio : radioFilterCategory) {
            // Lấy label text từ label[for] tương ứng
            String val = radio.getAttribute("value");
            String label = "";
            try {
                String id = radio.getAttribute("id");
                label = driver.findElement(By.cssSelector("label[for='" + id + "']")).getText().trim();
            } catch (Exception ignored) {
                label = val;
            }
            if (label.equalsIgnoreCase(categoryLabel) || val.equalsIgnoreCase(categoryLabel)) {
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", radio);
                found = true;
                break;
            }
        }
        if (!found) throw new IllegalArgumentException("Không tìm thấy category: " + categoryLabel);
        waitForProductsToLoad();
        return this;
    }

    /**
     * PROD-FIL-01: Chọn [filter-price] radio button theo value range.
     * @param priceRangeValue Ví dụ: "100000-200000"
     */
    public ProductListPage filterByPrice(String priceRangeValue) {
        System.out.println("[ProductListPage] Filtering by price range: " + priceRangeValue);
        boolean found = false;
        for (WebElement radio : radioFilterPrice) {
            if (priceRangeValue.equalsIgnoreCase(radio.getAttribute("value"))) {
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", radio);
                found = true;
                break;
            }
        }
        if (!found) throw new IllegalArgumentException("Không tìm thấy price range: " + priceRangeValue);
        waitForProductsToLoad();
        return this;
    }

    /**
     * PROD-SRT-01/02: Chọn option sắp xếp trong [filter-sort] dropdown.
     * @param visibleText Ví dụ: "Giá tăng dần", "Hàng mới nhất"
     */
    public ProductListPage sortBy(String visibleText) {
        System.out.println("[ProductListPage] Sorting by: " + visibleText);
        wait.until(ExpectedConditions.elementToBeClickable(selFilterSort));
        new Select(selFilterSort).selectByVisibleText(visibleText);
        waitForProductsToLoad();
        return this;
    }

    public String getSelectedSortOption() {
        wait.until(ExpectedConditions.visibilityOf(selFilterSort));
        return new Select(selFilterSort).getFirstSelectedOption().getText().trim();
    }

    public List<WebElement> getDisplayedBooks() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(BOOK_ITEM_LOCATOR));
        return driver.findElements(BOOK_ITEM_LOCATOR);
    }

    public int getBookCount() {
        try { return getDisplayedBooks().size(); } catch (Exception e) { return 0; }
    }

    public String getBookNameAt(int index) {
        List<WebElement> books = getDisplayedBooks();
        return books.get(index).findElement(BOOK_NAME_IN_CARD).getText().trim();
    }

    public String getBookPriceAt(int index) {
        List<WebElement> books = getDisplayedBooks();
        return books.get(index).findElement(BOOK_PRICE_IN_CARD).getText().trim();
    }

    /**
     * Lấy giá dạng số (long) từ chuỗi "120.000đ".
     */
    public long getBookPriceAsLong(int index) {
        String raw = getBookPriceAt(index).replaceAll("[^0-9]", "");
        return raw.isEmpty() ? 0L : Long.parseLong(raw);
    }

    public boolean isNoResultMessageDisplayed() {
        try {
            return wait.until(ExpectedConditions.visibilityOf(noResultMessage)).isDisplayed();
        } catch (Exception e) { return false; }
    }

    public boolean isProductContainerDisplayed() {
        try {
            return wait.until(ExpectedConditions.visibilityOf(productContainer)).isDisplayed();
        } catch (Exception e) { return false; }
    }

    /**
     * PROD-NAV-03 / PROD-FIL-02/03: Kiểm tra trang không crash (không có Error 500).
     */
    public boolean isPageSafe() {
        String title = driver.getTitle().toLowerCase();
        String url   = driver.getCurrentUrl().toLowerCase();
        return !title.contains("500") && !title.contains("error") && !url.contains("error/500");
    }

    public boolean is404Page() {
        String title = driver.getTitle().toLowerCase();
        return title.contains("404") || title.contains("not found");
    }

    /**
     * Click vào card sách ở vị trí index → ProductDetailPage.
     */
    public ProductDetailPage clickBookAt(int index) {
        List<WebElement> books = getDisplayedBooks();
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", books.get(index));
        return PageFactoryManager.getProductDetailPage(driver,baseUrl);
    }

    public int getPaginationCount() {
        try {
            wait.until(ExpectedConditions.visibilityOf(paginationWrapper));
            return driver.findElements(PAGINATION_ITEMS).size();
        } catch (Exception e) { return 0; }
    }

    public ProductListPage goToNextPage() {
        WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(NEXT_PAGE_BTN));
        btn.click();
        waitForProductsToLoad();
        return this;
    }

    // --- Internal ---
    private void waitForProductsToLoad() {
        try {
            Thread.sleep(800); // Chờ AJAX
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("product-container")));
        } catch (Exception ignored) {}
    }
}
