package com.tonyjhuang.fblive.ui.createbroadcast

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.tonyjhuang.fblive.R

sealed class CreateBroadcastFormFragment : Fragment() {

    protected val viewModel: CreateBroadcastViewModel by activityViewModels()

    abstract fun getContentLayoutId(): Int

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(getContentLayoutId(), container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val input: EditText? = view.findViewById(R.id.input)
    }
}

class BroadcastNameFragment : CreateBroadcastFormFragment() {
    override fun getContentLayoutId() = R.layout.fragment_form_cb_broadcast_name
}

class ProductNameFragment : CreateBroadcastFormFragment() {
    override fun getContentLayoutId() = R.layout.fragment_form_cb_product_name
}

class IsAuctionFragment : CreateBroadcastFormFragment() {
    override fun getContentLayoutId() = R.layout.fragment_form_cb_is_auction
}

class ProductPriceFragment : CreateBroadcastFormFragment() {
    override fun getContentLayoutId() = R.layout.fragment_form_cb_product_price
}

class ProductInventoryFragment : CreateBroadcastFormFragment() {
    override fun getContentLayoutId() = R.layout.fragment_form_cb_product_inventory
}