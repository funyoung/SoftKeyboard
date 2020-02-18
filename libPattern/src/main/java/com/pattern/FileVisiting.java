package com.pattern;

import com.pattern.util.ClassUtil;

/**
 * An visitor pattern implement which invoke the visitor's method while traversing a
 * file recursively.
 *
 * @param <U>
 * @param <T>
 */
public class FileVisiting<U, T extends BaseStrategy<U>> extends BaseTraverse<U> {
    /**
     * The type of template parameter.
     */
    private final Class<T> clazz;
    public FileVisiting(Class<T> clazz) {
        this.clazz = clazz;
    }

    @Override
    protected BaseStrategy<U> getVisitStrategy() {
        return ClassUtil.createInstance(clazz);
    }
}
