package com.bodyweight.fitness;

import android.app.Application;
import android.content.Context;

import com.bodyweight.fitness.model.persistence.Glacier;
import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.answers.Answers;
import com.liulishuo.filedownloader.FileDownloader;

import net.danlew.android.joda.JodaTimeAndroid;

import io.fabric.sdk.android.Fabric;

/**
 * TODO: Rename drawables to use the uniqueId's.
 * TODO: Update all gif videos to match 16:9 format.
 * TODO: Update videos for Antranik new video.
 * TODO: If your default routine does not exist - revert back.
 */
public class App extends Application {
    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();

        Glacier.init(getApplicationContext());
        JodaTimeAndroid.init(getApplicationContext());

        if (!BuildConfig.DEBUG) {
            Fabric.with(
                    getApplicationContext(),
                    new Crashlytics(),
                    new Answers()
            );
        }

//        FileDownloader.init(this);

        mContext = getApplicationContext();
    }

    public static Context getContext() {
        return mContext;
    }
}
