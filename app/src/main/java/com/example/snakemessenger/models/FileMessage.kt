package com.example.snakemessenger.models

data class FileMessage(
        val messageId: String,
        val fileName: String,
        var payloadId: Long = 0,
        val sourceId: String,
        val destinationId: String,
        var timestamp: Long,
        val totalSize: Int,
        val fileExtension: String,
        val parts: MutableList<FilePart> = mutableListOf()) {

    constructor(messageId: String, fileName: String, sourceId: String, destinationId: String, timestamp: Long, totalSize: Int, fileExtension: String)
            : this(messageId = messageId, fileName = fileName, sourceId = sourceId, destinationId = destinationId, timestamp = timestamp, totalSize = totalSize, fileExtension = fileExtension, payloadId = 0)

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
