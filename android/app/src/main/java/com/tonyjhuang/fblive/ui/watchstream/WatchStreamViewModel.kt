package com.tonyjhuang.fblive.ui.watchstream

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class WatchStreamViewModel : ViewModel() {

    val streamName = MutableLiveData<String>("My awesome stream!!")
    val activeViewers = MutableLiveData<Int>(32)
}