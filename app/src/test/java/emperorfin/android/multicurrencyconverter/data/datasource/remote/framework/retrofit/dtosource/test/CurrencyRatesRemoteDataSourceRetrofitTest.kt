package emperorfin.android.multicurrencyconverter.data.datasource.remote.framework.retrofit.dtosource.test

import emperorfin.android.multicurrencyconverter.data.constant.StringConstants.ERROR_MESSAGE_INAPPROPRIATE_ARGUMENT_PASSED
import emperorfin.android.multicurrencyconverter.data.constant.StringConstants.ERROR_MESSAGE_NOT_YET_IMPLEMENTED
import emperorfin.android.multicurrencyconverter.data.datasource.remote.framework.retrofit.dtosource.CurrencyRatesRemoteDataSourceRetrofit
import emperorfin.android.multicurrencyconverter.data.datasource.remote.framework.retrofit.webservice.openexchangerates.api.FakeCurrencyRatesApi
import emperorfin.android.multicurrencyconverter.data.datasource.util.CurrencyRateDataGeneratorUtil.CURRENCY_SYMBOL_BASE_USD
import emperorfin.android.multicurrencyconverter.data.datasource.util.CurrencyRateDataGeneratorUtil.CURRENCY_SYMBOL_OTHER_AED
import emperorfin.android.multicurrencyconverter.data.datasource.util.CurrencyRateDataGeneratorUtil.CURRENCY_SYMBOL_OTHER_AFN
import emperorfin.android.multicurrencyconverter.data.datasource.util.CurrencyRateDataGeneratorUtil.ID_AED
import emperorfin.android.multicurrencyconverter.data.datasource.util.CurrencyRateDataGeneratorUtil.ID_AFN
import emperorfin.android.multicurrencyconverter.data.datasource.util.CurrencyRateDataGeneratorUtil.RATE_AED
import emperorfin.android.multicurrencyconverter.data.datasource.util.CurrencyRateDataGeneratorUtil.RATE_AFN
import emperorfin.android.multicurrencyconverter.domain.exception.CurrencyRateFailure.CurrencyRateRemoteError
import emperorfin.android.multicurrencyconverter.domain.exception.CurrencyRateFailure.GetCurrencyRateRemoteError
import emperorfin.android.multicurrencyconverter.domain.model.currencyrate.CurrencyRateModel
import emperorfin.android.multicurrencyconverter.domain.model.currencyrate.CurrencyRateModelMapper
import emperorfin.android.multicurrencyconverter.domain.uilayer.event.input.currencyrate.CurrencyRateParams
import emperorfin.android.multicurrencyconverter.domain.uilayer.event.input.currencyrate.None
import emperorfin.android.multicurrencyconverter.domain.uilayer.event.output.ResultData
import emperorfin.android.multicurrencyconverter.domain.uilayer.event.output.currencyrate.Params
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.runTest
import org.hamcrest.core.IsEqual
import org.hamcrest.core.IsEqual.equalTo
import org.hamcrest.core.IsInstanceOf
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExpectedException


internal class CurrencyRatesRemoteDataSourceRetrofitTest {

    private companion object {

        const val ERROR_MESSAGE_TODO_NOT_YET_IMPLEMENTED: String =
            "An operation is not implemented: Not yet implemented"

        const val IS_EXCEPTION_TRUE: Boolean = true
        const val IS_GET_CURRENCY_RATES_FAILED_TRUE: Boolean = true

        val PARAMS_NONE: None = None()
        val PARAMS_CURRENCY_CONVERTER: CurrencyRateParams =
            CurrencyRateParams(currencySymbolBase = "USD")
        val PARAMS_BAD: BadParams = BadParams()

    }

    private lateinit var currencyRatesDao: FakeCurrencyRatesApi

    // Class under test
    private lateinit var currencyConverterRemoteDataSourceRetrofit: CurrencyRatesRemoteDataSourceRetrofit

    @JvmField
    @Rule
    val expectedException: ExpectedException = ExpectedException.none()

    @Before
    fun createLocalDataSource() {

        currencyRatesDao = FakeCurrencyRatesApi()

        currencyConverterRemoteDataSourceRetrofit = CurrencyRatesRemoteDataSourceRetrofit(
            currencyRatesDao = currencyRatesDao,
            ioDispatcher = Dispatchers.Unconfined,
            mainDispatcher = Dispatchers.Unconfined,
            currencyRateModelMapper = CurrencyRateModelMapper()
        )
    }

