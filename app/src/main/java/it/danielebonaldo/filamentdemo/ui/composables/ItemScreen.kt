package it.danielebonaldo.filamentdemo.ui.composables

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
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import it.danielebonaldo.filamentdemo.models.Item

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
