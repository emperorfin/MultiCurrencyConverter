package emperorfin.android.multicurrencyconverter.domain.model.currencyrate

import emperorfin.android.multicurrencyconverter.domain.uilayer.event.output.currencyrate.CurrencyRateModelParams

data class CurrencyRateModel private constructor(
    override val id: String,
    override val currencySymbolBase: String,
    override val currencySymbolOther: String,
    override val rate: Double
) : CurrencyRateModelParams {

    companion object {

        fun newInstance(
            id: String,
            currencySymbolBase: String,
            currencySymbolOther: String,
            rate: Double
        ): CurrencyRateModel {
            return CurrencyRateModel(
                id = id,
                currencySymbolBase = currencySymbolBase,
                currencySymbolOther = currencySymbolOther,
                rate = rate
            )
        }

    }

}
