package com.bookstore.pages;

import com.bookstore.base.BaseSetup;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;

import java.util.List;

public class ProductListPage extends BasePage {

    private static final String PAGE_URL = "/books";

    /** Card từng sản phẩm trong danh sách kết quả */
    private static final By BOOK_ITEM_LOCATOR =
            By.cssSelector(".product-item");

    /** Tên sách trong mỗi card */
    private static final By BOOK_NAME_IN_CARD =
            By.cssSelector(".product-name");

    /** Giá hiện tại trong mỗi card */
    private static final By BOOK_PRICE_IN_CARD =
            By.cssSelector(".product-price");

    /** Li items trong pagination */
    private static final By PAGINATION_ITEMS =
            By.cssSelector("#pagination .page-item");

    /** Nút trang tiếp theo */
    private static final By NEXT_PAGE_BTN =
            By.cssSelector("#pagination .page-item.next .page-link, "
                    + "#pagination .page-item:last-child .page-link");

    // ------  Search  ------

    /** Ô tìm kiếm trong header */
    @FindBy(id = "searchInput")
    private WebElement searchInput;

    /** Nút kính lúp / submit tìm kiếm */
    @FindBy(css = "button.search-btn")
    private WebElement searchButton;

    // ------  Sort (Sắp xếp)  ------

    /** Dropdown sắp xếp sản phẩm */
    @FindBy(id = "sort-by")
    private WebElement sortDropdown;

    /** Tất cả radio chọn thể loại */
    @FindBy(css = "input[name='category']")
    private List<WebElement> categoryRadios;

    /** Tất cả radio chọn nhà xuất bản */
    @FindBy(css = "input[name='publisher']")
    private List<WebElement> publisherRadios;

    /** Tất cả radio khoảng giá */
    @FindBy(css = "input[name='priceRange']")
    private List<WebElement> priceRangeRadios;

    /** Tất cả radio đánh giá sao */
    @FindBy(css = "input[name='rating']")
    private List<WebElement> ratingRadios;

    /**
     * Container chính chứa danh sách sản phẩm.
     * Dùng id — định danh duy nhất, ổn định nhất với DOM thay đổi do AJAX.
     */
    @FindBy(id = "product-container")
    private WebElement productContainer;


    /** Thông báo khi không tìm thấy sách */
    @FindBy(css = ".no-result-message")
    private WebElement noResultMessage;

    /** UL/nav phân trang */
    @FindBy(id = "pagination")
    private WebElement paginationWrapper;

    public ProductListPage(WebDriver driver) {
        super(driver);
    }


    /** Mở trực tiếp trang danh sách sản phẩm */
    public void open() {
        driver.get(getCurrentUrl()+ PAGE_URL);
    }


    /**
     * Nhập từ khoá và bấm tìm kiếm.
     *
     * @param keyword từ khoá cần tìm
     */
    public void searchBook(String keyword) {
        sendText(searchInput, keyword);
        clickElement(searchButton);
        waitForProductsToLoad();
    }

    /**
     * Lấy giá trị hiện tại trong ô tìm kiếm.
     *
     * @return text trong search input attribute "value"
     */
    public String getSearchInputValue() {
        wait.until(ExpectedConditions.visibilityOf(searchInput));
        return searchInput.getAttribute("value");
    }


    /**
     * Chọn tuỳ chọn sắp xếp theo text hiển thị trong dropdown.
     *
     * @param visibleText text của option (e.g. "Giá tăng dần", "Tên A→Z")
     */
    public void sortBy(String visibleText) {
        wait.until(ExpectedConditions.elementToBeClickable(sortDropdown));
        new Select(sortDropdown).selectByVisibleText(visibleText);
        waitForProductsToLoad();
    }

    /**
     * Lấy option đang được chọn hiện tại trong dropdown sắp xếp.
     *
     * @return text của option đang chọn
     */
    public String getSelectedSortOption() {
        wait.until(ExpectedConditions.visibilityOf(sortDropdown));
        return new Select(sortDropdown).getFirstSelectedOption().getText().trim();
    }

    /**
     * Chọn bộ lọc thể loại sách theo giá trị {@code value} của radio button.
     *
     * @param categoryValue value attribute (e.g. "van-hoc", "ky-nang-song")
     */
    public void filterByCategory(String categoryValue) {
        selectRadioByValue(categoryRadios, categoryValue);
        waitForProductsToLoad();
    }

    /**
     * Kiểm tra radio thể loại đang được chọn hay chưa.
     *
     * @param categoryValue value cần kiểm tra
     * @return true nếu radio đó đang checked
     */
    public boolean isCategorySelected(String categoryValue) {
        return isRadioSelected(categoryRadios, categoryValue);
    }

