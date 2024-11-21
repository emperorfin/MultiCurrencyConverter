package emperorfin.android.multicurrencyconverter.data.datasource.remote.framework.retrofit.dto.currencyrate

import emperorfin.android.multicurrencyconverter.domain.uilayer.event.output.currencyrate.CurrencyRateDataTransferObjectParams

data class CurrencyRateDataTransferObject private constructor(
    override val currencySymbolBase: String?,
    override val currencySymbolOther: String?,
    override val rate: Double?
) : CurrencyRateDataTransferObjectParams {

    companion object {

        fun newInstance(
            currencySymbolBase: String?,
            currencySymbolOther: String?,
            rate: Double?
        ): CurrencyRateDataTransferObject {
            return CurrencyRateDataTransferObject(
                currencySymbolBase = currencySymbolBase,
                currencySymbolOther = currencySymbolOther,
                rate = rate
            )
        }

    }

}
