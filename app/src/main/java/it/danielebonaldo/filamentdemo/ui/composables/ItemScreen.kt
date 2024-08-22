package it.danielebonaldo.filamentdemo.ui.composables

import android.view.TextureView
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.filament.Colors
import it.danielebonaldo.filamentdemo.ModelViewer
import it.danielebonaldo.filamentdemo.models.Item
import it.danielebonaldo.filamentdemo.setupModelViewer

@Composable
fun ItemScreen(
    item: Item,
    onColorSelected: (Color) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.padding(8.dp)) {
        Text(
            text = "Print time: ${item.printTime}",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.primary
        )
        ColorSelector(item.currentColor, onColorSelected)

        FilamentViewer(item = item)
    }
}

@Composable
fun ColorSelector(
    currentColor: Color,
    onColorSelected: (Color) -> Unit
) {
    val availableColors = listOf(
        Color.Red, Color.White, Color.Blue, Color.Green, Color.Yellow, Color.Gray
    )
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        availableColors.forEach { color ->
            Box(modifier = Modifier
                .size(32.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(color)
                .border(
                    width = 2.dp,
                    color = if (currentColor == color) Color.Black else Color.LightGray,
                    shape = RoundedCornerShape(16.dp)
                )
                .clickable { onColorSelected(color) }
            )
        }
    }
}

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