    @Test
    fun getCurrencyRates_CurrencyRatesListNotEmpty() = runTest {

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

        val currencyRatesModel = listOf(currencyRateModel1, currencyRateModel2)

        val params = PARAMS_CURRENCY_CONVERTER

        val currencyRatesModelResultData: ResultData.Success<List<CurrencyRateModel>> =
            currencyConverterRemoteDataSourceRetrofit.getCurrencyRates(params = params) as ResultData.Success

        assertThat(currencyRatesModelResultData.data, IsEqual(currencyRatesModel))
    }

    @Test
    fun getCurrencyRates_GetCurrencyRatesRemoteError() = runTest {

        currencyRatesDao = currencyRatesDao
            .copy(isGetRemoteCurrencyRatesFailed = IS_GET_CURRENCY_RATES_FAILED_TRUE)

        currencyConverterRemoteDataSourceRetrofit = currencyConverterRemoteDataSourceRetrofit
            .copy(currencyRatesDao = currencyRatesDao)

        val params = PARAMS_CURRENCY_CONVERTER

        val errorResultData: ResultData.Error =
            currencyConverterRemoteDataSourceRetrofit.getCurrencyRates(params = params) as ResultData.Error

        assertThat(errorResultData.failure, IsInstanceOf(GetCurrencyRateRemoteError::class.java))
    }

    @Test
    fun getCurrencyRates_ExceptionThrown() = runTest {

        currencyRatesDao = currencyRatesDao.copy(isException = IS_EXCEPTION_TRUE)

        currencyConverterRemoteDataSourceRetrofit = currencyConverterRemoteDataSourceRetrofit
            .copy(currencyRatesDao = currencyRatesDao)

        val params = PARAMS_CURRENCY_CONVERTER

        val errorResultData: ResultData.Error =
            currencyConverterRemoteDataSourceRetrofit.getCurrencyRates(params = params) as ResultData.Error

        assertThat(errorResultData.failure, IsInstanceOf(CurrencyRateRemoteError::class.java))
    }

    @Test
    fun getCurrencyRates_IllegalArgumentExceptionThrown() = runTest {

        val params = PARAMS_NONE

        expectedException.expect(IllegalArgumentException::class.java)
        expectedException.expectMessage(equalTo(ERROR_MESSAGE_INAPPROPRIATE_ARGUMENT_PASSED))

        currencyConverterRemoteDataSourceRetrofit.getCurrencyRates(params = params)
    }

    @Test
    fun getCurrencyRates_NotImplementedErrorThrown() = runTest {

        val params = PARAMS_BAD

        expectedException.expect(NotImplementedError::class.java)
        expectedException.expectMessage(equalTo(ERROR_MESSAGE_NOT_YET_IMPLEMENTED))

        currencyConverterRemoteDataSourceRetrofit.getCurrencyRates(params = params)
    }

    @Test
    fun countAllCurrencyRates_NotYetImplementedErrorThrown() = runTest {

        val params = PARAMS_BAD

        expectedException.expect(NotImplementedError::class.java)
        expectedException.expectMessage(equalTo(ERROR_MESSAGE_TODO_NOT_YET_IMPLEMENTED))

        currencyConverterRemoteDataSourceRetrofit.countAllCurrencyRates(params = params)
    }

    @Test
    fun countCurrencyRates_NotYetImplementedErrorThrown() = runTest {

        val params = PARAMS_BAD

        expectedException.expect(NotImplementedError::class.java)
        expectedException.expectMessage(equalTo(ERROR_MESSAGE_TODO_NOT_YET_IMPLEMENTED))

        currencyConverterRemoteDataSourceRetrofit.countCurrencyRates(params = params)
    }

    @Test
    fun saveCurrencyRates_NotYetImplementedErrorThrown() = runTest {

        expectedException.expect(NotImplementedError::class.java)
        expectedException.expectMessage(equalTo(ERROR_MESSAGE_TODO_NOT_YET_IMPLEMENTED))

        currencyConverterRemoteDataSourceRetrofit.saveCurrencyRates(currencyRatesModel = emptyList())
    }

    @Test
    fun deleteCurrencyRates_NotYetImplementedErrorThrown() = runTest {

        val params = PARAMS_BAD

        expectedException.expect(NotImplementedError::class.java)
        expectedException.expectMessage(equalTo(ERROR_MESSAGE_TODO_NOT_YET_IMPLEMENTED))

        currencyConverterRemoteDataSourceRetrofit.deleteCurrencyRates(params = params)
    }

}

private class BadParams : Params