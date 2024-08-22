package it.danielebonaldo.filamentdemo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import it.danielebonaldo.filamentdemo.ui.composables.ItemsListScreen
import it.danielebonaldo.filamentdemo.ui.theme.FilamentDemo3dPrinterTheme

class MainActivity : ComponentActivity() {

    private val viewModel: FilamentViewModel by viewModels { FilamentViewModel.Factory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            FilamentDemo3dPrinterTheme {
                Scaffold(
                    topBar = { DemoAppBar() },
                    containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
//                    val item = viewModel.itemsUiState.items.firstOrNull()
//                    if (item != null) {
//                        ItemScreen(
//                            item = item,
//                            onColorSelected = { viewModel.onColorSelected(item, it) },
//                            onMetallicUpdated = { viewModel.onMetallicUpdated(item, it) },
//                            onRoughnessUpdated = { viewModel.onRoughnessUpdated(item, it) },
//                            modifier = Modifier.padding(innerPadding)
//                        )
//                    }

                    ItemsListScreen(
                        items = viewModel.itemsUiState.items,
                        modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun DemoAppBar() {
        val title =
            stringResource(R.string.app_name)

        TopAppBar(
            title = {
                Text(
                    title,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    style = MaterialTheme.typography.titleLarge,
                )
            },
            colors = TopAppBarDefaults.topAppBarColors().copy(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        )
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    FilamentDemo3dPrinterTheme {
        Greeting("Android")
    }
}
