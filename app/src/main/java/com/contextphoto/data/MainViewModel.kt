package com.contextphoto.data

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class MainViewModel: ViewModel() {
    private val _bottomMenu = mutableStateOf(false)
    private val _showDialog = mutableStateOf(false)
    private val _listPictures = MutableStateFlow<List<Picture>>(emptyList())
    var bottomMenu  = _bottomMenu
    var showDialog  = _showDialog
    val listPictures = _listPictures.asStateFlow()


    fun onPictureLongClick() {_bottomMenu.value = !_bottomMenu.value}
    fun onFBClick() {_showDialog.value = !_showDialog.value}


    fun add(pic: Picture) {
        _listPictures.update { oldList ->
            oldList + pic
        }
    }

}