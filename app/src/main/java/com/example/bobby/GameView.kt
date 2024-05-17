package com.example.bobby

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.view.MotionEvent
import android.view.View
import android.os.Handler
import android.view.Display
import android.graphics.Point
import android.app.Activity
import java.util.ArrayList
import java.util.Random

class GameView(context: Context) : View(context) {

    private var background: Bitmap
    private var ground: Bitmap
    private var rabbit: Bitmap
    private var rectBackground: Rect
    private var rectGround: Rect
    private var context: Context
    private var handler: Handler
    private val UPDATE_MILLIS = 30L
    private var runnable: Runnable
    private var textPaint: Paint = Paint()
    private var healthPaint: Paint = Paint()
    private val TEXT_SIZE = 120f
    private var life = 3 // Default life value
    private var points = 0
    private var random: Random
    private var rabbitX = 0f
    private var rabbitY = 0f
    private var oldX = 0f
    private var oldRabbitX = 0f
    private var spikes = ArrayList<Spike>()
    private var explosions = ArrayList<Explosion>()

    init {
        this.context = context
        background = BitmapFactory.decodeResource(resources, R.drawable.background)
        ground = BitmapFactory.decodeResource(resources, R.drawable.ground)
        rabbit = BitmapFactory.decodeResource(resources, R.drawable.ninja)
        val display: Display = (context as Activity).windowManager.defaultDisplay
        val size = Point()
        display.getSize(size)
        val dWidth = size.x
        val dHeight = size.y
        rectBackground = Rect(0, 0, dWidth, dHeight)
        rectGround = Rect(0, dHeight - ground.height, dWidth, dHeight)
        handler = Handler()
        runnable = object : Runnable {
            override fun run() {
                invalidate()
                handler.postDelayed(this, UPDATE_MILLIS)
            }
        }
        textPaint.color = Color.rgb(255, 165, 0)
        textPaint.textSize = TEXT_SIZE
        textPaint.textAlign = Paint.Align.LEFT
        healthPaint.color = Color.GREEN
        random = Random()
        rabbitX = dWidth / 2 - rabbit.width / 2.toFloat()
        rabbitY = dHeight - ground.height - rabbit.height.toFloat()
        for (i in 0 until 3) {
            val spike = Spike(context)
            spikes.add(spike)
            spike.resetPosition(dWidth, dHeight)
        }
        handler.post(runnable)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawBitmap(background, null, rectBackground, null)
        canvas.drawBitmap(ground, null, rectGround, null)
        canvas.drawBitmap(rabbit, rabbitX, rabbitY, null)
        for (i in spikes.indices) {
            spikes[i].getSpike(spikes[i].spikeFrame)?.let {
                canvas.drawBitmap(
                    it,
                    spikes[i].spikeX.toFloat(),
                    spikes[i].spikeY.toFloat(),
                    null
                )
            }
            spikes[i].spikeFrame++
            if (spikes[i].spikeFrame > 2) {
                spikes[i].spikeFrame = 0
            }
            spikes[i].spikeY += spikes[i].spikeVelocity
            if (spikes[i].spikeY + spikes[i].getSpikeHeight() >= height - ground.height) {
                points += 10
                val explosion = Explosion(context)
                explosion.explosionX = spikes[i].spikeX
                explosion.explosionY = spikes[i].spikeY
                explosions.add(explosion)
                spikes[i].resetPosition(width, height)
            }
        }
        for (i in spikes.indices) {
            if (spikes[i].spikeX + spikes[i].getSpikeWidth() >= rabbitX &&
                spikes[i].spikeX <= rabbitX + rabbit.width &&
                spikes[i].spikeY + spikes[i].getSpikeHeight() >= rabbitY &&
                spikes[i].spikeY <= rabbitY + rabbit.height
            ) {
                life--
                spikes[i].resetPosition(width, height)
                if (life == 0) {
                    val intent = Intent(context, GameOver::class.java)
                    intent.putExtra("points", points)
                    context.startActivity(intent)
                    (context as Activity).finish()
                }
            }
        }

        for (i in explosions.indices) {
            explosions[i].getExplosion(explosions[i].explosionFrame)?.let {
                canvas.drawBitmap(
                    it,
                    explosions[i].explosionX.toFloat(),
                    explosions[i].explosionY.toFloat(),
                    null
                )
            }
            explosions[i].explosionFrame++
            if (explosions[i].explosionFrame > 3) {
                explosions.removeAt(i)
            }
        }
        healthPaint.color = when {
            life == 2 -> Color.YELLOW
            life == 1 -> Color.RED
            else -> Color.GREEN
        }
        canvas.drawRect(width - 200.toFloat(), 30f, width - 200 + 60 * life.toFloat(), 80f, healthPaint)
        canvas.drawText("$points", 20f, TEXT_SIZE, textPaint)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val touchX = event.x
        val touchY = event.y
        if (touchY >= rabbitY) {
            val action = event.action
            if (action == MotionEvent.ACTION_DOWN) {
                oldX = event.x
                oldRabbitX = rabbitX
            }
            if (action == MotionEvent.ACTION_MOVE) {
                val shift = oldX - touchX
                val newRabbitX = oldRabbitX - shift
                rabbitX = when {
                    newRabbitX <= 0 -> 0f
                    newRabbitX >= width - rabbit.width -> (width - rabbit.width).toFloat()
                    else -> newRabbitX
                }
            }
        }
        return true
    }
}
