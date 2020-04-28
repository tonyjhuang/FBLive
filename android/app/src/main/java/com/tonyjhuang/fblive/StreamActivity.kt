package com.tonyjhuang.fblive

import android.Manifest
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.view.WindowManager
import android.widget.Chronometer
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.streamaxia.android.CameraPreview
import com.tonyjhuang.fblive.core.SimplePermissionsChecker
import java.lang.ref.WeakReference

class StreamActivity : AppCompatActivity() {

    private lateinit var cameraView: CameraPreview
    private lateinit var chronometer: Chronometer
    private lateinit var streamButton: TextView
    private lateinit var stateTextView: TextView

    private val publisher = LifecycleAwareStreamPublisher(
        WeakReference(this),
        StreamListener()
    )

    private var isStreaming = false
    set (value) {
        field = value
        if (value) {
            streamButton.text = "STOP"
            startChronometer()
        } else {
            streamButton.text = "START"
            stopChronometer()
        }
    }

    private val simplePermissionsChecker = SimplePermissionsChecker(WeakReference(this))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        setContentView(R.layout.activity_stream)
        setUpViews()

        publisher.bindTo(cameraView)
        lifecycle.addObserver(publisher)
        isStreaming = false
    }

    private fun setUpViews() {
        cameraView = findViewById(R.id.preview)
        chronometer = findViewById(R.id.chronometer)
        streamButton = findViewById<TextView>(R.id.start_stop).apply {
            setOnClickListener { onStreamButtonClicked() }
        }
        stateTextView = findViewById(R.id.state_text)
    }

    private fun getPermissions() {
        simplePermissionsChecker.getPermissions(
            REQUIRED_PERMISSIONS,
            { /* intentionally left blank */ },
            {
                showToast("Please grant permissions")
                finish()
            }
        )
    }

    override fun onStart() {
        super.onStart()
        if (simplePermissionsChecker.hasPermissions(REQUIRED_PERMISSIONS)) {
            cameraView.startCamera()
            stopChronometer()
        } else {
            getPermissions()
        }
    }

    override fun onStop() {
        super.onStop()
        cameraView.stopCamera()
        isStreaming = false
    }

    private fun onStreamButtonClicked() {
        if (!isStreaming) {
            publisher.startPublish(DEFAULT_STREAM)
        } else {
            publisher.stopPublish()
        }
    }

    private fun setStatusMessage(msg: String) {
        runOnUiThread { stateTextView.text = "[$msg]" }
    }

    private fun showToast(msg: String) {
        runOnUiThread { Toast.makeText(this, msg, Toast.LENGTH_SHORT).show() }
    }

    private fun startChronometer() {
        chronometer.base = SystemClock.elapsedRealtime()
        chronometer.start()
    }

    private fun stopChronometer() {
        chronometer.base = SystemClock.elapsedRealtime()
        chronometer.stop()
    }

    inner class StreamListener : LifecycleAwareStreamPublisher.Listener() {

        override fun onConnecting(s: String) {
            setStatusMessage("CONNECTING")
            isStreaming = false
        }

        override fun onConnected(s: String) {
            setStatusMessage("CONNECTED")
            isStreaming = true
        }

        override fun onStopped() {
            setStatusMessage("STOPPED")
            isStreaming = false
        }

        override fun onDisconnected() {
            setStatusMessage("Disconnected")
            isStreaming = false
        }

        override fun onException(e: Exception) {
            Log.w("---FBL---", e)
            isStreaming = false
            showToast(e.message ?: return)
        }
    }

    companion object {
        const val DEFAULT_STREAM_ID = "95c77aff-cfc8-0eb0-91ad-79f9218a2276"
        const val DEFAULT_STREAM = "rtmp://global-live.mux.com:5222/app/$DEFAULT_STREAM_ID"
        val REQUIRED_PERMISSIONS =
            listOf(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO)
    }
}