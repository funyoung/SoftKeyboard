package com.example.com.softkeyboard;

import org.junit.Before;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public abstract class AbstractForestSkinTest {
    protected static final String TEST_PATH = "/sdcard/baidu/ime/skins/tmp";
    private static final String DUMB_FILE_NAME = "sample.bds";

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

        try (InputStream stream = getClass().getClassLoader().getResourceAsStream(DUMB_FILE_NAME)) {
            copyFile(stream, new File(folder, DUMB_FILE_NAME));
        } catch (IOException ex) {

        }
    }

    private static void copyFile(InputStream is, File dstFile) throws IOException {
        try (OutputStream os = new FileOutputStream(dstFile)) {
            byte[] buffer = new byte[1024];
            int length;
            while ((length = is.read(buffer)) > 0) {
                os.write(buffer, 0, length);
            }
        }
    }
}
