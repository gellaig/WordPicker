package com.gellaig.wordpicker.model

import android.widget.SeekBar
import android.widget.TextView

class DifficultyBarListener(var difficultyText: TextView): SeekBar.OnSeekBarChangeListener {
    override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
        difficultyText.text = when(p1){
            0 -> DifficultyEnum.EASY.type
            1 -> DifficultyEnum.MEDIUM.type
            2 -> DifficultyEnum.HARD.type
            else -> "Unselected difficulty"
        }
    }

    override fun onStartTrackingTouch(p0: SeekBar?) {}

    override fun onStopTrackingTouch(p0: SeekBar?) {}
}