/*
 * com.example.ballsort
 * GameLogic saf Kotlin birim testleri.
 */
package com.example.ballsort

import com.example.ballsort.domain.GameLogic
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class GameLogicTest {

    @Test
    fun canMove_validatesRulesProperly() {
        val tubes = listOf(
            listOf(0, 1),
            listOf(1),
            emptyList(),
            listOf(0, 0, 0, 0),
        )
        assertFalse(GameLogic.canMove(tubes, 0, 0, 4)) // aynı tüp
        assertFalse(GameLogic.canMove(tubes, 2, 1, 4)) // boş kaynak
        assertFalse(GameLogic.canMove(tubes, 0, 3, 4)) // dolu hedef
        assertTrue(GameLogic.canMove(tubes, 0, 1, 4))  // aynı üst renk
        assertTrue(GameLogic.canMove(tubes, 0, 2, 4))  // boş hedef
        assertFalse(GameLogic.canMove(tubes, 3, 1, 4)) // farklı üst renk
    }

    @Test
    fun moveCount_respectsMultiMoveAndFreeSpace() {
        val tubes = listOf(
            listOf(0, 1, 1, 1),
            listOf(1, 1),
            listOf(1, 1, 1),
        )
        assertEquals(2, GameLogic.moveCount(tubes, 0, 1, 4, multiMove = true))
        assertEquals(1, GameLogic.moveCount(tubes, 0, 1, 4, multiMove = false))
        assertEquals(1, GameLogic.moveCount(tubes, 0, 2, 4, multiMove = true))
    }

    @Test
    fun applyMove_andUndo_areImmutableAndReversible() {
        val initial = listOf(
            listOf(0, 1, 1),
            listOf(1),
            emptyList(),
        )
        val snapshot = initial.map { it.toList() }

        val applied = GameLogic.applyMove(initial, 0, 1, 4, multiMove = true)
        assertNotNull(applied)
        val (nextTubes, move) = applied!!

        // Orijinal liste değişmemeli
        assertEquals(snapshot, initial)
        assertEquals(listOf(0), nextTubes[0])
        assertEquals(listOf(1, 1, 1), nextTubes[1])
        assertEquals(2, move.count)

        val undone = GameLogic.undoMove(nextTubes, move)
        assertEquals(initial, undone)
    }

    @Test
    fun applyMove_returnsNullOnInvalidMove() {
        val tubes = listOf(listOf(0), listOf(1))
        assertNull(GameLogic.applyMove(tubes, 0, 1, 4))
    }

    @Test
    fun isSolved_requiresFullUniformOrEmptyTubes() {
        val notFull = listOf(listOf(0, 0), listOf(1, 1, 1, 1), emptyList())
        assertFalse(GameLogic.isSolved(notFull, 4))

        val solved = listOf(listOf(0, 0, 0, 0), listOf(1, 1, 1, 1), emptyList())
        assertTrue(GameLogic.isSolved(solved, 4))
    }

    @Test
    fun isStuck_detectsDeadlock() {
        // Kapasite 2, boş tüp yok ve hiçbir top hareket edemiyor
        val deadlocked = listOf(
            listOf(0, 1),
            listOf(1, 0),
        )
        assertTrue(GameLogic.isStuck(deadlocked, 2))
    }
}

