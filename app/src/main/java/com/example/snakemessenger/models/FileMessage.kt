package com.example.snakemessenger.models

data class FileMessage(
        val messageId: String,
        var payloadId: Long = 0,
        val sourceId: String,
        val destinationId: String,
        var timestamp: Long,
        val totalSize: Int,
        val parts: MutableList<FilePart> = mutableListOf()) {

    constructor(messageId: String, sourceId: String, destinationId: String, timestamp: Long, totalSize: Int)
            : this(messageId = messageId, sourceId = sourceId, destinationId = destinationId, timestamp = timestamp, totalSize = totalSize, payloadId = 0)

    fun getCurrentSize(): Int {
        var size = 0
        for (part in parts) {
            size += part.size
        }
        return size
    }

    fun addPart(part: FilePart) {
        parts.add(part)
    }
}
