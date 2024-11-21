package emperorfin.android.multicurrencyconverter.domain.datalayer.dao

import emperorfin.android.multicurrencyconverter.data.datasource.local.framework.room.entity.currencyrate.CurrencyRateEntity
import emperorfin.android.multicurrencyconverter.domain.uilayer.event.output.currencyrate.CurrencyRateEntityParams

interface ICurrencyRatesDao {

    suspend fun countAllCurrencyRates(): Int

    suspend fun countCurrencyRates(currencySymbolBase: String): Int

//    suspend fun getCurrencyRates(currencySymbolBase: String): List<CurrencyRateEntityParams>
    suspend fun getCurrencyRates(currencySymbolBase: String): List<CurrencyRateEntity>

    suspend fun getCurrencyRates(currencySymbolBase: String, appId: String): Any

    suspend fun insertCurrencyRates(currencyRates: List<CurrencyRateEntity>): List<Long>

    suspend fun deleteCurrencyRates(currencySymbolBase: String): Int

}