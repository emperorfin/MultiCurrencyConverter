package emperorfin.android.multicurrencyconverter.ui.navigation

import android.content.Context
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import emperorfin.android.multicurrencyconverter.ui.component.AppModalDrawer
import emperorfin.android.multicurrencyconverter.ui.screen.about.AboutScreen
import emperorfin.android.multicurrencyconverter.ui.screen.currencyconverter.CurrencyConverterScreen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


@Composable
fun NavGraph(
    context: Context,
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    drawerState: DrawerState = rememberDrawerState(initialValue = DrawerValue.Closed),
    startDestination: String = Destinations.ROUTE_CURRENCY_CONVERTER,
    navActions: NavigationActions = remember(navController) {
        NavigationActions(navController)
    },
) {

    val currentNavBackStackEntry by navController.currentBackStackEntryAsState()

    val currentRoute: String = currentNavBackStackEntry?.destination?.route ?: startDestination

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(Destinations.ROUTE_CURRENCY_CONVERTER) {
            AppModalDrawer(drawerState, currentRoute, navActions) {
                CurrencyConverterScreen(
                    context = context,
                    openDrawer = { coroutineScope.launch { drawerState.open() } },
                )
            }
        }

        composable(Destinations.ROUTE_ABOUT) {
            AppModalDrawer(drawerState, currentRoute, navActions) {
                AboutScreen(
                    openDrawer = { coroutineScope.launch { drawerState.open() } }
                )
            }
        }
    }

}