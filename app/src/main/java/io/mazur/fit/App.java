package io.mazur.fit;

import android.app.Application;
import android.content.Context;

import com.crashlytics.android.Crashlytics;
import net.danlew.android.joda.JodaTimeAndroid;

import io.mazur.glacier.Glacier;
import io.fabric.sdk.android.Fabric;

/**
 * Done: Connect HomeUI Dialog to Realm.
 * TODO: Delete Workouts.
 *
 * TODO: Calculate length of workout and set times, e.g. firstTime when created and endTime when lastLogged.
 * TODO: Length calculated based on below values \/.
 * TODO: StartTime can be changed manually.
 * TODO: EndTime can be changed manually.
 * TODO: EndTime cannot be before StartTime.
 */
public class App extends Application {
    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();

        Glacier.init(getApplicationContext());
        JodaTimeAndroid.init(getApplicationContext());

        if(!BuildConfig.DEBUG) {
            Fabric.with(this, new Crashlytics());
        }

        mContext = getApplicationContext();
    }

    public static Context getContext() {
        return mContext;
    }
}
