package com.example.snakemessenger

import java.io.InputStream

object Utils {
    fun convertInputStreamToByteArray(stream: InputStream) = stream.readBytes()
}