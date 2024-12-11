package com.capstone.plantcare.ui.detail

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.capstone.plantcare.R
import com.capstone.plantcare.databinding.ActivityDetailBinding

class DetailActivity : AppCompatActivity() {
    private lateinit var binding : ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val disease = intent.getStringExtra("disease")
        val plantType = intent.getStringExtra("plantType")
        val probability = intent.getFloatExtra("probability", 0f)
        val imageUri = intent.getStringExtra("imageUri")
        val treatment = intent.getStringExtra("treatment")


        binding.tvName.text = disease ?: "Tidak diketahui"
        val probabilityInt = (probability * 100).toInt()
        binding.tvAccuracy.text = "$probabilityInt%"
        binding.tvSubtitle.text = plantType ?: "Jenis tanaman tidak diketahui"
        binding.tvGuide.text = treatment ?: "Treatment tidak tersedia"

        imageUri?.let { uri ->
            Glide.with(this)
                .load(uri)
                .into(binding.previewImageView)
        }
    }

}