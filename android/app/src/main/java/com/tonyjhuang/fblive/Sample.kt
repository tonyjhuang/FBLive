package com.tonyjhuang.fblive

import android.content.Intent
import android.net.Uri

internal abstract class Sample {
    class HlsSample(uri: Uri?) : Sample() {
        val uri: Uri = uri ?: Uri.parse(DEFAULT_STREAM)
    }

    companion object {
        fun createFromIntent(intent: Intent): Sample {
            return HlsSample(intent.data)
        }

        private const val DEFAULT_STREAM =
            "https://stream.mux.com/uQYPThOb9025tjrsMyiHisTe0257eS00kCpbw301sl00h47M.m3u8"
    }
}