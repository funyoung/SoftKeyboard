package com.pattern.util;

import java.lang.reflect.Constructor;

public class ClassUtil {
    private ClassUtil() {
    }

    /**
     * create visitor instance with the concrete template type.
     * @param path
     * @return
     */
    public static <T> T createInstance(Class<T> clazz, String path) {
        try {
            Constructor c1 = clazz.getDeclaredConstructor(new Class[]{String.class});
            c1.setAccessible(true);
            return (T) c1.newInstance(new Object[]{path});
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static <T> T createInstance(Class<T> clazz) {
        try {
            Constructor c1 = clazz.getDeclaredConstructor();
            c1.setAccessible(true);
            return (T) c1.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
