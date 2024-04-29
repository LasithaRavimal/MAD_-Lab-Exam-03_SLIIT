package com.example.battletanks

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.view.MotionEvent
import android.view.View

// Define GameView class which extends View
class GameView(var c: Context, var gameTask: GameTask) : View(c) {

    // Initialize variables
    private var myPaint: Paint = Paint()
    private var speed = 1
    private var time = 0
    private var score = 0
    private var otherTanks = ArrayList<HashMap<String, Any>>()

    var viewWidth = 0
    var viewHeight = 0
    var myTankPosition = 0

    // SharedPreferences for storing high score
    private val preferences: SharedPreferences = c.getSharedPreferences("GamePreferences", Context.MODE_PRIVATE)

    // Constructor
    init {
        myPaint = Paint()
    }

    // Method to reset game state
    fun resetGameState() {
        otherTanks.clear()
        score = 0
        speed = 1
    }

    // onDraw method to draw the game elements
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        viewWidth = measuredWidth
        viewHeight = measuredHeight

        // Generate other Tanks randomly
        if (time % 700 < 10 + speed) {
            val map = HashMap<String, Any>()
            map["lane"] = (0..2).random()
            map["startTime"] = time
            otherTanks.add(map)
        }
        // Update game time
        time += 10 + speed

        // Set up drawing properties
        myPaint.style = Paint.Style.FILL

        // Draw the player's tank
        val TankWidth = viewWidth / 5
        val TankHeight = TankWidth + 10

        val d = resources.getDrawable(R.drawable.bluetank, null)
        d.setBounds(
            myTankPosition * viewWidth / 3 + viewWidth / 15 + 25,
            viewHeight - 2 - TankHeight,
            myTankPosition * viewWidth / 3 + viewWidth / 15 + TankWidth - 25,
            viewHeight - 2
        )
        d.draw(canvas)
        myPaint.color = Color.GREEN
        var highScore = getHighScore()

        // Iterate through other tanks
        for (i in otherTanks.indices) {
            try {
                val tankX = otherTanks[i]["lane"] as Int * viewWidth / 3 + viewWidth / 15
                val tankY = time - otherTanks[i]["startTime"] as Int
                val d2 = resources.getDrawable(R.drawable.redtank, null)

                d2.setBounds(
                    tankX + 25, tankY - TankHeight, tankX + TankWidth - 25, tankY
                )
                d2.draw(canvas)
                if (otherTanks[i]["lane"] as Int == myTankPosition) {
                    if (tankY > viewHeight - 2 - TankHeight && tankY < viewHeight - 2) {
                        gameTask.closeGame(score) // Close game if collision detected
                    }
                }
                if (tankY > viewHeight + TankHeight) {
                    otherTanks.removeAt(i)
                    score++
                    speed = 1 + Math.abs(score / 8)
                    if (score > highScore) {
                        highScore = score
                        saveHighScore(highScore)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        // Draw score, high score, and speed on canvas
        myPaint.isFakeBoldText = true
        myPaint.color = Color.WHITE
        myPaint.textSize = 60f
        canvas.drawText("Score : $score", 80f, 80f, myPaint)
        canvas.drawText("High Score : $highScore", 250f, 150f, myPaint)
        canvas.drawText("Speed : $speed", 600f, 80f, myPaint)
        invalidate() // Redraw the view
    }

    // onTouchEvent method to handle touch events
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when (event!!.action) {
            MotionEvent.ACTION_DOWN -> {
                val x1 = event.x
                if (x1 < viewWidth / 2) {
                    if (myTankPosition > 0) {
                        myTankPosition--
                    }
                }
                if (x1 > viewWidth / 2) {
                    if (myTankPosition < 2) {
                        myTankPosition++
                    }
                }
                invalidate() // Redraw the view after updating tank position
            }
            MotionEvent.ACTION_UP -> {
                // Handle touch up event if needed
            }
        }
        return true
    }

    // Method to save high score in SharedPreferences
    private fun saveHighScore(score: Int) {
        preferences.edit().putInt("HighScore", score).apply()
    }

    // Method to retrieve high score from SharedPreferences
    private fun getHighScore(): Int {
        return preferences.getInt("HighScore", 0)
    }
}