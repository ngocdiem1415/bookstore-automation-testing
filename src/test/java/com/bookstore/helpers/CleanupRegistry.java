package com.bookstore.helpers;

import java.util.ArrayList;
import java.util.List;

public class CleanupRegistry {
    public static final List<String> createdUsers = new ArrayList<>();
    // dùng để xóa user test
    public static final List<String> createdProducts = new ArrayList<>();
    // dùng để xóa product test
    public static final List<String> createdCategories = new ArrayList<>();
    // dùng để xóa category test
    public static final List<String> adminCompleteOrderIds = new ArrayList<>();
    public static final List<String> adminCancelOrderIds = new ArrayList<>();
    public static final List<String> customerCancelOrderIds = new ArrayList<>();
    public static ProfileSnapshot profileSnapshot; //Dùng để lưu dữ liệu profile cũ trước khi test sửa profile.

    private CleanupRegistry() {
    }
}
