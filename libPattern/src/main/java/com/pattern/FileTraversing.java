package com.pattern;

import java.io.File;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * An visitor pattern implement which invoke the visitor's method while traversing a
 * file recursively.
 *
 * @param <U>
 * @param <T>
 */
public class FileTraversing<U, T extends FileTraversing.AbstractVisitor<U>> {
    /**
     * The type of template parameter.
     */
    private final Class<T> clazz;
    public FileTraversing(Class<T> clazz) {
        this.clazz = clazz;
    }

    /**
     * create visitor instance with the concrete template type.
     * @param folder
     * @param name
     * @return
     */
    private T createFolderVisitor(File folder, String name) {
        try {
            Constructor c1 = clazz.getDeclaredConstructor(new Class[]{File.class, String.class});
            c1.setAccessible(true);
            return (T) c1.newInstance(new Object[]{folder, name});
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Start traversing with the a file path.
     * @param path
     * @return
     */
    public List<U> traverse(String path) {
        File file = new File(path);
        if (file.exists() && file.isDirectory()) {
            String[] subNames = file.list();
            if (null != subNames && subNames.length > 0) {
                List<U> fileList = new ArrayList<>();
                for (String name : subNames) {
                    AbstractVisitor visitor = createFolderVisitor(file, name);
                    if (null != visitor) {
                        fileList.addAll(visitor.visit());
                    }
                }
                return fileList;
            }
        }
        return Collections.emptyList();
    }

    public abstract static class AbstractVisitor<U> implements Visitor<List<U>> {
        private final File folder;
        private final String name;

        public AbstractVisitor(File folder, String name) {
            this.folder = folder;
            this.name = name;
        }

        @Override
        public final List<U> visit() {
            File file = new File(folder, name);
            String subPath = file.getAbsolutePath();
            if (file.isDirectory()) {
                return visitFolder(subPath);
            } else {
                U gift = visitFile(subPath);
                if (null != gift) {
                    List<U> list = new ArrayList<>();
                    list.add(gift);
                    return list;
                } else {
                    return Collections.emptyList();
                }
            }
        }

        protected abstract U visitFile(String subPath);
        protected abstract List<U> visitFolder(String folderPath);
    }
}
