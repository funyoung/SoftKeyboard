package com.pattern.strategy;

import com.pattern.BaseStrategy;

import java.io.File;

public final class ParseFileNameStrategy extends BaseStrategy<String> {
    @Override
    public String parseFile(String filePath) {
        return new File(filePath).getName();
    }
}
