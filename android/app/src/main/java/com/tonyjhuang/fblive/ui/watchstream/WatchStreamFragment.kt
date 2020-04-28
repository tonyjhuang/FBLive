package com.tonyjhuang.fblive.ui.watchstream

import android.graphics.Point
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory
import com.google.android.exoplayer2.util.Util
import com.mux.stats.sdk.core.model.CustomerPlayerData
import com.mux.stats.sdk.core.model.CustomerVideoData
import com.mux.stats.sdk.muxstats.MuxStatsExoPlayer
import com.tonyjhuang.fblive.R
import kotlinx.android.synthetic.main.fragment_home.view.*


class WatchStreamFragment : Fragment() {

    private lateinit var homeViewModel: WatchStreamViewModel
    private lateinit var playerView: PlayerView
    private lateinit var muxPlayer: MuxStatsExoPlayer

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homeViewModel =
            ViewModelProviders.of(this).get(WatchStreamViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_home, container, false)
        playerView = root.player_view
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val exoPlayer = SimpleExoPlayer.Builder(requireContext()).build();
        muxPlayer = setUpMuxPlayer(exoPlayer)
        val assetId = "yyZnFFWRTq02Kyn4sz52RplVvlP3Vl6mJdk22n2BIM9Y"
        val streamUrl = "https://stream.mux.com/uQYPThOb9025tjrsMyiHisTe0257eS00kCpbw301sl00h47M.m3u8" //"https://stream.mux.com/$assetId.m3u8"
        val mediaSource = buildMediaSource(Uri.parse(streamUrl))
        exoPlayer.prepare(mediaSource);
    }

    private fun setUpMuxPlayer(exoPlayer: ExoPlayer): MuxStatsExoPlayer {

        val customerPlayerData = CustomerPlayerData()
        customerPlayerData.environmentKey = "7e6ba2a4-672c-4962-942a-dec8ef11838c"
        val customerVideoData = CustomerVideoData()

        val muxPlayer = MuxStatsExoPlayer(
            requireContext(),
            exoPlayer,
            "demo-player",
            customerPlayerData,
            customerVideoData
        )

        val size = Point()
        requireActivity().windowManager.defaultDisplay.getSize(size)
        muxPlayer.setScreenSize(size.x, size.y)
        muxPlayer.setPlayerView(playerView.videoSurfaceView);
        return muxPlayer
    }

    private fun buildMediaSource(
        uri: Uri,
        overrideExtension: String? = null
    ): MediaSource {
        val dataSourceFactory: DataSource.Factory =
            DefaultHttpDataSourceFactory(
                Util.getUserAgent(
                    requireContext(),
                    "(Android) FB Live"
                )
            )
        val type: Int = Util.inferContentType(uri, overrideExtension);
        muxPlayer.setStreamType(type);
        when (type) {
            C.TYPE_HLS -> {
                return HlsMediaSource.Factory(dataSourceFactory)
                    .createMediaSource(uri)
            }
            else -> {
                throw IllegalStateException("Unsupported type: " + type);
            }
        }
    }

    override fun onStop() {
        super.onStop()
        muxPlayer.release()
    }
}

