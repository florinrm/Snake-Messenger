package com.example.snakemessenger.chats

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.MediaController
import com.example.snakemessenger.databinding.ActivityPreviewAudioBinding

class PreviewAudioActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPreviewAudioBinding
    private lateinit var mediaController: MediaController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPreviewAudioBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mediaController = MediaController(this)
        setUIAction()
    }

    override fun onBackPressed() {
        setResult(RESULT_CANCELED)
        super.onBackPressed()
    }

    private fun setUIAction() {
        binding.sendAudioBtn.setOnClickListener {
            setResult(RESULT_OK)
            finish()
        }
        binding.cancelBtn.setOnClickListener {
            setResult(RESULT_CANCELED)
            finish()
        }

        mediaController.setMediaPlayer(binding.audioImageView)
        binding.audioImageView.setMediaController(mediaController)
        binding.audioImageView.setVideoURI(Uri.parse(audioURI.toString()))
    }

    companion object {
        lateinit var audioURI: Uri
    }
}