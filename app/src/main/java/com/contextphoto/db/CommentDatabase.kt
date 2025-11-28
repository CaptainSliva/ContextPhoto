package com.contextphoto.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import com.contextphoto.data.commentDatabase
import com.contextphoto.data.versionDB

@Database(
    entities = [Comment::class],
    version = versionDB,
    exportSchema = false
)

abstract class CommentDatabase : RoomDatabase() {
    abstract fun commentDao(): CommentDao

    companion object {
        @Volatile
        private var INSTANCE: CommentDatabase? = null

        fun getDatabse(context: Context): CommentDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    CommentDatabase::class.java,
                    commentDatabase
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}