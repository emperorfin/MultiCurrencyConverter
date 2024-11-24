package emperorfin.android.multicurrencyconverter.data.datasource.remote.framework.retrofit.dtosource

import emperorfin.android.multicurrencyconverter.data.constant.StringConstants.ERROR_MESSAGE_INAPPROPRIATE_ARGUMENT_PASSED
import emperorfin.android.multicurrencyconverter.data.constant.StringConstants.ERROR_MESSAGE_NOT_YET_IMPLEMENTED
import emperorfin.android.multicurrencyconverter.data.datasource.remote.framework.retrofit.dto.currencyrate.CurrencyRateDataTransferObject
import emperorfin.android.multicurrencyconverter.di.IoDispatcher
import emperorfin.android.multicurrencyconverter.di.MainDispatcher
import emperorfin.android.multicurrencyconverter.di.RemoteCurrencyRatesDao
import emperorfin.android.multicurrencyconverter.domain.datalayer.datasource.CurrencyRatesDataSource
import emperorfin.android.multicurrencyconverter.domain.model.currencyrate.CurrencyRateModel
import emperorfin.android.multicurrencyconverter.domain.uilayer.event.output.ResultData
import emperorfin.android.multicurrencyconverter.domain.uilayer.event.output.ResultData.Success
import emperorfin.android.multicurrencyconverter.domain.uilayer.event.output.ResultData.Error
import emperorfin.android.multicurrencyconverter.domain.uilayer.event.output.currencyrate.Params
import emperorfin.android.multicurrencyconverter.domain.datalayer.dao.ICurrencyRatesDao
import emperorfin.android.multicurrencyconverter.data.datasource.remote.framework.retrofit.dto.currencyrate.CurrencyRateDataTransferObjectMapper
import emperorfin.android.multicurrencyconverter.data.datasource.remote.framework.retrofit.webservice.openexchangerates.api.CurrencyRatesApi
import emperorfin.android.multicurrencyconverter.data.datasource.remote.framework.retrofit.webservice.openexchangerates.endpoint.currencyrates.CurrencyRatesResponse
import emperorfin.android.multicurrencyconverter.domain.exception.CurrencyRateFailure.GetCurrencyRateRemoteError
import emperorfin.android.multicurrencyconverter.domain.exception.CurrencyRateFailure.CurrencyRateRemoteError
import emperorfin.android.multicurrencyconverter.domain.model.currencyrate.CurrencyRateModelMapper
import emperorfin.android.multicurrencyconverter.domain.uilayer.event.input.currencyrate.CurrencyRateParams
import emperorfin.android.multicurrencyconverter.domain.uilayer.event.input.currencyrate.None
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import retrofit2.Response
import javax.inject.Inject

data class CurrencyRatesRemoteDataSourceRetrofit @Inject internal constructor(
    @RemoteCurrencyRatesDao private val currencyRatesDao: ICurrencyRatesDao,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    @MainDispatcher private val mainDispatcher: CoroutineDispatcher,
    private val currencyRateModelMapper: CurrencyRateModelMapper
) : CurrencyRatesDataSource {
    override suspend fun countAllCurrencyRates(params: Params): ResultData<Int> =
        TODO("Not yet implemented")

    override suspend fun countCurrencyRates(params: Params): ResultData<Int> =
        TODO("Not yet implemented")

    override suspend fun getCurrencyRates(
        params: Params
    ): ResultData<List<CurrencyRateModel>> = withContext(ioDispatcher) {
        when(params){
            is None -> {
                throw IllegalArgumentException(ERROR_MESSAGE_INAPPROPRIATE_ARGUMENT_PASSED)
            }
            is CurrencyRateParams -> {

                return@withContext try {

                    val response: Response<CurrencyRatesResponse> = currencyRatesDao.getCurrencyRates(
                        currencySymbolBase = params.currencySymbolBase!!,
                        appId = CurrencyRatesApi.APP_ID
                    ) as Response<CurrencyRatesResponse>

                    withContext(mainDispatcher){
                        if (response.isSuccessful){

                            val responseBody: CurrencyRatesResponse? = response.body()

                            responseBody?.let {
                                val currencyRatesModel: List<CurrencyRateModel> =
                                    buildCurrencyRateModelList(base = it.base, openExchangesRates = it.rates)

                                return@withContext Success(currencyRatesModel)
                            }
                        }

                        return@withContext Error(failure = GetCurrencyRateRemoteError())
                    }

                } catch (e: Exception){
                    return@withContext Error(failure = CurrencyRateRemoteError(cause = e))
                }
            }
            else -> throw NotImplementedError(ERROR_MESSAGE_NOT_YET_IMPLEMENTED)
        }
    }

    override suspend fun saveCurrencyRates(currencyRatesModel: List<CurrencyRateModel>): ResultData<List<Long>> =
        TODO("Not yet implemented")

    override suspend fun deleteCurrencyRates(params: Params): ResultData<Int> =
        TODO("Not yet implemented")

    private fun buildCurrencyRateModelList(
        base: String,
        openExchangesRates: Map<String, Number>
    ): List<CurrencyRateModel> {
        val currencyRatesDto = mutableListOf<CurrencyRateDataTransferObject>()

        openExchangesRates.forEach {

            val currencySymbolOther: String = it.key
            val rate: Double = it.value.toDouble()

            val currencyRate = CurrencyRateDataTransferObject.newInstance(
                currencySymbolBase = base,
                currencySymbolOther = currencySymbolOther,
                rate = rate
            )

            currencyRatesDto.add(currencyRate)
        }

        return currencyRatesDto.map {
            currencyRateModelMapper.transform(it)
        }
    }
}
