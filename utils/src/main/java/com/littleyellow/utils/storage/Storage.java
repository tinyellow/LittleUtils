package com.littleyellow.utils.storage;

import android.app.Activity;
import android.content.Context;
import android.os.Environment;
import android.os.StatFs;
import android.os.storage.StorageManager;
import android.text.TextUtils;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class Storage {

    public static List<StorageBean> getStorages(Context context){
        StorageManager mStorageManager = (StorageManager)context
                .getSystemService(Activity.STORAGE_SERVICE);
        try {
            Method mMethodGetPaths = mStorageManager.getClass()
                    .getMethod("getVolumePaths");
            String[] paths = (String[]) mMethodGetPaths.invoke(mStorageManager);
            List<StorageBean> reuslt = new ArrayList<>();
            String externalStoragePath = getExternalStoragePath();
            for(String s:paths){
                if (!TextUtils.isEmpty(s) && new File(s).exists()){
                    StatFs statFs = new StatFs(s);
                    if (statFs.getBlockCount() * statFs.getBlockSize() != 0){
                        StorageBean storage = new StorageBean();
                        storage.setInternal(s.equals(externalStoragePath));
                        storage.setAvailableBytes(DecimalUtil.getDirAvailaleSize(s));
                        storage.setTotalBytes(DecimalUtil.getDirSize(s));
                        reuslt.add(storage);
                    }
                }
            }
            return reuslt;
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public static String getExternalStoragePath(){
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)
                ||!Environment.isExternalStorageRemovable()){
            return Environment.getExternalStorageDirectory().getAbsolutePath();
        }else{
            return null;
        }
    }

}
