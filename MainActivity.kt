package com.mulokozi.tictactac_2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHost
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import androidx.navigation.compose.rememberNavController

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TicTacToeApp()
        }
    }
}

@Composable
fun TicTacToeApp() {
    // Initialize NavController
    val navController = rememberNavController()

    // Set up NavHost for navigation
    NavHost(navController = navController, startDestination = "welcome") {
        composable("welcome") {
            WelcomePage(navController)
        }
        composable("choose_mode") {
            ChooseModePage(navController)
        }
        composable("game/{mode}") { backStackEntry ->
            val mode = backStackEntry.arguments?.getString("mode") ?: "Human vs Human"
            TicTacToeGame(navController, mode)
        }
    }
}

@Composable
fun WelcomePage(navController: NavHostController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.LightGray)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "TIC TAC TOE",
            fontSize = 40.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(32.dp))
        Button(
            onClick = {
                navController.navigate("choose_mode") // Navigate to Choose Mode page
            },
            modifier = Modifier.padding(16.dp)
        ) {
            Text(text = "Click Here to Start")
        }
    }
}

@Composable
fun ChooseModePage(navController: NavHostController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.LightGray)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Choose Mode",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(32.dp))
        Button(
            onClick = {
                navController.navigate("game/Human vs Human") // Navigate to game page with selected mode
            },
            modifier = Modifier.padding(16.dp)
        ) {
            Text(text = "Human vs Human")
        }
        Button(
            onClick = {
                navController.navigate("game/Human vs Computer") // Navigate to game page with selected mode
            },
            modifier = Modifier.padding(16.dp)
        ) {
            Text(text = "Human vs Computer")
        }
    }
}

@Composable
fun TicTacToeGame(navController: NavHostController, mode: String) {
    var currentPlayer by remember { mutableStateOf("X") }
    var board by remember { mutableStateOf(Array(3) { arrayOfNulls<String>(3) }) }
    var isGameActive by remember { mutableStateOf(true) }
    var statusText by remember { mutableStateOf("Player X's Turn") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.LightGray)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = statusText,
            fontSize = 24.sp,
            color = Color.Black,
            modifier = Modifier.padding(16.dp),
            textAlign = TextAlign.Center
        )
        Box(
            modifier = Modifier
                .size(300.dp)
                .background(Color(0xFF6650a4)),
            contentAlignment = Alignment.Center
        ) {
            Column {
                for (row in 0..2) {
                    Row {
                        for (col in 0..2) {
                            TicTacToeCell(
                                player = board[row][col],
                                enabled = isGameActive && board[row][col] == null,
                                onClick = {
                                    board[row][col] = currentPlayer
                                    if (checkWinner(board, row, col, currentPlayer)) {
                                        statusText = "Player $currentPlayer Wins!"
                                        isGameActive = false
                                    } else if (board.flatten().none { it == null }) {
                                        statusText = "It's a Draw!"
                                        isGameActive = false
                                    } else {
                                        currentPlayer = if (currentPlayer == "X") "O" else "X"
                                        statusText = "Player $currentPlayer's Turn"
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
        Button(
            onClick = {
                board = Array(3) { arrayOfNulls<String>(3) }
                currentPlayer = "X"
                isGameActive = true
                statusText = "Player X's Turn"
            },
            modifier = Modifier.padding(16.dp)
        ) {
            Text(text = "Reset Game")
        }
        if (!isGameActive) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = statusText,
                    fontSize = 24.sp,
                    color = Color.Black,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        navController.popBackStack("choose_mode", false) // Navigate back to Choose Mode page
                    },
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(text = "Reset Game")
                }
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = {
                        // Exit game logic can be added here, for now we just pop back
                    },
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(text = "Exit Game")
                }
            }
        }
    }
}

@Composable
fun TicTacToeCell(player: String?, enabled: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(100.dp)
            .padding(4.dp)
            .background(
                if (enabled) Color.Gray else Color.LightGray,
                shape = CircleShape
            )
            .clickable(enabled = enabled) { onClick() }
    ) {
        Text(
            text = player.orEmpty(),
            fontSize = 32.sp,
            color = Color.Black,
            textAlign = TextAlign.Center,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

fun checkWinner(board: Array<Array<String?>>, row: Int, col: Int, currentPlayer: String): Boolean {
    if (board[row].all { it == currentPlayer }) return true
    if (board.all { it[col] == currentPlayer }) return true
    if (row == col && board.indices.all { board[it][it] == currentPlayer }) return true
    if (row + col == 2 && board.indices.all { board[it][2 - it] == currentPlayer }) return true
    return false
}
