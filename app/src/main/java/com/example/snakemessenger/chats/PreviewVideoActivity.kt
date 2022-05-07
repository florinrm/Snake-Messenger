package com.example.snakemessenger.chats

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.snakemessenger.databinding.ActivityPreviewVideoBinding

class PreviewVideoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPreviewVideoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPreviewVideoBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}