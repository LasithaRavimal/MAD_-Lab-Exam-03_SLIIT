package com.example.battletanks

interface GameTask {
    // Method signature for closing the game, takes an integer score as a parameter
    fun closeGame(mScore:Int)
}