package com.pattern.strategy;

import com.pattern.BaseStrategy;

public final class ParseFilePathStrategy extends BaseStrategy<String> {
    @Override
    public String parseFile(String filePath) {
        return filePath;
    }
}
