package it.danielebonaldo.filamentdemo.ui.composables

import android.view.TextureView
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.filament.Colors
import it.danielebonaldo.filamentdemo.ModelViewer
import it.danielebonaldo.filamentdemo.models.Item
import it.danielebonaldo.filamentdemo.setupModelViewer

@Composable
fun FilamentViewer(item: Item) {
    var modelViewer by remember { mutableStateOf<ModelViewer?>(null) }

    LaunchedEffect(true) {
        while (true) {
            withFrameNanos { nano ->
                modelViewer?.render(nano)
            }
        }
    }

    SideEffect {
        val (engine, _, asset) = item.itemScene
        modelViewer?.scene = item.itemScene.scene

        asset.entities.find {
            asset.getName(it)?.startsWith("imagetostl") ?: false
        }?.also { entity ->
            val manager = engine.renderableManager
            val instance = manager.getInstance(entity)
            val material = manager.getMaterialInstanceAt(instance, 0)

            val r = item.currentColor.red
            val g = item.currentColor.green
            val b = item.currentColor.blue

            material.setParameter(
                "baseColorFactor", Colors.RgbaType.SRGB, r, g, b, 1.0f
            )
        }
    }

    AndroidView({ context ->
        TextureView(context).also { textureView ->
            val (engine) = item.itemScene
            modelViewer = ModelViewer(engine, textureView).also { modelViewer ->
                setupModelViewer(modelViewer)

                textureView.setOnTouchListener { _, event ->
                    modelViewer.onTouchEvent(event)
                    true
                }
            }
        }
    })
}
