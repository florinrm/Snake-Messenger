package com.example.snakemessenger.models

data class AudioPart(
        val partNo: Int = 0,
        val size: Int = 0,
        val content: ByteArray) : Comparable<AudioPart> {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as AudioPart

        if (partNo != other.partNo) return false
        if (size != other.size) return false
        if (!content.contentEquals(other.content)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = partNo
        result = 31 * result + size
        result = 31 * result + content.contentHashCode()
        return result
    }

    override fun compareTo(other: AudioPart) = partNo - other.partNo
}