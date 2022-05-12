package pt.isel.pdm.chess4android.controllers.about_activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import pt.isel.pdm.chess4android.BuildConfig
import pt.isel.pdm.chess4android.databinding.ActivityAboutBinding

class AboutActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityAboutBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.BuildVersion.text = BuildConfig.VERSION_NAME
    }
}