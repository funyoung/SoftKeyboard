package com.pattern;

public abstract class BaseStrategy<U> {
    public abstract U parseFile(String filePath);
}
