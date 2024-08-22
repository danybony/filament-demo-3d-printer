package it.danielebonaldo.filamentdemo

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.google.android.filament.Colors
import com.google.android.filament.Engine
import com.google.android.filament.EntityManager
import com.google.android.filament.IndirectLight
import com.google.android.filament.LightManager
import com.google.android.filament.gltfio.AssetLoader
import com.google.android.filament.gltfio.ResourceLoader
import com.google.android.filament.gltfio.UbershaderProvider
import com.google.android.filament.utils.KTX1Loader
import com.google.android.filament.utils.Utils
import it.danielebonaldo.filamentdemo.models.Item
import it.danielebonaldo.filamentdemo.models.ItemMaterial
import it.danielebonaldo.filamentdemo.models.ItemScene
import it.danielebonaldo.filamentdemo.models.ItemsUiState
import it.danielebonaldo.filamentdemo.models.allItems
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.launch

class FilamentViewModel(
    private val application: Application
) : ViewModel() {

    private val assetLoader: AssetLoader by lazy {
        AssetLoader(engine, UbershaderProvider(engine), EntityManager.get())
    }
    private val engine by lazy {
        Engine.create()
    }
    private val resourceLoader: ResourceLoader by lazy {
        val normalizeSkinningWeights = true
        ResourceLoader(engine, normalizeSkinningWeights)
    }

    private lateinit var indirectLight: IndirectLight
    private var light: Int = 0

    var itemsUiState by mutableStateOf(ItemsUiState())
        private set

    init {
        Utils.init()
        initFilament()
    }

    private fun initFilament() {
        viewModelScope.launch {


            val ibl = "default_env"
            readCompressedAsset(application, "envs/${ibl}/${ibl}_ibl.ktx").let {
                indirectLight = KTX1Loader.createIndirectLight(engine, it)
                indirectLight.intensity = 30_000.0f
            }

            light = EntityManager.get().create()
            val (r, g, b) = Colors.cct(6_000.0f)
            LightManager.Builder(LightManager.Type.SUN)
                .color(r, g, b)
                .intensity(70_000.0f)
                .direction(0.28f, -0.6f, -0.76f)
                .build(engine, light)

            val items = mutableListOf<Item>()

            allItems.forEachIndexed { index, item ->
                val newScene = engine.createScene()
                val asset = readCompressedAsset(application, "models/${item.assetModel}.glb").let {
                    val asset = loadModelGlb(assetLoader, resourceLoader, it)
                    transformToUnitCube(engine, asset, item.topDownView)
                    asset
                }

                newScene.addEntities(asset.entities)

                newScene.indirectLight = indirectLight
                newScene.skybox = null
                newScene.addEntity(light)

                val itemScene = ItemScene(
                    engine = engine,
                    scene = newScene,
                    resourceLoader = resourceLoader,
                    asset = asset
                )

                items.add(
                    Item(
                        id = index.toString(),
                        name = item.name,
                        printTime = formatPrintTime(item.printTimeMin),
                        itemScene = itemScene,
                        material = ItemMaterial()
                    )
                )
            }

            itemsUiState = itemsUiState.copy(
                items = items.toImmutableList()
            )
        }
    }

    private fun formatPrintTime(totalMinutes: Int): String {
        val hours = totalMinutes / 60
        val minutes = totalMinutes % 60
        return if (hours > 0) "${hours}h ${minutes}m" else "${minutes}m"
    }

    fun onColorSelected(item: Item, color: Color) {
        updateMaterial(item) { this.copy(color = color) }
    }

    fun onMetallicUpdated(item: Item, value: Float) {
        updateMaterial(item) { this.copy(metallicFactor = value) }
    }

    fun onRoughnessUpdated(item: Item, value: Float) {
        updateMaterial(item) { this.copy(roughness = value) }
    }

    private fun updateMaterial(item: Item, update: ItemMaterial.() -> ItemMaterial) {
        itemsUiState = itemsUiState.copy(
            items = buildList {
                itemsUiState.items.forEach {
                    if (it.id == item.id) {
                        add(
                            it.copy(
                                material = it.material.update()
                            )
                        )
                    } else {
                        add(it)
                    }
                }
            }.toImmutableList()
        )
    }

    override fun onCleared() {
        super.onCleared()
        engine.lightManager.destroy(light)
        engine.destroyEntity(light)
        engine.destroyIndirectLight(indirectLight)

        itemsUiState.items.forEach {
            engine.destroyScene(it.itemScene.scene)
            assetLoader.destroyAsset(it.itemScene.asset)
        }

        assetLoader.destroy()
        resourceLoader.destroy()
        engine.destroy()
    }

    companion object {

        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(
                modelClass: Class<T>,
                extras: CreationExtras
            ): T {
                // Get the Application object from extras
                val application = checkNotNull(extras[APPLICATION_KEY])

                return FilamentViewModel(
                    application
                ) as T
            }
        }
    }
}
