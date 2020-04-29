package com.tonyjhuang.fblive.ui.createbroadcast

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class CreateBroadcastFormFragmentStateAdapter(
    fragment: Fragment
) : FragmentStateAdapter(fragment) {

    override fun getItemCount() = 5

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> BroadcastNameFragment()
            1 -> ProductNameFragment()
            2 -> IsAuctionFragment()
            3 -> ProductPriceFragment()
            4 -> ProductInventoryFragment()

            else -> throw Exception("Unexpected frag position")
        }
    }
}
