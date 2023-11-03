package ru.nsu.ccfit.tsd.pinmap

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import ru.nsu.ccfit.tsd.pinmap.databinding.ActivityPinCreateBinding

class PinCreateActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPinCreateBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPinCreateBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val secondButton: Button = binding.button
        secondButton.setOnClickListener {
            val context = secondButton.context
            val intent = Intent(context, MainActivity::class.java)
            context.startActivity(intent)
        }

    }
}