package emperorfin.android.multicurrencyconverter.ui.model.currencyrate

import android.content.Context
import emperorfin.android.multicurrencyconverter.domain.constant.StringConstants.EMPTY
import emperorfin.android.multicurrencyconverter.domain.model.currencyrate.CurrencyRateModel
import emperorfin.android.multicurrencyconverter.ui.util.CountryFlagsUtil
import javax.inject.Inject

class CurrencyRateUiModelMapper @Inject constructor(
    private val context: Context
) {

    fun transform(currencyRate: CurrencyRateModel): CurrencyRateUiModel {

        val id: String = currencyRate.id
        val currencySymbolBase: String = currencyRate.currencySymbolBase
        val currencySymbolOther: String = currencyRate.currencySymbolOther
        val rate: Double = currencyRate.rate

        val mapOfCurrencySymbolsToFlag: Map<String, String> = CountryFlagsUtil.loadMapOfCurrencySymbolToFlag(context.assets)

        val currencySymbolOtherFlag: String = mapOfCurrencySymbolsToFlag[currencySymbolOther] ?: EMPTY

        return CurrencyRateUiModel.newInstance(
            id = id,
            currencySymbolBase = currencySymbolBase,
            currencySymbolOther = currencySymbolOther,
            rate = rate,
            currencySymbolOtherFlag = currencySymbolOtherFlag,
        )

    }

}