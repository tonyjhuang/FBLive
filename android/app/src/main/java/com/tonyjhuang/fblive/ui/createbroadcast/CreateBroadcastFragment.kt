package com.tonyjhuang.fblive.ui.createbroadcast

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2

import com.tonyjhuang.fblive.R
import kotlinx.android.synthetic.main.fragment_create_broadcast.view.*

class CreateBroadcastFragment : Fragment() {

    private  val viewModel: CreateBroadcastViewModel by activityViewModels()

    private lateinit var pagerAdapter: CreateBroadcastFormFragmentStateAdapter
    private lateinit var pager: ViewPager2

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel.clear()
        return inflater.inflate(R.layout.fragment_create_broadcast, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpViews(view)
        setUpViewModel(view)
    }

    private fun setUpViews(view: View) {
        pagerAdapter = CreateBroadcastFormFragmentStateAdapter(this)
        pager = view.form_pager
        with(pager) {
            isUserInputEnabled = false
            adapter = pagerAdapter
        }
        view.prev.setOnClickListener { viewModel.prev() }
        view.next.setOnClickListener { viewModel.next() }
    }

    private fun setUpViewModel(view: View) {
        viewModel.currentState.observe(viewLifecycleOwner, Observer { newState ->
            handleNewState(newState)
        })
    }

    private fun handleNewState(newState: CreateBroadcastViewModel.FormState) {
        if (newState == CreateBroadcastViewModel.FormState.EXIT) {
            activity?.onBackPressed()
            return
        }
        if (newState == CreateBroadcastViewModel.FormState.SUCCESS) {
            // TODO set stream url
            findNavController().navigate(CreateBroadcastFragmentDirections.actionCreateBroadcastToBroadcast())
            return
        }

        val newIndex = CreateBroadcastViewModel.STATE_ORDER
            .withIndex()
            .first { it.value == newState}
            .index
        pager.currentItem = newIndex
    }
}
