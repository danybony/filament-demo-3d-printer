package it.danielebonaldo.filamentdemo.ui.composables

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import it.danielebonaldo.filamentdemo.models.Item
import kotlinx.collections.immutable.ImmutableList

@Composable
fun ItemsListScreen(
    items: ImmutableList<Item>,
    modifier: Modifier
) {
    LazyColumn(modifier = modifier) {
        items(
            items = items,
            itemContent = { item ->
                ItemCard(item)
            }
        )
    }
}

@Composable
fun ItemCard(item: Item) {
    Surface(
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(8.dp),
        shadowElevation = 4.dp
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            ) {
                FilamentViewer(item = item, autoRotate = true)
            }

            Row(
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth()
            ) {
                Text(text = item.name, modifier = Modifier.padding(start = 8.dp))
            }
        }
    }
}
