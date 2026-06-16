package com.bookstore.helpers;

import java.util.ArrayList;
import java.util.List;

public class CleanupRegistry {
    public static final List<String> createdUsers = new ArrayList<>();
    public static final List<String> createdProducts = new ArrayList<>();
    public static final List<String> createdCategories = new ArrayList<>();
    public static final List<String> adminCompleteOrderIds = new ArrayList<>();
    public static final List<String> adminCancelOrderIds = new ArrayList<>();
    public static final List<String> customerCancelOrderIds = new ArrayList<>();
    public static ProfileSnapshot profileSnapshot;

    private CleanupRegistry() {
    }
}
