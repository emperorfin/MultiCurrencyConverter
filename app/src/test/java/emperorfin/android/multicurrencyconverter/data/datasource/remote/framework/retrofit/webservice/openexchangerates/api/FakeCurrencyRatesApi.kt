package emperorfin.android.multicurrencyconverter.data.datasource.remote.framework.retrofit.webservice.openexchangerates.api

import emperorfin.android.multicurrencyconverter.data.datasource.local.framework.room.entity.currencyrate.CurrencyRateEntity
import emperorfin.android.multicurrencyconverter.data.datasource.remote.framework.retrofit.webservice.openexchangerates.endpoint.currencyrates.CurrencyRatesResponse
import emperorfin.android.multicurrencyconverter.data.datasource.util.CurrencyRateEntityDataGeneratorUtil
import emperorfin.android.multicurrencyconverter.domain.datalayer.dao.IFakeCurrencyRatesDao
import okhttp3.ResponseBody
import retrofit2.Response


internal data class FakeCurrencyRatesApi(
    private val noOfCurrencyRates: Int = NUM_OF_CURRENCY_RATES_150,
    private val noOfCurrencyRatesDeleted: Int = NUM_OF_CURRENCY_RATES_150,
    private val tableRowIds: List<Long> = TABLE_ROW_IDS_TWO,
    private val currencyRatesEntity: List<CurrencyRateEntity> = CURRENCY_RATES_ENTITY,
    private val isException: Boolean = false,
    private val isCountException: Boolean = false,
    private val isEmptyList: Boolean = false,
    private val isGetRemoteCurrencyRatesFailed: Boolean = false
) : IFakeCurrencyRatesDao {

    companion object {

        const val NUM_OF_CURRENCY_RATES_150: Int = 150

        private val CURRENCY_RATES_MAP: Map<String, Double> = mapOf(
            "AED" to 3.6721,
            "AFN" to 69.845466
        )

        val TABLE_ROW_IDS_TWO: List<Long> = listOf(1L, 2L)

        val CURRENCY_RATES_ENTITY: List<CurrencyRateEntity> =
            CurrencyRateEntityDataGeneratorUtil.getCurrencyRateEntityList()

        fun getSuccessfulRemoteCurrencyRates(): Response<CurrencyRatesResponse> {
            val responseWrapper = CurrencyRatesResponse(
                disclaimer = "Usage subject to terms: https://openexchangerates.org/terms",
                license = "https://openexchangerates.org/license",
                timestamp = 1701774000,
                base = "USD",
                rates = CURRENCY_RATES_MAP
            )

            val response: Response<CurrencyRatesResponse> = Response.success(responseWrapper)

            return response
        }

        fun getFailedRemoteCurrencyRates(): Response<CurrencyRatesResponse> {
            val responseBody: ResponseBody = ResponseBody.create(null, "Error encountered.")

            val response: Response<CurrencyRatesResponse> = Response.error(404, responseBody)

            return response
        }
    }

    override suspend fun countAllCurrencyRates(): Int {
        TODO("Not yet implemented")
    }

    override suspend fun countCurrencyRates(currencySymbolBase: String): Int {
        TODO("Not yet implemented")
    }

    override suspend fun getCurrencyRates(currencySymbolBase: String): List<CurrencyRateEntity> {
        TODO("Should not be implemented")
    }

    override suspend fun getCurrencyRates(currencySymbolBase: String, appId: String): Any {

        if (isException) throw Exception()

        if (isGetRemoteCurrencyRatesFailed) return getFailedRemoteCurrencyRates()

        return getSuccessfulRemoteCurrencyRates()
    }

    override suspend fun insertCurrencyRates(currencyRates: List<CurrencyRateEntity>): List<Long> {
        TODO("Not yet implemented")
    }

    override suspend fun deleteCurrencyRates(currencySymbolBase: String): Int {
        TODO("Not yet implemented")
    }
}
