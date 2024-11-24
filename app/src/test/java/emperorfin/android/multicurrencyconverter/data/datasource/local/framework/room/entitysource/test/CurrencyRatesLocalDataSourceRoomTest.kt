package emperorfin.android.multicurrencyconverter.data.datasource.local.framework.room.entitysource.test

import emperorfin.android.multicurrencyconverter.R
import emperorfin.android.multicurrencyconverter.data.constant.StringConstants.ERROR_MESSAGE_INAPPROPRIATE_ARGUMENT_PASSED
import emperorfin.android.multicurrencyconverter.data.constant.StringConstants.ERROR_MESSAGE_NOT_YET_IMPLEMENTED
import emperorfin.android.multicurrencyconverter.data.datasource.local.framework.room.dao.FakeCurrencyRatesDao
import emperorfin.android.multicurrencyconverter.data.datasource.local.framework.room.dao.FakeCurrencyRatesDao.Companion.NUM_OF_CURRENCY_RATES_150
import emperorfin.android.multicurrencyconverter.data.datasource.local.framework.room.dao.FakeCurrencyRatesDao.Companion.TABLE_ROW_IDS_TWO
import emperorfin.android.multicurrencyconverter.data.datasource.local.framework.room.entity.currencyrate.CurrencyRateEntityMapper
import emperorfin.android.multicurrencyconverter.data.datasource.local.framework.room.entitysource.CurrencyRatesLocalDataSourceRoom
import emperorfin.android.multicurrencyconverter.data.datasource.util.CurrencyRateDataGeneratorUtil.CURRENCY_SYMBOL_BASE_USD
import emperorfin.android.multicurrencyconverter.data.datasource.util.CurrencyRateDataGeneratorUtil.CURRENCY_SYMBOL_OTHER_AED
import emperorfin.android.multicurrencyconverter.data.datasource.util.CurrencyRateDataGeneratorUtil.CURRENCY_SYMBOL_OTHER_AFN
import emperorfin.android.multicurrencyconverter.data.datasource.util.CurrencyRateDataGeneratorUtil.CURRENCY_SYMBOL_OTHER_ALL
import emperorfin.android.multicurrencyconverter.data.datasource.util.CurrencyRateDataGeneratorUtil.ID_AED
import emperorfin.android.multicurrencyconverter.data.datasource.util.CurrencyRateDataGeneratorUtil.ID_AFN
import emperorfin.android.multicurrencyconverter.data.datasource.util.CurrencyRateDataGeneratorUtil.ID_ALL
import emperorfin.android.multicurrencyconverter.data.datasource.util.CurrencyRateDataGeneratorUtil.RATE_AED
import emperorfin.android.multicurrencyconverter.data.datasource.util.CurrencyRateDataGeneratorUtil.RATE_AFN
import emperorfin.android.multicurrencyconverter.data.datasource.util.CurrencyRateDataGeneratorUtil.RATE_ALL
import emperorfin.android.multicurrencyconverter.domain.exception.CurrencyRateFailure.CurrencyRateListNotAvailableLocalError
import emperorfin.android.multicurrencyconverter.domain.exception.CurrencyRateFailure.CurrencyRateLocalError
import emperorfin.android.multicurrencyconverter.domain.exception.CurrencyRateFailure.DeleteCurrencyRateLocalError
import emperorfin.android.multicurrencyconverter.domain.exception.CurrencyRateFailure.InsertCurrencyRateLocalError
import emperorfin.android.multicurrencyconverter.domain.exception.CurrencyRateFailure.NonExistentCurrencyRateDataLocalError
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


internal class CurrencyRatesLocalDataSourceRoomTest {

    private companion object {

        const val NUM_OF_CURRENCY_RATES_MINUS_1: Int = -1
        const val NUM_OF_CURRENCY_RATES_0: Int = 0
        const val NUM_OF_CURRENCY_RATES_DELETED_1: Int = 1

        const val IS_EXCEPTION_TRUE: Boolean = true
        const val IS_CURRENCY_RATES_LIST_EMPTY_TRUE: Boolean = true

        val PARAMS_NONE: None = None()
        val PARAMS_CURRENCY_CONVERTER: CurrencyRateParams =
            CurrencyRateParams(currencySymbolBase = "USD")
        val PARAMS_BAD: BadParams = BadParams()

    }

    private lateinit var currencyRatesDao: FakeCurrencyRatesDao

