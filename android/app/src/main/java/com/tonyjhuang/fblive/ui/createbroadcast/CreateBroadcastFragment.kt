package com.tonyjhuang.fblive.ui.createbroadcast

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels

import com.tonyjhuang.fblive.R

class CreateBroadcastFragment : Fragment() {

    private  val viewModel: CreateBroadcastViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_create_broadcast, container, false)
    }
}
