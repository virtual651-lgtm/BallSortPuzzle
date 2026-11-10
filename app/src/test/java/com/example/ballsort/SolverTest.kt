/*
 * com.example.ballsort
 * Solver çözülebilirlik ve ipucu doğrulama testleri.
 */
package com.example.ballsort

import com.example.ballsort.domain.DifficultyCurve
import com.example.ballsort.domain.GameLogic
import com.example.ballsort.domain.LevelGenerator
import com.example.ballsort.domain.Solver
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class SolverTest {

    @Test
    fun first100Levels_areSolvableAndMovesReachSolvedState() {
        for (levelId in 1..100) {
            val cfg = DifficultyCurve.configFor(levelId)
            val start = LevelGenerator.generate(cfg)
            val solution = Solver.solveSync(start, cfg.capacity, multiMove = true, maxVisited = 30_000)
            assertNotNull("Level $levelId çözülemedi", solution)

            var board = start
            for (move in solution!!) {
                val applied = GameLogic.applyMove(board, move.from, move.to, cfg.capacity, multiMove = true)
                assertNotNull("Geçersiz çözüm hamlesi: $move", applied)
                board = applied!!.first
            }
            assertTrue("Level $levelId çözüm sonunda bitmedi", GameLogic.isSolved(board, cfg.capacity))
        }
    }

    @Test
    fun hint_returnsValidMove() = runTest {
        val cfg = DifficultyCurve.configFor(5)
        val start = LevelGenerator.generate(cfg)
        val hintMove = Solver.hint(start, cfg.capacity, dispatcher = coroutineContext)
        assertNotNull(hintMove)
        assertTrue(GameLogic.canMove(start, hintMove!!.from, hintMove.to, cfg.capacity))
    }
}

