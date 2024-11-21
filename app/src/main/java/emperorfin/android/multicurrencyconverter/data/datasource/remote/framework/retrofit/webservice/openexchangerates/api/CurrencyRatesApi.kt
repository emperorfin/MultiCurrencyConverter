package emperorfin.android.multicurrencyconverter.data.datasource.remote.framework.retrofit.webservice.openexchangerates.api

import emperorfin.android.multicurrencyconverter.BuildConfig
import emperorfin.android.multicurrencyconverter.data.datasource.remote.framework.retrofit.webservice.openexchangerates.endpoint.currencyrates.CurrencyRatesResponse
import emperorfin.android.multicurrencyconverter.domain.datalayer.dao.ICurrencyRatesDao
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface CurrencyRatesApi : ICurrencyRatesDao {

    companion object {

        const val APP_ID: String = BuildConfig.OPEN_EXCHANGE_RATES_API_KEY

    }

    @GET("latest.json")
    override suspend fun getCurrencyRates(
        @Query("base") currencySymbolBase: String,
        @Query("app_id") appId: String
    ): Response<CurrencyRatesResponse>

}