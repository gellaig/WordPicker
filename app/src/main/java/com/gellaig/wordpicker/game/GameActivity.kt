package com.gellaig.wordpicker.game

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.view.Gravity
import android.view.View
import android.widget.TableRow
import android.widget.ToggleButton
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.children
import com.gellaig.wordpicker.MainActivity
import com.gellaig.wordpicker.R
import kotlinx.android.synthetic.main.activity_game.*


class GameActivity : AppCompatActivity() {

    // timer for seekbar
    private lateinit var timer: CountDownTimer
    private lateinit var words: Array<String>
    private val foundWords = mutableListOf<String>()
    private val MAX_LETTER_BUTTONS_NUMBER = 24
    private var currentSelectedLetters = mutableMapOf<Int, String>()
    private var letterButtonIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        scoreNumber.text = "0"
        timer = startTimer()
        words = resources.getStringArray(R.array.english_words)

        drawLetterButtons(true)
        handleEndButton()

    }

    private fun fillRandomLetters(init: Boolean, currentTableSize: Int): String {
        var randomWords = ""
        if (init) randomWords = getRandomWord(1..2) + getRandomWord(1..2) + getRandomWord(2..3) + getRandomWord(2..3)

        var maxLength = MAX_LETTER_BUTTONS_NUMBER - (currentTableSize + randomWords.length)
        while (maxLength > 1) {
            // println("maxLength:$maxLength")
            randomWords += getRandomWord(2..maxLength)
            maxLength = MAX_LETTER_BUTTONS_NUMBER - (currentTableSize + randomWords.length)
        }
        return randomWords
    }

    private fun drawLetterButtons(init: Boolean = false) {
        fun isNewRowNeeded(i: Int) = i % 4 == 0

        val currentTableSize = getLetterButtons().count()
        // println("currentTableSize:$currentTableSize")

        val randomLetters = fillRandomLetters(init, currentTableSize).toCharArray()

        if (currentTableSize + randomLetters.size <= MAX_LETTER_BUTTONS_NUMBER )
            for ((i, letter) in randomLetters.withIndex()) {
                val currentRow = if (isNewRowNeeded(i)) TableRow(this) else gameTable.children.last() as TableRow

                currentRow.apply {
                    gravity = Gravity.CENTER
                    addView(createToggleButton(letterButtonIndex++, letter.toString()))
                }

                if (isNewRowNeeded(i)) gameTable.addView(currentRow)
            }

        shuffleLetterButtons()
    }

    private fun shuffleLetterButtons() {
        gameTable.children
            .map { it as TableRow }
            .forEach {
                it.children.toMutableList().shuffle()

            }

        gameTable.children.toMutableList().shuffle()
    }

    private fun checkSelectedButtons() {
        val pressedLetterButtons = getLetterButtons().filter { it.isChecked}
        val currentWord = currentSelectedLetters.map { it.value }.joinToString("")

        println("currentWord: $currentWord")

        if (words.any { it == currentWord && currentWord !in foundWords}) {
            handleWordFound(pressedLetterButtons, currentWord)
            drawLetterButtons()
        }
    }

    private fun getLetterButtons() = gameTable.children.map { it as TableRow }.flatMap { it.children.map { it as ToggleButton } }.toMutableList()

    private fun handleWordFound(buttons: List<ToggleButton>, currentWord: String) {
        timer.restart()
        foundWords.add(currentWord)
        increaseScore(currentSelectedLetters.size)
        currentSelectedLetters.clear()

        buttons.forEach {
            it.isChecked = false
            it.visibility = View.GONE
        }

        gameTable.children
            .map { it as TableRow }
            .forEach {tableRow ->
                getLetterButtons().filter { it.visibility == View.GONE }.forEach {
                    tableRow.removeView(it)
                }
            }
    }

    private fun CountDownTimer.restart() {
        this.cancel()
        this.start()
    }

    private fun createToggleButton(index: Int, letter: String) =
        ToggleButton(this).apply {
            id = index
            text = letter
            textOff = text
            textOn = text

            setTextColor(Color.WHITE)
            background = ContextCompat.getDrawable(context!!, R.drawable.letter_button_shape)

            setOnCheckedChangeListener { buttonView, isChecked ->
                if(isChecked)
                    currentSelectedLetters[buttonView.id] = buttonView.text.toString()
                else currentSelectedLetters.remove(buttonView.id)

                checkSelectedButtons()

                buttonView.background = if (isChecked) ContextCompat.getDrawable(context!!, R.drawable.letter_button_pressed_shape)
                else ContextCompat.getDrawable(context!!, R.drawable.letter_button_shape)
            }
        }
    
    private fun getRandomWord(range: IntRange): String {
       var retVal = ""
        runCatching {
            retVal = words.filter { it.length in range && it !in foundWords }.random()
        }.onFailure {
            // retVal = words.filter { it.length in range.first+1..range.last+1 && it !in foundWords }.random()
            retVal = ""
        }
        return retVal
    }

    //Game time in milisecs
    private fun getGameTimeByDifficulty() =
        when(intent.getIntExtra("difficultyLevel", 0)) {
            0 -> 45000
            1 -> 20000
            2 -> 10000
            else -> 45000
        }

    private fun increaseScore(score: Int) {
        scoreNumber.text = Integer.valueOf(scoreNumber.text.toString()).plus(score).toString()
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

    private fun startTimer(): CountDownTimer {
        val gameTime = getGameTimeByDifficulty()
        return object : CountDownTimer(gameTime.toLong(), 100) {
            override fun onTick(millisUntilFinished: Long) {
                val total = ((millisUntilFinished.toFloat() / gameTime.toFloat()) * 100.0).toInt()
                timerBar.progress = total
            }

            override fun onFinish() {
                navigateToMainActivity()
            }
        }.start()
    }
}