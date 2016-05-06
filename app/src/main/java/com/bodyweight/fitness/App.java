package com.bodyweight.fitness;

import android.app.Application;
import android.content.Context;

import com.bodyweight.fitness.model.persistence.Glacier;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.answers.Answers;
import com.kobakei.ratethisapp.RateThisApp;

import net.danlew.android.joda.JodaTimeAndroid;

import io.fabric.sdk.android.Fabric;

public class App extends Application {
    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();

        Glacier.init(getApplicationContext());
        JodaTimeAndroid.init(getApplicationContext());

        RateThisApp.Config config = new RateThisApp.Config(3, 5);
        RateThisApp.init(config);

        if (!BuildConfig.DEBUG) {
            Fabric.with(
                    getApplicationContext(),
                    new Crashlytics(),
                    new Answers()
            );
        }

        mContext = getApplicationContext();

        SchemaMigration schemaMigration = new SchemaMigration();
        schemaMigration.migrateSchemaIfNeeded();
    }

    public static Context getContext() {
        return mContext;
    }
}
