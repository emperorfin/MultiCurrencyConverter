package emperorfin.android.multicurrencyconverter.ui.screen.currencyconverter.component

import android.content.Context
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.core.text.isDigitsOnly
import emperorfin.android.multicurrencyconverter.R
import emperorfin.android.multicurrencyconverter.ui.component.AppName
import emperorfin.android.multicurrencyconverter.ui.component.ContentLoader
import emperorfin.android.multicurrencyconverter.ui.component.EmptyContent
import emperorfin.android.multicurrencyconverter.ui.component.LoadingIndicator
import emperorfin.android.multicurrencyconverter.ui.model.currencyrate.CurrencyRateUiModel
import emperorfin.android.multicurrencyconverter.ui.theme.MultiCurrencyConverterTheme
import emperorfin.android.multicurrencyconverter.ui.util.CountryFlagsUtil


private const val EMPTY: String = ""
private const val CURRENCY_SYMBOL_USD: String = "USD"
private const val CURRENCY_SYMBOL_AFN: String = "AFN"
private const val CURRENCY_SYMBOL_ALL: String = "ALL"
private const val CURRENCY_SYMBOL_AMD: String = "AMD"
private const val CURRENCY_SYMBOL_ANG: String = "ANG"
private const val CURRENCY_SYMBOL_EUR: String = "EUR"

