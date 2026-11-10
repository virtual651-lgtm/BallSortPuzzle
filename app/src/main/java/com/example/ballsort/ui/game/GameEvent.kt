/*
 * com.example.ballsort.ui.game
 * Kullanıcı etkileşimleri ve yaşam döngüsü olayları.
 */
package com.example.ballsort.ui.game

sealed interface GameEvent {
    data class TubeTapped(val index: Int) : GameEvent
    data object Undo : GameEvent
    data object Restart : GameEvent
    data object AddTube : GameEvent
    data object RequestHint : GameEvent
    data object NextLevel : GameEvent
    data object AnimationFinished : GameEvent
    data object ScreenResumed : GameEvent
    data object ScreenPaused : GameEvent
}

sealed interface GameEffect {
    data object SelectTube : GameEffect
    data object InvalidMove : GameEffect
    data object BallPlaced : GameEffect
    data object TubeCompleted : GameEffect
    data object LevelWon : GameEffect
}

