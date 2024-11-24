package emperorfin.android.multicurrencyconverter.ui.screen.currencyconverter

import android.content.Context
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import emperorfin.android.multicurrencyconverter.R
import emperorfin.android.multicurrencyconverter.ui.component.CurrencyConverterTopAppBar
import emperorfin.android.multicurrencyconverter.ui.screen.currencyconverter.component.Content
import emperorfin.android.multicurrencyconverter.ui.screen.currencyconverter.stateholder.CurrencyConverterViewModel
import emperorfin.android.multicurrencyconverter.ui.theme.MultiCurrencyConverterTheme


@Composable
fun CurrencyConverterScreen(
    context: Context,
    modifier: Modifier = Modifier,
    openDrawer: () -> Unit,
    viewModel: CurrencyConverterViewModel = hiltViewModel()
) {

    val snackBarHostState = remember { SnackbarHostState() }

    val uiState by viewModel.uiState.collectAsState()

    var baseCurrencySymbolRefresh by rememberSaveable { mutableStateOf("USD") }

    var baseAmountRefresh by rememberSaveable { mutableStateOf(1) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackBarHostState) },
        topBar = {
            CurrencyConverterTopAppBar(
                openDrawer = openDrawer,
                onRefresh = {
                    if (uiState.errorMessage != null && uiState.initRates) {
                        viewModel.initCurrencyRates()
                    } else {
                        viewModel.refreshCurrencyRates(
                            baseAmount = baseAmountRefresh,
                            baseCurrencySymbol = baseCurrencySymbolRefresh
                        )
                    }
                }
            )
        }
    ) { paddingValues ->

        Content(
            context = context,
            loading = uiState.isLoading,
            currencyRates = uiState.items,
            noCurrenciesLabel = uiState.errorMessage ?: R.string.no_currencies,
            noCurrenciesIconRes = R.drawable.icon_refresh,
            onRefresh = {

                if (uiState.errorMessage != null && uiState.initRates) {
                    viewModel.initCurrencyRates()
                } else {
                    viewModel.refreshCurrencyRates(
                        baseAmount = baseAmountRefresh,
                        baseCurrencySymbol = baseCurrencySymbolRefresh
                    )
                }

            },
            mapOfCurrencySymbolsToFlag = uiState.mapOfCurrencySymbolsToFlag,
            modifier = Modifier.padding(paddingValues),
            onConvert = { baseAmount, baseCurrencySymbol ->

                baseAmountRefresh = baseAmount
                baseCurrencySymbolRefresh = baseCurrencySymbol

                viewModel.convert(
                    baseAmount = baseAmount,
                    baseCurrencySymbol = baseCurrencySymbol
                )

            }
        )

        uiState.messageSnackBar?.let { message ->
            val snackBarText = stringResource(message)

            LaunchedEffect(snackBarHostState, viewModel, message, snackBarText) {
                snackBarHostState.showSnackbar(message = snackBarText)
                viewModel.snackBarMessageShown()
            }
        }

    }
}

@Preview
@Composable
private fun CurrencyConverterScreenPreview() {
    MultiCurrencyConverterTheme {
        Surface {
            CurrencyConverterScreen(
                context = LocalContext.current,
                openDrawer = {},
            )
        }
    }
}