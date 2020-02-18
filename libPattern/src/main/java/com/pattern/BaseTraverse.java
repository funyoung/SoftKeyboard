package com.pattern;

import com.pattern.util.ArrayUtil;
import com.pattern.util.FileUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class BaseTraverse <U> {
    /**
     * Start traversing with the a file path.
     * @param path
     * @return
     */
    public final List<U> traverse(String path) {
        return traverse(path, getVisitStrategy());
    }

    protected abstract BaseStrategy<U> getVisitStrategy();

    // todo: including both deep-first and broad-first traversing.
    private static <U> List<U> traverse(String path, BaseStrategy strategy) {
        U singleItem = traverseFileItem(path, strategy);
        if (null == singleItem) {
            String[] subNameArray = FileUtil.list(path);
            if (ArrayUtil.isEmpty(subNameArray)) {
                return Collections.emptyList();
            }

            List<U> list = new ArrayList<>();
            traverse(list, path, subNameArray, strategy);
            return list;
        } else {
            List<U> list = new ArrayList<>();
            list.add(singleItem);
            return list;
        }
    }

    private static <U> void traverse(List<U> list, String path, String[] subNameArray, BaseStrategy strategy) {
        if (null != subNameArray) {
            for (String name : subNameArray) {
                String subPath = FileUtil.getSubPath(path, name);
                list.addAll(traverse(subPath, strategy));
            }
        }
    }

    private static <U> U traverseFileItem(String path, BaseStrategy strategy) {
        if (FileUtil.isFile(path)) {
            Object item = strategy.parseFile(path);
            if (null != item) {
                return (U) item;
            }
        }

        return null;
    }
}
