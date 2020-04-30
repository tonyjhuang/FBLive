package com.tonyjhuang.fblive.ui.createbroadcast

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class CreateBroadcastViewModel : ViewModel() {

    private var formValues = FormValues()

    private var currentStateFormPosition = 0
        set(value) {
            field = value
            _currentState = STATE_ORDER[value]
            updateCanProceed()
        }

    private var _currentState: FormState = FormState.BROADCAST_NAME
    set(value) {
        field = value
        currentState.postValue(value)
    }
    val currentState = MutableLiveData<FormState>(_currentState)
    val canProceed = MutableLiveData<Boolean>(false)

    val isAuction = MutableLiveData<Boolean>()

    fun clear() {
        formValues = FormValues()
        currentStateFormPosition = 0
    }

    fun next() {
        when (currentStateFormPosition) {
            STATE_ORDER.size - 1 -> currentState.postValue(FormState.SUCCESS)
            else -> if (canProceed.value == true) currentStateFormPosition += 1
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
        updateCanProceed()
    }
    fun setProductName(input: String) {
        formValues.productName = input
        updateCanProceed()
    }
    fun setIsAuction(input: Boolean) {
        formValues.isAuction = input
        isAuction.postValue(input)
        updateCanProceed()
    }
    fun setProductPrice(input: Number) {
        formValues.productPrice = input
        updateCanProceed()
    }
    fun setProductInventory(input: Int) {
        formValues.productInventory = input
        updateCanProceed()
    }

    private fun updateCanProceed() {
        val canProceed = when (_currentState) {
            FormState.BROADCAST_NAME -> formValues.broadcastName.isNotBlank()
            FormState.PRODUCT_NAME -> formValues.productName.isNotBlank()
            FormState.IS_AUCTION -> formValues.isAuction != null
            FormState.PRODUCT_PRICE -> formValues.productPrice != 0.0
            FormState.PRODUCT_INVENTORY -> formValues.productInventory != 0
            else -> false
        }
        this.canProceed.postValue(canProceed)
    }

    data class FormValues(
        var broadcastName: String = "3",
        var productName: String = "3",
        var isAuction: Boolean? = false,
        var productPrice: Number = 01.0,
        var productInventory: Int = 1
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