    // Class under test
    private lateinit var currencyConverterLocalDataSourceRoom: CurrencyRatesLocalDataSourceRoom

    @JvmField
    @Rule
    val expectedException: ExpectedException = ExpectedException.none()

    @Before
    fun createLocalDataSource() {

        currencyRatesDao = FakeCurrencyRatesDao()

        currencyConverterLocalDataSourceRoom = CurrencyRatesLocalDataSourceRoom(
            currencyRatesDao = currencyRatesDao,
            ioDispatcher = Dispatchers.Unconfined,
            currencyRateEntityMapper = CurrencyRateEntityMapper(),
            currencyRateModelMapper = CurrencyRateModelMapper(),
        )
    }

    @Test
    fun countAllCurrencyRates_CurrencyRatesMoreThanZero() = runTest {
        val noOfCurrencyRatesExpected: Int = NUM_OF_CURRENCY_RATES_150

        val params = PARAMS_NONE

        val numOfAllCurrencyRatesResultData: ResultData.Success<Int> =
            currencyConverterLocalDataSourceRoom
                .countAllCurrencyRates(params = params) as ResultData.Success

        assertThat(numOfAllCurrencyRatesResultData.data, IsEqual(noOfCurrencyRatesExpected))
    }

    @Test
    fun countAllCurrencyRates_NonExistentCurrencyRateDataError() = runTest {

        currencyRatesDao = currencyRatesDao.copy(noOfCurrencyRates = NUM_OF_CURRENCY_RATES_0)

        currencyConverterLocalDataSourceRoom = currencyConverterLocalDataSourceRoom.copy(
            currencyRatesDao = currencyRatesDao
        )

        val params = PARAMS_NONE

        val errorResultData: ResultData.Error = currencyConverterLocalDataSourceRoom
            .countAllCurrencyRates(params = params) as ResultData.Error

        assertThat(
            errorResultData.failure,
            IsInstanceOf(NonExistentCurrencyRateDataLocalError::class.java)
        )
    }

    @Test
    fun countAllCurrencyRates_GeneralError() = runTest {

        currencyRatesDao = currencyRatesDao.copy(noOfCurrencyRates = NUM_OF_CURRENCY_RATES_MINUS_1)

        currencyConverterLocalDataSourceRoom = currencyConverterLocalDataSourceRoom.copy(
            currencyRatesDao = currencyRatesDao
        )

        val params = PARAMS_NONE

        val errorResultData: ResultData.Error = currencyConverterLocalDataSourceRoom
            .countAllCurrencyRates(params = params) as ResultData.Error

        assertThat(errorResultData.failure, IsInstanceOf(CurrencyRateLocalError::class.java))
    }

    @Test
    fun countAllCurrencyRates_ExceptionThrown() = runTest {

        currencyRatesDao = currencyRatesDao.copy(isCountException = IS_EXCEPTION_TRUE)

        currencyConverterLocalDataSourceRoom = currencyConverterLocalDataSourceRoom.copy(
            currencyRatesDao = currencyRatesDao
        )

        val params = PARAMS_NONE

        val errorResultData: ResultData.Error = currencyConverterLocalDataSourceRoom
            .countAllCurrencyRates(params = params) as ResultData.Error

        assertThat(errorResultData.failure, IsInstanceOf(CurrencyRateLocalError::class.java))
    }

    @Test
    fun countAllCurrencyRates_IllegalArgumentExceptionThrown() = runTest {

        val params = CurrencyRateParams()

        expectedException.expect(IllegalArgumentException::class.java)
        expectedException.expectMessage(equalTo(ERROR_MESSAGE_INAPPROPRIATE_ARGUMENT_PASSED))

        currencyConverterLocalDataSourceRoom.countAllCurrencyRates(params = params)
    }

    @Test
    fun countAllCurrencyRates_NotImplementedErrorThrown() = runTest {

        val params = PARAMS_BAD

        expectedException.expect(NotImplementedError::class.java)
        expectedException.expectMessage(equalTo(ERROR_MESSAGE_NOT_YET_IMPLEMENTED))

        currencyConverterLocalDataSourceRoom.countAllCurrencyRates(params = params)
    }

