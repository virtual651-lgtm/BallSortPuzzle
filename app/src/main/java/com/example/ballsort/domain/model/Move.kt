/*
 * com.example.ballsort.domain.model
 * Tek bir top taşıma hamlesini temsil eden saf Kotlin modeli.
 */
package com.example.ballsort.domain.model

/** Tüp = alttan üste renk kimliği listesi. */
typealias Tube = List<Int>

data class Move(
    val from: Int,
    val to: Int,
    val count: Int,
)

