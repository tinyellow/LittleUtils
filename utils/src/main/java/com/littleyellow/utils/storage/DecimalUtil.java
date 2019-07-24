package com.littleyellow.utils.storage;

import java.io.File;
import java.util.Locale;

public class DecimalUtil {

    /**
     * Byte与Byte的倍数
     */
    public static final int BYTE = 1;
    /**
     * KB与Byte的倍数
     */
    public static final int KB = 1024;
    /**
     * MB与Byte的倍数
     */
    public static final int MB = 1048576;
    /**
     * GB与Byte的倍数
     */
    public static final int GB = 1073741824;
    /**
     * 字节数转合适大小
     * <p>保留3位小数</p>
     *
     * @param byteNum 字节数
     * @return 1...1024 unit
     */
    public static String byte2FitSize(long byteNum) {
        if (byteNum < 0) {
            return "shouldn't be less than zero!";
        } else if (byteNum < KB) {
            return String.format(Locale.getDefault(), "%.1fB", (double) byteNum);
        } else if (byteNum < MB) {
            return String.format(Locale.getDefault(), "%.1fKB", (double) byteNum / KB);
        } else if (byteNum < GB) {
            return String.format(Locale.getDefault(), "%.1fMB", (double) byteNum / MB);
        } else {
            return String.format(Locale.getDefault(), "%.1fGB", (double) byteNum / GB);
        }
    }

    /**
     * 获取某个目录总大小.
     */
    public static long getDirSize(String path) {
        try {
            return new File(path).getTotalSpace();
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 获取某个目录可用空间.
     */
    public static long getDirAvailaleSize(String path) {
        try {
            return new File(path).getUsableSpace();
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
}
