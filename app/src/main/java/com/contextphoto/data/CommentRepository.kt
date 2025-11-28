package com.contextphoto.data

import android.content.Context
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.contextphoto.db.Comment
import com.contextphoto.db.CommentDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class CommentRepository(context: Context) {
    private val database = CommentDatabase.getDatabse(context)
    private val commentDao = database.commentDao()

    fun getAllComments() = commentDao.getAllComments()

    fun findImageByComment(comment: String) = commentDao.findImageByComment(comment)

    suspend fun findImageByHash(hash: String) = withContext(Dispatchers.IO) {
        commentDao.findImageByHash(hash)
    }

    suspend fun replaceCommentByHash(imageHash: String, comment: String) = withContext(Dispatchers.IO) {
        commentDao.replaceCommentByHash(imageHash, comment)
    }

    suspend fun deleteCommentByHash(vararg imageHash: String) = withContext(Dispatchers.IO) {
        commentDao.deleteCommentByHash(*imageHash)
    }

    suspend fun addComment(comment: Comment) = withContext(Dispatchers.IO) {
        commentDao.addComment(comment)
    }

    suspend fun delete(vararg comments: Comment) = withContext(Dispatchers.IO) {
        commentDao.delete(*comments)
    }

    suspend fun clearComments() = withContext(Dispatchers.IO) {
        commentDao.clearComments()
    }
}