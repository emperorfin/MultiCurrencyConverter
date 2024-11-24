package emperorfin.android.multicurrencyconverter.ui.model.currencyrate

import android.app.Application
import emperorfin.android.multicurrencyconverter.domain.constant.StringConstants.EMPTY
import emperorfin.android.multicurrencyconverter.domain.model.currencyrate.CurrencyRateModel
import emperorfin.android.multicurrencyconverter.ui.util.CountryFlagsUtil
import java.math.RoundingMode
import javax.inject.Inject

class CurrencyRateUiModelMapper @Inject constructor(
    private val applicationContext: Application
) {

    companion object {
        private const val DECIMAL_PLACES_2: Int = 2
    }

    fun transform(currencyRate: CurrencyRateModel): CurrencyRateUiModel {

        val id: String = currencyRate.id
        val currencySymbolBase: String = currencyRate.currencySymbolBase
        val currencySymbolOther: String = currencyRate.currencySymbolOther
        val rate: Double = currencyRate.rate

        val rateRoundedUp =
            rate.toBigDecimal().setScale(DECIMAL_PLACES_2, RoundingMode.UP).toDouble()

        val mapOfCurrencySymbolsToFlag: Map<String, String> =
            CountryFlagsUtil.loadMapOfCurrencySymbolToFlag(applicationContext.assets)

        val currencySymbolOtherFlag: String =
            mapOfCurrencySymbolsToFlag[currencySymbolOther] ?: EMPTY

        return CurrencyRateUiModel.newInstance(
            id = id,
            currencySymbolBase = currencySymbolBase,
            currencySymbolOther = currencySymbolOther,
            rate = rateRoundedUp,
            currencySymbolOtherFlag = currencySymbolOtherFlag,
        )

    }

}