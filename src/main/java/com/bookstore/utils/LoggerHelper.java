package com.bookstore.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LoggerHelper {
    private static final Logger logger = LogManager.getLogger(LoggerHelper.class);

    public static void info(String message) {
        logger.info(message);
    }

    public static void warn(String message) {
        logger.warn(message);
    }

    public static void error(String message) {
        logger.error(message);
    }

    public static void error(String message, Throwable throwable) {
        logger.error(message, throwable);
    }

    public static void debug(String message) {
        logger.debug(message);
    }

    public static void startTestCase(String testCaseName) {
        info("BẮT ĐẦU TEST CASE: " + testCaseName.toUpperCase());
    }

    public static void endTestCase(String testCaseName) {
        info("KẾT THÚC TEST CASE: " + testCaseName.toUpperCase());
    }
}
