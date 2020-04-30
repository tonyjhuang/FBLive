package com.tonyjhuang.fblive.ui.createbroadcast

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class CreateBroadcastViewModel : ViewModel() {

    private val formValues = FormValues()

    private var currentStateFormPosition = 0
        set(value) {
            field = value
            currentState.postValue(STATE_ORDER[value])
        }
    val currentState = MutableLiveData<FormState>(FormState.BROADCAST_NAME)

    val isAuction = MutableLiveData<Boolean>()

    fun clear() {

    }

    fun next() {
        when (currentStateFormPosition) {
            STATE_ORDER.size - 1 -> currentState.postValue(FormState.SUCCESS)
            else -> currentStateFormPosition += 1
        }
    }

    fun prev() {
        when (currentStateFormPosition) {
            0 -> currentState.postValue(FormState.EXIT)
            else -> currentStateFormPosition -= 1
        }
    }

    fun setBroadcastName(input: String) {
        formValues.broadcastName = input
    }
    fun setProductName(input: String) {
        formValues.productName = input
    }
    fun setIsAuction(input: Boolean) {
        formValues.isAuction = input
        isAuction.postValue(input)
    }
    fun setProductPrice(input: Number) {
        formValues.productPrice = input
    }
    fun setProductInventory(input: Int) {
        formValues.productInventory = input
    }

    data class FormValues(
        var broadcastName: String = "",
        var productName: String = "",
        var isAuction: Boolean = false,
        var productPrice: Number = 0.0,
        var productInventory: Int = 0
    )

    enum class FormState {
        EXIT,
        BROADCAST_NAME,
        PRODUCT_NAME,
        IS_AUCTION,
        PRODUCT_PRICE,
        PRODUCT_INVENTORY,
        SUCCESS
    }

    companion object {
        val STATE_ORDER = listOf(
            FormState.BROADCAST_NAME,
            FormState.PRODUCT_NAME,
            FormState.IS_AUCTION,
            FormState.PRODUCT_PRICE,
            FormState.PRODUCT_INVENTORY
        )
    }
}
