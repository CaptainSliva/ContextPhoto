package com.contextphoto.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "comments",
    indices = [Index("id")]
)
data class Comment(
    @PrimaryKey(autoGenerate = true) val id: Int,
    @ColumnInfo(name = "image_uri") val image_uri: String,
    @ColumnInfo(name = "image_hash") val image_hash: String,
    @ColumnInfo(name = "image_comment") val image_comment: String
)
