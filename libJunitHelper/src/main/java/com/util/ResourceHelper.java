package com.util;

import java.net.URL;

public class ResourceHelper {
    private ResourceHelper() {
    }
    public static final String getResourcePath(Class<?> clazz, String name) {
        ClassLoader classLoader = clazz.getClassLoader();
        URL resource = classLoader.getResource(name);
        return resource.getPath();
    }

}
