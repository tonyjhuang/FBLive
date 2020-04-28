package com.tonyjhuang.fblive

import android.Manifest
import android.content.res.Configuration
import android.os.Bundle
import android.os.Handler
import android.os.SystemClock
import android.view.View
import android.view.WindowManager
import android.widget.Chronometer
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.streamaxia.android.CameraPreview
import com.streamaxia.android.StreamaxiaPublisher
import com.streamaxia.android.handlers.EncoderHandler
import com.streamaxia.android.handlers.EncoderHandler.EncodeListener
import com.streamaxia.android.handlers.RecordHandler
import com.streamaxia.android.handlers.RecordHandler.RecordListener
import com.streamaxia.android.handlers.RtmpHandler
import com.streamaxia.android.handlers.RtmpHandler.RtmpListener
import com.tonyjhuang.fblive.core.SimplePermissionsChecker
import java.io.IOException
import java.lang.ref.WeakReference
import java.net.SocketException

class StreamActivity : AppCompatActivity() {

    private lateinit var cameraView: CameraPreview
    private lateinit var chronometer: Chronometer
    private lateinit var startStopTextView: TextView
    private lateinit var stateTextView: TextView
    private lateinit var mPublisher: StreamaxiaPublisher

    private val rtmpListener: RtmpListener = FBRtmpListener()
    private val recordListener: RecordListener = FBRecordListener()
    private val encodeListener: EncodeListener = FBEncodeListener()

    private val simplePermissionsChecker = SimplePermissionsChecker(WeakReference(this))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        setContentView(R.layout.activity_stream)
        setUpViews()

        hideStatusBar()

        mPublisher = StreamaxiaPublisher(cameraView, this)
        mPublisher.setEncoderHandler(EncoderHandler(encodeListener))
        mPublisher.setRtmpHandler(RtmpHandler(rtmpListener))
        mPublisher.setRecordEventHandler(RecordHandler(recordListener))
    }

    private fun setUpViews() {
        cameraView = findViewById(R.id.preview)
        chronometer = findViewById(R.id.chronometer)
        startStopTextView = findViewById<TextView>(R.id.start_stop).apply {
            setOnClickListener { startStopStream() }
        }
        stateTextView = findViewById(R.id.state_text)
    }

    override fun onResume() {
        super.onResume()
        if (simplePermissionsChecker.hasPermissions(REQUIRED_PERMISSIONS)) {
            cameraView.startCamera()
            setStreamerDefaultValues()
            stopStreaming()
            stopChronometer()
            startStopTextView.text = "START"
        } else {
            getPermissions()
        }
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

    override fun onRestart() {
        super.onRestart()
        cameraView.startCamera()
    }

    override fun onPause() {
        super.onPause()
        cameraView.stopCamera()
        mPublisher.stopPublish()
        mPublisher.pauseRecord()
    }

    override fun onDestroy() {
        super.onDestroy()
        mPublisher.stopPublish()
        mPublisher.stopRecord()
    }

    fun startStopStream() {
        if (startStopTextView.text.toString().toLowerCase() == "start") {
            startStopTextView.text = "STOP"
            chronometer.base = SystemClock.elapsedRealtime()
            chronometer.start()
            mPublisher.startPublish(DEFAULT_STREAM)
            takeSnapshot()
        } else {
            startStopTextView.text = "START"
            stopChronometer()
            mPublisher.stopPublish()
        }
    }

    private fun stopStreaming() {
        mPublisher.stopPublish()
    }

    private fun takeSnapshot() {
        val handler = Handler()
        handler.postDelayed({
            cameraView.takeSnapshot {
                //Do something with the snapshot
            }
        }, 5000)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        mPublisher.setScreenOrientation(newConfig.orientation)
    }

    private fun hideStatusBar() {
        val decorView = window.decorView
        // Hide the status bar.
        val uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN
        decorView.systemUiVisibility = uiOptions
    }

    private fun setStreamerDefaultValues() { // Set one of the available resolutions
        val sizes =
            mPublisher.getSupportedPictureSizes(resources.configuration.orientation)
        val resolution = sizes[0]
        mPublisher.setVideoOutputResolution(
            resolution.width,
            resolution.height,
            this.resources.configuration.orientation
        )
    }

    private fun setStatusMessage(msg: String) {
        runOnUiThread { stateTextView.text = "[$msg]" }
    }

    private fun showToast(msg: String) {
        runOnUiThread { Toast.makeText(this, msg, Toast.LENGTH_SHORT).show() }
    }

    private fun stopChronometer() {
        chronometer.base = SystemClock.elapsedRealtime()
        chronometer.stop()
    }

    private fun handleException(e: Exception) {
        try {
            Toast.makeText(applicationContext, e.message, Toast.LENGTH_SHORT).show()
            mPublisher.stopPublish()
        } catch (e1: Exception) { // Ignore
        }
    }

    inner class FBEncodeListener : EncodeListener {
        override fun onNetworkWeak() {}
        override fun onNetworkResume() {}
        override fun onEncodeIllegalArgumentException(e: IllegalArgumentException) {
            handleException(e)
        }
    }

    inner class FBRecordListener : RecordListener {
        override fun onRecordPause() {}
        override fun onRecordResume() {}
        override fun onRecordStarted(s: String) {}
        override fun onRecordFinished(s: String) {}
        override fun onRecordIllegalArgumentException(e: IllegalArgumentException) {
            handleException(e)
        }

        override fun onRecordIOException(e: IOException) {
            handleException(e)
        }
    }

    inner class FBRtmpListener : RtmpListener {

        override fun onRtmpConnecting(s: String) {
            setStatusMessage(s)
        }

        override fun onRtmpConnected(s: String) {
            setStatusMessage(s)
            startStopTextView.text = "STOP"
        }

        override fun onRtmpVideoStreaming() {}
        override fun onRtmpAudioStreaming() {}
        override fun onRtmpStopped() {
            setStatusMessage("STOPPED")
        }

        override fun onRtmpDisconnected() {
            setStatusMessage("Disconnected")
        }

        override fun onRtmpVideoFpsChanged(v: Double) {}
        override fun onRtmpVideoBitrateChanged(v: Double) {}
        override fun onRtmpAudioBitrateChanged(v: Double) {}
        override fun onRtmpSocketException(e: SocketException) {
            handleException(e)
        }

        override fun onRtmpIOException(e: IOException) {
            handleException(e)
        }

        override fun onRtmpIllegalArgumentException(e: IllegalArgumentException) {
            handleException(e)
        }

        override fun onRtmpIllegalStateException(e: IllegalStateException) {
            handleException(e)
        }

        override fun onRtmpAuthenticationg(s: String) {}
    }

    companion object {
        // Set default values for the streamer
        const val DEFAULT_STREAM_ID = "95c77aff-cfc8-0eb0-91ad-79f9218a2276"
        const val DEFAULT_STREAM = "rtmp://global-live.mux.com:5222/app/$DEFAULT_STREAM_ID"
        val REQUIRED_PERMISSIONS =
            listOf(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO)
    }
}