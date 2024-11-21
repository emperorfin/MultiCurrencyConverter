package emperorfin.android.multicurrencyconverter.domain.model.currencyrate

import emperorfin.android.multicurrencyconverter.domain.constant.DoubleConstants.MINUS_0_0
import emperorfin.android.multicurrencyconverter.domain.constant.StringConstants.EMPTY
import emperorfin.android.multicurrencyconverter.domain.uilayer.event.output.currencyrate.CurrencyRateDataTransferObjectParams
import emperorfin.android.multicurrencyconverter.domain.uilayer.event.output.currencyrate.CurrencyRateEntityParams
import emperorfin.android.multicurrencyconverter.domain.uilayer.event.output.currencyrate.CurrencyRateUiModelParams
import javax.inject.Inject

class CurrencyRateModelMapper @Inject constructor() {

    fun transform(currencyRate: CurrencyRateDataTransferObjectParams): CurrencyRateModel {

        val currencySymbolBase: String = currencyRate.currencySymbolBase ?: EMPTY
        val currencySymbolOther: String = currencyRate.currencySymbolOther ?: EMPTY
        val rate: Double = currencyRate.rate ?: MINUS_0_0

        val id = "$currencySymbolBase-$currencySymbolOther"

        return CurrencyRateModel.newInstance(
            id  = id,
            currencySymbolBase  = currencySymbolBase,
            currencySymbolOther  = currencySymbolOther,
            rate  = rate,
        )
    }

    fun transform(currencyRate: CurrencyRateEntityParams): CurrencyRateModel {

        val id: String = currencyRate.id
        val currencySymbolBase: String = currencyRate.currencySymbolBase!!
        val currencySymbolOther: String = currencyRate.currencySymbolOther!!
        val rate: Double = currencyRate.rate!!

        return CurrencyRateModel.newInstance(
            id  = id,
            currencySymbolBase  = currencySymbolBase,
            currencySymbolOther  = currencySymbolOther,
            rate  = rate,
        )
    }

    fun transform(currencyRate: CurrencyRateUiModelParams): CurrencyRateModel {

        val id: String = currencyRate.id
        val currencySymbolBase: String = currencyRate.currencySymbolBase!!
        val currencySymbolOther: String = currencyRate.currencySymbolOther!!
        val rate: Double = currencyRate.rate!!

        return CurrencyRateModel.newInstance(
            id  = id,
            currencySymbolBase  = currencySymbolBase,
            currencySymbolOther  = currencySymbolOther,
            rate  = rate,
        )
    }

}