@Composable
fun Content(
    modifier: Modifier = Modifier,
    context: Context,
    loading: Boolean,
    currencyRates: List<CurrencyRateUiModel>,
    @StringRes noCurrenciesLabel: Int,
    @DrawableRes noCurrenciesIconRes: Int,
    onRefresh: () -> Unit,
    mapOfCurrencySymbolsToFlag: Map<String, String?>,
    onConvert: (Int, String) -> Unit
) {

    var baseCurrencySymbol: String by rememberSaveable { mutableStateOf(CURRENCY_SYMBOL_USD) }

    var baseAmount: String by rememberSaveable { mutableStateOf("1") }

    Column(
        modifier = modifier
            .padding(
                horizontal = dimensionResource(id = R.dimen.horizontal_margin),
                vertical = dimensionResource(id = R.dimen.horizontal_margin)
            )
    ) {
        AppName(
            modifier = Modifier.padding(
                vertical = dimensionResource(id = R.dimen.currency_converter_screen_content_app_name_padding_vertical),
                horizontal = dimensionResource(id = R.dimen.currency_converter_screen_content_app_name_padding_horizontal)
            )
        )

        Spacer(modifier = Modifier.height(height = dimensionResource(id = R.dimen.currency_converter_screen_content_spacer_height_var1)))

        ContentLoader(
            loading = loading,
            empty = currencyRates.isEmpty() && !loading,
            emptyContent = {
                EmptyContent(
                    noCurrenciesLabel,
                    noCurrenciesIconRes,
                    modifier,
                    onRefresh
                )
            },
            loadingIndicator = { LoadingIndicator(modifier = modifier) },
            onRefresh = onRefresh
        ) {

            Column {
                Row(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {

                    CurrencyPicker(
                        modifier,
                        defaultSymbol = baseCurrencySymbol,
                        mapOfCurrencySymbolsToFlag = mapOfCurrencySymbolsToFlag,
                        onSymbolSelected = { newText -> baseCurrencySymbol = newText.uppercase() }
                    )

                    Spacer(modifier = Modifier.width(width = dimensionResource(id = R.dimen.currency_converter_screen_content_spacer_width_var1)))

                    RateTextField(
                        modifier,
                        value = baseAmount,
                        onBaseAmountChanged = { newBaseAmount -> baseAmount = newBaseAmount }
                    )

                }

                Spacer(modifier = Modifier.height(height = dimensionResource(id = R.dimen.currency_converter_screen_content_spacer_height_var1)))

                Button(
                    elevation = null,
                    onClick = {

                        if (baseAmount.isNotEmpty()) {

                            if (!baseAmount.isDigitsOnly()) {
                                Toast.makeText(
                                    context,
                                    context.getString(R.string.message_input_must_be_digits_only),
                                    Toast.LENGTH_SHORT
                                ).show()

                                return@Button
                            }

                            if (!mapOfCurrencySymbolsToFlag.containsKey(baseCurrencySymbol)) {
                                Toast.makeText(
                                    context,
                                    context.getString(R.string.message_please_select_a_valid_currency_symbol),
                                    Toast.LENGTH_SHORT
                                ).show()

                                return@Button
                            }

                            onConvert(baseAmount.toInt(), baseCurrencySymbol)

                        } else {
                            Toast.makeText(
                                context,
                                context.getString(R.string.message_enter_an_amount),
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                    },
                    modifier = Modifier
                        .height(height = dimensionResource(id = R.dimen.currency_converter_screen_content_button_height))
                        .fillMaxWidth()
                        .padding(bottom = dimensionResource(id = R.dimen.currency_converter_screen_content_button_padding_bottom)),
                    shape = RoundedCornerShape(size = dimensionResource(id = R.dimen.currency_converter_screen_content_button_shape)),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.inversePrimary,
                        contentColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text(
                        text = stringResource(R.string.convert_text),
                        fontWeight = FontWeight.Bold,
                        fontSize = dimensionResource(id = R.dimen.currency_converter_screen_content_text_font_size).value.sp
                    )
                }

                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = dimensionResource(id = R.dimen.currency_converter_screen_content_lazy_vertical_grid_min_size)),
                    contentPadding = PaddingValues(
                        top = dimensionResource(id = R.dimen.currency_converter_screen_content_lazy_vertical_grid_padding_top),
                        start = dimensionResource(id = R.dimen.currency_converter_screen_content_lazy_vertical_grid_padding_start),
                        end = dimensionResource(id = R.dimen.currency_converter_screen_content_lazy_vertical_grid_padding_end),
                        bottom = dimensionResource(id = R.dimen.currency_converter_screen_content_lazy_vertical_grid_padding_bottom)
                    ),
                    content = {
                        items(items = currencyRates) { currencyRate ->
                            CurrencyRateItem(
                                currencyRate = currencyRate
                            )
                        }
                    }
                )
            }
        }
    }
}

@Preview
@Composable
private fun ContentPreview() {
    MultiCurrencyConverterTheme {
        Surface {

            Content(
                context = ComponentActivity(),
                loading = false,
                currencyRates = listOf(
                    CurrencyRateUiModel.newInstance(
                        id = EMPTY,
                        currencySymbolBase = CURRENCY_SYMBOL_USD,
                        currencySymbolOther = CURRENCY_SYMBOL_AFN,
                        rate = 73.6,
                        currencySymbolOtherFlag = CountryFlagsUtil.FLAG_AFN,
                    ),
                    CurrencyRateUiModel.newInstance(
                        id = EMPTY,
                        currencySymbolBase = CURRENCY_SYMBOL_USD,
                        currencySymbolOther = CURRENCY_SYMBOL_ALL,
                        rate = 95.8,
                        currencySymbolOtherFlag = CountryFlagsUtil.FLAG_ALL,
                    ),
                    CurrencyRateUiModel.newInstance(
                        id = EMPTY,
                        currencySymbolBase = CURRENCY_SYMBOL_USD,
                        currencySymbolOther = CURRENCY_SYMBOL_AMD,
                        rate = 401.7,
                        currencySymbolOtherFlag = CountryFlagsUtil.FLAG_AMD,
                    ),
                    CurrencyRateUiModel.newInstance(
                        id = EMPTY,
                        currencySymbolBase = CURRENCY_SYMBOL_USD,
                        currencySymbolOther = CURRENCY_SYMBOL_ANG,
                        rate = 1.8,
                        currencySymbolOtherFlag = CountryFlagsUtil.FLAG_ANG,
                    ),
                    CurrencyRateUiModel.newInstance(
                        id = EMPTY,
                        currencySymbolBase = CURRENCY_SYMBOL_USD,
                        currencySymbolOther = CURRENCY_SYMBOL_EUR,
                        rate = 1.8,
                        currencySymbolOtherFlag = CountryFlagsUtil.FLAG_EUR,
                    )
                ),
                noCurrenciesLabel = R.string.no_currencies,
                noCurrenciesIconRes = R.drawable.logo_no_fill,
                mapOfCurrencySymbolsToFlag = mutableMapOf(CURRENCY_SYMBOL_USD to CountryFlagsUtil.FLAG_USD),
                onRefresh = { },
                onConvert = { _, _ -> }
            )
        }
    }
}

@Preview
@Composable
private fun ContentEmptyPreview() {
    MultiCurrencyConverterTheme {
        Surface {
            Content(
                context = ComponentActivity(),
                loading = false,
                currencyRates = emptyList(),
                noCurrenciesLabel = R.string.no_currencies,
                noCurrenciesIconRes = R.drawable.logo_no_fill,
                onRefresh = { },
                mapOfCurrencySymbolsToFlag = mutableMapOf(CURRENCY_SYMBOL_USD to CountryFlagsUtil.FLAG_USD),
                onConvert = { _, _ -> }
            )
        }
    }
}