
package com.doex.demo.md5;

import android.text.TextUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Utils {

    public static String getFileMD5(File file) {
        InputStream in = null;
        try {
            in = new FileInputStream(file);
            return getStreamMD5(in);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    public static String getStreamMD5(InputStream in) {
        try {
            byte[] buffer = new byte[in.available()];
            in.read(buffer);
            return getMD5(buffer);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getMD5(String content) {
        if (TextUtils.isEmpty(content)) {
            return null;
        }
        return getMD5(content.getBytes());
    }

    public static String getMD5(byte[] content) {
        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance("MD5");
            md5.update(content);
            byte[] m = md5.digest();
            return binaryToHexString(m);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String binaryToHexString(byte[] messageDigest) {
        if (messageDigest == null) {
            return null;
        }
        StringBuffer hexString = new StringBuffer();
        for (int i = 0; i < messageDigest.length; i++) {
            int by = 0xFF & messageDigest[i];
            if (by < 0x10) {
                hexString.append("0" + Integer.toHexString(by));
            } else if (by >= 0x10) {
                hexString.append(Integer.toHexString(by));
            }
        }
        return hexString.toString();
    }
}
