package it.danielebonaldo.filamentdemo.models

import androidx.compose.runtime.Immutable
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Immutable
data class ItemsUiState(
    val items: ImmutableList<Item> = persistentListOf()
)
