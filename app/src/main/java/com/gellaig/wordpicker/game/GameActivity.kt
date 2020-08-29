package com.gellaig.wordpicker.game

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import androidx.appcompat.app.AppCompatActivity
import com.gellaig.wordpicker.MainActivity
import com.gellaig.wordpicker.R
import kotlinx.android.synthetic.main.activity_game.*
import kotlinx.android.synthetic.main.activity_main.*

class GameActivity : AppCompatActivity() {

    val words = listOf("Test", "This", "Word")

    // timer for seekbar
    private lateinit var timer: CountDownTimer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        scoreNumber.text = "0"

        handleTimerBar()
        handleEndButton()

    }

    //Game time in milisecs
    private fun getGameTimeByDifficulty() =
        when(intent.getIntExtra("difficultyLevel", 0)) {
            0 -> 45000
            1 -> 20000
            2 -> 10000
            else -> 45000
        }

    private fun increaseScore() {
        scoreNumber.text = Integer.valueOf(scoreNumber.text.toString()).inc().toString()
    }

    private fun handleEndButton() {
        endButton.setOnClickListener {
            timer.cancel()
            navigateToMainActivity()
        }
    }

    private fun navigateToMainActivity() = startActivity(Intent(this, MainActivity::class.java).apply {
        putExtra("scoreNumber", scoreNumber.text)
    })

    private fun handleTimerBar() {
        val gameTime = getGameTimeByDifficulty()
        timer = object : CountDownTimer(gameTime.toLong(), 500) {
            override fun onTick(millisUntilFinished: Long) {
                val total = ((millisUntilFinished.toFloat() / gameTime.toFloat()) * 100.0).toInt()
                println(total)
                timerBar.progress = total
            }

            override fun onFinish() {
                navigateToMainActivity()
            }
        }.start()
    }

}