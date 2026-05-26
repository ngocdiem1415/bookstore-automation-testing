package com.bookstore.pages;

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
 * Page Object: Trang danh sách sản phẩm (/books).
 * Đã cấu hình đồng bộ chính xác với cấu trúc DOM sinh bởi AJAX.
 */
public class ProductListPage extends BasePage {

    private static final String PAGE_URL = "/books";

    private static final By BOOK_ITEM_LOCATOR    = By.cssSelector("[data-testid='book-item']");
    private static final By BOOK_NAME_IN_CARD    = By.cssSelector(".home-product-item__name");
    private static final By BOOK_PRICE_IN_CARD   = By.cssSelector(".home-product-item__price-current");
    private static final By PAGINATION_ITEMS     = By.cssSelector("#pagination .page-item");
    private static final By NEXT_PAGE_BTN        = By.cssSelector("#pagination .page-item:last-child .page-link");

    @FindBy(css = "[data-testid='filter-category']")
    private List<WebElement> radioFilterCategory;

    @FindBy(css = "[data-testid='filter-price']")
    private List<WebElement> radioFilterPrice;

    @FindBy(css = "[data-testid='filter-sort']")
    private WebElement selFilterSort;

    @FindBy(id = "product-container")
    private WebElement productContainer;

    @FindBy(id = "pagination")
    private WebElement paginationWrapper;

    @FindBy(id = "searchInput")
    private WebElement searchInput;

    @FindBy(css = "button.search-btn")
    private WebElement searchButton;

    public ProductListPage(WebDriver driver, String baseUrl) {
        super(driver, baseUrl);
    }

    public ProductListPage open() {
        driver.get(baseUrl + PAGE_URL);
        waitForProductsToLoad();
        return this;
    }

    public ProductListPage navigateTo(String url) {
        driver.get(url);
        waitForProductsToLoad();
        return this;
    }

    public ProductListPage filterByCategory(String categoryLabel) {
        boolean found = false;
        for (WebElement radio : radioFilterCategory) {
            String val = radio.getAttribute("value");
            String id = radio.getAttribute("id");
            String label = "";
            try {
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
        if (!found) throw new IllegalArgumentException("Không tìm thấy Danh mục: " + categoryLabel);
        waitForProductsToLoad();
        return this;
    }

    public ProductListPage filterByPrice(String priceRangeValue) {
        boolean found = false;
        for (WebElement radio : radioFilterPrice) {
            if (priceRangeValue.equalsIgnoreCase(radio.getAttribute("value"))) {
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", radio);
                found = true;
                break;
            }
        }
        if (!found) throw new IllegalArgumentException("Không tìm thấy khoảng giá trên UI: " + priceRangeValue);
        waitForProductsToLoad();
        return this;
    }


    public ProductListPage sortBy(String visibleText) {
        wait.until(ExpectedConditions.elementToBeClickable(selFilterSort));
        new Select(selFilterSort).selectByVisibleText(visibleText);
        waitForProductsToLoad();
        return this;
    }

    public List<WebElement> getDisplayedBooks() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(BOOK_ITEM_LOCATOR));
        return driver.findElements(BOOK_ITEM_LOCATOR);
    }

    public int getBookCount() {
        try { return getDisplayedBooks().size(); } catch (Exception e) { return 0; }
    }

    public String getBookNameAt(int index) {
        return getDisplayedBooks().get(index).findElement(BOOK_NAME_IN_CARD).getText().trim();
    }

    public String getBookPriceAt(int index) {
        return getDisplayedBooks().get(index).findElement(BOOK_PRICE_IN_CARD).getText().trim();
    }

    public long getBookPriceAsLong(int index) {
        String raw = getBookPriceAt(index).replaceAll("[^0-9]", "");
        return raw.isEmpty() ? 0L : Long.parseLong(raw);
    }

    public boolean isPageSafe() {
        String title = driver.getTitle().toLowerCase();
        return !title.contains("500") && !title.contains("error");
    }

    public ProductDetailPage clickBookAt(int index) {
        // 1. Đợi và lấy toàn bộ danh sách các Card sách đang hiển thị
        List<WebElement> books = getDisplayedBooks();
        WebElement targetBookCard = books.get(index);

        // 2. Tìm chính xác thẻ <a> chứa thuộc tính href điều hướng bên trong Card đó
        WebElement hrefLink = targetBookCard.findElement(By.xpath("./a | .//a"));

        System.out.println("[ProductListPage] Đang thực hiện cuộn và click vào sách tại vị trí index: " + index);

        // 3. Cuộn chuột đưa phần tử vào tầm nhìn hiển thị để tránh lỗi bị che khuất bởi Header/Footer cố định
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", hrefLink);

        // Thêm một nhịp nghỉ cực ngắn (100-200ms) để hiệu ứng cuộn trang hoàn tất ổn định
        try { Thread.sleep(200); } catch (InterruptedException ignored) {}

        // 4. Ép trình duyệt kích hoạt sự kiện click trực tiếp trên thẻ <a> bằng JavaScript (Bypass mọi lớp đè giao diện)
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", hrefLink);

        // 5. Trả về đối tượng trang chi tiết sản phẩm qua PageFactoryManager
        return PageFactoryManager.getProductDetailPage(driver, baseUrl);
    }

    public int getPaginationCount() {
        try {
            if (!paginationWrapper.isDisplayed()) return 0;
            return driver.findElements(PAGINATION_ITEMS).size();
        } catch (Exception e) { return 0; }
    }

    private void waitForProductsToLoad() {
        try {
            // Chờ đồng bộ 800ms khớp hoàn toàn với độ trễ phản hồi của hàm $.ajax trên Frontend
            Thread.sleep(800);
            wait.until(ExpectedConditions.visibilityOf(productContainer));
        } catch (Exception ignored) {}
    }
}