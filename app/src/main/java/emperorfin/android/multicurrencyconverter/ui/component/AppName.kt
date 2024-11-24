package emperorfin.android.multicurrencyconverter.ui.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import emperorfin.android.multicurrencyconverter.R
import emperorfin.android.multicurrencyconverter.ui.theme.MultiCurrencyConverterTheme


@Composable
fun AppName(modifier: Modifier) {

    val fontSize = dimensionResource(id = R.dimen.app_name_text_font_size).value.sp

    Spacer(modifier = modifier.height(height = dimensionResource(id = R.dimen.app_name_spacer_height)))

    Column {
        Text(
            text = stringResource(R.string.currency_text),
            color = MaterialTheme.colorScheme.primary,
            fontSize = fontSize,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.SansSerif,
            modifier = modifier.fillMaxWidth()
        )

        Row(modifier.fillMaxWidth()) {
            Text(
                text = stringResource(R.string.converter_text),
                color = MaterialTheme.colorScheme.primary,
                fontSize = fontSize,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.SansSerif
            )

            Text(
                text = stringResource(R.string.dot_text),
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                fontSize = fontSize,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.SansSerif,
            )
        }
    }

}

@Preview
@Composable
private fun AppNamePreview() {
    MultiCurrencyConverterTheme {
        Surface {
            AppName(
                modifier = Modifier.padding(
                    vertical = dimensionResource(id = R.dimen.currency_converter_screen_content_app_name_padding_vertical),
                    horizontal = dimensionResource(id = R.dimen.currency_converter_screen_content_app_name_padding_horizontal)
                )
            )
        }
    }
}