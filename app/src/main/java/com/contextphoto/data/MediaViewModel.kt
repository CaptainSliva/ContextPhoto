package com.contextphoto.data

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class MediaViewModel: ViewModel() {
    private val _listPictures = MutableStateFlow<List<Picture>>(emptyList())
    val _mediaPosition = MutableStateFlow<Int>(0)
    val listPictures = _listPictures.asStateFlow()
    val mediaPosition = _mediaPosition.asStateFlow()


    fun addPicture(pic: Picture) {
        _listPictures.update { currentList ->
            currentList.toMutableList().apply {
                add(pic)
            }
        }
    }

    fun updateMediaPosition(pos: Int) {
        _mediaPosition.value = pos
    }

    fun deletePicture(pic: Picture) {
        _listPictures.update { currentList ->
            currentList.toMutableList().apply {
                remove(pic)
            }
        }
    }

    fun resetMediaPosition() {
        _mediaPosition.value = 0
    }

    fun updatePicture(pic: Picture) {
        _listPictures.update { currentList ->
            currentList.map {
                if (it.bID == pic.bID) {
                    pic
                } else {
                    it
                }
            }
        }
    }

}