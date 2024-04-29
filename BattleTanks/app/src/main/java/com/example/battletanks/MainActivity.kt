package com.example.battletanks

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.media.MediaPlayer


//  extends
class MainActivity : AppCompatActivity(), GameTask {

    // Declare variables for UI elements
    private lateinit var rootLayout: LinearLayout
    private lateinit var startBtn: Button
    private lateinit var mGameView: GameView
    private lateinit var score: TextView
    private lateinit var highScoreTextView: TextView

    // Declare MediaPlayer for background music
    private lateinit var mediaPlayer: MediaPlayer

    // onCreate method called when activity is created
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main) // Set layout

        // Initialize UI elements
        startBtn = findViewById(R.id.startBtn)
        rootLayout = findViewById(R.id.rootLayout)
        score = findViewById(R.id.score)
        highScoreTextView = findViewById(R.id.highScore)
        mGameView = GameView(this, this) // Initialize GameView

        // Initialize MediaPlayer with background music
        mediaPlayer = MediaPlayer.create(this, R.raw.background_music)
        mediaPlayer.isLooping = true // Loop the music

        // Set background image
        rootLayout.setBackgroundResource(R.drawable.firstbackground)

        // Set click listener for start button to start the game
        startBtn.setOnClickListener {
            startGame() // Start the game when the button is clicked
        }

        // Set click listener for score text view to toggle visibility of high score
        score.setOnClickListener {
            toggleHighScoreVisibility() // Toggle visibility of high score when clicked
        }
    }

    // Method to start the game
    private fun startGame() {
        // Stop and start the background music to ensure it restarts
        mediaPlayer.stop()
        mediaPlayer.prepare() // Prepare the MediaPlayer for playback again
        mediaPlayer.start()

        // Set game background, add GameView to layout, and hide UI elements
        mGameView.setBackgroundResource(R.drawable.gamebackground)
        rootLayout.addView(mGameView)
        startBtn.visibility = View.GONE
        score.visibility = View.GONE
        highScoreTextView.visibility = View.GONE
    }

    // Method to toggle visibility of high score
    private fun toggleHighScoreVisibility() {
        highScoreTextView.visibility =
            if (highScoreTextView.visibility == View.VISIBLE) View.GONE else View.VISIBLE
        highScoreTextView.text = "High Score: ${getHighScore()}"
    }

    // Method called when game is closed
    override fun closeGame(mScore: Int) {
        // Stop background music when game ends
        mediaPlayer.stop()

        // Display score and update high score if necessary
        score.text = "Score: $mScore"
        val currentHighScore = getHighScore()
        if (mScore > currentHighScore) {
            saveHighScore(mScore)
            highScoreTextView.text = "High Score: $mScore"
        }

        // Remove GameView from layout and show UI elements
        rootLayout.removeView(mGameView)
        startBtn.visibility = View.VISIBLE
        score.visibility = View.VISIBLE
        highScoreTextView.visibility = View.VISIBLE

        // Reset game state
        mGameView.resetGameState()
    }

    // Method to save high score in SharedPreferences
    private fun saveHighScore(score: Int) {
        val sharedPreferences = getSharedPreferences("game_preferences", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putInt(HIGH_SCORE_KEY, score)
        editor.apply()
    }

    // Method to retrieve high score from SharedPreferences
    private fun getHighScore(): Int {
        val sharedPreferences = getSharedPreferences("game_preferences", Context.MODE_PRIVATE)
        return sharedPreferences.getInt(HIGH_SCORE_KEY, 0)
    }

    // Companion object to hold constant for high score key
    companion object {
        private const val HIGH_SCORE_KEY = "high_score"
    }

    // onDestroy method called when activity is destroyed
    override fun onDestroy() {
        super.onDestroy()
        // Release the MediaPlayer when the activity is destroyed
        mediaPlayer.release()
    }
}


