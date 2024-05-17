package com.example.bobby

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.util.Random

class Spike(context: Context) {

    private val spike = arrayOfNulls<Bitmap>(3)
    var spikeFrame = 0
    var spikeX: Int = 0
    var spikeY: Int = 0
    var spikeVelocity: Int = 0
    private val random = Random()

    init {
        try {
            spike[0] = BitmapFactory.decodeResource(context.resources, R.drawable.spike0)
            spike[1] = BitmapFactory.decodeResource(context.resources, R.drawable.spike1)
            spike[2] = BitmapFactory.decodeResource(context.resources, R.drawable.spike2)
        } catch (e: Exception) {
            // Handle exception if resources are not found
            e.printStackTrace()
        }
    }

    fun getSpike(spikeFrame: Int): Bitmap? {
        return if (spikeFrame in 0 until spike.size) {
            spike[spikeFrame]
        } else {
            null
        }
    }

    fun getSpikeHeight(): Int {
        return spike[0]?.height ?: 0
    }

    fun getSpikeWidth(): Int {
        return spike[0]?.width ?: 0
    }

    fun resetPosition(dWidth: Int, dHeight: Int) {
        spikeX = random.nextInt(dWidth - getSpikeWidth())
        spikeY = -200 + random.nextInt(600) * -1
        spikeVelocity = 35 + random.nextInt(16)
    }
}
