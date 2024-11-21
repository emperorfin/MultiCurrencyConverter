package emperorfin.android.multicurrencyconverter.data.datasource.remote.framework.retrofit.dto.currencyrate

import emperorfin.android.multicurrencyconverter.domain.model.currencyrate.CurrencyRateModel
import javax.inject.Inject

class CurrencyRateDataTransferObjectMapper @Inject constructor() {

    fun transform(currencyRate: CurrencyRateModel): CurrencyRateDataTransferObject {

        val currencySymbolBase: String = currencyRate.currencySymbolBase
        val currencySymbolOther: String = currencyRate.currencySymbolOther
        val rate: Double = currencyRate.rate

        return CurrencyRateDataTransferObject.newInstance(
            currencySymbolBase = currencySymbolBase,
            currencySymbolOther = currencySymbolOther,
            rate = rate
        )

    }

}