    @Test
    fun countCurrencyRates_CurrencyRatesMoreThanZero() = runTest {
        val noOfCurrencyRatesExpected: Int = NUM_OF_CURRENCY_RATES_150

        val params = PARAMS_CURRENCY_CONVERTER

        val numOfCurrencyRatesResultData: ResultData.Success<Int> =
            currencyConverterLocalDataSourceRoom
                .countCurrencyRates(params = params) as ResultData.Success

        assertThat(numOfCurrencyRatesResultData.data, IsEqual(noOfCurrencyRatesExpected))
    }

    @Test
    fun countCurrencyRates_NonExistentCurrencyRateDataError() = runTest {
        currencyRatesDao = currencyRatesDao.copy(noOfCurrencyRates = NUM_OF_CURRENCY_RATES_0)

        currencyConverterLocalDataSourceRoom = currencyConverterLocalDataSourceRoom.copy(
            currencyRatesDao = currencyRatesDao
        )

        val params = PARAMS_CURRENCY_CONVERTER

        val errorResultData: ResultData.Error = currencyConverterLocalDataSourceRoom
            .countCurrencyRates(params = params) as ResultData.Error

        assertThat(
            errorResultData.failure,
            IsInstanceOf(NonExistentCurrencyRateDataLocalError::class.java)
        )
    }

    @Test
    fun countCurrencyRates_GeneralError() = runTest {

        currencyRatesDao = currencyRatesDao.copy(noOfCurrencyRates = NUM_OF_CURRENCY_RATES_MINUS_1)

        currencyConverterLocalDataSourceRoom = currencyConverterLocalDataSourceRoom.copy(
            currencyRatesDao = currencyRatesDao
        )

        val params = PARAMS_CURRENCY_CONVERTER

        val errorResultData: ResultData.Error = currencyConverterLocalDataSourceRoom
            .countCurrencyRates(params = params) as ResultData.Error

        assertThat(errorResultData.failure, IsInstanceOf(CurrencyRateLocalError::class.java))
    }

    @Test
    fun countCurrencyRates_ExceptionThrown() = runTest {

        currencyRatesDao = currencyRatesDao.copy(isCountException = IS_EXCEPTION_TRUE)

        currencyConverterLocalDataSourceRoom = currencyConverterLocalDataSourceRoom.copy(
            currencyRatesDao = currencyRatesDao
        )

        val params = PARAMS_CURRENCY_CONVERTER

        val errorResultData: ResultData.Error = currencyConverterLocalDataSourceRoom
            .countCurrencyRates(params = params) as ResultData.Error

        assertThat(errorResultData.failure, IsInstanceOf(CurrencyRateLocalError::class.java))
    }

    @Test
    fun countCurrencyRates_ExceptionThrownWhenBaseCurrencySymbolParamsIsNull() = runTest {

        val params = CurrencyRateParams() // When currencySymbolBase is null.

        val errorResultData: ResultData.Error = currencyConverterLocalDataSourceRoom
            .countCurrencyRates(params = params) as ResultData.Error

        assertThat(errorResultData.failure, IsInstanceOf(CurrencyRateLocalError::class.java))
    }

    @Test
    fun countCurrencyRates_IllegalArgumentExceptionThrown() = runTest {

        val params = PARAMS_NONE

        expectedException.expect(IllegalArgumentException::class.java)
        expectedException.expectMessage(equalTo(ERROR_MESSAGE_INAPPROPRIATE_ARGUMENT_PASSED))

        currencyConverterLocalDataSourceRoom.countCurrencyRates(params = params)
    }

    @Test
    fun countCurrencyRates_NotImplementedErrorThrown() = runTest {

        val params = PARAMS_BAD

        expectedException.expect(NotImplementedError::class.java)
        expectedException.expectMessage(equalTo(ERROR_MESSAGE_NOT_YET_IMPLEMENTED))

        currencyConverterLocalDataSourceRoom.countCurrencyRates(params = params)
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
            currencyConverterLocalDataSourceRoom
                .getCurrencyRates(params = params) as ResultData.Success

        assertThat(currencyRatesModelResultData.data, IsEqual(currencyRatesModel))
    }

    @Test
    fun getCurrencyRates_CurrencyRateListNotAvailableLocalError() = runTest {

        currencyRatesDao = currencyRatesDao.copy(isEmptyList = IS_CURRENCY_RATES_LIST_EMPTY_TRUE)

        currencyConverterLocalDataSourceRoom = currencyConverterLocalDataSourceRoom.copy(
            currencyRatesDao = currencyRatesDao
        )

        val params = PARAMS_CURRENCY_CONVERTER

        val errorResultData: ResultData.Error = currencyConverterLocalDataSourceRoom
            .getCurrencyRates(params = params) as ResultData.Error

        assertThat(
            errorResultData.failure,
            IsInstanceOf(CurrencyRateListNotAvailableLocalError::class.java)
        )
    }

