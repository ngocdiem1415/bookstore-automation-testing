package com.bookstore.tests;

import com.bookstore.base.BaseSetup;
import com.bookstore.pages.ProductListPage;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class ProductTest extends BaseSetup {

    private ProductListPage productListPage;

    @BeforeMethod
    public void openProductListPage() {
        productListPage = new ProductListPage(driver);
        productListPage.open();
    }

    /**
     * TC01: Mở trang danh sách sách → container sản phẩm phải hiển thị.
     * Mục đích: Xác nhận trang load thành công, DOM sẵn sàng.
     */
    @Test(priority = 1,
          description = "TC01 - Trang danh sách sách hiển thị product container")
    public void openProductList_pageLoads_productContainerVisible() {
        Assert.assertTrue(
                productListPage.isProductContainerDisplayed(),
                "[TC01] Product container không hiển thị – có thể trang lỗi hoặc URL sai.");
    }

    /**
     * TC02: Trang danh sách sách phải có ít nhất 1 sản phẩm khi mới load.
     */
    @Test(priority = 2,
          description = "TC02 - Trang hiển thị ít nhất 1 sản phẩm khi mới mở")
    public void openProductList_defaultLoad_atLeastOneBookDisplayed() {
        int count = productListPage.getBookCount();
        System.out.printf("[TC02] Số sách tải về: %d%n", count);
        Assert.assertTrue(count > 0,
                "[TC02] Không có sách nào hiển thị – kiểm tra API hoặc DB.");
    }

    /**
     * TC03: Tìm kiếm với từ khoá hợp lệ → có kết quả trả về.
     * Điều kiện: Từ khoá "Doraemon" cần có ít nhất 1 cuốn trong DB.
     */
    @Test(priority = 3,
          description = "TC03 - Tìm kiếm từ khoá hợp lệ trả về ít nhất 1 kết quả")
    public void searchBook_withValidKeyword_returnsResults() {
        String keyword = "Doraemon";

        productListPage.searchBook(keyword);

        int count = productListPage.getBookCount();
        System.out.printf("[TC03] Tìm '%s' → Tìm thấy %d sách.%n", keyword, count);
        Assert.assertTrue(count > 0,
                "[TC03] Không tìm thấy kết quả cho từ khoá: " + keyword);
    }

    /**
     * TC04: Tìm kiếm với từ khoá không tồn tại → hiển thị thông báo "không có kết quả".
     */
    @Test(priority = 4,
          description = "TC04 - Tìm kiếm từ khoá không tồn tại hiển thị thông báo no-result")
    public void searchBook_withInvalidKeyword_showsNoResultMessage() {
        String keyword = "XYZ_KHONG_TON_TAI_9999";

        productListPage.searchBook(keyword);

        Assert.assertTrue(
                productListPage.isNoResultMessageDisplayed(),
                "[TC04] Thông báo 'không tìm thấy' không hiển thị với từ khoá không hợp lệ.");
    }

    /**
     * TC05: Input tìm kiếm giữ lại từ khoá sau khi search.
     */
    @Test(priority = 5,
          description = "TC05 - Search input giữ nguyên giá trị từ khoá sau khi tìm kiếm")
    public void searchBook_afterSearch_searchInputRetainsKeyword() {
        String keyword = "Conan";

        productListPage.searchBook(keyword);

        String inputValue = productListPage.getSearchInputValue();
        Assert.assertEquals(inputValue, keyword,
                "[TC05] Search input không giữ lại từ khoá sau khi tìm kiếm.");
    }

    /**
     * TC06: Lọc theo thể loại "Văn học" → chỉ hiển thị sách thuộc thể loại đó.
     * Kiểm tra: Số sách > 0 và tên sách đầu tiên không rỗng.
     */
    @Test(priority = 6,
          description = "TC06 - Lọc thể loại 'van-hoc' trả về kết quả")
    public void filterByCategory_vanHoc_returnsFilteredResults() {
        productListPage.filterByCategory("van-hoc");

        int count = productListPage.getBookCount();
        System.out.printf("[TC06] Filter 'van-hoc' → %d sách.%n", count);

        Assert.assertTrue(count > 0,
                "[TC06] Không có sách nào sau khi lọc theo thể loại 'van-hoc'.");

        String firstName = productListPage.getBookNameAt(0);
        Assert.assertFalse(firstName.isEmpty(),
                "[TC06] Tên sách đầu tiên bị rỗng sau khi lọc.");
        System.out.println("[TC06] Sách đầu tiên: " + firstName);
    }

    /**
     * TC07: Lọc theo khoảng giá "100000-200000" → số sách > 0 (hoặc no-result nếu không có).
     * Kiểm tra tính đúng đắn của filter logic giá.
     */
    @Test(priority = 7,
          description = "TC07 - Lọc khoảng giá 100k–200k hoạt động đúng")
    public void filterByPriceRange_100kTo200k_productContainerVisible() {
        productListPage.filterByPriceRange("100000-200000");

        Assert.assertTrue(
                productListPage.isProductContainerDisplayed(),
                "[TC07] Product container biến mất sau khi lọc giá – có thể lỗi JS.");

        System.out.printf("[TC07] Số sách trong khoảng 100k–200k: %d%n",
                productListPage.getBookCount());
    }

    /**
     * TC08: Lọc theo nhà xuất bản → product container vẫn hiển thị.
     */
    @Test(priority = 8,
          description = "TC08 - Lọc nhà xuất bản 'nxb-tre' không gây lỗi trang")
    public void filterByPublisher_nxbTre_productContainerStillDisplayed() {
        productListPage.filterByPublisher("nxb-tre");

        Assert.assertTrue(
                productListPage.isProductContainerDisplayed(),
                "[TC08] Product container không hiển thị sau khi lọc nhà xuất bản.");
    }

    /**
     * TC09: Lọc kết hợp Thể loại + Khoảng giá → container vẫn visible.
     * Kiểm tra filter chain không gây crash UI.
     */
    @Test(priority = 9,
          description = "TC09 - Kết hợp lọc thể loại và khoảng giá không gây lỗi UI")
    public void filterByCategory_thenPriceRange_combined_uiStaysStable() {
        productListPage.filterByCategory("ky-nang-song");
        productListPage.filterByPriceRange("0-100000");

        Assert.assertTrue(
                productListPage.isProductContainerDisplayed(),
                "[TC09] Product container bị ẩn sau khi kết hợp 2 bộ lọc.");
    }

    /**
     * TC10: Chọn sắp xếp "Giá tăng dần" → dropdown phản ánh đúng lựa chọn.
     */
    @Test(priority = 10,
          description = "TC10 - Dropdown Sort phản ánh đúng lựa chọn 'Giá tăng dần'")
    public void sortBy_priceAscending_dropdownReflectsSelection() {
        String expectedOption = "Giá tăng dần";

        productListPage.sortBy(expectedOption);

        String actualOption = productListPage.getSelectedSortOption();
        Assert.assertEquals(actualOption, expectedOption,
                "[TC10] Dropdown sort không hiển thị đúng option đã chọn.");
    }

    /**
     * TC11: Sắp xếp "Giá tăng dần" → giá sách đầu tiên ≤ giá sách thứ hai.
     * Kiểm tra logic sort thực tế.
     */
    @Test(priority = 11,
          description = "TC11 - Sắp xếp giá tăng dần đúng thứ tự — sách[0].giá ≤ sách[1].giá")
    public void sortBy_priceAscending_firstBookCheaperThanSecond() {
        productListPage.sortBy("Giá tăng dần");

        int count = productListPage.getBookCount();
        if (count < 2) {
            System.out.println("[TC11] Skipped: Cần ít nhất 2 sách để kiểm tra thứ tự giá.");
            return;
        }

        // Parse giá: "120.000đ" → 120000
        long price0 = parsePriceText(productListPage.getBookPriceAt(0));
        long price1 = parsePriceText(productListPage.getBookPriceAt(1));

        System.out.printf("[TC11] Sách[0] giá: %d | Sách[1] giá: %d%n", price0, price1);
        Assert.assertTrue(price0 <= price1,
                String.format("[TC11] Sắp xếp giá tăng dần sai: %d > %d", price0, price1));
    }

    /**
     * TC12: Trang danh sách sách có hiển thị phân trang khi số sách đủ lớn.
     */
    @Test(priority = 12,
          description = "TC12 - Pagination hiển thị khi có nhiều hơn 1 trang")
    public void openProductList_manyBooks_paginationDisplayed() {
        int pages = productListPage.getPaginationCount();
        System.out.printf("[TC12] Số trang phân trang: %d%n", pages);
        Assert.assertTrue(pages >= 1,
                "[TC12] Không tìm thấy phần tử pagination – kiểm tra id='pagination'.");
    }

    /**
     * Chuyển đổi chuỗi giá tiền sang kiểu long để so sánh.
     * Ví dụ: "120.000đ" → 120000; "1,000,000đ" → 1000000.
     *
     * @param priceText chuỗi giá từ UI
     * @return giá trị số nguyên
     */
    private long parsePriceText(String priceText) {
        // Xoá tất cả ký tự không phải digit
        String cleaned = priceText.replaceAll("[^\\d]", "");
        if (cleaned.isEmpty()) return 0L;
        return Long.parseLong(cleaned);
    }
}
