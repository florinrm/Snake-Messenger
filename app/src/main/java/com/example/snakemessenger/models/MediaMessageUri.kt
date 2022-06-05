package com.example.snakemessenger.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "mediamessageuri")
data class MediaMessageUri(
        @PrimaryKey
        @ColumnInfo(name = "messageId")
        val messageId: String,
        @ColumnInfo(name = "videoUri")
        val videoUri: String
)
