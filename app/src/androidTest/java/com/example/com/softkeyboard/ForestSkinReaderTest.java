package com.example.com.softkeyboard;

import android.support.test.runner.AndroidJUnit4;

import com.skin.ForestSkinReader;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
@RunWith(AndroidJUnit4.class)
public class ForestSkinReaderTest extends AbstractForestSkinTest {
    @Test
    public void readSkinPathListTest() {
        List<String> fileList = ForestSkinReader.readSkinPathList(TEST_PATH);
        Assert.assertFalse(fileList.isEmpty());
    }

    @Test
    public void readSkinNameListTest() {
        List<String> nameList = ForestSkinReader.readSkinNameList(TEST_PATH);
        Assert.assertFalse(nameList.isEmpty());
    }
}