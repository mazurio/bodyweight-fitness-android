package com.bodyweight.fitness.persistence.file;

import android.content.Context;
import android.support.annotation.NonNull;

import com.bodyweight.fitness.exception.CacheDirectoryCreationException;
import com.bodyweight.fitness.persistence.Duration;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class FileObjectPersister {
    private static final String CACHE_DIRECTORY = "glacier-cache";

    private File mCacheDirectory;

    public synchronized void setCacheDirectory(@NonNull Context context) throws CacheDirectoryCreationException {
        if(mCacheDirectory == null) {
            mCacheDirectory = new File(context.getCacheDir(), CACHE_DIRECTORY);
        }

        if(!mCacheDirectory.exists() && !mCacheDirectory.mkdirs()) {
            throw new CacheDirectoryCreationException("Cache Directory could not be created.");
        }
    }

    public synchronized void setCacheDirectory(@NonNull File baseCacheDirectory) throws CacheDirectoryCreationException {
        if(mCacheDirectory == null) {
            mCacheDirectory = new File(baseCacheDirectory, CACHE_DIRECTORY);
        }

        if(!mCacheDirectory.exists() && !mCacheDirectory.mkdirs()) {
            throw new CacheDirectoryCreationException("Cache Directory could not be created.");
        }
    }

    public File getCacheDirectory() {
        return mCacheDirectory;
    }

    public synchronized <T> boolean putDataInCache(@NonNull String cacheKey, @NonNull T data) throws IOException {
        if(cacheKey == null || data == null) {
            return false;
        }

        try {
            FileOutputStream fileOutputStream = new FileOutputStream(new File(mCacheDirectory,
                    createFileName(cacheKey, data.getClass())));

            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);

            objectOutputStream.writeObject(data);
            objectOutputStream.close();

            return true;
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }

        return false;
    }

    public synchronized <T> Object getDataFromCache(@NonNull String cacheKey,
                                                    @NonNull Class<T> dataType, long duration) {
        T object = null;

        try {
            File file = new File(mCacheDirectory, createFileName(cacheKey, dataType));

            if(!file.exists() || !isCacheValid(file.lastModified(), duration)) {
                return null;
            }

            FileInputStream fileInputStream = new FileInputStream(file);

            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);

            object = (T) objectInputStream.readObject();

            objectInputStream.close();
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }

        return object;

    }

    public <T> String createFileName(@NonNull String cacheKey, @NonNull Class<T> dataType) {
        return "Class." + dataType.getName() + ".With.Key." + (cacheKey.hashCode());
    }

    public boolean isCacheValid(@NonNull long fileLastModified, @NonNull long duration) {
        long timeInCache = System.currentTimeMillis() - fileLastModified;

        return (duration == Duration.ALWAYS_RETURNED || timeInCache <= duration);
    }

    public synchronized void removeAllDataFromCache() {
        try {
            FileUtils.cleanDirectory(mCacheDirectory);
        } catch (IOException e) {
            System.out.println("Exception: " + e.getMessage());
        }
    }
}