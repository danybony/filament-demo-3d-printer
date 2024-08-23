package it.danielebonaldo.filamentdemo.ui.composables

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import it.danielebonaldo.filamentdemo.R
import it.danielebonaldo.filamentdemo.models.Animation
import it.danielebonaldo.filamentdemo.models.Item
import it.danielebonaldo.filamentdemo.models.ItemMaterial
import kotlinx.collections.immutable.ImmutableList

@Composable
fun ItemScreen(
    item: Item,
    onColorSelected: (Color) -> Unit,
    onMetallicUpdated: (Float) -> Unit,
    onRoughnessUpdated: (Float) -> Unit,
    onToggleAnimation: (Int, Animation.State) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.padding(8.dp)) {
        Text(
            text = "Print time: ${item.printTime}",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.primary
        )
        if (item.material is ItemMaterial.Mutable) {
            ColorSelector(item.material.color, onColorSelected)
            ParameterSlider("Metallic", item.material.metallicFactor, onMetallicUpdated)
            ParameterSlider("Roughness", item.material.roughness, onRoughnessUpdated)
        }
        AnimationsToggles(item.animations, onToggleAnimation)
        FilamentViewer(item = item, autoRotate = false)
    }
}

@Composable
private fun ColorSelector(
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
private fun ParameterSlider(
    name: String,
    currentValue: Float,
    onValueUpdated: (Float) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp), verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = name, modifier = Modifier.fillMaxWidth(0.3f),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.primary
        )
        Slider(
            value = currentValue,
            onValueChange = onValueUpdated,
            modifier = Modifier.weight(1f)
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun AnimationsToggles(
    animations: ImmutableList<Animation>,
    onToggle: (Int, Animation.State) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = stringResource(R.string.animations), modifier = Modifier.fillMaxWidth(0.3f),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.primary
        )

        FlowRow(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            animations.forEachIndexed { index, animation ->
                Button(
                    onClick = { onToggle(index, animation.targetState.toggle()) },
                    colors = ButtonDefaults.buttonColors().copy(
                        containerColor = if (animation.targetState == Animation.State.On) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            Color.Transparent
                        }
                    ),
                    border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
                ) {
                    Text(
                        text = animation.name,
                        style = MaterialTheme.typography.bodyLarge,
                        color = if (animation.targetState == Animation.State.On) {
                            MaterialTheme.colorScheme.onPrimary
                        } else {
                            MaterialTheme.colorScheme.primary
                        }
                    )
                }
            }
        }
    }
}
