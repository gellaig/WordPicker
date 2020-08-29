package com.gellaig.wordpicker

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.AlarmClock.EXTRA_MESSAGE
import android.view.View
import com.gellaig.wordpicker.game.GameActivity
import com.gellaig.wordpicker.model.DifficultyBarListener
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        handleDifficulty()
        handleStartButton()
        handleScore()

    }

    private fun handleScore() {
        scoreNumberText.visibility = View.INVISIBLE
        intent.getStringExtra("scoreNumber")?.let {
            scoreNumberText.visibility = View.VISIBLE
            val text = "Final score: $it"
            scoreNumberText.text = text
        }

    }

    private fun handleStartButton() {
        startButton.setOnClickListener {
            startActivity(Intent(this, GameActivity::class.java).apply {
                putExtra("difficultyLevel", difficultyBar.progress)
            })
        }
    }

    private fun handleDifficulty() = difficultyBar.setOnSeekBarChangeListener(DifficultyBarListener(difficultyText))

}