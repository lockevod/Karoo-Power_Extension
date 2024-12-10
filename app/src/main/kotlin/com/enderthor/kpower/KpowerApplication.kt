package com.enderthor.kpower

import android.app.Application
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import com.enderthor.kremote.utils.FileLoggingTree
import timber.log.Timber
import android.os.Environment
import java.io.File

class KpowerApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            if (!Environment.isExternalStorageManager()) {
                val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                val uri = Uri.fromParts("package", this.packageName, null)
                intent.data = uri
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
            }

            val dir = File("//sdcard//")
            val logfile = File(dir, "powerlog.txt")
            Timber.plant(FileLoggingTree(logfile))

         } else {
            Timber.plant(Timber.DebugTree())
        }

        Timber.d("Starting KPower App")
    }
}
