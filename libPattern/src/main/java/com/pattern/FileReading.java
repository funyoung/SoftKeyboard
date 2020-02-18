package com.pattern;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FileReading<U> {
    private final BaseStrategy strategy;
    public FileReading(BaseStrategy strategy) {
        this.strategy = strategy;
    }

    /**
     * Start traversing with the a file path.
     * @param path
     * @return
     */
    public List<U> read(String path) {
        File file = new File(path);
        if (file.exists() && file.isDirectory()) {
            String[] subNames = file.list();
            if (null != subNames && subNames.length > 0) {
                List<U> list = new ArrayList<>();
                for (String name : subNames) {
                    File subFile = new File(file, name);
                    String subPath = subFile.getAbsolutePath();
                    if (subFile.isDirectory()) {
                        list.addAll(read(subPath));
                    } else {
                        Object item = strategy.readFile(subPath);
                        if (null != item) {
                            list.add((U)item);
                        }
                    }
                }
                return list;
            }
        }
        return Collections.emptyList();
    }

    public static abstract class BaseStrategy<U> {
        public abstract U readFile(String subPath);
    }
}
