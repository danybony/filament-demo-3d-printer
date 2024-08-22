package it.danielebonaldo.filamentdemo.models

import androidx.compose.ui.graphics.Color

data class Item(
    val id: Int,
    val name: String,
    val printTime: String,
    val material: ItemMaterial,
    val itemScene: ItemScene
)

data class ItemMaterial(
    val color: Color = Color.Gray,
    val roughness: Float = 1f,
    val metallicFactor: Float = 0f
)
