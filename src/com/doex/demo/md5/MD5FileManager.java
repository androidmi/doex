
package com.doex.demo.md5;

import android.os.Environment;
import android.text.TextUtils;

import com.doex.demo.utils.FileUtils;

import java.io.File;

public class MD5FileManager {

    private static final String SDCARD_PATH = Environment.getExternalStorageDirectory().getPath();
    public static final String DATA_PATH = FileUtils.fillPath(SDCARD_PATH, "dianxinDemo");

    public static String getSDCardFile(String prefix) throws MD5VerifyException {
        String fileName = FileUtils.getFullFileNameWithPrefix(DATA_PATH, prefix);

        String md5 = getFileMD5(prefix, fileName);

        String filePath = FileUtils.fillPath(DATA_PATH, fileName);
        File file = new File(filePath);

        String fileMd5 = MD5Utils.getFileMD5(file);
        if (TextUtils.equals(md5, fileMd5)) {
            return filePath;
        } else {
            throw new MD5VerifyException("file has changed");
        }
    }

    public static String generateFileMD5(String prefix, File file) {
        if (file == null) {
            throw new IllegalArgumentException("file is null");
        }
        return prefix + MD5Utils.getFileMD5(file);
    }

    private static String getFileMD5(String prefix, String fileName) {
        return fileName.substring(prefix.length());
    }
}
