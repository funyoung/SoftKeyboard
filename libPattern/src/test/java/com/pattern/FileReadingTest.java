package com.pattern;

import com.pattern.strategy.ParseFileNameStrategy;
import com.pattern.strategy.ParseFilePathStrategy;

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class FileReadingTest extends FileBaseTest {
    @Test
    public void existFileTest() {
        assertTrue(existsFile());
    }

    @Test
    public void isDirectoryFileTest() {
        assertTrue(isDirectoryFile());
    }

    @Test
    public void readFilePathListTest() {
        List<String> pathList = read(new ParseFilePathStrategy());
        assertFalse(pathList.isEmpty());
    }

    @Test
    public void readFileNameListTest() {
        List<String> nameList = read(new ParseFileNameStrategy());
        assertFalse(nameList.isEmpty());
    }
}