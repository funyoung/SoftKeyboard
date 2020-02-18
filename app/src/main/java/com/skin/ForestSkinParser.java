package com.skin;

import com.pattern.FileTraversing;

import java.io.File;
import java.util.List;

public class ForestSkinParser {

    public static List<String> readSkinPathList(String path) {
        FileTraversing scanner = new FileTraversing(SkinPathVisitor.class);
        return scanner.traverse(path);
    }

    public static List<String> readSkinNameList(String path) {
        FileTraversing scanner = new FileTraversing(SkinNameVisitor.class);
        return scanner.traverse(path);
    }

    private static final class SkinNameVisitor extends FileTraversing.AbstractVisitor {
        public SkinNameVisitor(File folder, String name) {
            super(folder, name);
        }

        @Override
        protected String visitFile(String subPath) {
            return new File(subPath).getName();
        }

        @Override
        public List<String> visitFolder(String folderPath) {
            return readSkinNameList(folderPath);
        }
    }

    private static final class SkinPathVisitor extends FileTraversing.AbstractVisitor {
        public SkinPathVisitor(File folder, String name) {
            super(folder, name);
        }

        @Override
        protected String visitFile(String subPath) {
            return subPath;
        }

        @Override
        public List<String> visitFolder(String folderPath) {
            return readSkinPathList(folderPath);
        }
    }
}