    /**
     * Chọn bộ lọc nhà xuất bản.
     *
     * @param publisherValue value attribute (e.g. "nxb-tre", "nxb-kim-dong")
     */
    public void filterByPublisher(String publisherValue) {
        selectRadioByValue(publisherRadios, publisherValue);
        waitForProductsToLoad();
    }


    /**
     * Chọn bộ lọc khoảng giá.
     *
     * @param priceRangeValue value (e.g. "0-100000", "100000-200000")
     */
    public void filterByPriceRange(String priceRangeValue) {
        selectRadioByValue(priceRangeRadios, priceRangeValue);
        waitForProductsToLoad();
    }

    /**
     * Chọn bộ lọc đánh giá sao.
     *
     * @param ratingValue value (e.g. "4" = 4 sao trở lên, "5" = 5 sao)
     */
    public void filterByRating(String ratingValue) {
        selectRadioByValue(ratingRadios, ratingValue);
        waitForProductsToLoad();
    }


    /**
     * Lấy danh sách tất cả sản phẩm đang hiển thị trên trang.
     *
     * @return List WebElement của các book card
     */
    public List<WebElement> getDisplayedBooks() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(BOOK_ITEM_LOCATOR));
        return driver.findElements(BOOK_ITEM_LOCATOR);
    }

    /**
     * Số lượng sách đang hiển thị.
     *
     * @return số card sách; 0 nếu không có
     */
    public int getBookCount() {
        try {
            return getDisplayedBooks().size();
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * Tên sách tại vị trí index (0-based).
     *
     * @param index vị trí card sách
     * @return chuỗi tên sách đã trim()
     */
    public String getBookNameAt(int index) {
        List<WebElement> books = getDisplayedBooks();
        validateIndex(index, books.size());
        return books.get(index).findElement(BOOK_NAME_IN_CARD).getText().trim();
    }

    /**
     * Giá hiện tại của sách tại vị trí index.
     *
     * @param index vị trí card sách
     * @return chuỗi giá (e.g. "120.000đ")
     */
    public String getBookPriceAt(int index) {
        List<WebElement> books = getDisplayedBooks();
        validateIndex(index, books.size());
        return books.get(index).findElement(BOOK_PRICE_IN_CARD).getText().trim();
    }

    /**
     * Kiểm tra product-container có hiển thị trên trang không.
     *
     * @return true nếu visible
     */
    public boolean isProductContainerDisplayed() {
        try {
            return wait.until(ExpectedConditions.visibilityOf(productContainer)).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Kiểm tra thông báo "không tìm thấy kết quả" hiển thị.
     *
     * @return true nếu thông báo visible
     */
    public boolean isNoResultMessageDisplayed() {
        try {
            return wait.until(ExpectedConditions.visibilityOf(noResultMessage)).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Số lượng items trong pagination.
     *
     * @return số page items; 0 nếu không có pagination
     */
    public int getPaginationCount() {
        try {
            wait.until(ExpectedConditions.visibilityOf(paginationWrapper));
            return driver.findElements(PAGINATION_ITEMS).size();
        } catch (Exception e) {
            return 0;
        }
    }

    /** Điều hướng sang trang tiếp theo. */
    public void goToNextPage() {
        WebElement nextBtn = wait.until(
                ExpectedConditions.elementToBeClickable(NEXT_PAGE_BTN));
        nextBtn.click();
        waitForProductsToLoad();
    }

    private void selectRadioByValue(List<WebElement> radios, String targetValue) {
        boolean found = false;
        for (WebElement radio : radios) {
            String val = radio.getAttribute("value");
            if (targetValue.equalsIgnoreCase(val)) {
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", radio);
                found = true;
                break;
            }
        }
        if (!found) {
            throw new IllegalArgumentException(
                    "Không tìm thấy radio với value='" + targetValue + "'");
        }
    }


    private boolean isRadioSelected(List<WebElement> radios, String targetValue) {
        for (WebElement radio : radios) {
            if (targetValue.equalsIgnoreCase(radio.getAttribute("value"))) {
                return radio.isSelected();
            }
        }
        return false;
    }

    private void waitForProductsToLoad() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.id("product-container")));
    }

    /**
     * Kiểm tra index hợp lệ để tránh IndexOutOfBoundsException.
     */
    private void validateIndex(int index, int size) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException(
                    "Index " + index + " không hợp lệ với danh sách có " + size + " phần tử.");
        }
    }
}
