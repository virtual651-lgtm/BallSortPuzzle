/*
 * com.example.ballsort.domain
 * Ağırlıklı A* (Best-First) çözücü ve ipucu motoru (saf Kotlin).
 */
package com.example.ballsort.domain

import com.example.ballsort.domain.model.Move
import com.example.ballsort.domain.model.Tube
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import java.util.PriorityQueue
import kotlin.coroutines.CoroutineContext

object Solver {

    private data class Node(
        val tubes: List<Tube>,
        val g: Int,
        val f: Int,
        val parent: Node?,
        val moveFromParent: Move?,
    ) : Comparable<Node> {
        override fun compareTo(other: Node): Int = f.compareTo(other.f)
    }

    /**
     * Simetri indirgemeli durum anahtarı.
     * Tüp sırası oyun mantığını değiştirmediği için sıralanmış temsil kullanılır.
     */
    private fun stateKey(tubes: List<Tube>): String =
        tubes.map { it.joinToString(",") }.sorted().joinToString("|")

    /**
     * Sezgisel tahmin: her tüpteki renk geçişlerini sayar.
     * Her renk değişimi en az 1 ayırma hamlesi gerektirir; tamamlanmış tüpler 0 katkı verir.
     */
    private fun heuristic(tubes: List<Tube>, capacity: Int): Int {
        var score = 0
        for (tube in tubes) {
            if (tube.isEmpty()) continue
            var transitions = 0
            for (i in 1 until tube.size) {
                if (tube[i] != tube[i - 1]) transitions++
            }
            score += transitions * 2
            if (transitions > 0) {
                // Karışık tüpü temizlemek için ekstra maliyet
                score += 1
            } else if (tube.size < capacity) {
                // Tek renkli ama henüz dolmamış tüpü birleştirmeye teşvik
                score += 1
            }
        }
        return score
    }

    private fun reconstructPath(goal: Node): List<Move> {
        val path = mutableListOf<Move>()
        var cur: Node? = goal
        while (cur?.moveFromParent != null) {
            path += cur.moveFromParent
            cur = cur.parent
        }
        return path.reversed()
    }

    /**
     * Senkron çözücü (testler ve Seviye Üretici doğrulaması için).
     */
    fun solveSync(
        tubes: List<Tube>,
        capacity: Int,
        multiMove: Boolean = true,
        maxVisited: Int = 25_000,
    ): List<Move>? {
        if (GameLogic.isSolved(tubes, capacity)) return emptyList()

        val visited = HashSet<String>(1024)
        val open = PriorityQueue<Node>()
        val startKey = stateKey(tubes)
        visited.add(startKey)
        open.add(Node(tubes, g = 0, f = heuristic(tubes, capacity), parent = null, moveFromParent = null))

        while (open.isNotEmpty() && visited.size < maxVisited) {
            val current = open.poll() ?: break
            if (GameLogic.isSolved(current.tubes, capacity)) {
                return reconstructPath(current)
            }

            for ((from, to) in GameLogic.usefulMoves(current.tubes, capacity)) {
                val (nextTubes, move) = GameLogic.applyMove(
                    current.tubes, from, to, capacity, multiMove
                ) ?: continue
                val key = stateKey(nextTubes)
                if (!visited.add(key)) continue

                val g = current.g + 1
                val h = heuristic(nextTubes, capacity)
                open.add(
                    Node(
                        tubes = nextTubes,
                        g = g,
                        f = g + h * 2, // Ağırlıklı A* hız için
                        parent = current,
                        moveFromParent = move,
                    )
                )
            }
        }
        return null
    }

    /**
     * Zaman sınırlı asenkron çözücü (UI iş parçacığını bloke etmez).
     */
    suspend fun solve(
        tubes: List<Tube>,
        capacity: Int,
        multiMove: Boolean = true,
        timeoutMs: Long = 2_000L,
        dispatcher: CoroutineContext = Dispatchers.Default,
    ): List<Move>? = withContext(dispatcher) {
        withTimeoutOrNull(timeoutMs) {
            if (GameLogic.isSolved(tubes, capacity)) return@withTimeoutOrNull emptyList()

            val visited = HashSet<String>(1024)
            val open = PriorityQueue<Node>()
            visited.add(stateKey(tubes))
            open.add(Node(tubes, 0, heuristic(tubes, capacity), null, null))

            var bestFallback: Node? = null
            var bestH = Int.MAX_VALUE
            var steps = 0

            while (open.isNotEmpty()) {
                if (steps++ % 128 == 0) {
                    currentCoroutineContext().ensureActive()
                }
                val current = open.poll() ?: break
                if (GameLogic.isSolved(current.tubes, capacity)) {
                    return@withTimeoutOrNull reconstructPath(current)
                }

                for ((from, to) in GameLogic.usefulMoves(current.tubes, capacity)) {
                    val (nextTubes, move) = GameLogic.applyMove(
                        current.tubes, from, to, capacity, multiMove
                    ) ?: continue
                    val key = stateKey(nextTubes)
                    if (!visited.add(key)) continue

                    val g = current.g + 1
                    val h = heuristic(nextTubes, capacity)
                    val child = Node(nextTubes, g, g + h * 2, current, move)
                    if (h < bestH) {
                        bestH = h
                        bestFallback = child
                    }
                    open.add(child)
                }
            }
            bestFallback?.let { reconstructPath(it) }
        }
    }

    /**
     * Bir sonraki en iyi hamleyi döndürür. Zaman aşımında en umut verici hamleye düşer.
     */
    suspend fun hint(
        tubes: List<Tube>,
        capacity: Int,
        multiMove: Boolean = true,
        timeoutMs: Long = 2_000L,
        dispatcher: CoroutineContext = Dispatchers.Default,
    ): Move? {
        val solution = solve(tubes, capacity, multiMove, timeoutMs, dispatcher)
        if (!solution.isNullOrEmpty()) return solution.first()

        // Çözücü süre sınırına takılırsa sezgisel puanı en çok düşüren hamleyi seç
        return withContext(dispatcher) {
            GameLogic.usefulMoves(tubes, capacity)
                .mapNotNull { (f, t) -> GameLogic.applyMove(tubes, f, t, capacity, multiMove) }
                .minByOrNull { (next, _) -> heuristic(next, capacity) }
                ?.second
        }
    }
}

