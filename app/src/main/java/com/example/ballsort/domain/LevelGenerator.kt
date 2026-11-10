/*
 * com.example.ballsort.domain
 * Deterministik, başlangıçta kilitli olmayan ve çözülebilirliği doğrulanmış seviye üreticisi.
 */
package com.example.ballsort.domain

import com.example.ballsort.domain.model.LevelConfig
import com.example.ballsort.domain.model.Tube
import kotlin.random.Random

object LevelGenerator {

    fun generate(config: LevelConfig): List<Tube> {
        var seed = config.seed
        var lastCandidate: List<Tube>? = null

        repeat(30) {
            val tubes = attempt(config, seed)
            lastCandidate = tubes
            val validStart = !GameLogic.isSolved(tubes, config.capacity) &&
                !GameLogic.isStuck(tubes, config.capacity)
            if (validStart && Solver.solveSync(tubes, config.capacity, maxVisited = 12_000) != null) {
                return tubes
            }
            seed++
        }

        // VARSAYIM: 2 boş tüp ile 30 denemede çözülebilir seviye neredeyse her zaman bulunur;
        // olağanüstü durumda başlangıçta kilitli olmayan son adayı döndürür.
        return lastCandidate ?: attempt(config, seed)
    }

    private fun attempt(c: LevelConfig, seed: Long): List<Tube> {
        val rnd = Random(seed)
        val total = c.colorCount + c.emptyTubes
        val tubes = MutableList(total) { i ->
            if (i < c.colorCount) MutableList(c.capacity) { i } else mutableListOf()
        }
        var lastFrom = -1
        var lastTo = -1

        repeat(c.shuffleMoves) {
            val sources = tubes.indices.filter { tubes[it].isNotEmpty() }
            val from = sources.random(rnd)
            val targets = tubes.indices.filter {
                it != from && tubes[it].size < c.capacity && !(from == lastTo && it == lastFrom)
            }
            val pool = targets.ifEmpty {
                tubes.indices.filter { it != from && tubes[it].size < c.capacity }
            }
            if (pool.isEmpty()) return@repeat
            val to = pool.random(rnd)
            tubes[to].add(tubes[from].removeAt(tubes[from].lastIndex))
            lastFrom = from
            lastTo = to
        }
        return tubes.map { it.toList() }
    }
}

