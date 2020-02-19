package com.pattern;

import com.pattern.strategy.ParseFileNameStrategy;
import com.pattern.strategy.ParseFilePathStrategy;

import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class FileVisitingTest extends FileBaseTest {
    @Test
    public void readFilePathList() {
        List<String> pathList = read(ParseFilePathStrategy.class);
        Assert.assertFalse(pathList.isEmpty());
    }

    @Test
    public void readFileNameList() {
        List<String> nameList = read(ParseFileNameStrategy.class);
        Assert.assertFalse(nameList.isEmpty());
    }
}
