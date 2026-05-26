package com.bookstore.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

public class DataHelper {
    private static Properties properties;

    static {
        properties = new Properties();
        try {
            FileInputStream file = new FileInputStream("src/test/resources/testData.properties");
            properties.load(new InputStreamReader(file, StandardCharsets.UTF_8));
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Không thể tải file cấu hình testData.properties!");
        }
    }

    public static String getValue(String key) {
        return properties.getProperty(key);
    }
}