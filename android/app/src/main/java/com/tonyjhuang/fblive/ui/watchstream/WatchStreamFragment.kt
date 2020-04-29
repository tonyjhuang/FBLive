package com.tonyjhuang.fblive.ui.watchstream

import android.content.pm.PackageManager
import android.graphics.Point
import android.os.Bundle
import android.util.Pair
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.Toast
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.google.android.exoplayer2.ExoPlaybackException
import com.google.android.exoplayer2.mediacodec.MediaCodecRenderer
import com.google.android.exoplayer2.mediacodec.MediaCodecUtil
import com.google.android.exoplayer2.ui.PlayerControlView
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.util.ErrorMessageProvider
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.tonyjhuang.fblive.R
import kotlinx.android.synthetic.main.fragment_watch_stream.view.*
import java.net.CookieHandler
import java.net.CookieManager
import java.net.CookiePolicy


class WatchStreamFragment : Fragment(), PlayerControlView.VisibilityListener {

    private val defaultCookieManager = CookieManager().apply {
        setCookiePolicy(CookiePolicy.ACCEPT_ORIGINAL_SERVER)
    }

    private val viewModel: WatchStreamViewModel by activityViewModels()

    private lateinit var playerView: PlayerView

    private lateinit var playerManager: PlayerManager

    private lateinit var streamUrl: String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_watch_stream, container, false)
        CookieHandler.setDefault(defaultCookieManager)
        streamUrl = arguments?.getString(URI_EXTRA) ?: DEFAULT_STREAM

        playerManager = PlayerManager(
            requireActivity(),
            requireContext(),
            streamUrl,
            object : PlayerManager.Listener() {
                override fun onPlayerError(e: ExoPlaybackException) {
                    initializePlayerManager()
                }

                override fun onErrorString(errorMsg: String) {
                    showToast(errorMsg)
                }
            })

        if (savedInstanceState != null) {
            playerManager.restoreFrom(savedInstanceState)
        }

        setUpViews(view)
        return view
    }

    private fun setUpViews(view: View) {
        playerView = view.findViewById<PlayerView>(R.id.player_view).apply {
            hideController()
            setControllerVisibilityListener(this@WatchStreamFragment)
            setErrorMessageProvider(PlayerErrorMessageProvider())
            requestFocus()
        }
        val bottomSheetContainer = view.findViewById<FrameLayout>(R.id.chat_bottom_sheet_container)
        setUpBottomSheet(bottomSheetContainer)
    }

    private fun setUpBottomSheet(bottomSheetContainer: FrameLayout) {
        val bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetContainer)

        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        bottomSheetBehavior.peekHeight = requireContext()
            .resources
            .getDimension(R.dimen.watch_chat_bottom_sheet_peek)
            .toInt()

        val display = requireActivity().windowManager.defaultDisplay
        val screenSize = Point()
        display.getSize(screenSize)

        val params = bottomSheetContainer.layoutParams as CoordinatorLayout.LayoutParams
        params.height = screenSize.y / 2
        bottomSheetContainer.layoutParams = params
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpViewModel(view)
    }

    private fun setUpViewModel(view: View) {
        viewModel.streamName.observe(viewLifecycleOwner, Observer {
            view.stream_name.text = it
        })
        viewModel.activeViewers.observe(viewLifecycleOwner, Observer {
            view.active_viewers.text = it.toString()
        })
    }

    override fun onStart() {
        super.onStart()
        initializePlayerManager()
        playerView.onResume()
    }

    override fun onResume() {
        super.onResume()
        if (playerManager.player == null) {
            initializePlayerManager()
            playerView.onResume()
        }
    }

    override fun onStop() {
        super.onStop()
        playerView.onPause()
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
            initializePlayerManager()
        } else {
            showToast("Storage permission not granted")
            // TODO show error state
            // finish()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        playerManager.saveTo(outState)
    }

    // PlaybackControlView.VisibilityListener implementation
    override fun onVisibilityChange(visibility: Int) {
        playerView.hideController()
    }

    private fun initializePlayerManager() {
        playerManager.initializePlayer {
            playerView.player = it
            playerView.setPlaybackPreparer(playerManager)
        }
    }


    private fun releasePlayerManager() {
        playerManager.releasePlayer()
    }


    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
    }

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
        const val URI_EXTRA = "uri"
        private const val DEFAULT_STREAM =
            "https://stream.mux.com/uQYPThOb9025tjrsMyiHisTe0257eS00kCpbw301sl00h47M.m3u8"
    }
}

