package emperorfin.android.multicurrencyconverter.data.datasource.remote.framework.retrofit.dtosource.fake

import emperorfin.android.multicurrencyconverter.data.constant.StringConstants.ERROR_MESSAGE_INAPPROPRIATE_ARGUMENT_PASSED
import emperorfin.android.multicurrencyconverter.data.constant.StringConstants.ERROR_MESSAGE_NOT_YET_IMPLEMENTED
import emperorfin.android.multicurrencyconverter.data.datasource.util.CurrencyRateDataGeneratorUtil.CURRENCY_SYMBOL_BASE_USD
import emperorfin.android.multicurrencyconverter.data.datasource.util.CurrencyRateDataGeneratorUtil.CURRENCY_SYMBOL_OTHER_AED
import emperorfin.android.multicurrencyconverter.data.datasource.util.CurrencyRateDataGeneratorUtil.CURRENCY_SYMBOL_OTHER_AFN
import emperorfin.android.multicurrencyconverter.data.datasource.util.CurrencyRateDataGeneratorUtil.ID_AED
import emperorfin.android.multicurrencyconverter.data.datasource.util.CurrencyRateDataGeneratorUtil.ID_AFN
import emperorfin.android.multicurrencyconverter.data.datasource.util.CurrencyRateDataGeneratorUtil.RATE_AED
import emperorfin.android.multicurrencyconverter.data.datasource.util.CurrencyRateDataGeneratorUtil.RATE_AFN
import emperorfin.android.multicurrencyconverter.domain.datalayer.datasource.FakeCurrencyRatesDataSource
import emperorfin.android.multicurrencyconverter.domain.exception.CurrencyRateFailure.GetCurrencyRateRemoteError
import emperorfin.android.multicurrencyconverter.domain.exception.CurrencyRateFailure.CurrencyRateRemoteError
import emperorfin.android.multicurrencyconverter.domain.model.currencyrate.CurrencyRateModel
import emperorfin.android.multicurrencyconverter.domain.uilayer.event.input.currencyrate.CurrencyRateParams
import emperorfin.android.multicurrencyconverter.domain.uilayer.event.input.currencyrate.None
import emperorfin.android.multicurrencyconverter.domain.uilayer.event.output.ResultData
import emperorfin.android.multicurrencyconverter.domain.uilayer.event.output.currencyrate.Params


internal data class FakeCurrencyRatesRemoteDataSourceRetrofit(
    private val isGetCurrencyRatesException: Boolean = false,
    private val getCurrencyRatesResultData: ResultData.Success<List<CurrencyRateModel>> =
        ResultData.Success(CURRENCY_RATES_MODEL),
    private val isGetCurrencyRatesResponseUnsuccessful: Boolean = false,
) : FakeCurrencyRatesDataSource {

    private companion object {

        val CURRENCY_RATES_MODEL: List<CurrencyRateModel> = buildModelCurrencyRates()

        fun buildModelCurrencyRates(): List<CurrencyRateModel> {

            val currencyRateModel1 = CurrencyRateModel.newInstance(
                id = ID_AED,
                rate = RATE_AED,
                currencySymbolBase = CURRENCY_SYMBOL_BASE_USD,
                currencySymbolOther = CURRENCY_SYMBOL_OTHER_AED,
            )

            val currencyRateModel2 = CurrencyRateModel.newInstance(
                id = ID_AFN,
                rate = RATE_AFN,
                currencySymbolBase = CURRENCY_SYMBOL_BASE_USD,
                currencySymbolOther = CURRENCY_SYMBOL_OTHER_AFN,
            )

            return listOf(currencyRateModel1, currencyRateModel2)
        }

    }

    override suspend fun countAllCurrencyRates(params: Params): ResultData<Int> {
        TODO("Not yet implemented")
    }

    override suspend fun countCurrencyRates(params: Params): ResultData<Int> {
        TODO("Not yet implemented")
    }

    override suspend fun getCurrencyRates(params: Params): ResultData<List<CurrencyRateModel>> {
        when(params){
            is None -> {
                throw IllegalArgumentException(ERROR_MESSAGE_INAPPROPRIATE_ARGUMENT_PASSED)
            }
            is CurrencyRateParams -> {

                if (isGetCurrencyRatesException)
                    return ResultData.Error(failure = CurrencyRateRemoteError())

                if (isGetCurrencyRatesResponseUnsuccessful)
                    return ResultData.Error(failure = GetCurrencyRateRemoteError())

                if (getCurrencyRatesResultData.data.isEmpty())
                    throw IllegalArgumentException("getCurrencyRatesResultData.data must not be empty.")

                return getCurrencyRatesResultData
            }

            else -> throw NotImplementedError(ERROR_MESSAGE_NOT_YET_IMPLEMENTED)
        }
    }

    override suspend fun saveCurrencyRates(currencyRatesModel: List<CurrencyRateModel>): ResultData<List<Long>> {
        TODO("Not yet implemented")
    }

    override suspend fun deleteCurrencyRates(params: Params): ResultData<Int> {
        TODO("Not yet implemented")
    }
}
