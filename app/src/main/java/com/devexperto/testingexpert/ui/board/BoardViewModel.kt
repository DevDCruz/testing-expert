package com.devexperto.testingexpert.ui.board

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devexperto.testingexpert.data.GameRepository
import com.devexperto.testingexpert.domain.GameState
import com.devexperto.testingexpert.domain.TicTacToe
import com.devexperto.testingexpert.domain.findWinner
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BoardViewModel @Inject constructor(private val gameRepository: GameRepository) : ViewModel() {

    private val _state = MutableStateFlow(UiState())
    val state = _state.asStateFlow()

    fun startGame() {
        viewModelScope.launch {
            gameRepository.game.collect { game ->
                _state.value = UiState(
                    ticTacToe = game,
                    gameState = when (val winner = game.findWinner()) {
                        null -> GameState.InProgress
                        else -> GameState.Finished(winner)
                    }
                )
            }
        }
    }

    fun move(row: Int, column: Int) {
        viewModelScope.launch {
            gameRepository.move(row, column)
        }
    }

    fun resetGame() {
        viewModelScope.launch {
            gameRepository.reset()
            startGame()
        }
    }

    data class UiState(
        val ticTacToe: TicTacToe = TicTacToe(),
        val gameState: GameState = GameState.NotStarted
    )
}