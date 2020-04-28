package com.tonyjhuang.fblive.core

import com.streamaxia.android.handlers.EncoderHandler
import com.streamaxia.android.handlers.RecordHandler
import com.streamaxia.android.handlers.RtmpHandler
import java.io.IOException
import java.net.SocketException

object StreamUtils {


    open class SimpleEncodeListener(val exceptionHandler: (Exception) -> Unit) :
        EncoderHandler.EncodeListener {
        override fun onNetworkWeak() {}
        override fun onNetworkResume() {}
        override fun onEncodeIllegalArgumentException(exception: IllegalArgumentException) {
            exceptionHandler(exception)
        }
    }

    open class SimpleRecordListener(val exceptionHandler: (Exception) -> Unit) :
        RecordHandler.RecordListener {
        override fun onRecordIOException(exception: IOException) {
            exceptionHandler(exception)
        }

        override fun onRecordIllegalArgumentException(exception: IllegalArgumentException) {
            exceptionHandler(exception)
        }

        override fun onRecordFinished(p0: String) {

        }

        override fun onRecordPause() {

        }

        override fun onRecordResume() {

        }

        override fun onRecordStarted(p0: String) {

        }
    }

    open class SimpleRtmpListener(val exceptionHandler: (Exception) -> Unit) :
        RtmpHandler.RtmpListener {
        override fun onRtmpConnected(p0: String) {

        }

        override fun onRtmpIllegalStateException(exception: IllegalStateException) {
            exceptionHandler(exception)
        }

        override fun onRtmpStopped() {

        }

        override fun onRtmpIOException(exception: IOException) {
            exceptionHandler(exception)
        }

        override fun onRtmpAudioStreaming() {

        }

        override fun onRtmpSocketException(exception: SocketException) {
            exceptionHandler(exception)
        }

        override fun onRtmpDisconnected() {

        }

        override fun onRtmpVideoFpsChanged(p0: Double) {

        }

        override fun onRtmpConnecting(p0: String) {

        }

        override fun onRtmpAuthenticationg(p0: String) {

        }

        override fun onRtmpVideoStreaming() {

        }

        override fun onRtmpAudioBitrateChanged(p0: Double) {

        }

        override fun onRtmpVideoBitrateChanged(p0: Double) {

        }

        override fun onRtmpIllegalArgumentException(exception: IllegalArgumentException) {
            exceptionHandler(exception)
        }
    }
}