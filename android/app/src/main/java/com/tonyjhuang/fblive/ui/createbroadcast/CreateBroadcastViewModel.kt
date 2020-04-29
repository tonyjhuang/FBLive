package com.tonyjhuang.fblive.ui.createbroadcast

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class CreateBroadcastViewModel : ViewModel() {

    private var currentStateFormPosition = 0
    set(value) {
        field = value
        currentState.postValue(STATE_ORDER[value])
    }
    val currentState = MutableLiveData<FormState>(FormState.BROADCAST_NAME)

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
