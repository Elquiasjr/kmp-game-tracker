package br.edu.utfpr.kmptracker

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import br.edu.utfpr.kmptracker.navigation.DetailRoute
import br.edu.utfpr.kmptracker.navigation.DiscoverRoute
import br.edu.utfpr.kmptracker.navigation.LibraryRoute
import br.edu.utfpr.kmptracker.ui.detail.DetailScreen
import br.edu.utfpr.kmptracker.ui.discover.DiscoverScreen
import br.edu.utfpr.kmptracker.ui.library.LibraryScreen
import coil3.ImageLoader
import coil3.compose.setSingletonImageLoaderFactory
import coil3.network.ktor3.KtorNetworkFetcherFactory
import coil3.request.crossfade

private val KotlinPurple = Color(0xFF7F52FF)

/** Ponto de entrada da UI compartilhada: chamado pelo Android, iOS e Desktop. */
@Composable
fun App() {
    // Coil: carregamento de imagens pela rede usando o Ktor
    setSingletonImageLoaderFactory { context ->
        ImageLoader.Builder(context)
            .components { add(KtorNetworkFetcherFactory()) }
            .crossfade(true)
            .build()
    }

    val colors = if (isSystemInDarkTheme()) {
        darkColorScheme(primary = Color(0xFFB9A3FF))
    } else {
        lightColorScheme(primary = KotlinPurple)
    }

    MaterialTheme(colorScheme = colors) {
        val navController = rememberNavController()
        val backStackEntry by navController.currentBackStackEntryAsState()
        val destination = backStackEntry?.destination
        val showBottomBar = destination?.hasRoute(DetailRoute::class) != true

        Scaffold(
            bottomBar = {
                if (showBottomBar) {
                    NavigationBar {
                        NavigationBarItem(
                            selected = destination?.hasRoute(DiscoverRoute::class) == true,
                            onClick = { navController.navigateToTab(DiscoverRoute) },
                            icon = { Text("🔎") },
                            label = { Text("Descobrir") },
                        )
                        NavigationBarItem(
                            selected = destination?.hasRoute(LibraryRoute::class) == true,
                            onClick = { navController.navigateToTab(LibraryRoute) },
                            icon = { Text("📚") },
                            label = { Text("Biblioteca") },
                        )
                    }
                }
            },
        ) { padding ->
            NavHost(
                navController = navController,
                startDestination = DiscoverRoute,
                modifier = Modifier.padding(padding),
            ) {
                composable<DiscoverRoute> {
                    DiscoverScreen(onGameClick = { id -> navController.navigate(DetailRoute(id)) })
                }
                composable<LibraryRoute> {
                    LibraryScreen(onGameClick = { id -> navController.navigate(DetailRoute(id)) })
                }
                composable<DetailRoute> { entry ->
                    val route = entry.toRoute<DetailRoute>()
                    DetailScreen(gameId = route.gameId, onBack = { navController.popBackStack() })
                }
            }
        }
    }
}

/** Troca de aba preservando o estado de cada uma. */
private fun NavHostController.navigateToTab(route: Any) {
    navigate(route) {
        popUpTo(graph.findStartDestination().id) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}
