package com.example.cakapAi.core.speech

import android.content.Context
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager

class AndroidAudioFeedbackController(private val context: Context) : AudioFeedbackController {
    
    private val toneGenerator = ToneGenerator(AudioManager.STREAM_MUSIC, 100)

    override fun playCorrectSound() {
        // High pitched short beep
        toneGenerator.startTone(ToneGenerator.TONE_PROP_PROMPT, 150)
    }

    override fun playIncorrectSound() {
        // Low pitched error beep
        toneGenerator.startTone(ToneGenerator.TONE_CDMA_SOFT_ERROR_LITE, 300)
    }

    override fun vibrateError() {
        val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(300, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(300)
        }
    }
}
