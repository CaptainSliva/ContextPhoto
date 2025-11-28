package com.contextphoto.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CommentDao {
    @Query("SELECT * FROM comments")
    fun getAllComments(): Flow<List<Comment>>

    @Query("SELECT * FROM comments WHERE LOWER(image_comment) LIKE LOWER('%' || :comment || '%')")
    fun findImageByComment(comment: String): Flow<List<Comment>>

    @Query("SELECT * FROM comments WHERE image_hash LIKE :hash")
    suspend fun findImageByHash(hash: String): Comment?

    @Query("UPDATE comments SET image_comment = :comment WHERE image_hash = :imageHash")
    suspend fun replaceCommentByHash(imageHash: String, comment: String): Int

    @Query("DELETE FROM comments WHERE image_hash = :imageHash")
    suspend fun deleteCommentByHash(vararg imageHash: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addComment(comment: Comment)

    @Delete
    suspend fun delete(vararg comments: Comment)

    @Query("DELETE FROM comments")
    suspend fun clearComments()
}