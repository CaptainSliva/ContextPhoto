package com.contextphoto.data

import android.R
import com.contextphoto.db.Comment
import jakarta.inject.Inject

class CommentsRepository @Inject constructor(private val fireBaseSource: FireBaseSource) {
    fun saveComments(uId: String, comments: List<Comment>) {
        fireBaseSource.saveComments(uId, comments)
    }

    fun deleteComments(uId: String) {
        fireBaseSource.deleteComments(uId)
    }

}