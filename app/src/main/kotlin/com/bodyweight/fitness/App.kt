package com.bodyweight.fitness

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import com.bodyweight.fitness.repository.SchemaMigration

import com.crashlytics.android.Crashlytics
import com.crashlytics.android.answers.Answers
import com.kobakei.ratethisapp.RateThisApp

import net.danlew.android.joda.JodaTimeAndroid

import io.fabric.sdk.android.Fabric
import io.realm.Realm


class App : Application() {
  companion object {
    @JvmStatic
    var context: Context? = null
  }

  override fun onCreate() {
    super.onCreate()

    JodaTimeAndroid.init(applicationContext)

    val config = RateThisApp.Config(2, 7)
    RateThisApp.init(config)

    if (!BuildConfig.DEBUG) {
      Fabric.with(applicationContext, Crashlytics(), Answers())
    }

    context = applicationContext

    SchemaMigration().apply {
      migrateSchemaIfNeeded()
    }
  }

  override fun onTerminate() {
    Realm.getDefaultInstance().close()
    super.onTerminate()
  }
}
