package ir.a9z.v2rayconfig

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.core.view.WindowCompat
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import ir.a9z.v2rayconfig.ui.screens.ConfigScreen
import ir.a9z.v2rayconfig.ui.screens.SubLinkScreen
import ir.a9z.v2rayconfig.ui.theme.A9ZV2RayConfigTheme
import ir.a9z.v2rayconfig.ui.viewmodel.MainViewModel

// Define sealed class for Bottom Navigation Items
sealed class Screen(
    val route: String,
    val labelResId: Int,
    val icon: @Composable () -> Unit
) {
    object SubLink : Screen(
        route = "subLink",
        labelResId = R.string.sub_link_title,
        icon = {
            Icon(
                painter = painterResource(id = R.drawable.ic_subscription),
                contentDescription = "Subscription",
            )
        }
    )

    object Config : Screen(
        route = "config",
        labelResId = R.string.config_title,
        icon = {
            Icon(
                painter = painterResource(id = R.drawable.ic_configuration),
                contentDescription = "Copy",
            )
        }
    )
}

val bottomNavItems = listOf(
    Screen.SubLink,
    Screen.Config,
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Set status bar color - Consider adjusting based on theme later
//        window.statusBarColor = Primary.toArgb // Using Primary color directly
        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.statusBarColor = android.graphics.Color.TRANSPARENT
        actionBar?.hide()

        setContent {
            A9ZV2RayConfigTheme {
                val navController = rememberNavController()
                val viewModel = remember { MainViewModel() }

                Scaffold(
                    bottomBar = {
                        NavigationBar {
                            val navBackStackEntry by navController.currentBackStackEntryAsState()
                            val currentDestination = navBackStackEntry?.destination

                            bottomNavItems.forEach { screen ->
                                NavigationBarItem(
                                    icon = { screen.icon() },
                                    label = {
                                        Text(
                                            text = stringResource(screen.labelResId),
                                            style = MaterialTheme.typography.titleMedium
                                        ) },
                                    selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                                    // Optional: Adjust item colors for contrast if needed
                                    // colors = NavigationBarItemDefaults.colors(
                                    //     selectedIconColor = MaterialTheme.colorScheme.onPrimary,
                                    //     selectedTextColor = MaterialTheme.colorScheme.onPrimary,
                                    //     indicatorColor = MaterialTheme.colorScheme.primaryContainer, // Or another contrasting color
                                    //     unselectedIconColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.6f),
                                    //     unselectedTextColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.6f)
                                    // ),
                                    onClick = {
                                        navController.navigate(screen.route) {
                                            // Pop up to the start destination of the graph to
                                            // avoid building up a large stack of destinations
                                            // on the back stack as users select items
                                            popUpTo(navController.graph.findStartDestination().id) {
                                                saveState = true
                                            }
                                            // Avoid multiple copies of the same destination when
                                            // reselecting the same item
                                            launchSingleTop = true
                                            // Restore state when reselecting a previously selected item
                                            restoreState = true
                                        }
                                    }
                                )
                            }
                        }
                    }
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = Screen.SubLink.route, // Start with SubLink
                        modifier = Modifier.padding(innerPadding) // Apply padding from Scaffold
                    ) {
                        composable(Screen.SubLink.route) {
                            SubLinkScreen(viewModel = viewModel, navController = navController) // Pass NavController if needed internally
                        }
                        composable(Screen.Config.route) {
                            ConfigScreen(viewModel = viewModel, navController = navController) // Pass NavController if needed internally
                        }
                    }
                }
            }
        }
    }
}