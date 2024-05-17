package com.example.bobby

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class GameOver : AppCompatActivity() {

     lateinit var tvPoints: TextView
     lateinit var tvHighest: TextView
     lateinit var ivNewHighest: ImageView
     lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.game_over)

        // Initialize views
        tvPoints = findViewById(R.id.tvPoints)
        tvHighest = findViewById(R.id.tvHighest)
        ivNewHighest = findViewById(R.id.ivNewHighest)

        // Get points from intent
        val points = intent.getIntExtra("points", 0)
        tvPoints.text = points.toString()

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("my_pref", Context.MODE_PRIVATE)

        // Retrieve highest score
        var highest = sharedPreferences.getInt("highest", 0)
        if (points > highest) {
            ivNewHighest.visibility = View.VISIBLE
            highest = points
            // Update highest score in SharedPreferences
            val editor = sharedPreferences.edit()
            editor.putInt("highest", highest)
            editor.apply()
        }
        // Set highest score text
        tvHighest.text = highest.toString()
    }


    fun restart(view: View) {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    fun exit(view: View) {
        finish()
    }
}
