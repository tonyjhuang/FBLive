package com.tonyjhuang.fblive.ui.watchstream

import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Pair
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.audio.AudioAttributes
import com.google.android.exoplayer2.mediacodec.MediaCodecRenderer
import com.google.android.exoplayer2.mediacodec.MediaCodecUtil
import com.google.android.exoplayer2.source.BehindLiveWindowException
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.TrackGroupArray
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.trackselection.MappingTrackSelector
import com.google.android.exoplayer2.trackselection.TrackSelectionArray
import com.google.android.exoplayer2.ui.DebugTextViewHelper
import com.google.android.exoplayer2.ui.PlayerControlView
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.util.ErrorMessageProvider
import com.google.android.exoplayer2.util.EventLogger
import com.google.android.exoplayer2.util.Util
import com.tonyjhuang.fblive.App
import com.tonyjhuang.fblive.R
import com.tonyjhuang.fblive.Sample
import java.net.CookieHandler
import java.net.CookieManager
import java.net.CookiePolicy


class WatchStreamFragment : Fragment(), PlaybackPreparer, PlayerControlView.VisibilityListener {

    private lateinit var viewModel: WatchStreamViewModel

    private lateinit var playerView: PlayerView
    private lateinit var debugTextView: TextView

    //private val trackSelectionFactory = AdaptiveTrackSelection.Factory()
    //private lateinit var renderersFactory: RenderersFactory
    //private lateinit var dataSourceFactory: DataSource.Factory
    //private var player: SimpleExoPlayer? = null
    //private var mediaSource: MediaSource? = null
    //private lateinit var trackSelector: DefaultTrackSelector
    //private lateinit var trackSelectorParameters: DefaultTrackSelector.Parameters
    private lateinit var debugViewHelper: DebugTextViewHelper
    //private var lastSeenTrackGroupArray: TrackGroupArray? = null

    //var streamPlayer: StreamPlayer? = null

    lateinit var playerManager: PlayerManager

    //private val state = StreamPlayerState()

    private val application
        get() = requireActivity().application as App

    private lateinit var streamUrl: String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(this).get(WatchStreamViewModel::class.java)
        val view = inflater.inflate(R.layout.fragment_watch_stream, container, false)
        //dataSourceFactory = application.buildDataSourceFactory()
        CookieHandler.setDefault(DEFAULT_COOKIE_MANAGER)
        streamUrl = arguments?.getString(URI_EXTRA) ?: DEFAULT_STREAM

        /*streamPlayer = StreamPlayer(requireContext(), object : StreamPlayer.Listener() {
            override fun onPlayerError(e: ExoPlaybackException) {
                super.onPlayerError(e)
            }
        })*/

        playerManager = PlayerManager(requireActivity(), requireContext(), streamUrl, object: StreamPlayer.Listener() {
            override fun onPlayerError(e: ExoPlaybackException) {
                initializePlayerManager()
            }
        })

        if (savedInstanceState != null) {
            playerManager.restoreFromSavedInstanceState(savedInstanceState)
            /*with(savedInstanceState) {
                trackSelectorParameters = getParcelable(KEY_TRACK_SELECTOR_PARAMETERS)!!
            }
            state.restoreFrom(savedInstanceState)*/
        } else {
            /*val builder = DefaultTrackSelector.ParametersBuilder(requireContext())
            trackSelectorParameters = builder.build()*/
        }

