package com.tonyjhuang.fblive.ui.createbroadcast

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.tonyjhuang.fblive.R
import kotlinx.android.synthetic.main.fragment_form_cb_is_auction.view.*
import kotlinx.android.synthetic.main.fragment_form_cb_product_inventory.view.*

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
}

abstract class SimpleTextFieldFormFragment : CreateBroadcastFormFragment() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val input: EditText = view.findViewById(R.id.input)
        input.addTextChangedListener(object : SimpleTextWatcher() {
            override fun afterTextChanged(s: Editable?) {
                handleNewText(s.toString())
            }
        })
    }

    abstract fun handleNewText(text: String)
}

class BroadcastNameFragment : SimpleTextFieldFormFragment() {
    override fun getContentLayoutId() = R.layout.fragment_form_cb_broadcast_name

    override fun handleNewText(text: String) {
        viewModel.setBroadcastName(text)
    }
}

class ProductNameFragment : SimpleTextFieldFormFragment() {
    override fun getContentLayoutId() = R.layout.fragment_form_cb_product_name

    override fun handleNewText(text: String) {
        viewModel.setProductName(text)
    }
}

class IsAuctionFragment : CreateBroadcastFormFragment() {
    override fun getContentLayoutId() = R.layout.fragment_form_cb_is_auction

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.isAuction.observe(viewLifecycleOwner, Observer {
            if (it) {
                view.is_auction.backgroundTintList =
                    requireContext().resources.getColorStateList(R.color.colorAccent)
                view.is_not_auction.backgroundTintList = null
            } else {
                view.is_auction.backgroundTintList = null
                view.is_not_auction.backgroundTintList =
                    requireContext().resources.getColorStateList(R.color.colorAccent)
            }
        })
        view.is_auction.setOnClickListener {
            viewModel.setIsAuction(true)
        }
        view.is_not_auction.setOnClickListener {
            viewModel.setIsAuction(false)
        }
    }
}

class ProductPriceFragment : SimpleTextFieldFormFragment() {
    override fun getContentLayoutId() = R.layout.fragment_form_cb_product_price

    override fun handleNewText(text: String) {
        viewModel.setProductPrice(if (text.isEmpty()) 0 else text.toDouble())
    }
}

class ProductInventoryFragment : SimpleTextFieldFormFragment() {
    override fun getContentLayoutId() = R.layout.fragment_form_cb_product_inventory

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.start_broadcast.setOnClickListener { viewModel.next() }
        viewModel.canProceed.observe(viewLifecycleOwner, Observer {
            view.start_broadcast.isEnabled = it
        })
    }

    override fun handleNewText(text: String) {
        viewModel.setProductInventory(if (text.isEmpty()) 0 else text.toInt())
    }
}

open class SimpleTextWatcher : TextWatcher {
    override fun afterTextChanged(s: Editable?) {}

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
}