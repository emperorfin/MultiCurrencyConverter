package emperorfin.android.multicurrencyconverter.ui.screen.currencyconverter.stateholder

import emperorfin.android.multicurrencyconverter.ui.model.currencyrate.CurrencyRateUiModel


data class CurrencyConverterUiState(
    val items: List<CurrencyRateUiModel> = emptyList(),
    val mapOfCurrencySymbolsToFlag: Map<String, String?> = emptyMap(),
    val isLoading: Boolean = false,
    val errorMessage: Int? = null,
    val messageSnackBar: Int? = null,
    val initRates: Boolean = false
)
