package com.pattern.util;

import com.pattern.FileBaseTest;
import com.pattern.common.FileTestConstant;

import org.junit.Test;

import static org.junit.Assert.*;

import static com.pattern.common.FileTestConstant.*;

public class FileUtilTest extends FileBaseTest {

    @Test
    public void isFileTest() {
        assertFalse(FileUtil.isFile(null));
        assertFalse(FileUtil.isFile(SPACE));
        assertFalse(FileUtil.isFile(PARENT));

        assertFalse(FileUtil.isFile(getDirectory()));
        assertTrue(FileUtil.isFile(getFilePath()));
    }

    @Test
    public void listTest() {
        assertNull(FileUtil.list(getFilePath()));
        assertNotNull(FileUtil.list(getDirectory()));
    }

    @Test
    public void getSubPathTest() {
        assertNull(FileUtil.getSubPath(null, null));
        assertNull(FileUtil.getSubPath(null, SUB_PATH));
        assertNull(FileUtil.getSubPath(PARENT, null));

        assertNotNull(FileUtil.getSubPath(ROOT_PATH, SUB_PATH));
        assertNotNull(FileUtil.getSubPath(PARENT, SUB_PATH));
    }

    @Test
    public void isEmptyTest() {
        assertTrue("null is empty string.", FileUtil.isEmpty(null));
        assertTrue("\"\" is empty string.", FileUtil.isEmpty(EMPTY));
        assertFalse("Space text is NOT empty string.", FileUtil.isEmpty(SPACE));
    }
}