        /*renderersFactory = application.buildRenderersFactory()
        trackSelector = DefaultTrackSelector(requireContext(), trackSelectionFactory).apply {
            parameters = trackSelectorParameters
        }*/
        setUpViews(view)
        return view
    }

    private fun setUpViews(view: View) {
        debugTextView = view.findViewById(R.id.debug_text_view)
        playerView = view.findViewById<PlayerView>(R.id.player_view).apply {
            hideController()
            setControllerVisibilityListener(this@WatchStreamFragment)
            setErrorMessageProvider(PlayerErrorMessageProvider())
            requestFocus()
        }
    }

    override fun onStart() {
        super.onStart()
        initializePlayerManager()
        //initializePlayer()
        playerView.onResume()
    }

    override fun onResume() {
        super.onResume()
        /*if (player == null) {
            initializePlayer()
            playerView.onResume()
        }*/
        if (playerManager.player == null) {
            initializePlayerManager()
            playerView.onResume()
        }
    }

    override fun onStop() {
        super.onStop()
        playerView.onPause()
        //releasePlayer()
        releasePlayerManager()
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
            //initializePlayer()
            initializePlayerManager()
        } else {
            showToast("Storage permission not granted")
            // TODO show error state
            // finish()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        /*updateTrackSelectorParameters()
        updateStartPosition()
        outState.putParcelable(
            KEY_TRACK_SELECTOR_PARAMETERS,
            trackSelectorParameters
        )
        state.saveTo(outState)*/
        playerManager.onSaveInstanceState(outState)
    }

    // PlaybackControlView.PlaybackPreparer implementation
    override fun preparePlayback() {
        //player!!.retry()
    }

    // PlaybackControlView.VisibilityListener implementation
    override fun onVisibilityChange(visibility: Int) {
        playerView.hideController()
        //debugRootView.setVisibility(visibility);
    }

    private fun initializePlayerManager() {
        playerManager.initializePlayer {
            playerView.player = it
            playerView.setPlaybackPreparer(playerManager)
            debugViewHelper = DebugTextViewHelper(it!!, debugTextView)
            debugViewHelper.start()
        }
    }

    // Internal methods
    //private fun initializePlayer() {
        /*if (player == null) {
            mediaSource = createTopLevelMediaSource(streamUrl)
            if (mediaSource == null) {
                return
            }
            lastSeenTrackGroupArray = null
            player = SimpleExoPlayer.Builder(requireContext(), renderersFactory)
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
            playerView.player = player
            playerView.setPlaybackPreparer(this)
            debugViewHelper = DebugTextViewHelper(player!!, debugTextView)
            debugViewHelper.start()
        }
        if (mediaSource != null) {
            val haveStartPosition = state.startWindow != C.INDEX_UNSET
            if (haveStartPosition) {
                player!!.seekTo(state.startWindow, state.startPosition)
            }
            player!!.prepare(mediaSource!!, !haveStartPosition, false)
        }*/
    //}

    /*private fun createTopLevelMediaSource(stream: String?): MediaSource? {
        val streamPath = stream ?: DEFAULT_STREAM
        val sample = Sample.HlsSample.createFromString(streamPath)
        if (!Util.checkCleartextTrafficPermitted(sample.uri)) {
            showToast("R.string.error_cleartext_not_permitted")
            return null
        }
        return if (Util.maybeRequestReadExternalStoragePermission(
                requireActivity(),
                sample.uri
            )
        ) { // The player will be reinitialized if the permission is granted.
            null
        } else createHlsMediaSource(sample)
    }*/

    /*private fun createHlsMediaSource(parameters: Sample.HlsSample): MediaSource {
        return HlsMediaSource.Factory(dataSourceFactory)
            .createMediaSource(parameters.uri)
    }*/

    private fun releasePlayerManager() {
        playerManager.releasePlayer()
        debugViewHelper.stop()
    }

    /*private fun releasePlayer() {
        if (player != null) {
            updateTrackSelectorParameters()
            updateStartPosition()
            debugViewHelper.stop()
            player!!.release()
            player = null
            mediaSource = null
        }
    }*/

    /*private fun updateTrackSelectorParameters() {
        trackSelectorParameters = trackSelector.parameters
    }*/

    /*private fun updateStartPosition() {
        val player = this.player ?: return
        state.updateFromPlayer(player)
    }*/


    private fun showToast(messageId: Int) {
        showToast(getString(messageId))
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
    }

    /*private inner class PlayerEventListener : Player.EventListener {

        override fun onPlayerError(e: ExoPlaybackException) {
            if (isBehindLiveWindow(e)) {
                state.clearStartPosition()
                //initializePlayer()
            } else {
                //showControls()
                showToast("player error: [$e]")
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
                        == MappingTrackSelector.MappedTrackInfo.RENDERER_SUPPORT_UNSUPPORTED_TRACKS
                    ) {
                        showToast("R.string.error_unsupported_video")
                    }
                    if (mappedTrackInfo.getTypeSupport(C.TRACK_TYPE_AUDIO)
                        == MappingTrackSelector.MappedTrackInfo.RENDERER_SUPPORT_UNSUPPORTED_TRACKS
                    ) {
                        showToast("R.string.error_unsupported_audio")
                    }
                }
                lastSeenTrackGroupArray = trackGroups
            }
        }
    }*/

    private class PlayerErrorMessageProvider :
        ErrorMessageProvider<ExoPlaybackException> {
        override fun getErrorMessage(e: ExoPlaybackException): Pair<Int, String> {
            var errorString = "R.string.error_generic"
            if (e.type == ExoPlaybackException.TYPE_RENDERER) {
                val cause = e.rendererException
                if (cause is MediaCodecRenderer.DecoderInitializationException) { // Special case for decoder initialization failures.
                    val decoderInitializationException =
                        cause
                    errorString = if (decoderInitializationException.codecInfo == null) {
                        if (decoderInitializationException.cause is MediaCodecUtil.DecoderQueryException) {
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


    companion object {
        // Media item configuration extras.
        const val URI_EXTRA = "uri"
        // Saved instance state keys.
        private const val KEY_TRACK_SELECTOR_PARAMETERS = "track_selector_parameters"
        private const val KEY_WINDOW = "window"
        private const val KEY_POSITION = "position"
        private const val KEY_AUTO_PLAY = "auto_play"
        private val DEFAULT_COOKIE_MANAGER = CookieManager().apply {
            setCookiePolicy(CookiePolicy.ACCEPT_ORIGINAL_SERVER)
        }

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

        private const val DEFAULT_STREAM =
            "https://stream.mux.com/uQYPThOb9025tjrsMyiHisTe0257eS00kCpbw301sl00h47M.m3u8"
    }
}