    @Test
    fun getCurrencyRates_ExceptionThrown() = runTest {

        currencyRatesDao = currencyRatesDao.copy(isException = IS_EXCEPTION_TRUE)

        currencyConverterLocalDataSourceRoom = currencyConverterLocalDataSourceRoom.copy(
            currencyRatesDao = currencyRatesDao
        )

        val params = PARAMS_CURRENCY_CONVERTER

        val errorResultData: ResultData.Error = currencyConverterLocalDataSourceRoom
            .getCurrencyRates(params = params) as ResultData.Error

        assertThat(errorResultData.failure, IsInstanceOf(CurrencyRateLocalError::class.java))
    }

    @Test
    fun getCurrencyRates_IllegalArgumentExceptionThrown() = runTest {

        val params = PARAMS_NONE

        expectedException.expect(IllegalArgumentException::class.java)
        expectedException.expectMessage(equalTo(ERROR_MESSAGE_INAPPROPRIATE_ARGUMENT_PASSED))

        currencyConverterLocalDataSourceRoom.getCurrencyRates(params = params)
    }

    @Test
    fun getCurrencyRates_NotImplementedErrorThrown() = runTest {

        val params = PARAMS_BAD

        expectedException.expect(NotImplementedError::class.java)
        expectedException.expectMessage(equalTo(ERROR_MESSAGE_NOT_YET_IMPLEMENTED))

        currencyConverterLocalDataSourceRoom.getCurrencyRates(params = params)
    }

    @Test
    fun saveCurrencyRates_CurrencyRatesListNotEmpty() = runTest {
        val tableRowIdsExpected: List<Long> = TABLE_ROW_IDS_TWO

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

        val currencyRatesModel: List<CurrencyRateModel> =
            listOf(currencyRateModel1, currencyRateModel2)

        val tableRowIdsResultData: ResultData.Success<List<Long>> =
            currencyConverterLocalDataSourceRoom
                .saveCurrencyRates(currencyRatesModel = currencyRatesModel) as ResultData.Success

        assertThat(tableRowIdsResultData.data, IsEqual(tableRowIdsExpected))
    }

    @Test
    fun saveCurrencyRates_CurrencyRatesListIsEmpty() = runTest {

        currencyRatesDao = currencyRatesDao.copy(isEmptyList = IS_CURRENCY_RATES_LIST_EMPTY_TRUE)

        currencyConverterLocalDataSourceRoom = currencyConverterLocalDataSourceRoom.copy(
            currencyRatesDao = currencyRatesDao
        )

        val errorMessageExpected: Int = R.string.error_cant_save_empty_currency_rate_list

        val currencyRatesModel = emptyList<CurrencyRateModel>()

        val errorResultData: ResultData.Error = currencyConverterLocalDataSourceRoom
            .saveCurrencyRates(currencyRatesModel = currencyRatesModel) as ResultData.Error

        val errorMessageActual: Int =
            (errorResultData.failure as InsertCurrencyRateLocalError).message

        assertThat(errorMessageActual, IsEqual(errorMessageExpected))
        assertThat(errorResultData.failure, IsInstanceOf(InsertCurrencyRateLocalError::class.java))
    }

    @Test
    fun saveCurrencyRates_AllCurrencyRatesNotSavedError() = runTest {

        currencyRatesDao = currencyRatesDao.copy(tableRowIds = TABLE_ROW_IDS_TWO)

        currencyConverterLocalDataSourceRoom = currencyConverterLocalDataSourceRoom.copy(
            currencyRatesDao = currencyRatesDao
        )

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

        val currencyRateModel3 = CurrencyRateModel.newInstance(
            id = ID_ALL,
            rate = RATE_ALL,
            currencySymbolBase = CURRENCY_SYMBOL_BASE_USD,
            currencySymbolOther = CURRENCY_SYMBOL_OTHER_ALL,
        )

        val currencyRatesModel: List<CurrencyRateModel> =
            listOf(currencyRateModel1, currencyRateModel2, currencyRateModel3)

        val errorMessageExpected: Int = R.string.error_all_currency_rates_not_saved

        val errorResultData: ResultData.Error = currencyConverterLocalDataSourceRoom
            .saveCurrencyRates(currencyRatesModel = currencyRatesModel) as ResultData.Error

        val errorMessageActual: Int =
            (errorResultData.failure as InsertCurrencyRateLocalError).message

        assertThat(errorMessageActual, IsEqual(errorMessageExpected))
        assertThat(errorResultData.failure, IsInstanceOf(InsertCurrencyRateLocalError::class.java))
    }

