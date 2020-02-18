package com.pattern;

import com.util.ResourceHelper;

import org.junit.Before;

import java.io.File;
import java.util.List;

public class FileBaseTest {
    private static final String SKIN_DIR = "skin";
    private static final String SAMPLE_FILE = "sample.bds";

    private String path;
    private String filePath;
    private File skinFile;

    @Before
    public void init() {
        path = ResourceHelper.getResourcePath(getClass(), SKIN_DIR);
        filePath = path + File.separator + SAMPLE_FILE;

        skinFile = new File(path);
    }

    protected final boolean existsFile() {
        return skinFile.exists();
    }

    protected final boolean isDirectoryFile() {
        return skinFile.isDirectory();
    }

    protected final <T> List<T> read(BaseStrategy<T> strategy) {
        return new FileReading(strategy).traverse(path);
    }

    protected final <T> List<T> read(Class<? extends BaseStrategy<T>> clazz) {
        return new FileVisiting(clazz).traverse(path);
    }

    protected final String getDirectory() {
        return path;
    }

    protected final String getFilePath() {
        return filePath;
    }


    protected static final class NameVisitor extends BaseStrategy<String> {
        @Override
        public String parseFile(String filePath) {
            return new File(filePath).getName();
        }
    }

    protected static final class PathVisitor extends BaseStrategy<String> {
        @Override
        public String parseFile(String filePath) {
            return filePath;
        }
    }
}
