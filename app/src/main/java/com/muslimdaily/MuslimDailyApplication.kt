package com.muslimdaily

import android.app.Application
import com.muslimdaily.domain.prayer.PrayerNotificationWorker

class MuslimDailyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        PrayerNotificationWorker.createChannel(this)
    }
}
