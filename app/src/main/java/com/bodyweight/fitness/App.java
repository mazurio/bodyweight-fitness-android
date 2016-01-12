package com.bodyweight.fitness;

import android.app.Application;
import android.content.Context;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.answers.Answers;

import net.danlew.android.joda.JodaTimeAndroid;

import io.mazur.glacier.Glacier;
import io.fabric.sdk.android.Fabric;

/**
 * TODO: Update the beginner routine JSON schema to provide unique IDs for categories, sections and exercises.
 * TODO: Update the logic to save sectionId for chosen level/exercise.
 * TODO: Update the realm logic to save those id's into the database as well.
 * TODO: Rename drawables to use the uniqueId's.
 *
 * TODO: Update all gif videos to match 16:9 format.
 * TODO: Update videos for Antranik new video.
 *
 * TODO BUG: ProgressActivity missing items sometimes.
 *
 * TODO: Move to Dagger 2 injection rather than using getInstance singletons.
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

        mContext = getApplicationContext();
    }

    public static Context getContext() {
        return mContext;
    }
}
