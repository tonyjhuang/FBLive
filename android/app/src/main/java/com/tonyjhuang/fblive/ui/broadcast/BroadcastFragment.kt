package com.tonyjhuang.fblive.ui.broadcast

import android.Manifest
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Chronometer
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.streamaxia.android.CameraPreview
import com.tonyjhuang.fblive.LifecycleAwareStreamPublisher
import com.tonyjhuang.fblive.R
import com.tonyjhuang.fblive.core.SimplePermissionsChecker
import java.lang.ref.WeakReference

class BroadcastFragment : Fragment() {

    private lateinit var viewModel: BroadcastViewModel


    private lateinit var cameraView: CameraPreview
    private lateinit var chronometer: Chronometer
    private lateinit var streamButton: TextView
    private lateinit var stateTextView: TextView

    private lateinit var publisher: LifecycleAwareStreamPublisher

    private var isStreaming = false
        set(value) {
            field = value
            if (value) {
                streamButton.text = "STOP"
                startChronometer()
            } else {
                streamButton.text = "START"
                stopChronometer()
            }
        }

    private lateinit var simplePermissionsChecker: SimplePermissionsChecker


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(this).get(BroadcastViewModel::class.java)
        val view = inflater.inflate(R.layout.fragment_broadcast, container, false)
        setUpViews(view)
        return view
    }

    private fun setUpViews(view: View) {
        cameraView = view.findViewById(R.id.preview)
        chronometer = view.findViewById(R.id.chronometer)
        streamButton = view.findViewById<TextView>(R.id.start_stop).apply {
            setOnClickListener { onStreamButtonClicked() }
        }
        stateTextView = view.findViewById(R.id.state_text)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        simplePermissionsChecker =
            SimplePermissionsChecker(WeakReference(requireContext()))
        publisher = LifecycleAwareStreamPublisher(
            WeakReference(requireContext()),
            StreamListener()
        ).apply {
            bindTo(cameraView)
            lifecycle.addObserver(this)
        }
    }

    private fun getPermissions() {
        simplePermissionsChecker.getPermissions(
            REQUIRED_PERMISSIONS,
            { /* intentionally left blank */ },
            {
                showToast("Please grant permissions")
                // TODO show error
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
        requireActivity().runOnUiThread { stateTextView.text = "[$msg]" }
    }

    private fun showToast(msg: String) {
        requireActivity().runOnUiThread {
            Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
        }
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
