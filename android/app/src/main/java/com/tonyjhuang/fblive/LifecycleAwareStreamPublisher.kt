package com.tonyjhuang.fblive

import android.content.Context
import android.content.res.Configuration
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import com.streamaxia.android.CameraPreview
import com.streamaxia.android.StreamaxiaPublisher
import com.streamaxia.android.handlers.EncoderHandler
import com.streamaxia.android.handlers.RecordHandler
import com.streamaxia.android.handlers.RtmpHandler
import com.tonyjhuang.fblive.core.StreamUtils
import java.lang.ref.WeakReference

class LifecycleAwareStreamPublisher(
    private val context: WeakReference<Context>,
    private val listener: Listener
) {

    private val rtmpListener: RtmpHandler.RtmpListener = RtmpListener()
    private val recordListener: RecordHandler.RecordListener =
        StreamUtils.SimpleRecordListener(listener::onException)
    private val encodeListener: EncoderHandler.EncodeListener =
        StreamUtils.SimpleEncodeListener(listener::onException)

    private lateinit var publisher: StreamaxiaPublisher

    fun onStart() {
        setStreamerDefaultValues()
    }

    fun onStop() {
        stopPublish()
    }

    private fun setStreamerDefaultValues() {
        val context = this.context.get() ?: return
        publisher.setScreenOrientation(Configuration.ORIENTATION_PORTRAIT)
        val sizes =
            publisher.getSupportedPictureSizes(context.resources.configuration.orientation)
        val resolution = sizes[0]
        publisher.setVideoOutputResolution(
            resolution.width,
            resolution.height,
            context.resources.configuration.orientation
        )
    }

    fun bindTo(cameraView: CameraPreview) {
        val context = this.context.get() ?: return
        publisher = StreamaxiaPublisher(cameraView, context).apply {
            setEncoderHandler(EncoderHandler(encodeListener))
            setRtmpHandler(RtmpHandler(rtmpListener))
            setRecordEventHandler(RecordHandler(recordListener))
        }
    }


    fun startPublish(stream: String) {
        publisher.startPublish(stream)
    }

    fun stopPublish() {
        publisher.stopPublish()
    }


    private inner class RtmpListener : StreamUtils.SimpleRtmpListener(listener::onException) {

        override fun onRtmpConnecting(s: String) {
            listener.onConnecting(s)
        }

        override fun onRtmpConnected(s: String) {
            listener.onConnected(s)
        }

        override fun onRtmpStopped() {
            listener.onStopped()
        }

        override fun onRtmpDisconnected() {
            listener.onDisconnected()
        }
    }

    open class Listener {
        open fun onConnecting(s: String) {}

        open fun onConnected(s: String) {}

        open fun onStopped() {}

        open fun onDisconnected() {}

        open fun onException(e: Exception) {}
    }
}