package emperorfin.android.multicurrencyconverter.ui.model.currencyrate

import emperorfin.android.multicurrencyconverter.domain.uilayer.event.output.currencyrate.CurrencyRateUiModelParams

data class CurrencyRateUiModel private constructor(
    override val id: String,
    override val currencySymbolBase: String,
    override val currencySymbolOther: String,
    override val rate: Double,
    override val currencySymbolOtherFlag: String
) : CurrencyRateUiModelParams {

    companion object {

        fun newInstance(
            id: String,
            currencySymbolBase: String,
            currencySymbolOther: String,
            rate: Double,
            currencySymbolOtherFlag: String
        ): CurrencyRateUiModel {
            return CurrencyRateUiModel(
                id = id,
                currencySymbolBase = currencySymbolBase,
                currencySymbolOther = currencySymbolOther,
                rate = rate,
                currencySymbolOtherFlag = currencySymbolOtherFlag
            )
        }

    }

}
