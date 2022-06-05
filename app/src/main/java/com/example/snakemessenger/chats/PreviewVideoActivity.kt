package com.example.snakemessenger.chats

import android.net.Uri
import android.os.Bundle
import android.widget.MediaController
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.snakemessenger.databinding.ActivityPreviewVideoBinding

class PreviewVideoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPreviewVideoBinding
    private lateinit var mediaController: MediaController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPreviewVideoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mediaController = MediaController(this)
        setUIAction()
    }

    override fun onBackPressed() {
        setResult(RESULT_CANCELED)
        super.onBackPressed()
    }

    private fun setUIAction() {
        binding.sendVideoBtn.setOnClickListener {
            setResult(RESULT_OK)
            finish()
        }
        binding.cancelBtn.setOnClickListener {
            setResult(RESULT_CANCELED)
            finish()
        }

        mediaController.setMediaPlayer(binding.videoImageView)
        binding.videoImageView.setMediaController(mediaController)
        binding.videoImageView.setVideoURI(Uri.parse(videoURI.toString()))
    }

    companion object {
        lateinit var videoURI: Uri
    }
}