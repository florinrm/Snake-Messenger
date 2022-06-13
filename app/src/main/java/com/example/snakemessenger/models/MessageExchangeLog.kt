package com.example.snakemessenger.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "logs")
data class MessageExchangeLog(
        @PrimaryKey(autoGenerate = true)
        val id: Int,
        @ColumnInfo(name = "sourceContactId")
        val sourceContactId: String,
        @ColumnInfo(name = "destinationContactId")
        val destinationContactId: String,
        @ColumnInfo(name = "sourceTimestamp")
        val sourceTimestamp: Long,
        @ColumnInfo(name = "destinationTimestamp")
        val destinationTimestamp: Long,
        @ColumnInfo(name = "routingNodes")
        val routingNodes: String,
)
