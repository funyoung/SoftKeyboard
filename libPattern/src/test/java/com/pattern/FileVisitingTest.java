package com.pattern;

import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class FileVisitingTest extends FileBaseTest {
    @Test
    public void readFilePathList() {
        List<String> pathList = read(PathVisitor.class);
        Assert.assertFalse(pathList.isEmpty());
    }

    @Test
    public void readFileNameList() {
        List<String> nameList = read(NameVisitor.class);
        Assert.assertFalse(nameList.isEmpty());
    }
}
