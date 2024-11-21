package emperorfin.android.multicurrencyconverter.domain.uilayer.event.input.currencyrate

import emperorfin.android.multicurrencyconverter.domain.uilayer.event.output.currencyrate.CurrencyRateModelParams

data class CurrencyRateParams(
    override val id: String = "",
    override val currencySymbolBase: String? = null,
    override val currencySymbolOther: String? = null,
    override val rate: Double? = null
) : CurrencyRateModelParams
