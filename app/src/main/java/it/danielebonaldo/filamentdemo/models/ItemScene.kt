package it.danielebonaldo.filamentdemo.models

import com.google.android.filament.Engine
import com.google.android.filament.Scene
import com.google.android.filament.gltfio.FilamentAsset
import com.google.android.filament.gltfio.ResourceLoader

data class ItemScene(
    val engine: Engine,
    val scene: Scene,
    val asset: FilamentAsset,
    val resourceLoader: ResourceLoader
)
