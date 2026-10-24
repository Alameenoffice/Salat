package com.example.sensor

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.exp
import kotlin.math.sin

/**
 * Synthesizes a reverent Maqam Hijaz Adhan chime & Takbir melody offline using Android AudioTrack.
 */
object AdhanAudioSynthesizer {
    private var currentJob: Job? = null
    private var currentTrack: AudioTrack? = null

    private data class Note(val freqHz: Double, val durationMs: Int)

    // Maqam Hijaz melodic Takbir cadence (D4, F#4, G4, A4, Bb4, G4, F#4, Eb4, D4)
    private val HIJAZ_NOTES = listOf(
        Note(293.66, 700),
        Note(369.99, 700),
        Note(392.00, 950),
        Note(440.00, 650),
        Note(392.00, 550),
        Note(369.99, 750),
        Note(311.13, 550),
        Note(293.66, 1200),
        Note(392.00, 650),
        Note(440.00, 750),
        Note(466.16, 750),
        Note(440.00, 700),
        Note(392.00, 650),
        Note(369.99, 750),
        Note(293.66, 1400)
    )

    fun startAdhanMelody(scope: CoroutineScope) {
        stopAdhanMelody()
        currentJob = scope.launch(Dispatchers.IO) {
            try {
                val sampleRate = 22050
                val totalSamples = HIJAZ_NOTES.sumOf { (sampleRate * it.durationMs) / 1000 }
                val pcm = ShortArray(totalSamples)
                var offset = 0

                for (note in HIJAZ_NOTES) {
                    if (!isActive) break
                    val count = (sampleRate * note.durationMs) / 1000
                    for (i in 0 until count) {
                        val t = i.toDouble() / sampleRate
                        val attack = (i.toDouble() / (sampleRate * 0.08)).coerceAtMost(1.0)
                        val decay = exp(-1.6 * t)
                        val env = attack * decay
                        val wave = sin(2.0 * PI * note.freqHz * t) * 0.75 +
                            sin(4.0 * PI * note.freqHz * t) * 0.25
                        val sample = (wave * env * 12000).toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt())
                        if (offset < pcm.size) {
                            pcm[offset++] = sample.toShort()
                        }
                    }
                }

                val track = AudioTrack.Builder()
                    .setAudioAttributes(
                        AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                            .build()
                    )
                    .setAudioFormat(
                        AudioFormat.Builder()
                            .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                            .setSampleRate(sampleRate)
                            .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                            .build()
                    )
                    .setBufferSizeInBytes(pcm.size * 2)
                    .setTransferMode(AudioTrack.MODE_STATIC)
                    .build()

                currentTrack = track
                track.write(pcm, 0, pcm.size)
                track.play()
            } catch (_: Exception) {
            }
        }
    }

    fun stopAdhanMelody() {
        currentJob?.cancel()
        currentJob = null
        try {
            currentTrack?.stop()
            currentTrack?.release()
        } catch (_: Exception) {
        }
        currentTrack = null
    }
}
