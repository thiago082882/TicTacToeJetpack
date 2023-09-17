package com.thiago.tictactoegamecodingjetpackcompose.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TicTacToeViewModel : ViewModel() {

    private val _gamelevel: MutableStateFlow<Int> = MutableStateFlow(3)
    val gameLevel = _gamelevel.asStateFlow()

    private val _gameTurn = MutableStateFlow(0)
    val gameTurn = _gameTurn.asStateFlow()
    val listOfMoviments = mutableStateListOf(*initialMovements.toTypedArray())
    private val initialMovements: List<Movement>
        get() = MutableList(gameLevel.value * gameLevel.value) {
            Movement()
        }

    fun resetGame() {
        listOfMoviments.clear()
        _gameTurn.update { 0 }
        listOfMoviments.addAll(initialMovements)

}
    fun checkWin(index: Int, onWin: (Int?) -> Unit) {
        val fromLeft = (index % _gamelevel.value)
        val fromRight = _gamelevel.value - ((index % _gamelevel.value) + 1)

        val listOfRowIndexes = mutableListOf<Int>()
        for (i in 1..fromLeft) {
            val newIndex = index - i
            listOfRowIndexes.add(newIndex)

        }
        listOfRowIndexes.add(index)

        for (i in 1..fromRight) {
            val newIndex = index + i
            listOfRowIndexes.add(newIndex)
        }
        val listOfRowTurns = mutableListOf<Int?>()
        listOfRowIndexes.forEach {
            listOfRowTurns.add(listOfMoviments[it].turn)
        }
        if (listOfRowTurns.count { it == null } <= 0) {
            if (listOfRowTurns.distinct().count() == 1 && listOfRowTurns.count() > 1) {
                onWin(listOfRowTurns.first())

            }
        }
        val listOfVerticalIndexes = mutableListOf(index)
        for (i in 1.._gamelevel.value) {
            val newIndexTop = index - (_gamelevel.value * i)
            if (newIndexTop < 0) {
                break

            } else {
                listOfVerticalIndexes.add(newIndexTop)
            }
        }
        for (i in 1.._gamelevel.value) {
            val newIndexBottom = index + (_gamelevel.value * i)
            if (newIndexBottom > listOfMoviments.size - 1) {
                break

            } else {
                listOfVerticalIndexes.add(newIndexBottom)
            }
        }

        val listOfVerticallyTurns = mutableListOf<Int?>()

        listOfVerticalIndexes.forEach {
            listOfVerticallyTurns.add(listOfMoviments[it].turn)

        }

        if (listOfVerticallyTurns.count { it == null } <= 0) {
            if (listOfVerticallyTurns.distinct()
                    .count() == 1 && listOfVerticallyTurns.count() > 1
            ) {
                val winTurn = listOfVerticallyTurns.first()
                onWin(winTurn)
            }
        }
        val listOfBeveIndexes = mutableListOf<Int>()
        listOfBeveIndexes.add(index)
        for (i in 1.._gamelevel.value) {
            val newIndexBottom = index + ((_gamelevel.value + 1) * i)
            if (newIndexBottom <= listOfMoviments.size - 1) {
                listOfBeveIndexes.add(newIndexBottom)

            } else {
                break
            }

        }
        val listOfBevelTurns = mutableListOf<Int?>()
        listOfBeveIndexes.forEach {
            listOfBevelTurns.add(listOfMoviments[it].turn)
        }

        if (listOfVerticallyTurns.filterNotNull()
                .count() >= _gamelevel.value && listOfVerticallyTurns.count { it == null } <= 0
        ) {
            if (listOfBevelTurns.distinct().count() == 1 && listOfBevelTurns.count() > 1) {
                onWin(listOfBevelTurns.first())
            }
        }

        val listOfBeveIndexes2 = mutableListOf<Int>()
        for (i in 1.._gamelevel.value) {
            listOfBeveIndexes2.add(((_gamelevel.value - 1) * i))

        }
        val listOfBevelTurns2 = mutableListOf<Int?>()
        listOfBeveIndexes2.forEach {
            listOfBevelTurns2.add(listOfMoviments[it].turn)
        }
        if (listOfBevelTurns2.filterNotNull()
                .count() >= _gamelevel.value && listOfBevelTurns2.count { it == null } <= 0) {
            if (listOfBevelTurns2.distinct().count() == 1 && listOfBevelTurns2.count() > 1){
                onWin(listOfBevelTurns2.first())
            }

        }
    }

    fun newMoviment(index: Int, turn: Int? = _gameTurn.value) {
        if (!listOfMoviments[index].filled) {
            listOfMoviments[index] = listOfMoviments[index].copy(
                filled = true,
                turn = turn

            )
            changeTurn()
        }
    }

    fun randowMovements() {
        viewModelScope.launch {
            delay(500)
            val index = getFreeIndex()
            if (index != null) {
                newMoviment(index, turn = if (_gameTurn.value == 1) 0 else 1)
            }
        }

        fun changeGameLevel(newLevel: Int) {
            _gamelevel.update { newLevel }
            listOfMoviments.clear()
            listOfMoviments.addAll(initialMovements)
        }
    }

    fun getFreeIndex(): Int? {
        val indexesList = listOfMoviments.mapIndexed { index, movement ->
            if (!movement.filled) return@mapIndexed index else return@mapIndexed null

        }.filterNotNull()
        return if (indexesList.isEmpty()) {
            null
        } else {
            indexesList.random()
        }
    }

    fun changeTurn() {
        _gameTurn.update {
            if (it == 1) 0 else 1
        }
    }

    fun changeGameLevel(newLevel: Int) {
        _gamelevel.update { newLevel }
        listOfMoviments.clear()
        listOfMoviments.addAll(initialMovements)
    }
}

data class Movement(
    val filled: Boolean = false,
    val turn: Int? = null
)