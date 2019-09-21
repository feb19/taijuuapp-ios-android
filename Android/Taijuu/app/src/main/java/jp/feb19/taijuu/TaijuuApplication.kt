package jp.feb19.taijuu

import android.app.Activity
import android.app.Application
import android.content.ComponentCallbacks2
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.app.AlarmManager
import android.app.PendingIntent
import java.util.*


class TaijuuApplication: Application(), Application.ActivityLifecycleCallbacks {
    var isNeedPasscodeConfirmation = true

    companion object {
        lateinit var shared: TaijuuApplication
            private set
    }
    init {
        shared = this
    }

    override fun onCreate() {
        super.onCreate()
        registerActivityLifecycleCallbacks(this)
        Log.i("TaijuuApp", "onCreate")
    }

    override fun onTrimMemory(level: Int) {
        super.onTrimMemory(level)
        Log.i("TaijuuApp", level.toString())
        if (level == ComponentCallbacks2.TRIM_MEMORY_UI_HIDDEN) {
            isNeedPasscodeConfirmation = true
        }
    }

    override fun onActivityStarted(activity: Activity?) {
        Log.i("TaijuuApp", "onActivityStarted")

        val sharedPreferences = getSharedPreferences("userData", Context.MODE_PRIVATE)

        val passcode = sharedPreferences.getString("passcode", "")
        Log.i("TaijuuApp", "${passcode }")
        if (isNeedPasscodeConfirmation && passcode != "") {
            val intent = Intent(this, PasscodeActivity::class.java)
            activity!!.startActivity(intent)
        }
        isNeedPasscodeConfirmation = false

    }

    override fun onActivityPaused(activity: Activity?) {
    }

    override fun onActivityResumed(activity: Activity?) {
    }

    override fun onActivityDestroyed(activity: Activity?) {
    }

    override fun onActivitySaveInstanceState(activity: Activity?, outState: Bundle?) {
    }

    override fun onActivityStopped(activity: Activity?) {
    }

    override fun onActivityCreated(activity: Activity?, savedInstanceState: Bundle?) {
    }

    override fun onTerminate() {
        super.onTerminate()
        unregisterActivityLifecycleCallbacks(this)
    }


}