package emperorfin.android.multicurrencyconverter.domain.datalayer.datasource

import emperorfin.android.multicurrencyconverter.domain.model.currencyrate.CurrencyRateModel
import emperorfin.android.multicurrencyconverter.domain.uilayer.event.output.ResultData
import emperorfin.android.multicurrencyconverter.domain.uilayer.event.output.currencyrate.Params

interface CurrencyRatesDataSource {

    suspend fun countAllCurrencyRates(params: Params): ResultData<Int>

    suspend fun countCurrencyRates(params: Params): ResultData<Int>

    suspend fun getCurrencyRates(params: Params): ResultData<List<CurrencyRateModel>>

    suspend fun saveCurrencyRates(currencyRatesModel: List<CurrencyRateModel>): ResultData<List<Long>>

    suspend fun deleteCurrencyRates(params: Params): ResultData<Int>

}