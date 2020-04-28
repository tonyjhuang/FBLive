package com.tonyjhuang.fblive

import android.content.Intent
import android.net.Uri

internal abstract class Sample {

    class HlsSample private constructor(val uri: Uri) : Sample() {

        companion object {
            fun createFromIntent(intent: Intent): HlsSample {
                return HlsSample(intent.data ?: Uri.parse(DEFAULT_STREAM))
            }

            fun createFromString(path: String?): HlsSample {
                if (path == null) {
                    return HlsSample(Uri.parse(DEFAULT_STREAM))
                }
                return HlsSample(Uri.parse(path))
            }

            private const val DEFAULT_STREAM =
                "https://stream.mux.com/uQYPThOb9025tjrsMyiHisTe0257eS00kCpbw301sl00h47M.m3u8"
        }
    }
}