    @Test
    fun deleteCurrencyRates_CurrencyRatesDeletedSuccessfully() = runTest {
        val numOfCurrencyRatesDeletedExpected: Int = NUM_OF_CURRENCY_RATES_150

        val params = PARAMS_CURRENCY_CONVERTER

        val numOfCurrencyRatesDeletedResultData: ResultData.Success<Int> =
            currencyConverterLocalDataSourceRoom
                .deleteCurrencyRates(params = params) as ResultData.Success

        assertThat(
            numOfCurrencyRatesDeletedResultData.data,
            IsEqual(numOfCurrencyRatesDeletedExpected)
        )
    }

    @Test
    fun deleteCurrencyRates_ExceptionThrownWhileCountingCurrencyRates() = runTest {

        currencyRatesDao = currencyRatesDao.copy(isCountException = IS_EXCEPTION_TRUE)

        currencyConverterLocalDataSourceRoom = currencyConverterLocalDataSourceRoom.copy(
            currencyRatesDao = currencyRatesDao
        )

        val params = PARAMS_CURRENCY_CONVERTER

        val errorResultData: ResultData.Error = currencyConverterLocalDataSourceRoom
            .deleteCurrencyRates(params = params) as ResultData.Error

        assertThat(errorResultData.failure, IsInstanceOf(DeleteCurrencyRateLocalError::class.java))
    }

    @Test
    fun deleteCurrencyRates_ErrorDeletingCurrencyRates() = runTest {

        currencyRatesDao =
            currencyRatesDao.copy(noOfCurrencyRatesDeleted = NUM_OF_CURRENCY_RATES_DELETED_1)

        currencyConverterLocalDataSourceRoom = currencyConverterLocalDataSourceRoom.copy(
            currencyRatesDao = currencyRatesDao
        )

        val params = PARAMS_CURRENCY_CONVERTER

        val errorMessageExpected: Int = R.string.error_deleting_currency_rates

        val errorResultData: ResultData.Error = currencyConverterLocalDataSourceRoom
            .deleteCurrencyRates(params = params) as ResultData.Error

        val errorMessageActual: Int =
            (errorResultData.failure as DeleteCurrencyRateLocalError).message

        assertThat(errorMessageActual, IsEqual(errorMessageExpected))
        assertThat(errorResultData.failure, IsInstanceOf(DeleteCurrencyRateLocalError::class.java))
    }

    @Test
    fun deleteCurrencyRates_ExceptionThrown() = runTest {

        currencyRatesDao = currencyRatesDao.copy(isException = IS_EXCEPTION_TRUE)

        currencyConverterLocalDataSourceRoom = currencyConverterLocalDataSourceRoom.copy(
            currencyRatesDao = currencyRatesDao
        )

        val params = PARAMS_CURRENCY_CONVERTER

        val errorResultData: ResultData.Error = currencyConverterLocalDataSourceRoom
            .deleteCurrencyRates(params = params) as ResultData.Error

        assertThat(errorResultData.failure, IsInstanceOf(DeleteCurrencyRateLocalError::class.java))
    }

    @Test
    fun deleteCurrencyRates_IllegalArgumentExceptionThrown() = runTest {

        val params = PARAMS_NONE

        expectedException.expect(IllegalArgumentException::class.java)
        expectedException.expectMessage(equalTo(ERROR_MESSAGE_INAPPROPRIATE_ARGUMENT_PASSED))

        currencyConverterLocalDataSourceRoom.deleteCurrencyRates(params = params)
    }

    @Test
    fun deleteCurrencyRates_NotImplementedErrorThrown() = runTest {

        val params = PARAMS_BAD

        expectedException.expect(NotImplementedError::class.java)
        expectedException.expectMessage(equalTo(ERROR_MESSAGE_NOT_YET_IMPLEMENTED))

        currencyConverterLocalDataSourceRoom.deleteCurrencyRates(params = params)
    }

}

private class BadParams : Params