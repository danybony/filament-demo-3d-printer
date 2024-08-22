package it.danielebonaldo.filamentdemo.models

import androidx.compose.ui.graphics.Color

data class Item(
    val id: Int,
    val name: String,
    val printTime: String,
    val currentColor: Color,
    val itemScene: ItemScene
)
