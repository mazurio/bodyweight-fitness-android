package com.bodyweight.fitness

import android.app.Application
import android.content.Context

import com.bodyweight.fitness.model.persistence.Glacier

import com.crashlytics.android.Crashlytics
import com.crashlytics.android.answers.Answers
import com.kobakei.ratethisapp.RateThisApp

import net.danlew.android.joda.JodaTimeAndroid

import io.fabric.sdk.android.Fabric

class App : Application() {
    companion object {
        @JvmStatic
        var context: Context? = null
    }

    override fun onCreate() {
        super.onCreate()

        Glacier.init(applicationContext)
        JodaTimeAndroid.init(applicationContext)

        val config = RateThisApp.Config(3, 5)
        RateThisApp.init(config)

        if (!BuildConfig.DEBUG) {
            Fabric.with(applicationContext, Crashlytics(), Answers())
        }

        context = applicationContext

        SchemaMigration().apply {
            migrateSchemaIfNeeded()
        }
    }
}
