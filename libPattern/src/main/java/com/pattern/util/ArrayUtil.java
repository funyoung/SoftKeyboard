package com.pattern.util;

/**
 * @author funyoung
 *
 */
public class ArrayUtil {
    private ArrayUtil() {
    }

    /**
     * check if an array is null or contains zero element.
     * @param array
     * @param <T>
     * @return
     */
    public static <T> boolean isEmpty(T[] array) {
        return null == array && array.length <= 0;
    }
}
