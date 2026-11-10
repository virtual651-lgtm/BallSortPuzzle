/*
 * com.example.ballsort.domain
 * Ball Sort oyununun saf Kotlin kuralları (değişmez/immutable listeler üretir).
 */
package com.example.ballsort.domain

import com.example.ballsort.domain.model.Move
import com.example.ballsort.domain.model.Tube

object GameLogic {

    fun canMove(tubes: List<Tube>, from: Int, to: Int, capacity: Int): Boolean {
        if (from !in tubes.indices || to !in tubes.indices) return false
        if (from == to) return false
        val src = tubes[from]
        val dst = tubes[to]
        if (src.isEmpty()) return false
        if (dst.size >= capacity) return false
        if (dst.isEmpty()) return true
        return dst.last() == src.last()
    }

    fun moveCount(
        tubes: List<Tube>,
        from: Int,
        to: Int,
        capacity: Int,
        multiMove: Boolean = true,
    ): Int {
        if (!canMove(tubes, from, to, capacity)) return 0
        if (!multiMove) return 1
        val src = tubes[from]
        val color = src.last()
        val free = capacity - tubes[to].size
        var run = 0
        for (i in src.indices.reversed()) {
            if (src[i] == color) run++ else break
        }
        return minOf(run, free)
    }

    /** Hamle geçerliyse (yeni tüpler, Move), değilse null döner. */
    fun applyMove(
        tubes: List<Tube>,
        from: Int,
        to: Int,
        capacity: Int,
        multiMove: Boolean = true,
    ): Pair<List<Tube>, Move>? {
        val count = moveCount(tubes, from, to, capacity, multiMove)
        if (count == 0) return null
        val next = tubes.map { it.toMutableList() }
        repeat(count) {
            next[to].add(next[from].removeAt(next[from].lastIndex))
        }
        return next.map { it.toList() } to Move(from, to, count)
    }

    fun undoMove(tubes: List<Tube>, move: Move): List<Tube> {
        if (move.from !in tubes.indices || move.to !in tubes.indices) return tubes
        val next = tubes.map { it.toMutableList() }
        val actualCount = minOf(move.count, next[move.to].size)
        repeat(actualCount) {
            next[move.from].add(next[move.to].removeAt(next[move.to].lastIndex))
        }
        return next.map { it.toList() }
    }

    fun isTubeCompleted(tube: Tube, capacity: Int): Boolean =
        tube.size == capacity && tube.all { it == tube[0] }

    fun isSolved(tubes: List<Tube>, capacity: Int): Boolean =
        tubes.all { tube -> tube.isEmpty() || isTubeCompleted(tube, capacity) }

    /**
     * Anlamlı hamleleri döndürür:
     * - Tek renkli bir tüpü tamamen boş bir tüpe taşımayı eler (simetrik/boş hamle)
     * - Zaten tamamlanmış tüpten taşımayı eler
     * - Birden fazla boş tüp varsa yalnızca ilk boş tüpe izin verir (dallanma azaltma)
     */
    fun usefulMoves(tubes: List<Tube>, capacity: Int): List<Pair<Int, Int>> {
        val result = mutableListOf<Pair<Int, Int>>()
        val firstEmpty = tubes.indexOfFirst { it.isEmpty() }

        for (f in tubes.indices) {
            val src = tubes[f]
            if (src.isEmpty()) continue
            val srcUniform = src.all { it == src[0] }
            if (srcUniform && src.size == capacity) continue

            for (t in tubes.indices) {
                if (f == t) continue
                val dst = tubes[t]
                if (dst.isEmpty()) {
                    if (srcUniform) continue
                    if (t != firstEmpty) continue
                }
                if (canMove(tubes, f, t, capacity)) {
                    result += f to t
                }
            }
        }
        return result
    }

    fun isStuck(tubes: List<Tube>, capacity: Int): Boolean =
        !isSolved(tubes, capacity) && usefulMoves(tubes, capacity).isEmpty()
}

