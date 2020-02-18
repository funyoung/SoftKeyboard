package com.pattern.util;

import java.io.File;

/**
 * A simple util of file helper.
 * @author funyoung
 */
public class FileUtil {
    private FileUtil() {
    }

    /**
     * check if the path is a valid file.
     *
     * @param path
     * @return true if and only if the file exists, otherwise false.
     */
    public static boolean isFile(String path) {
        if (null == path) {
            return false;
        }

        File file = new File(path);
        return file.isFile();
    }

    public static boolean isDirectory(String path) {
        if (null == path) {
            return false;
        }

        File file = new File(path);
        return file.isDirectory();
    }

    /**
     * list the name array of sub files.
     * @param path
     * @return an array of names if and only if the directory contains sub files,
     * otherwise null.
     */
    public static String[] list(String path) {
        if (null != path) {
            if (isDirectory(path)) {
                return new File(path).list();
            }
        }

        return null;
    }

    /**
     * get the path string with prefix path and the name of sub file.
     * @param prefix prefix path of parent's directory
     * @param name the sub file
     * @return the sub path string or null.
     */
    public static String getSubPath(String prefix, String name) {
        if (isEmpty(prefix) || isEmpty(name)) {
            return null;
        }
        return prefix + File.separator + name;
    }

    /**
     * validate if a path string is null or empty.
     * @param path
     * @return
     */
    public static boolean isEmpty(String path) {
        return null == path || path.length() <= 0;
    }
}
