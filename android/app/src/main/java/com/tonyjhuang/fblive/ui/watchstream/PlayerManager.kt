package com.tonyjhuang.fblive.ui.watchstream

import android.app.Activity
import android.content.Context
import android.os.Bundle
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.audio.AudioAttributes
import com.google.android.exoplayer2.source.BehindLiveWindowException
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.TrackGroupArray
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.trackselection.MappingTrackSelector
import com.google.android.exoplayer2.trackselection.TrackSelectionArray
import com.google.android.exoplayer2.util.EventLogger
import com.google.android.exoplayer2.util.Util
import com.tonyjhuang.fblive.App
import com.tonyjhuang.fblive.Sample
import kotlin.math.max

class PlayerManager(
    private val activity: Activity,
    private val context: Context,
    private val streamUrl: String,
    private val listener: Listener? = null
) : PlaybackPreparer {

    var player: SimpleExoPlayer? = null
    private var mediaSource: MediaSource? = null

    private var lastSeenTrackGroupArray: TrackGroupArray? = null

    private var trackSelectorParameters =
        DefaultTrackSelector.ParametersBuilder(context).build()

    private val trackSelectionFactory = AdaptiveTrackSelection.Factory()
    private val trackSelector = DefaultTrackSelector(context, trackSelectionFactory).apply {
        parameters = trackSelectorParameters
    }


    private val state = StreamPlayerState()

    private val application = context.applicationContext as App
    private val dataSourceFactory = application.buildDataSourceFactory()
    private val renderersFactory = application.buildRenderersFactory()

    fun initializePlayer(playerReadyCallback: (SimpleExoPlayer) -> (Unit)) {
        if (player == null) {
            mediaSource = createTopLevelMediaSource() ?: return
            lastSeenTrackGroupArray = null
            player = SimpleExoPlayer.Builder(context, renderersFactory)
                .setTrackSelector(trackSelector)
                .build().apply {
                    addListener(PlayerEventListener())
                    setAudioAttributes(
                        AudioAttributes.DEFAULT,
                        true
                    )
                    playWhenReady = state.startAutoPlay
                    addAnalyticsListener(EventLogger(trackSelector))
                }
            playerReadyCallback(player!!)
        }
        if (mediaSource != null) {
            val haveStartPosition = state.startWindow != C.INDEX_UNSET
            if (haveStartPosition) {
                player!!.seekTo(state.startWindow, state.startPosition)
            }
            player!!.prepare(mediaSource!!, !haveStartPosition, false)
        }
    }


    private fun createTopLevelMediaSource(): MediaSource? {
        val sample = Sample.HlsSample.createFromString(streamUrl)
        if (!Util.checkCleartextTrafficPermitted(sample.uri)) {
            listener?.onErrorString("R.string.error_cleartext_not_permitted")
            return null
        }
        return if (Util.maybeRequestReadExternalStoragePermission(
                activity,
                sample.uri
            )
        ) { // The player will be reinitialized if the permission is granted.
            null
        } else {
            return HlsMediaSource.Factory(dataSourceFactory!!)
                .createMediaSource(sample.uri)
        }
    }

    override fun preparePlayback() {
        player!!.retry()
    }

    fun releasePlayer() {
        if (player != null) {
            updateTrackSelectorParameters()
            updateStartPosition()
            player!!.release()
            player = null
            mediaSource = null
        }
    }

    private fun updateTrackSelectorParameters() {
        trackSelectorParameters = trackSelector.parameters
    }

    private fun updateStartPosition() {
        val player = this.player ?: return
        state.updateFromPlayer(player)
    }

    fun restoreFrom(savedInstanceState: Bundle) {
        val trackSelectorParameters: DefaultTrackSelector.Parameters =
            savedInstanceState.getParcelable(KEY_TRACK_SELECTOR_PARAMETERS)!!
        trackSelector.parameters = trackSelectorParameters
        state.restoreFrom(savedInstanceState)
    }

    fun saveTo(outState: Bundle) {
        updateTrackSelectorParameters()
        updateStartPosition()
        outState.putParcelable(
            KEY_TRACK_SELECTOR_PARAMETERS,
            trackSelectorParameters
        )
        state.saveTo(outState)
    }

    private inner class PlayerEventListener : Player.EventListener {

        override fun onPlayerError(e: ExoPlaybackException) {
            if (isBehindLiveWindow(e)) {
                state.clearStartPosition()
                listener?.onPlayerError(e)
            } else {
                listener?.onErrorString("player error: [$e]")
            }
        }

        override fun onTracksChanged(
            trackGroups: TrackGroupArray,
            trackSelections: TrackSelectionArray
        ) {
            if (trackGroups !== lastSeenTrackGroupArray) {
                val mappedTrackInfo = trackSelector.currentMappedTrackInfo
                if (mappedTrackInfo != null) {
                    if (mappedTrackInfo.getTypeSupport(C.TRACK_TYPE_VIDEO)
                        == MappingTrackSelector.MappedTrackInfo.RENDERER_SUPPORT_UNSUPPORTED_TRACKS
                    ) {
                        listener?.onErrorString("R.string.error_unsupported_video")
                    }
                    if (mappedTrackInfo.getTypeSupport(C.TRACK_TYPE_AUDIO)
                        == MappingTrackSelector.MappedTrackInfo.RENDERER_SUPPORT_UNSUPPORTED_TRACKS
                    ) {
                        listener?.onErrorString("R.string.error_unsupported_audio")
                    }
                }
                lastSeenTrackGroupArray = trackGroups
            }
        }
    }

    abstract class Listener {
        open fun onPlayerError(e: ExoPlaybackException) {}

        open fun onErrorString(errorMsg: String) {}
    }

    companion object {
        private fun isBehindLiveWindow(e: ExoPlaybackException): Boolean {
            if (e.type != ExoPlaybackException.TYPE_SOURCE) {
                return false
            }
            var cause: Throwable? = e.sourceException
            while (cause != null) {
                if (cause is BehindLiveWindowException) {
                    return true
                }
                cause = cause.cause
            }
            return false
        }

        private const val KEY_TRACK_SELECTOR_PARAMETERS = "track_selector_parameters"
    }
}

class StreamPlayerState {
    var startAutoPlay = false
    var startWindow = 0
    var startPosition: Long = 0

    init {
        clearStartPosition()
    }

    fun clearStartPosition() {
        startAutoPlay = true
        startWindow = C.INDEX_UNSET
        startPosition = C.TIME_UNSET
    }

    fun restoreFrom(bundle: Bundle) {
        with(bundle) {
            startAutoPlay = getBoolean(KEY_AUTO_PLAY)
            startWindow = getInt(KEY_WINDOW)
            startPosition = getLong(KEY_POSITION)
        }
    }

    fun saveTo(bundle: Bundle) {
        bundle.putBoolean(KEY_AUTO_PLAY, startAutoPlay)
        bundle.putInt(KEY_WINDOW, startWindow)
        bundle.putLong(KEY_POSITION, startPosition)
    }

    fun updateFromPlayer(player: ExoPlayer) {
        startAutoPlay = player.playWhenReady
        startWindow = player.currentWindowIndex
        startPosition = max(0, player.contentPosition)
    }

    companion object {
        private const val KEY_WINDOW = "window"
        private const val KEY_POSITION = "position"
        private const val KEY_AUTO_PLAY = "auto_play"
    }
}