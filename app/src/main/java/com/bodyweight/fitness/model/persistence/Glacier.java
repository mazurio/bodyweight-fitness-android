package com.bodyweight.fitness.model.persistence;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.bodyweight.fitness.model.persistence.file.FileObjectPersister;

import java.io.File;
import java.util.HashMap;

import rx.Observable;
import rx.subjects.PublishSubject;

public class Glacier {
    public interface Callback<T> {
        T onCacheNotFound();
    }

    private static FileObjectPersister mFileObjectPersister;

    private static HashMap<String, PublishSubject> hashMap = new HashMap<>();

    public synchronized static void init() {
        mFileObjectPersister = new FileObjectPersister();

        try {
            mFileObjectPersister.setCacheDirectory(new File("cache"));
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }
    }

    public synchronized static void init(@NonNull Context context) {
        mFileObjectPersister = new FileObjectPersister();

        try {
            mFileObjectPersister.setCacheDirectory(context);
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }
    }

    public synchronized static void removeAllDataFromCache() {
        mFileObjectPersister.removeAllDataFromCache();
    }

    public synchronized static <T> Observable<T> getObservable(String cacheKey, Class<T> dataType) {
        if(!hashMap.containsKey(cacheKey)) {
            PublishSubject<T> publishSubject = PublishSubject.create();

            hashMap.put(cacheKey, publishSubject);
        }

        return hashMap.get(cacheKey);
    }

    /**
     * Put object into cache with given cache key used later to retrieve it.
     *
     * @param cacheKey cache key in format [a-z0-9].
     * @param data data type, e.g. String.class.
     */
    public synchronized static <T> void put(@NonNull String cacheKey, @NonNull T data) {
        try {
            mFileObjectPersister.putDataInCache(cacheKey, data);

            if(hashMap.containsKey(cacheKey)) {
                hashMap.get(cacheKey).onNext(data);
            }
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }
    }

    @Nullable
    public synchronized static <T> T get(@NonNull String cacheKey, @NonNull Class<T> dataType) {
        return (T) mFileObjectPersister.getDataFromCache(cacheKey, dataType, Duration.ALWAYS_RETURNED);
    }

    @Nullable
    public synchronized static <T> T get(@NonNull String cacheKey, @NonNull Class<T> dataType, @NonNull long cacheDuration) {
        return (T) mFileObjectPersister.getDataFromCache(cacheKey, dataType, cacheDuration);
    }

    @NonNull
    public synchronized static <T> T getOrElse(@NonNull String cacheKey, @NonNull Class<T> dataType,
                                               @NonNull long cacheDuration, @NonNull Callback<T> callback) {
        T cacheObject = (T) mFileObjectPersister.getDataFromCache(cacheKey, dataType, cacheDuration);

        if(cacheObject == null) {
            cacheObject = callback.onCacheNotFound();

            put(cacheKey, cacheObject);
        }

        return cacheObject;
    }

    @NonNull
    public synchronized static <T> T getOrElse(@NonNull String cacheKey, @NonNull Class<T> dataType,
                                               @NonNull Callback<T> callback) {
        T cacheObject = (T) mFileObjectPersister.getDataFromCache(cacheKey, dataType, Duration.ALWAYS_RETURNED);

        if(cacheObject == null) {
            cacheObject = callback.onCacheNotFound();

            put(cacheKey, cacheObject);
        }

        return cacheObject;
    }
}