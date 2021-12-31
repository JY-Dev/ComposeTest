package com.jydev.composetest

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

object MessageHolder {
    private val _messageList = MutableLiveData<List<String>>()
    val messageList : LiveData<List<String>> = _messageList

    fun setMessageList(messageList : List<String>){
        _messageList.value = messageList
    }
}