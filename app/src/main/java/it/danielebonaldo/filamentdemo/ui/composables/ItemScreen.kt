package it.danielebonaldo.filamentdemo.ui.composables

import android.view.SurfaceView
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import it.danielebonaldo.filamentdemo.ModelViewer
import it.danielebonaldo.filamentdemo.models.Item
import it.danielebonaldo.filamentdemo.setupModelViewer

@Composable
fun ItemScreen(
    item: Item,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.padding(8.dp)) {
        Text(
            text = "Print time: ${item.printTime}",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.primary
        )

        FilamentViewer(item = item)
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
        modelViewer?.scene = item.itemScene.scene
    }

    AndroidView({ context ->
        SurfaceView(context).also { surfaceView ->
            val (engine) = item.itemScene
            modelViewer = ModelViewer(engine, surfaceView).also { modelViewer ->
                setupModelViewer(modelViewer)
            }
        }
    })
}
