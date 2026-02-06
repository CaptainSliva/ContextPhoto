package com.contextphoto.data

import androidx.lifecycle.ViewModel
import com.contextphoto.db.Comment
import com.google.firebase.auth.FirebaseAuth
import javax.inject.Inject

class CommentsViewModel
    @Inject
    constructor(
        private val repository: CommentsRepository,
        private val firebaseAuth: FirebaseAuth,
    ) : ViewModel() {
        fun saveComments(
            uId: String,
            comments: List<Comment>,
        ) {
            repository.saveComments(uId, comments)
        }

        fun deleteComments(uId: String) {
            repository.deleteComments(uId)
        }
    }
