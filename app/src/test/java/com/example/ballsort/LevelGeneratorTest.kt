/*
 * com.example.ballsort
 * LevelGenerator deterministiklik ve dağılım testleri.
 */
package com.example.ballsort

import com.example.ballsort.domain.DifficultyCurve
import com.example.ballsort.domain.GameLogic
import com.example.ballsort.domain.LevelGenerator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test

class LevelGeneratorTest {

    @Test
    fun sameSeed_producesIdenticalBoard() {
        val cfg = DifficultyCurve.configFor(12)
        val first = LevelGenerator.generate(cfg)
        val second = LevelGenerator.generate(cfg)
        assertEquals(first, second)
    }

    @Test
    fun all200Levels_haveExactColorCountsAndValidStarts() {
        for (levelId in 1..200) {
            val cfg = DifficultyCurve.configFor(levelId)
            val tubes = LevelGenerator.generate(cfg)

            assertEquals(cfg.colorCount + cfg.emptyTubes, tubes.size)
            assertFalse("Level $levelId başlangıçta çözülmüş olmamalı", GameLogic.isSolved(tubes, cfg.capacity))
            assertFalse("Level $levelId başlangıçta çıkmazda olmamalı", GameLogic.isStuck(tubes, cfg.capacity))

            val colorHistogram = tubes.flatten().groupingBy { it }.eachCount()
            assertEquals(cfg.colorCount, colorHistogram.size)
            for (color in 0 until cfg.colorCount) {
                assertEquals("Level $levelId renk $color sayısı hatalı", cfg.capacity, colorHistogram[color])
            }
        }
    }
}

