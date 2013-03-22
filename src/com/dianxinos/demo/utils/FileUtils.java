
package com.dianxinos.demo.utils;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;

import com.google.common.io.Files;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class FileUtils {

    private static final String ROOT_CACHE_FOLDER = "DX-Dialer";

    public static final String TAG_ROOT_CACHE_FOLDER = "tag_patch";

    public static String getAssetFileNameWithPrefix(Context context, String prefix) {
        String[] fileNameArray = null;
        try {
            fileNameArray = context.getAssets().list("");
            return getFullFileNameWithPrefix(fileNameArray, prefix);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getAssetFileContent(Context context, String fileName) {
        if (TextUtils.isEmpty(fileName)) {
            return null;
        }
        InputStream in = null;
        try {
            in = getAssetFileStreamWithPrefix(context, fileName);
            return getFileContent(context, in);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static String getFileContent(Context context, String filePath) {
        if (TextUtils.isEmpty(filePath)) {
            return null;
        }
        InputStream in = null;
        try {
            in = new FileInputStream(filePath);
            return getFileContent(context, in);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static String getFileContent(Context context, InputStream in) {
        if (in == null) {
            return null;
        }
        byte[] buffer = null;
        try {
            buffer = new byte[in.available()];
            while (in.read(buffer) != -1)
                ;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return new String(buffer);
    }

    public static String getFullFileNameWithPrefix(String path, String prefix) {
        if (TextUtils.isEmpty(path) || TextUtils.isEmpty(prefix)) {
            return null;
        }
        File dir = new File(path);
        return getFullFileNameWithPrefix(dir.list(), prefix);
    }

    private static String getFullFileNameWithPrefix(String[] files, String prefix) {
        if (files == null) {
            return null;
        }

        for (int i = 0; i < files.length; ++i) {
            String fileName = files[i];
            if (fileName.startsWith(prefix)) {
                return fileName;
            }
        }

        return null;
    }

    public static void initDirs(String dir) {
        if (TextUtils.isEmpty(dir)) {
            return;
        }
        File file = new File(dir);
        if (file.exists() && file.isDirectory()) {
            for (File f : file.listFiles()) {
                f.delete();
            }
        } else {
            file.mkdir();
        }
    }

    public static void createDirs(String dir) {
        if (TextUtils.isEmpty(dir)) {
            return;
        }
        File file = new File(dir);
        if (!file.exists() || !file.isDirectory()) {
            file.mkdirs();
        }
    }

    public static String getSDCardPath() {
        return fillPath(Environment.getExternalStorageDirectory().getPath(), ROOT_CACHE_FOLDER);
    }

    public static String getSDCardPath(String dir) {
        return fillPath(getSDCardPath(), dir);
    }

    public static String getLocalPath(Context context) {
        return context.getFilesDir().getPath();
    }

    public static String getLocalPath(Context context, String dir) {
        return fillPath(getLocalPath(context), dir);
    }

    public static InputStream getAssetFileStreamWithPrefix(Context context, String prefix) {
        String[] files;
        try {
            files = context.getAssets().list("");
            for (int i = 0; i < files.length; i++) {
                if (files[i].startsWith(prefix)) {
                    return context.getAssets().open(files[i]);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void delete(String file) {
        if (!TextUtils.isEmpty(file)) {
            File delFile = new File(file);
            if (delFile.exists()) {
                delFile.delete();
            }
        }
    }

    public static void move(File from, File to) {
        try {
            Files.move(from, to);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void copy(InputStream in, File out) {
        FileOutputStream o = null;
        try {
            o = new FileOutputStream(out);
            byte[] buffer = new byte[in.available()];
            o.write(buffer);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (o != null) {
                try {
                    o.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static String fillPath(String path, String name) {
        return path + File.separator + name;
    }

}
