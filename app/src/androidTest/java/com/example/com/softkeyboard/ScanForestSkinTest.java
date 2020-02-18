package com.example.com.softkeyboard;

import android.support.test.runner.AndroidJUnit4;

import com.skin.ForestSkinParser;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
@RunWith(AndroidJUnit4.class)
public class ScanForestSkinTest {
    private static final String TEST_PATH = "/sdcard/baidu/ime/skins/";
    private static final String DUMB_FILE_NAME = "empty";

    @Before
    public void initCheck() {
        File folder = new File(TEST_PATH);

        if (folder.exists() && folder.isFile()) {
            folder.delete();
        }

        checkDir(folder);
    }

    private void checkDir(File folder) {
        if (!folder.exists()) {
            folder.mkdirs();
        }

        String[] children = folder.list();
        if (null != children) {
            for (String child : children) {
                File file = new File(folder, child);
                if (file.exists() && file.isFile()) {
                    return;
                }
            }
        }

        try {
            new File(folder, DUMB_FILE_NAME).createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void readSkinPathListTest() {
        List<String> fileList = ForestSkinParser.readSkinPathList(TEST_PATH);
        Assert.assertFalse(fileList.isEmpty());
    }

    @Test
    public void readSkinNameListTest() {
        List<String> nameList = ForestSkinParser.readSkinNameList(TEST_PATH);
        Assert.assertFalse(nameList.isEmpty());
    }
}