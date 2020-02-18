package com.example.com.softkeyboard;

import org.junit.Before;

import java.io.File;
import java.io.IOException;

public abstract class AbstractForestSkinTest {
    protected static final String TEST_PATH = "/sdcard/baidu/ime/skins/";
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
}
