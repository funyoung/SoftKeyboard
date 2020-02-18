package com.pattern;

/**
 * Traverse file recursively and parse file with strategy.
 *
 * @author funyoung
 *
 * @param <U> template type of parsing result list.
 */
public class FileReading<U> extends BaseTraverse<U> {
    private final BaseStrategy strategy;
    public FileReading(BaseStrategy strategy) {
        this.strategy = strategy;
    }
    @Override
    protected BaseStrategy<U> getVisitStrategy() {
        return strategy;
    }
}
