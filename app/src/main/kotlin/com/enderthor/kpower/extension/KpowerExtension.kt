package com.enderthor.kpower.extension

import io.hammerhead.karooext.KarooSystemService
import io.hammerhead.karooext.extension.KarooExtension
import io.hammerhead.karooext.internal.Emitter
import io.hammerhead.karooext.models.Device
import io.hammerhead.karooext.models.DeviceEvent

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

import com.enderthor.kpower.data.EstimatedPowerSource

import timber.log.Timber


class KpowerExtension : KarooExtension("kpower", "1.0-beta") {

    lateinit var karooSystem: KarooSystemService

    override fun onCreate() {
        super.onCreate()
        karooSystem = KarooSystemService(applicationContext)
        Timber.d("Service created")
    }

    override fun startScan(emitter: Emitter<Device>) {
        // Find estimated Power source
        val job = CoroutineScope(Dispatchers.IO).launch {
            delay(2000)
            Timber.d("Start scan")
            emitter.onNext(EstimatedPowerSource(extension, 2000, karooSystem, applicationContext).source)
        }
        emitter.setCancellable {
            job.cancel()
        }
    }

    override fun connectDevice(uid: String, emitter: Emitter<DeviceEvent>) {
        Timber.d("Connect Device")
        EstimatedPowerSource.Companion.fromUid(extension, uid, karooSystem,  applicationContext)?.connect(emitter)
    }

    override fun onDestroy() {
        karooSystem.disconnect()
        super.onDestroy()
    }
}