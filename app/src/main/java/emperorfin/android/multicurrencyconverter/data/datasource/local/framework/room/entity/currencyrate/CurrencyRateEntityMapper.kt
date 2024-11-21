package emperorfin.android.multicurrencyconverter.data.datasource.local.framework.room.entity.currencyrate

import emperorfin.android.multicurrencyconverter.domain.model.currencyrate.CurrencyRateModel
import javax.inject.Inject

class CurrencyRateEntityMapper @Inject constructor() {

    fun transform(currencyRate: CurrencyRateModel): CurrencyRateEntity {

        val id: String = currencyRate.id
        val currencySymbolBase: String = currencyRate.currencySymbolBase
        val currencySymbolOther: String = currencyRate.currencySymbolOther
        val rate: Double = currencyRate.rate

        return CurrencyRateEntity.newInstance(
            id = id,
            currencySymbolBase = currencySymbolBase,
            currencySymbolOther = currencySymbolOther,
            rate = rate
        )
    }

}