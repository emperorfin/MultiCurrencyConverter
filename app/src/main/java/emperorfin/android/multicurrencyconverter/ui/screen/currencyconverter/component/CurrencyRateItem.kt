package emperorfin.android.multicurrencyconverter.ui.screen.currencyconverter.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import emperorfin.android.multicurrencyconverter.R
import emperorfin.android.multicurrencyconverter.ui.model.currencyrate.CurrencyRateUiModel
import emperorfin.android.multicurrencyconverter.ui.theme.Black
import emperorfin.android.multicurrencyconverter.ui.util.CountryFlagsUtil


@Composable
fun CurrencyRateItem(
    currencyRate: CurrencyRateUiModel
) {

    Card(
        modifier = Modifier
            .padding(all = dimensionResource(id = R.dimen.currency_rate_item_card_padding))
            .fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .fillMaxWidth()
                .padding(start = dimensionResource(id = R.dimen.currency_rate_item_row_padding_start)),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            val base64String = currencyRate.currencySymbolOtherFlag

            if (base64String.isNotEmpty()) {
                Icon(
                    modifier = Modifier.size(size = dimensionResource(id = R.dimen.currency_rate_item_icon_size)),
                    bitmap = CountryFlagsUtil.getFlagImageBitMap(base64String),
                    contentDescription = stringResource(R.string.content_description_flag),
                    tint = Color.Unspecified
                )
            } else {
                Icon(
                    modifier = Modifier.size(size = dimensionResource(id = R.dimen.currency_rate_item_icon_size)),
                    bitmap = CountryFlagsUtil.getFlagImageBitMap(CountryFlagsUtil.FLAG_DEFAULT),
                    contentDescription = stringResource(R.string.content_description_flag),
                    tint = Color.Unspecified
                )
            }

            Text(
                modifier = Modifier.padding(all = dimensionResource(id = R.dimen.currency_rate_item_text_padding)),
                text = "${currencyRate.currencySymbolOther}: ${currencyRate.rate}",
                fontWeight = FontWeight.Bold,
                fontSize = dimensionResource(id = R.dimen.currency_rate_item_text_font_size).value.sp,
                color = Black,
                textAlign = TextAlign.Center,
            )
        }
    }
}