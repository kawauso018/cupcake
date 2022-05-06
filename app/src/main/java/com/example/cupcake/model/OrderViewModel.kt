package com.example.cupcake.model

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

//const修飾子を使用し、読み取り専用にする(値はコンパイル時に認識されます)
private const val PRICE_PER_CUPCAKE = 2.00
private const val PRICE_FOR_SAME_DAY_PICKUP = 3.00

//ViewModelをサブクラス化することでViewModelから拡張できるようにする
class OrderViewModel: ViewModel() {

    private val _quantity = MutableLiveData<Int>()
    val quantity: LiveData<Int> = _quantity
    private val _flavor = MutableLiveData<String>()
    val flavor: LiveData<String> = _flavor
    private val _date = MutableLiveData<String>()
    val date: LiveData<String> = _date
    private val _price = MutableLiveData<Double>()
    val price: LiveData<String> = Transformations.map(_price){
        //価格を現地通貨形式にする
        NumberFormat.getCurrencyInstance().format(it)
    }

    val dateOptions = getPickupOptions()

    init{
        resetOrder()
    }


    fun setQuantity(numberCupcakes: Int) {
        _quantity.value = numberCupcakes
        updatePrice()
    }
    fun setFlavor(desiredFlavor: String) {
        _flavor.value = desiredFlavor
    }
    fun setDate(pickupDate: String) {
        _date.value = pickupDate
        updatePrice()
    }

    fun hasNoFlavorSet(): Boolean{
        return _flavor.value.isNullOrEmpty()
    }

    private fun getPickupOptions(): List<String> {
        val options = mutableListOf<String>()
        //日付表示のフォーマットを設定
        val formatter = SimpleDateFormat("E MMM d", Locale.getDefault())
        //現在の日付と時刻
        val calendar = Calendar.getInstance()
        //4日分の日付を取得
        repeat(4){
            //リストにフォーマットを指定した日付を追加
            options.add(formatter.format(calendar.time))
            //一日加算
            calendar.add(Calendar.DATE, 1)
        }
        return options
    }

    fun resetOrder() {
        _quantity.value = 0
        _flavor.value = ""
        _date.value = dateOptions[0]
        _price.value = 0.0
    }

    private fun updatePrice(){
        var calculatedPrice = (quantity.value ?:0) * PRICE_PER_CUPCAKE
        if(dateOptions[0] == _date.value){
            calculatedPrice += PRICE_FOR_SAME_DAY_PICKUP
        }
        _price.value = calculatedPrice
    }
}