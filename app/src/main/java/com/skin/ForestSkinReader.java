package com.skin;

import com.pattern.FileReading;
import com.pattern.FileReading.BaseStrategy;

import java.io.File;
import java.util.List;

public class ForestSkinReader {
    public static List<String> readSkinPathList(String path) {
        FileReading reader = new FileReading(new SkinPathStrategy());
        return reader.read(path);
    }

    public static List<String> readSkinNameList(String path) {
        FileReading reader = new FileReading(new SkinNameStrategy());
        return reader.read(path);
    }

    private static class SkinPathStrategy extends BaseStrategy<String> {
        @Override
        public String readFile(String subPath) {
            return subPath;
        }
    }

    private static class SkinNameStrategy extends BaseStrategy<String> {
        @Override
        public String readFile(String subPath) {
            return new File(subPath).getName();
        }
    }
}
