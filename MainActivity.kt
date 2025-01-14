package com.example.tictactoe

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.gridlayout.widget.GridLayout

class MainActivity : AppCompatActivity() {

    private var currentPlayer = "X"
    private var board = Array(3) { arrayOfNulls<String>(3) }
    private var isGameActive = true

    @override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val gridLayout = findViewById<GridLayout>(R.id.gridLayout)
        val resetButton = findViewById<Button>(R.id.resetButton)
        val statusText = findViewById<TextView>(R.id.statusText)

        // Set click listeners for buttons
        for (i in 0 until gridLayout.childCount) {
            val button = gridLayout.getChildAt(i) as Button
            button.setOnClickListener { onCellClicked(button, i) }
        }

        // Reset game on button click
        resetButton.setOnClickListener { resetGame() }
    }

    private fun onCellClicked(button: Button, index: Int) {
        if (button.text.isNotEmpty() || !isGameActive) return

        val row = index / 3
        val col = index % 3

        board[row][col] = currentPlayer
        button.text = currentPlayer

        if (checkWinner(row, col)) {
            findViewById<TextView>(R.id.statusText).text = "Player $currentPlayer Wins!"
            isGameActive = false
        } else if (board.flatten().none { it.isNullOrEmpty() }) {
            findViewById<TextView>(R.id.statusText).text = "It's a Draw!"
            isGameActive = false
        } else {
            currentPlayer = if (currentPlayer == "X") "O" else "X"
            findViewById<TextView>(R.id.statusText).text = "Player $currentPlayer's Turn"
        }
    }

    private fun checkWinner(row: Int, col: Int): Boolean {
        // Check row
        if (board[row].all { it == currentPlayer }) return true

        // Check column
        if (board.all { it[col] == currentPlayer }) return true

        // Check diagonals
        if (row == col && board.indices.all { board[it][it] == currentPlayer }) return true
        if (row + col == 2 && board.indices.all { board[it][2 - it] == currentPlayer }) return true

        return false
    }

    private fun resetGame() {
        board = Array(3) { arrayOfNulls<String>(3) }
        currentPlayer = "X"
        isGameActive = true

        val gridLayout = findViewById<GridLayout>(R.id.gridLayout)
        for (i in 0 until gridLayout.childCount) {
            val button = gridLayout.getChildAt(i) as Button
            button.text = ""
        }

        findViewById<TextView>(R.id.statusText).text = "Player X's Turn"
    }
}
