/*
 * Copyright (C) 2019 The Android Open Source Project
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
package com.tonyjhuang.fblive;

import android.content.Intent;
import android.net.Uri;

abstract class Sample {

    public static final class HlsSample extends Sample {

        public static HlsSample createFromIntent(Uri uri, Intent intent, String extrasKeySuffix) {
            return new HlsSample(uri);
        }

        public final Uri uri;
        public final String extension = "m3u8";

        public HlsSample(Uri uri) {
          if (uri != null) {
            this.uri = uri;
          } else {
            this.uri = Uri.parse("https://stream.mux.com/uQYPThOb9025tjrsMyiHisTe0257eS00kCpbw301sl00h47M.m3u8");
          }
        }
    }

    public static Sample createFromIntent(Intent intent) {
      return new HlsSample(intent.getData());
    }
}
