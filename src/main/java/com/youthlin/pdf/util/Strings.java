package com.youthlin.pdf.util;

/**
 * @author : youthlin.chen @ 2019-10-13 16:32
 */
public class Strings {
    public static int parseInt(String input, int defaultValue) {
        try {
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
}
