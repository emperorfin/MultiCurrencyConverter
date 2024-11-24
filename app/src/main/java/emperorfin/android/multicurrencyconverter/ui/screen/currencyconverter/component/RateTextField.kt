package emperorfin.android.multicurrencyconverter.ui.screen.currencyconverter.component

import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import emperorfin.android.multicurrencyconverter.R
import emperorfin.android.multicurrencyconverter.ui.theme.MultiCurrencyConverterTheme


private const val TEXT_FIELD_VALUE_LENGTH: Int = 3

private const val TEXT_FIELD_VALUE_VALUE: String = "1234"

@Composable
fun RateTextField(
    modifier: Modifier = Modifier,
    value: String,
    onBaseAmountChanged: (newBaseAmount: String) -> Unit
) {

    TextField(
        value = value,
        onValueChange = { newValue ->
            if (newValue.length <= TEXT_FIELD_VALUE_LENGTH) onBaseAmountChanged(newValue)
        },
        modifier = modifier.wrapContentWidth(),
        singleLine = true,
        shape = RoundedCornerShape(size = dimensionResource(id = R.dimen.rate_text_field_text_field_shape_size)),
        colors = TextFieldDefaults.colors(
            disabledTextColor = Color.DarkGray,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
            cursorColor = MaterialTheme.colorScheme.onSecondary
        ),
        maxLines = 3,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
    )
}

@Preview
@Composable
private fun RateTextFieldPreview() {
    MultiCurrencyConverterTheme {
        Surface {
            RateTextField(
                value = TEXT_FIELD_VALUE_VALUE,
                onBaseAmountChanged = {}
            )
        }
    }
}