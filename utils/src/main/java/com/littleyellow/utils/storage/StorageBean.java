package com.littleyellow.utils.storage;

import android.content.Context;
import android.os.Build;

/**
 * Created by 小黄 on 2017/11/6.
 */

public class StorageBean {

    private String path;

    private long totalBytes;

    private long availableBytes;

    private boolean isInternal;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public long getTotalBytes() {
        return totalBytes;
    }

    public void setTotalBytes(long totalBytes) {
        this.totalBytes = totalBytes;
    }

    public long getAvailableBytes() {
        return availableBytes;
    }

    public void setAvailableBytes(long availableBytes) {
        this.availableBytes = availableBytes;
    }

    public boolean isInternal() {
        return isInternal;
    }

    public void setInternal(boolean internal) {
        isInternal = internal;
    }

    public String getTotalText() {
        return DecimalUtil.byte2FitSize(totalBytes);
    }

    public String getAvailableText() {
        return DecimalUtil.byte2FitSize(availableBytes);
    }

    public String getActionPath(Context context,Context directoryName){
        String result;
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT){
            result = String.format("%s/%s",path,directoryName);
        }else{
            result = String.format("%s/Android/data/%s/%s"+path,context.getApplicationContext().getPackageName(),directoryName);
        }
        return result;
    }
}
