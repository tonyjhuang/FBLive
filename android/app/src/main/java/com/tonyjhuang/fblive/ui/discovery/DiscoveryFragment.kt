package com.tonyjhuang.fblive.ui.discovery

import android.graphics.Point
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetBehavior

import com.tonyjhuang.fblive.R
import kotlinx.android.synthetic.main.fragment_discovery.view.*

class DiscoveryFragment : Fragment() {

    private val viewModel: DiscoveryViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_discovery, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpViews(view)
    }

    private fun setUpViews(view: View) {
        view.prev.setOnClickListener { activity?.onBackPressed() }
        view.next.setOnClickListener {
            findNavController().navigate(DiscoveryFragmentDirections.actionDiscoveryToWatchStream())
        }

        val bottomSheetContainer = view.findViewById<FrameLayout>(R.id.livestream_bottom_sheet_container)
        setUpBottomSheet(bottomSheetContainer)
    }

    private fun setUpBottomSheet(bottomSheetContainer: FrameLayout) {
        val bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetContainer)

        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        bottomSheetBehavior.peekHeight = requireContext()
            .resources
            .getDimension(R.dimen.discovery_bottom_sheet_peek)
            .toInt()
    }
}
