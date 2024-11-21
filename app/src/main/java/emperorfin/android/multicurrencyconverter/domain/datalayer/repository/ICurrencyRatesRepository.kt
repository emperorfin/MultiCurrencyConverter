package emperorfin.android.multicurrencyconverter.domain.datalayer.repository

import emperorfin.android.multicurrencyconverter.domain.model.currencyrate.CurrencyRateModel
import emperorfin.android.multicurrencyconverter.domain.uilayer.event.output.ResultData
import emperorfin.android.multicurrencyconverter.domain.uilayer.event.output.currencyrate.Params

interface ICurrencyRatesRepository {

    suspend fun countAllCurrencyRates(params: Params, countRemotely: Boolean = false): ResultData<Int>

    suspend fun countCurrencyRates(params: Params, countRemotely: Boolean = false): ResultData<Int>

    suspend fun getCurrencyRates(params: Params, forceUpdate: Boolean = false): ResultData<List<CurrencyRateModel>>

    suspend fun saveCurrencyRates(currencyRatesModel: List<CurrencyRateModel>, saveRemotely: Boolean = false): ResultData<List<Long>>

    suspend fun deleteCurrencyRates(params: Params, deleteRemotely: Boolean = false): ResultData<Int>

}