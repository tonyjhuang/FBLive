package com.tonyjhuang.fblive.ui.broadcast

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.tonyjhuang.fblive.R

class BroadcastFragment : Fragment() {

    private lateinit var viewModel: BroadcastViewModel

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        viewModel =
                ViewModelProvider(this).get(BroadcastViewModel::class.java)
        return inflater.inflate(R.layout.fragment_broadcast, container, false)
    }
}
