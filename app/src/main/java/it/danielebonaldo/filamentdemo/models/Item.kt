package it.danielebonaldo.filamentdemo.models

import androidx.compose.ui.graphics.Color
import kotlinx.collections.immutable.ImmutableList

data class Item(
    val id: String,
    val name: String,
    val printTime: String,
    val material: ItemMaterial,
    val itemScene: ItemScene,
    val animations: ImmutableList<Animation>
)

sealed class ItemMaterial {
    data class Mutable(
        val color: Color = Color.Gray,
        val roughness: Float = 1f,
        val metallicFactor: Float = 0f
    ) : ItemMaterial()

    data object Immutable : ItemMaterial()
}

data class Animation(
    val name: String,
    val durationSeconds: Float,
    val targetState: State,
    val startNano: Long
) {
    enum class State {
        On, Off;

        fun toggle() = when (this) {
            On -> Off
            Off -> On
        }
    }
}
