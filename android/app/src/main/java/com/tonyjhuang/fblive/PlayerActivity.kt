/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.tonyjhuang.fblive

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Pair
import android.view.KeyEvent
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.audio.AudioAttributes
import com.google.android.exoplayer2.mediacodec.MediaCodecRenderer.DecoderInitializationException
import com.google.android.exoplayer2.mediacodec.MediaCodecUtil.DecoderQueryException
import com.google.android.exoplayer2.source.BehindLiveWindowException
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.TrackGroupArray
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector.ParametersBuilder
import com.google.android.exoplayer2.trackselection.MappingTrackSelector.MappedTrackInfo
import com.google.android.exoplayer2.trackselection.TrackSelection
import com.google.android.exoplayer2.trackselection.TrackSelectionArray
import com.google.android.exoplayer2.ui.DebugTextViewHelper
import com.google.android.exoplayer2.ui.PlayerControlView
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.util.ErrorMessageProvider
import com.google.android.exoplayer2.util.EventLogger
import com.google.android.exoplayer2.util.Util
import com.tonyjhuang.fblive.Sample.HlsSample
import java.net.CookieHandler
import java.net.CookieManager
import java.net.CookiePolicy
import kotlin.math.max

/**
 * An activity that plays media using [SimpleExoPlayer].
 */
class PlayerActivity : AppCompatActivity(), PlaybackPreparer, PlayerControlView.VisibilityListener {
    companion object {
        // Media item configuration extras.
        const val URI_EXTRA = "uri"
        // Saved instance state keys.
        private const val KEY_TRACK_SELECTOR_PARAMETERS = "track_selector_parameters"
        private const val KEY_WINDOW = "window"
        private const val KEY_POSITION = "position"
        private const val KEY_AUTO_PLAY = "auto_play"
        private val DEFAULT_COOKIE_MANAGER = CookieManager()
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

        init {
            DEFAULT_COOKIE_MANAGER.setCookiePolicy(CookiePolicy.ACCEPT_ORIGINAL_SERVER)
        }
    }

