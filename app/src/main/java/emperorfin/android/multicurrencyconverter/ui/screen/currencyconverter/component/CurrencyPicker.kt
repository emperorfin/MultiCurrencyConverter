package emperorfin.android.multicurrencyconverter.ui.screen.currencyconverter.component

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize
import emperorfin.android.multicurrencyconverter.R
import emperorfin.android.multicurrencyconverter.ui.theme.MultiCurrencyConverterTheme
import emperorfin.android.multicurrencyconverter.ui.util.CountryFlagsUtil


private const val CURRENCY_SYMBOL_USD: String = "USD"

@Composable
fun CurrencyPicker(
    modifier: Modifier = Modifier,
    defaultSymbol: String,
    mapOfCurrencySymbolsToFlag: Map<String, String?>,
    onSymbolSelected: (String) -> Unit
) {

    var textFieldSize: Size by remember { mutableStateOf(Size.Zero) }

    var isExpanded: Boolean by rememberSaveable { mutableStateOf(false) }

    var selectedSymbol: String by rememberSaveable { mutableStateOf(defaultSymbol) }

    Box {
        OutlinedTextField(
            modifier = modifier
                .border(
                    width = dimensionResource(id = R.dimen.currency_picker_outlined_text_field_border_width),
                    color = MaterialTheme.colorScheme.secondary,
                    shape = RoundedCornerShape(size = dimensionResource(id = R.dimen.currency_picker_outlined_text_field_shape_size))
                )
                .width(width = dimensionResource(id = R.dimen.currency_picker_outlined_text_field_width))
                .onGloballyPositioned { coordinates ->
                    textFieldSize = coordinates.size.toSize()
                },
            value = selectedSymbol,
            leadingIcon = {
                val base64String = mapOfCurrencySymbolsToFlag[selectedSymbol]

                if (base64String != null) {
                    Icon(
                        modifier = Modifier.size(size = dimensionResource(id = R.dimen.currency_picker_icon_size)),
                        bitmap = CountryFlagsUtil.getFlagImageBitMap(base64String),
                        contentDescription = stringResource(R.string.content_description_flag),
                        tint = Color.Unspecified
                    )
                } else {
                    Icon(
                        modifier = Modifier.size(size = dimensionResource(id = R.dimen.currency_picker_icon_size)),
                        bitmap = CountryFlagsUtil.getFlagImageBitMap(CountryFlagsUtil.FLAG_DEFAULT),
                        contentDescription = stringResource(R.string.content_description_flag),
                        tint = Color.Unspecified
                    )
                }
            },
            textStyle = LocalTextStyle.current.copy(fontWeight = FontWeight.SemiBold),
            singleLine = true,
            onValueChange = { newInput ->
                selectedSymbol = newInput.uppercase()

                onSymbolSelected(newInput)
            },
            trailingIcon = {
                Icon(
                    imageVector = if (isExpanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                    contentDescription = if (isExpanded) stringResource(R.string.content_description_show_less) else stringResource(
                        R.string.content_description_show_more
                    ),
                    Modifier
                        .clip(CircleShape)
                        .clickable(enabled = true) { isExpanded = !isExpanded },
                    tint = MaterialTheme.colorScheme.onSecondary
                )
            },
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
                cursorColor = MaterialTheme.colorScheme.onSecondary,
            ),
            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Characters)
        )

        DropdownMenu(
            expanded = isExpanded,
            onDismissRequest = { isExpanded = false },
            modifier = modifier
                .width(with(LocalDensity.current) { textFieldSize.width.toDp() })
                .height(height = dimensionResource(id = R.dimen.currency_picker_drop_down_menu_height))
        ) {
            mapOfCurrencySymbolsToFlag.forEach { item ->

                DropdownMenuItem(
                    leadingIcon = {
                        if (item.value?.isNotEmpty() == true) {
                            Icon(
                                modifier = Modifier.size(size = dimensionResource(id = R.dimen.currency_picker_icon_size)),
                                bitmap = CountryFlagsUtil.getFlagImageBitMap(item.value!!),
                                contentDescription = stringResource(R.string.content_description_flag),
                                tint = Color.Unspecified
                            )
                        } else {
                            Icon(
                                modifier = Modifier.size(size = dimensionResource(id = R.dimen.currency_picker_icon_size)),
                                bitmap = CountryFlagsUtil.getFlagImageBitMap(CountryFlagsUtil.FLAG_DEFAULT),
                                contentDescription = stringResource(R.string.content_description_flag),
                                tint = Color.Unspecified
                            )
                        }
                    },
                    text = {
                        Text(
                            text = item.key,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = dimensionResource(id = R.dimen.currency_picker_text_font_size).value.sp
                        )
                    },
                    onClick = {
                        selectedSymbol = item.key
                        onSymbolSelected(item.key)
                        isExpanded = false
                    }
                )
            }
        }
    }
}

@Preview
@Composable
private fun CurrencyPickerPreview() {
    MultiCurrencyConverterTheme {
        Surface {
            CurrencyPicker(
                defaultSymbol = CURRENCY_SYMBOL_USD,
                mapOfCurrencySymbolsToFlag = mutableMapOf(CURRENCY_SYMBOL_USD to CountryFlagsUtil.FLAG_USD),
                onSymbolSelected = { _ -> }
            )
        }
    }
}