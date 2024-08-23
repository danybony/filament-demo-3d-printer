package it.danielebonaldo.filamentdemo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import it.danielebonaldo.filamentdemo.ui.composables.ItemScreen
import it.danielebonaldo.filamentdemo.ui.composables.ItemsListScreen
import it.danielebonaldo.filamentdemo.ui.theme.FilamentDemo3dPrinterTheme

class MainActivity : ComponentActivity() {

    private val viewModel: FilamentViewModel by viewModels { FilamentViewModel.Factory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FilamentDemo3dPrinterTheme {
                val navController = rememberNavController()
                Scaffold(
                    topBar = { DemoAppBar(navController) },
                    containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    val itemsUiState = viewModel.itemsUiState

                    NavHost(navController = navController, startDestination = "itemsList") {
                        composable("itemsList") {
                            ItemsListScreen(
                                items = itemsUiState.items,
                                onItemSelected = { navController.navigate("itemScreen/$it") },
                                modifier = Modifier.padding(innerPadding)
                            )
                        }
                        composable("itemScreen/{itemId}") { backStackEntry ->
                            val itemId = backStackEntry.arguments?.getString("itemId")
                            val item = itemsUiState.items.first { it.id == itemId }
                            ItemScreen(
                                item = item,
                                onColorSelected = { viewModel.onColorSelected(item, it) },
                                onMetallicUpdated = { viewModel.onMetallicUpdated(item, it) },
                                onRoughnessUpdated = { viewModel.onRoughnessUpdated(item, it) },
                                onToggleAnimation = {animationId, state -> viewModel.onToggleAnimation(item, animationId, state)},
                                modifier = Modifier.padding(innerPadding)
                            )
                        }
                    }
                }
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun DemoAppBar(navController: NavHostController) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination?.route ?: ""
        val isHome = currentDestination == "itemsList"

        val title = if (isHome) {
            stringResource(R.string.app_name)
        } else {
            val itemId = (navBackStackEntry?.arguments?.getString("itemId")) ?: "0"
            viewModel.itemsUiState.items.first { it.id == itemId }.name
        }

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
            ),
            navigationIcon = {
                if (!isHome) {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back to items list"
                        )
                    }
                }
            }
        )
    }
}