    private lateinit var playerView: PlayerView
    private lateinit var debugTextView: TextView
    private var dataSourceFactory: DataSource.Factory? = null
    private var player: SimpleExoPlayer? = null
    private var mediaSource: MediaSource? = null
    private var trackSelector: DefaultTrackSelector? = null
    private var trackSelectorParameters: DefaultTrackSelector.Parameters? = null
    private var debugViewHelper: DebugTextViewHelper? = null
    private var lastSeenTrackGroupArray: TrackGroupArray? = null
    private var startAutoPlay = false
    private var startWindow = 0
    private var startPosition: Long = 0
    // Activity lifecycle
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dataSourceFactory = buildDataSourceFactory()
        if (CookieHandler.getDefault() !== DEFAULT_COOKIE_MANAGER) {
            CookieHandler.setDefault(DEFAULT_COOKIE_MANAGER)
        }
        setContentView(R.layout.activity_player)
        debugTextView = findViewById(R.id.debug_text_view)
        playerView = findViewById(R.id.player_view)
        playerView.setControllerVisibilityListener(this)
        playerView.setErrorMessageProvider(PlayerErrorMessageProvider())
        playerView.requestFocus()
        if (savedInstanceState != null) {
            trackSelectorParameters =
                savedInstanceState.getParcelable(KEY_TRACK_SELECTOR_PARAMETERS)
            startAutoPlay = savedInstanceState.getBoolean(KEY_AUTO_PLAY)
            startWindow = savedInstanceState.getInt(KEY_WINDOW)
            startPosition = savedInstanceState.getLong(KEY_POSITION)
        } else {
            val builder = ParametersBuilder( /* context= */this)
            trackSelectorParameters = builder.build()
            clearStartPosition()
        }
    }

    public override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        releasePlayer()
        clearStartPosition()
        setIntent(intent)
    }

    public override fun onStart() {
        super.onStart()
        initializePlayer()
        playerView.onResume()
    }

    public override fun onResume() {
        super.onResume()
        if (player == null) {
            initializePlayer()
            playerView.onResume()
        }
    }

    public override fun onStop() {
        super.onStop()
        playerView.onPause()
        releasePlayer()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (grantResults.isEmpty()) {
            // Empty results are triggered if a permission is requested while another request was already
            // pending and can be safely ignored in this case.
            return
        }
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            initializePlayer()
        } else {
            showToast("Storage permission not granted")
            finish()
        }
    }

    public override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        updateTrackSelectorParameters()
        updateStartPosition()
        outState.putParcelable(
            KEY_TRACK_SELECTOR_PARAMETERS,
            trackSelectorParameters
        )
        outState.putBoolean(KEY_AUTO_PLAY, startAutoPlay)
        outState.putInt(KEY_WINDOW, startWindow)
        outState.putLong(KEY_POSITION, startPosition)
    }

    // Activity input
    override fun dispatchKeyEvent(event: KeyEvent): Boolean { // See whether the player view wants to handle media or DPAD keys events.
        return playerView.dispatchKeyEvent(event) || super.dispatchKeyEvent(event)
    }

    // PlaybackControlView.PlaybackPreparer implementation
    override fun preparePlayback() {
        player!!.retry()
    }

    // PlaybackControlView.VisibilityListener implementation
    override fun onVisibilityChange(visibility: Int) { //debugRootView.setVisibility(visibility);
    }

    // Internal methods
    private fun initializePlayer() {
        if (player == null) {
            mediaSource = createTopLevelMediaSource()
            if (mediaSource == null) {
                return
            }
            val trackSelectionFactory: TrackSelection.Factory
            trackSelectionFactory = AdaptiveTrackSelection.Factory()
            val renderersFactory =
                (application as DemoApplication).buildRenderersFactory()
            trackSelector = DefaultTrackSelector( /* context= */this, trackSelectionFactory)
            trackSelector!!.parameters = trackSelectorParameters!!
            lastSeenTrackGroupArray = null
            player = SimpleExoPlayer.Builder( /* context= */this, renderersFactory)
                .setTrackSelector(trackSelector!!)
                .build()
            player!!.addListener(PlayerEventListener())
            player!!.setAudioAttributes(
                AudioAttributes.DEFAULT,  /* handleAudioFocus= */
                true
            )
            player!!.playWhenReady = startAutoPlay
            player!!.addAnalyticsListener(EventLogger(trackSelector))
            playerView.player = player
            playerView.setPlaybackPreparer(this)
            debugViewHelper = DebugTextViewHelper(player!!, debugTextView)
            debugViewHelper!!.start()
        }
        val haveStartPosition = startWindow != C.INDEX_UNSET
        if (haveStartPosition) {
            player!!.seekTo(startWindow, startPosition)
        }
        player!!.prepare(mediaSource!!, !haveStartPosition, false)
    }

    private fun createTopLevelMediaSource(): MediaSource? {
        val sample =
            Sample.createFromIntent(intent) as HlsSample
        if (!Util.checkCleartextTrafficPermitted(sample.uri)) {
            showToast("R.string.error_cleartext_not_permitted")
            return null
        }
        return if (Util.maybeRequestReadExternalStoragePermission( /* activity= */
                this,
                sample.uri
            )
        ) { // The player will be reinitialized if the permission is granted.
            null
        } else createHlsMediaSource(sample)
    }

    private fun createHlsMediaSource(parameters: HlsSample): MediaSource {
        return HlsMediaSource.Factory(dataSourceFactory!!)
            .createMediaSource(parameters.uri)
    }

    private fun releasePlayer() {
        if (player != null) {
            updateTrackSelectorParameters()
            updateStartPosition()
            debugViewHelper!!.stop()
            debugViewHelper = null
            player!!.release()
            player = null
            mediaSource = null
            trackSelector = null
        }
    }

    private fun updateTrackSelectorParameters() {
        if (trackSelector != null) {
            trackSelectorParameters = trackSelector!!.parameters
        }
    }

    private fun updateStartPosition() {
        if (player != null) {
            startAutoPlay = player!!.playWhenReady
            startWindow = player!!.currentWindowIndex
            startPosition = max(0, player!!.contentPosition)
        }
    }

    private fun clearStartPosition() {
        startAutoPlay = true
        startWindow = C.INDEX_UNSET
        startPosition = C.TIME_UNSET
    }

    /**
     * Returns a new DataSource factory.
     */
    private fun buildDataSourceFactory(): DataSource.Factory {
        return (application as DemoApplication).buildDataSourceFactory()
    }

    // User controls
    private fun showControls() { // debugRootView.setVisibility(View.VISIBLE);
    }

    private fun showToast(messageId: Int) {
        showToast(getString(messageId))
    }

    private fun showToast(message: String) {
        Toast.makeText(applicationContext, message, Toast.LENGTH_LONG).show()
    }

    private inner class PlayerEventListener : Player.EventListener {
        override fun onPlayerStateChanged(playWhenReady: Boolean, @Player.State playbackState: Int) {
            if (playbackState == Player.STATE_ENDED) {
                showControls()
            }
        }

        override fun onPlayerError(e: ExoPlaybackException) {
            if (isBehindLiveWindow(e)) {
                clearStartPosition()
                initializePlayer()
            } else {
                showControls()
            }
        }

        override fun onTracksChanged(
            trackGroups: TrackGroupArray,
            trackSelections: TrackSelectionArray
        ) {
            if (trackGroups !== lastSeenTrackGroupArray) {
                val mappedTrackInfo = trackSelector!!.currentMappedTrackInfo
                if (mappedTrackInfo != null) {
                    if (mappedTrackInfo.getTypeSupport(C.TRACK_TYPE_VIDEO)
                        == MappedTrackInfo.RENDERER_SUPPORT_UNSUPPORTED_TRACKS
                    ) {
                        showToast("R.string.error_unsupported_video")
                    }
                    if (mappedTrackInfo.getTypeSupport(C.TRACK_TYPE_AUDIO)
                        == MappedTrackInfo.RENDERER_SUPPORT_UNSUPPORTED_TRACKS
                    ) {
                        showToast("R.string.error_unsupported_audio")
                    }
                }
                lastSeenTrackGroupArray = trackGroups
            }
        }
    }

    private class PlayerErrorMessageProvider :
        ErrorMessageProvider<ExoPlaybackException> {
        override fun getErrorMessage(e: ExoPlaybackException): Pair<Int, String> {
            var errorString = "R.string.error_generic"
            if (e.type == ExoPlaybackException.TYPE_RENDERER) {
                val cause = e.rendererException
                if (cause is DecoderInitializationException) { // Special case for decoder initialization failures.
                    val decoderInitializationException =
                        cause
                    errorString = if (decoderInitializationException.codecInfo == null) {
                        if (decoderInitializationException.cause is DecoderQueryException) {
                            "R.string.error_querying_decoders"
                        } else if (decoderInitializationException.secureDecoderRequired) {
                            "R.string.error_no_secure_decoder"
                        } else {
                            "R.string.error_no_decoder"
                        }
                    } else {
                        "R.string.error_instantiating_decoder"
                    }
                }
            }
            return Pair.create(0, errorString)
        }
    }
}