package emperorfin.android.multicurrencyconverter.ui.component

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import emperorfin.android.multicurrencyconverter.R
import emperorfin.android.multicurrencyconverter.ui.theme.MultiCurrencyConverterTheme


@Composable
fun EmptyContent(
    @StringRes noCurrenciesLabel: Int,
    @DrawableRes noCurrenciesIconRes: Int,
    modifier: Modifier = Modifier,
    onRefresh: () -> Unit
) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = noCurrenciesIconRes),
            contentDescription = stringResource(R.string.content_description_no_currencies_image),
            modifier = Modifier
                .size(size = dimensionResource(id = R.dimen.empty_content_image_size))
                .clickable { onRefresh() }
        )

        Text(stringResource(id = noCurrenciesLabel))
    }
}

@Preview
@Composable
private fun EmptyContentPreview() {
    MultiCurrencyConverterTheme {
        Surface {
            EmptyContent(
                noCurrenciesLabel = R.string.no_currencies,
                noCurrenciesIconRes = R.drawable.logo_no_fill,
                onRefresh = {}
            )
        }
    }
}