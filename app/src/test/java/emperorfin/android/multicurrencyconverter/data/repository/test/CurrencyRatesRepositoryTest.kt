package emperorfin.android.multicurrencyconverter.data.repository.test

import emperorfin.android.multicurrencyconverter.R
import emperorfin.android.multicurrencyconverter.data.constant.StringConstants.ERROR_MESSAGE_INAPPROPRIATE_ARGUMENT_PASSED
import emperorfin.android.multicurrencyconverter.data.constant.StringConstants.ERROR_MESSAGE_NOT_YET_IMPLEMENTED
import emperorfin.android.multicurrencyconverter.data.datasource.local.framework.room.entitysource.fake.FakeCurrencyRatesLocalDataSourceRoom
import emperorfin.android.multicurrencyconverter.data.datasource.remote.framework.retrofit.dtosource.fake.FakeCurrencyRatesRemoteDataSourceRetrofit
import emperorfin.android.multicurrencyconverter.data.datasource.util.CurrencyRateDataGeneratorUtil.CURRENCY_SYMBOL_BASE_USD
import emperorfin.android.multicurrencyconverter.data.datasource.util.CurrencyRateDataGeneratorUtil.CURRENCY_SYMBOL_OTHER_AED
import emperorfin.android.multicurrencyconverter.data.datasource.util.CurrencyRateDataGeneratorUtil.CURRENCY_SYMBOL_OTHER_AFN
import emperorfin.android.multicurrencyconverter.data.datasource.util.CurrencyRateDataGeneratorUtil.ID_AED
import emperorfin.android.multicurrencyconverter.data.datasource.util.CurrencyRateDataGeneratorUtil.ID_AFN
import emperorfin.android.multicurrencyconverter.data.datasource.util.CurrencyRateDataGeneratorUtil.RATE_AED
import emperorfin.android.multicurrencyconverter.data.datasource.util.CurrencyRateDataGeneratorUtil.RATE_AFN
import emperorfin.android.multicurrencyconverter.data.repository.CurrencyRatesRepository
import emperorfin.android.multicurrencyconverter.domain.exception.CurrencyRateFailure.CurrencyRateLocalError
import emperorfin.android.multicurrencyconverter.domain.exception.CurrencyRateFailure.DeleteCurrencyRateLocalError
import emperorfin.android.multicurrencyconverter.domain.exception.CurrencyRateFailure.GetCurrencyRateRemoteError
import emperorfin.android.multicurrencyconverter.domain.exception.CurrencyRateFailure.GetCurrencyRateRepositoryError
import emperorfin.android.multicurrencyconverter.domain.exception.CurrencyRateFailure.InsertCurrencyRateLocalError
import emperorfin.android.multicurrencyconverter.domain.exception.CurrencyRateFailure.NonExistentCurrencyRateDataLocalError
import emperorfin.android.multicurrencyconverter.domain.model.currencyrate.CurrencyRateModel
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


class CurrencyRatesRepositoryTest {

    private companion object {

        const val NUM_OF_CURRENCY_RATES_MINUS_1: Int = -1
        const val NUM_OF_CURRENCY_RATES_0: Int = 0
        const val NUM_OF_CURRENCY_RATES_150: Int = 150

        const val IS_FORCE_UPDATE_FALSE: Boolean = false
        const val IS_FORCE_UPDATE_TRUE: Boolean = true
        const val IS_COUNT_REMOTELY_FALSE: Boolean = false
        const val IS_COUNT_REMOTELY_TRUE: Boolean = true
        const val IS_GET_CURRENCY_RATES_RESPONSE_UNSUCCESSFUL_TRUE: Boolean = true
        const val IS_SAVE_REMOTELY_FALSE: Boolean = false
        const val IS_SAVE_REMOTELY_TRUE: Boolean = true
        const val IS_DELETE_REMOTELY_FALSE: Boolean = false
        const val IS_DELETE_REMOTELY_TRUE: Boolean = true
        const val IS_EXCEPTION_TRUE: Boolean = true
        const val IS_SAVE_CURRENCY_RATES_ERROR_TRUE: Boolean = true
        const val IS_DELETE_CURRENCY_RATES_ERROR_WHILE_DELETING_ALL_RATE_TRUE: Boolean = true
        const val IS_DELETE_CURRENCY_RATES_ERROR_DURING_RATES_COUNT_TRUE: Boolean = true

        const val ERROR_MESSAGE_TODO_NOT_YET_IMPLEMENTED: String =
            "An operation is not implemented: Not yet implemented"

        val PARAMS_NONE: None = None()
        val PARAMS_CURRENCY_CONVERTER: CurrencyRateParams =
            CurrencyRateParams(currencySymbolBase = "USD")
        val PARAMS_BAD: BadParams = BadParams()

    }

    private lateinit var currencyRatesLocalDataSourceRoom: FakeCurrencyRatesLocalDataSourceRoom
    private lateinit var currencyRatesRemoteDataSourceRetrofit: FakeCurrencyRatesRemoteDataSourceRetrofit

    // Class under test
    private lateinit var currencyRatesRepository: CurrencyRatesRepository

    @JvmField
    @Rule
    val expectedException: ExpectedException = ExpectedException.none()

    @Before
    fun createRepository() {

        currencyRatesLocalDataSourceRoom = FakeCurrencyRatesLocalDataSourceRoom()
        currencyRatesRemoteDataSourceRetrofit = FakeCurrencyRatesRemoteDataSourceRetrofit()

        currencyRatesRepository = CurrencyRatesRepository(
            currencyRatesLocalDataSource = currencyRatesLocalDataSourceRoom,
            currencyRatesRemoteDataSource = currencyRatesRemoteDataSourceRetrofit,
            ioDispatcher = Dispatchers.Unconfined
        )
    }

    @Test
    fun countAllCurrencyRates_LocalDataSourceCurrencyRatesMoreThanZero() = runTest {

        currencyRatesLocalDataSourceRoom = currencyRatesLocalDataSourceRoom.copy(
            countAllCurrencyRatesResultData = ResultData.Success(NUM_OF_CURRENCY_RATES_150)
        )

        currencyRatesRepository = currencyRatesRepository.copy(
            currencyRatesLocalDataSource = currencyRatesLocalDataSourceRoom
        )

        val noOfCurrencyRatesExpected: Int = NUM_OF_CURRENCY_RATES_150

        val params = PARAMS_NONE

        val numOfAllCurrencyRatesResultData: ResultData.Success<Int> = currencyRatesRepository
            .countAllCurrencyRates(
                params = params,
                countRemotely = IS_COUNT_REMOTELY_FALSE
            ) as ResultData.Success

        assertThat(numOfAllCurrencyRatesResultData.data, IsEqual(noOfCurrencyRatesExpected))
    }

    @Test
    fun countAllCurrencyRates_LocalDataSourceNonExistentCurrencyRateDataError() = runTest {

        currencyRatesLocalDataSourceRoom = currencyRatesLocalDataSourceRoom.copy(
            countAllCurrencyRatesResultData = ResultData.Success(NUM_OF_CURRENCY_RATES_0)
        )

        currencyRatesRepository = currencyRatesRepository.copy(
            currencyRatesLocalDataSource = currencyRatesLocalDataSourceRoom
        )

        val params = PARAMS_NONE

        val errorResultData: ResultData.Error = currencyRatesRepository
            .countAllCurrencyRates(
                params = params,
                countRemotely = IS_COUNT_REMOTELY_FALSE
            ) as ResultData.Error

        assertThat(
            errorResultData.failure,
            IsInstanceOf(NonExistentCurrencyRateDataLocalError::class.java)
        )
    }

    @Test
    fun countAllCurrencyRates_LocalDataSourceGeneralError() = runTest {

        currencyRatesLocalDataSourceRoom = currencyRatesLocalDataSourceRoom.copy(
            countAllCurrencyRatesResultData = ResultData.Success(NUM_OF_CURRENCY_RATES_MINUS_1)
        )

        currencyRatesRepository = currencyRatesRepository.copy(
            currencyRatesLocalDataSource = currencyRatesLocalDataSourceRoom
        )

        val params = PARAMS_NONE

        val errorResultData: ResultData.Error = currencyRatesRepository
            .countAllCurrencyRates(
                params = params,
                countRemotely = IS_COUNT_REMOTELY_FALSE
            ) as ResultData.Error

        assertThat(errorResultData.failure, IsInstanceOf(CurrencyRateLocalError::class.java))
    }

    @Test
    fun countAllCurrencyRates_LocalDataSourceExceptionThrown() = runTest {

        currencyRatesLocalDataSourceRoom = currencyRatesLocalDataSourceRoom.copy(
            isCountAllCurrencyRatesException = IS_EXCEPTION_TRUE
        )

        currencyRatesRepository = currencyRatesRepository.copy(
            currencyRatesLocalDataSource = currencyRatesLocalDataSourceRoom
        )

        val params = PARAMS_NONE

        val errorResultData: ResultData.Error = currencyRatesRepository
            .countAllCurrencyRates(
                params = params,
                countRemotely = IS_COUNT_REMOTELY_FALSE
            ) as ResultData.Error

        assertThat(errorResultData.failure, IsInstanceOf(CurrencyRateLocalError::class.java))
    }

    @Test
    fun countAllCurrencyRates_LocalDataSourceIllegalArgumentExceptionThrown() = runTest {

        val params = CurrencyRateParams()

        expectedException.expect(IllegalArgumentException::class.java)
        expectedException.expectMessage(equalTo(ERROR_MESSAGE_INAPPROPRIATE_ARGUMENT_PASSED))

        currencyRatesRepository.countAllCurrencyRates(
            params = params,
            countRemotely = IS_COUNT_REMOTELY_FALSE
        )
    }

    @Test
    fun countAllCurrencyRates_LocalDataSourceNotImplementedErrorThrown() = runTest {

        val params = PARAMS_BAD

        expectedException.expect(NotImplementedError::class.java)
        expectedException.expectMessage(equalTo(ERROR_MESSAGE_NOT_YET_IMPLEMENTED))

        currencyRatesRepository.countAllCurrencyRates(
            params = params,
            countRemotely = IS_COUNT_REMOTELY_FALSE
        )
    }

    @Test
    fun countCurrencyRates_LocalDataSourceCurrencyRatesMoreThanZero() = runTest {

        currencyRatesLocalDataSourceRoom = currencyRatesLocalDataSourceRoom.copy(
            countCurrencyRatesResultData = ResultData.Success(NUM_OF_CURRENCY_RATES_150)
        )

        currencyRatesRepository = currencyRatesRepository.copy(
            currencyRatesLocalDataSource = currencyRatesLocalDataSourceRoom
        )

        val noOfCurrencyRatesExpected: Int = NUM_OF_CURRENCY_RATES_150

        val params = PARAMS_CURRENCY_CONVERTER

        val numOfAllCurrencyRatesResultData: ResultData.Success<Int> = currencyRatesRepository
            .countCurrencyRates(
                params = params,
                countRemotely = IS_COUNT_REMOTELY_FALSE
            ) as ResultData.Success

        assertThat(numOfAllCurrencyRatesResultData.data, IsEqual(noOfCurrencyRatesExpected))
    }

    @Test
    fun countCurrencyRates_LocalDataSourceNonExistentCurrencyRateDataError() = runTest {

        currencyRatesLocalDataSourceRoom = currencyRatesLocalDataSourceRoom.copy(
            countCurrencyRatesResultData = ResultData.Success(NUM_OF_CURRENCY_RATES_0)
        )

        currencyRatesRepository = currencyRatesRepository.copy(
            currencyRatesLocalDataSource = currencyRatesLocalDataSourceRoom
        )

        val params = PARAMS_CURRENCY_CONVERTER

        val errorResultData: ResultData.Error = currencyRatesRepository
            .countCurrencyRates(
                params = params,
                countRemotely = IS_COUNT_REMOTELY_FALSE
            ) as ResultData.Error

        assertThat(
            errorResultData.failure,
            IsInstanceOf(NonExistentCurrencyRateDataLocalError::class.java)
        )
    }

    @Test
    fun countCurrencyRates_LocalDataSourceGeneralError() = runTest {

        currencyRatesLocalDataSourceRoom = currencyRatesLocalDataSourceRoom.copy(
            countCurrencyRatesResultData = ResultData.Success(NUM_OF_CURRENCY_RATES_MINUS_1)
        )

        currencyRatesRepository = currencyRatesRepository.copy(
            currencyRatesLocalDataSource = currencyRatesLocalDataSourceRoom
        )

        val params = PARAMS_CURRENCY_CONVERTER

        val errorResultData: ResultData.Error = currencyRatesRepository
            .countCurrencyRates(
                params = params,
                countRemotely = IS_COUNT_REMOTELY_FALSE
            ) as ResultData.Error

        assertThat(errorResultData.failure, IsInstanceOf(CurrencyRateLocalError::class.java))
    }

    @Test
    fun countCurrencyRates_LocalDataSourceExceptionThrown() = runTest {

        currencyRatesLocalDataSourceRoom = currencyRatesLocalDataSourceRoom.copy(
            isCountCurrencyRatesException = IS_EXCEPTION_TRUE
        )

        currencyRatesRepository = currencyRatesRepository.copy(
            currencyRatesLocalDataSource = currencyRatesLocalDataSourceRoom
        )

        val params = PARAMS_CURRENCY_CONVERTER

        val errorResultData: ResultData.Error = currencyRatesRepository
            .countCurrencyRates(
                params = params,
                countRemotely = IS_COUNT_REMOTELY_FALSE
            ) as ResultData.Error

        assertThat(errorResultData.failure, IsInstanceOf(CurrencyRateLocalError::class.java))
    }

    @Test
    fun countCurrencyRates_LocalDataSourceIllegalArgumentExceptionThrown() = runTest {

        val params = PARAMS_NONE

        expectedException.expect(IllegalArgumentException::class.java)
        expectedException.expectMessage(equalTo(ERROR_MESSAGE_INAPPROPRIATE_ARGUMENT_PASSED))

        currencyRatesRepository.countCurrencyRates(
            params = params,
            countRemotely = IS_COUNT_REMOTELY_FALSE
        )
    }

    @Test
    fun countCurrencyRates_LocalDataSourceNotImplementedErrorThrown() = runTest {

        val params = PARAMS_BAD

        expectedException.expect(NotImplementedError::class.java)
        expectedException.expectMessage(equalTo(ERROR_MESSAGE_NOT_YET_IMPLEMENTED))

        currencyRatesRepository.countCurrencyRates(
            params = params,
            countRemotely = IS_COUNT_REMOTELY_FALSE
        )
    }

    @Test
    fun getCurrencyRates_LocalDataSourceCurrencyRatesListNotEmpty() = runTest {

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
            currencyRatesRepository
                .getCurrencyRates(
                    params = params,
                    forceUpdate = IS_FORCE_UPDATE_FALSE
                ) as ResultData.Success

        assertThat(currencyRatesModelResultData.data, IsEqual(currencyRatesModel))
    }

    @Test
    fun getCurrencyRates_LocalDataSourceCurrencyRateListNotAvailableLocalError() = runTest {

        currencyRatesLocalDataSourceRoom = currencyRatesLocalDataSourceRoom.copy(
            getCurrencyRatesResultData = ResultData.Success(emptyList())
        )

        currencyRatesRepository = currencyRatesRepository.copy(
            currencyRatesLocalDataSource = currencyRatesLocalDataSourceRoom
        )

        val errorMessageExpected: Int = R.string.error_fetching_from_remote_and_local

        val params = PARAMS_CURRENCY_CONVERTER

        val errorResultData: ResultData.Error = currencyRatesRepository
            .getCurrencyRates(
                params = params,
                forceUpdate = IS_FORCE_UPDATE_FALSE
            ) as ResultData.Error

        val errorMessageActual: Int =
            (errorResultData.failure as GetCurrencyRateRepositoryError).message

        assertThat(errorMessageActual, IsEqual(errorMessageExpected))
        assertThat(
            errorResultData.failure,
            IsInstanceOf(GetCurrencyRateRepositoryError::class.java)
        )
    }

    @Test
    fun getCurrencyRates_LocalDataSourceExceptionThrown() = runTest {

        currencyRatesLocalDataSourceRoom = currencyRatesLocalDataSourceRoom.copy(
            isGetCurrencyRatesException = IS_EXCEPTION_TRUE
        )

        currencyRatesRepository = currencyRatesRepository.copy(
            currencyRatesLocalDataSource = currencyRatesLocalDataSourceRoom
        )

        val errorMessageExpected: Int = R.string.exception_occurred_local

        val params = PARAMS_CURRENCY_CONVERTER

        val errorResultData: ResultData.Error = currencyRatesRepository
            .getCurrencyRates(
                params = params,
                forceUpdate = IS_FORCE_UPDATE_FALSE
            ) as ResultData.Error

        val errorMessageActual: Int =
            (errorResultData.failure as GetCurrencyRateRepositoryError).message

        assertThat(errorMessageActual, IsEqual(errorMessageExpected))
        assertThat(
            errorResultData.failure,
            IsInstanceOf(GetCurrencyRateRepositoryError::class.java)
        )
    }

    @Test
    fun getCurrencyRates_LocalDataSourceIllegalArgumentExceptionThrown() = runTest {

        val params = PARAMS_NONE

        expectedException.expect(IllegalArgumentException::class.java)
        expectedException.expectMessage(equalTo(ERROR_MESSAGE_INAPPROPRIATE_ARGUMENT_PASSED))

        currencyRatesRepository.getCurrencyRates(
            params = params,
            forceUpdate = IS_FORCE_UPDATE_FALSE
        )
    }

    @Test
    fun getCurrencyRates_LocalDataSourceNotImplementedErrorThrown() = runTest {

        val params = PARAMS_BAD

        expectedException.expect(NotImplementedError::class.java)
        expectedException.expectMessage(equalTo(ERROR_MESSAGE_NOT_YET_IMPLEMENTED))

        currencyRatesRepository.getCurrencyRates(
            params = params,
            forceUpdate = IS_FORCE_UPDATE_FALSE
        )
    }

    @Test
    fun saveCurrencyRates_LocalDataSourceCurrencyRatesListNotEmpty() = runTest {
        val tableRowIdsExpected: List<Long> = listOf(1L, 2L)

        currencyRatesLocalDataSourceRoom = currencyRatesLocalDataSourceRoom.copy(
            saveCurrencyRatesResultData = ResultData.Success(tableRowIdsExpected)
        )

        currencyRatesRepository = currencyRatesRepository.copy(
            currencyRatesLocalDataSource = currencyRatesLocalDataSourceRoom
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

        val currencyRatesModel: List<CurrencyRateModel> =
            listOf(currencyRateModel1, currencyRateModel2)

        val tableRowIdsResultData: ResultData.Success<List<Long>> = currencyRatesRepository
            .saveCurrencyRates(
                currencyRatesModel = currencyRatesModel,
                saveRemotely = IS_SAVE_REMOTELY_FALSE
            ) as ResultData.Success

        assertThat(tableRowIdsResultData.data, IsEqual(tableRowIdsExpected))
    }

    @Test
    fun saveCurrencyRates_LocalDataSourceCurrencyRatesListIsEmpty() = runTest {

        val errorMessageExpected: Int = R.string.error_cant_save_empty_currency_rate_list

        val currencyRatesModel = emptyList<CurrencyRateModel>()

        val errorResultData: ResultData.Error = currencyRatesRepository
            .saveCurrencyRates(
                currencyRatesModel = currencyRatesModel,
                saveRemotely = IS_SAVE_REMOTELY_FALSE
            ) as ResultData.Error

        val errorMessageActual: Int =
            (errorResultData.failure as InsertCurrencyRateLocalError).message

        assertThat(errorMessageActual, IsEqual(errorMessageExpected))
        assertThat(errorResultData.failure, IsInstanceOf(InsertCurrencyRateLocalError::class.java))
    }

    @Test
    fun saveCurrencyRates_LocalDataSourceAllCurrencyRatesNotSavedError() = runTest {

        currencyRatesLocalDataSourceRoom = currencyRatesLocalDataSourceRoom.copy(
            isSaveCurrencyRatesError = IS_SAVE_CURRENCY_RATES_ERROR_TRUE
        )

        currencyRatesRepository = currencyRatesRepository.copy(
            currencyRatesLocalDataSource = currencyRatesLocalDataSourceRoom
        )

        val errorMessageExpected: Int = R.string.error_all_currency_rates_not_saved

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

        val errorResultData: ResultData.Error = currencyRatesRepository
            .saveCurrencyRates(
                currencyRatesModel = currencyRatesModel,
                saveRemotely = IS_SAVE_REMOTELY_FALSE
            ) as ResultData.Error

        val errorMessageActual: Int =
            (errorResultData.failure as InsertCurrencyRateLocalError).message

        assertThat(errorMessageActual, IsEqual(errorMessageExpected))
        assertThat(errorResultData.failure, IsInstanceOf(InsertCurrencyRateLocalError::class.java))
    }

    @Test
    fun deleteCurrencyRates_LocalDataSourceCurrencyRatesDeletedSuccessfully() = runTest {
        val numOfCurrencyRatesDeletedExpected: Int = NUM_OF_CURRENCY_RATES_150

        currencyRatesLocalDataSourceRoom = currencyRatesLocalDataSourceRoom.copy(
            deleteCurrencyRatesResultData = ResultData.Success(numOfCurrencyRatesDeletedExpected)
        )

        currencyRatesRepository = currencyRatesRepository.copy(
            currencyRatesLocalDataSource = currencyRatesLocalDataSourceRoom
        )

        val params = PARAMS_CURRENCY_CONVERTER

        val numOfCurrencyRatesDeletedResultData: ResultData.Success<Int> = currencyRatesRepository
            .deleteCurrencyRates(
                params = params,
                deleteRemotely = IS_DELETE_REMOTELY_FALSE
            ) as ResultData.Success

        assertThat(
            numOfCurrencyRatesDeletedResultData.data,
            IsEqual(numOfCurrencyRatesDeletedExpected)
        )
    }

    @Test
    fun deleteCurrencyRates_LocalDataSourceErrorDeletingCurrencyRates() = runTest {

        currencyRatesLocalDataSourceRoom = currencyRatesLocalDataSourceRoom.copy(
            isDeleteCurrencyRatesErrorWhileDeletingAllRates = IS_DELETE_CURRENCY_RATES_ERROR_WHILE_DELETING_ALL_RATE_TRUE
        )

        currencyRatesRepository = currencyRatesRepository.copy(
            currencyRatesLocalDataSource = currencyRatesLocalDataSourceRoom
        )

        val params = PARAMS_CURRENCY_CONVERTER

        val errorMessageExpected: Int = R.string.error_deleting_currency_rates

        val errorResultData: ResultData.Error = currencyRatesRepository
            .deleteCurrencyRates(
                params = params,
                deleteRemotely = IS_DELETE_REMOTELY_FALSE
            ) as ResultData.Error

        val errorMessageActual: Int =
            (errorResultData.failure as DeleteCurrencyRateLocalError).message

        assertThat(errorMessageActual, IsEqual(errorMessageExpected))
        assertThat(errorResultData.failure, IsInstanceOf(DeleteCurrencyRateLocalError::class.java))
    }

    @Test
    fun deleteCurrencyRates_LocalDataSourceExceptionThrownWhileCountingCurrencyRates() = runTest {

        currencyRatesLocalDataSourceRoom = currencyRatesLocalDataSourceRoom.copy(
            isDeleteCurrencyRatesErrorDuringRatesCount = IS_DELETE_CURRENCY_RATES_ERROR_DURING_RATES_COUNT_TRUE
        )

        currencyRatesRepository = currencyRatesRepository.copy(
            currencyRatesLocalDataSource = currencyRatesLocalDataSourceRoom
        )

        val params = PARAMS_CURRENCY_CONVERTER

        val errorResultData: ResultData.Error = currencyRatesRepository
            .deleteCurrencyRates(
                params = params,
                deleteRemotely = IS_DELETE_REMOTELY_FALSE
            ) as ResultData.Error

        assertThat(errorResultData.failure, IsInstanceOf(DeleteCurrencyRateLocalError::class.java))
    }

    @Test
    fun deleteCurrencyRates_LocalDataSourceExceptionThrown() = runTest {

        currencyRatesLocalDataSourceRoom = currencyRatesLocalDataSourceRoom.copy(
            isDeleteCurrencyRatesException = IS_EXCEPTION_TRUE
        )

        currencyRatesRepository = currencyRatesRepository.copy(
            currencyRatesLocalDataSource = currencyRatesLocalDataSourceRoom
        )

        val params = PARAMS_CURRENCY_CONVERTER

        val errorResultData: ResultData.Error = currencyRatesRepository
            .deleteCurrencyRates(
                params = params,
                deleteRemotely = IS_DELETE_REMOTELY_FALSE
            ) as ResultData.Error

        assertThat(errorResultData.failure, IsInstanceOf(DeleteCurrencyRateLocalError::class.java))
    }

    @Test
    fun deleteCurrencyRates_LocalDataSourceIllegalArgumentExceptionThrown() = runTest {

        val params = PARAMS_NONE

        expectedException.expect(IllegalArgumentException::class.java)
        expectedException.expectMessage(equalTo(ERROR_MESSAGE_INAPPROPRIATE_ARGUMENT_PASSED))

        currencyRatesRepository.deleteCurrencyRates(
            params = params,
            deleteRemotely = IS_DELETE_REMOTELY_FALSE
        )
    }

    @Test
    fun deleteCurrencyRates_LocalDataSourceNotImplementedErrorThrown() = runTest {

        val params = PARAMS_BAD

        expectedException.expect(NotImplementedError::class.java)
        expectedException.expectMessage(equalTo(ERROR_MESSAGE_NOT_YET_IMPLEMENTED))

        currencyRatesRepository.deleteCurrencyRates(
            params = params,
            deleteRemotely = IS_DELETE_REMOTELY_FALSE
        )
    }

    @Test
    fun getCurrencyRates_RemoteDataSourceCurrencyRatesListNotEmpty() = runTest {

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
            currencyRatesRepository.getCurrencyRates(
                params = params,
                forceUpdate = IS_FORCE_UPDATE_TRUE
            ) as ResultData.Success

        assertThat(currencyRatesModelResultData.data, IsEqual(currencyRatesModel))
    }

    @Test
    fun getCurrencyRates_RemoteDataSourceGetCurrencyRatesRemoteError() = runTest {

        currencyRatesRemoteDataSourceRetrofit = currencyRatesRemoteDataSourceRetrofit.copy(
            isGetCurrencyRatesResponseUnsuccessful = IS_GET_CURRENCY_RATES_RESPONSE_UNSUCCESSFUL_TRUE
        )

        currencyRatesRepository = currencyRatesRepository.copy(
            currencyRatesRemoteDataSource = currencyRatesRemoteDataSourceRetrofit
        )

        val params = PARAMS_CURRENCY_CONVERTER

        val errorResultData: ResultData.Error = currencyRatesRepository
            .getCurrencyRates(
                params = params,
                forceUpdate = IS_FORCE_UPDATE_TRUE
            ) as ResultData.Error

        assertThat(errorResultData.failure, IsInstanceOf(GetCurrencyRateRemoteError::class.java))
    }

    @Test
    fun getCurrencyRates_RemoteDataSourceExceptionThrown() = runTest {

        currencyRatesRemoteDataSourceRetrofit = currencyRatesRemoteDataSourceRetrofit.copy(
            isGetCurrencyRatesException = IS_EXCEPTION_TRUE
        )

        currencyRatesRepository = currencyRatesRepository.copy(
            currencyRatesRemoteDataSource = currencyRatesRemoteDataSourceRetrofit
        )

        val errorMessageExpected: Int = R.string.exception_occurred_remote

        val params = PARAMS_CURRENCY_CONVERTER

        val errorResultData: ResultData.Error = currencyRatesRepository
            .getCurrencyRates(
                params = params,
                forceUpdate = IS_FORCE_UPDATE_TRUE
            ) as ResultData.Error

        val errorMessageActual: Int =
            (errorResultData.failure as GetCurrencyRateRepositoryError).message

        assertThat(errorMessageActual, IsEqual(errorMessageExpected))
        assertThat(
            errorResultData.failure,
            IsInstanceOf(GetCurrencyRateRepositoryError::class.java)
        )
    }

    @Test
    fun getCurrencyRates_RemoteDataSourceIllegalArgumentExceptionThrown() = runTest {

        val params = PARAMS_NONE

        expectedException.expect(IllegalArgumentException::class.java)
        expectedException.expectMessage(equalTo(ERROR_MESSAGE_INAPPROPRIATE_ARGUMENT_PASSED))

        currencyRatesRepository.getCurrencyRates(
            params = params,
            forceUpdate = IS_FORCE_UPDATE_TRUE
        )
    }

    @Test
    fun getCurrencyRates_RemoteDataSourceNotImplementedErrorThrown() = runTest {

        val params = PARAMS_BAD

        expectedException.expect(NotImplementedError::class.java)
        expectedException.expectMessage(equalTo(ERROR_MESSAGE_NOT_YET_IMPLEMENTED))

        currencyRatesRepository.getCurrencyRates(
            params = params,
            forceUpdate = IS_FORCE_UPDATE_TRUE
        )
    }

    @Test
    fun countAllCurrencyRates_RemoteDataSourceNotYetImplementedErrorThrown() = runTest {

        val params = PARAMS_BAD

        expectedException.expect(NotImplementedError::class.java)
        expectedException.expectMessage(equalTo(ERROR_MESSAGE_TODO_NOT_YET_IMPLEMENTED))

        currencyRatesRepository.countAllCurrencyRates(
            params = params,
            countRemotely = IS_COUNT_REMOTELY_TRUE
        )
    }

    @Test
    fun countCurrencyRates_RemoteDataSourceNotYetImplementedErrorThrown() = runTest {

        val params = PARAMS_BAD

        expectedException.expect(NotImplementedError::class.java)
        expectedException.expectMessage(equalTo(ERROR_MESSAGE_TODO_NOT_YET_IMPLEMENTED))

        currencyRatesRepository.countCurrencyRates(
            params = params,
            countRemotely = IS_COUNT_REMOTELY_TRUE
        )
    }

    @Test
    fun saveCurrencyRates_RemoteDataSourceNotYetImplementedErrorThrown() = runTest {

        expectedException.expect(NotImplementedError::class.java)
        expectedException.expectMessage(equalTo(ERROR_MESSAGE_TODO_NOT_YET_IMPLEMENTED))

        currencyRatesRepository.saveCurrencyRates(
            currencyRatesModel = emptyList(),
            saveRemotely = IS_SAVE_REMOTELY_TRUE
        )
    }

    @Test
    fun deleteCurrencyRates_RemoteDataSourceNotYetImplementedErrorThrown() = runTest {

        val params = PARAMS_BAD

        expectedException.expect(NotImplementedError::class.java)
        expectedException.expectMessage(equalTo(ERROR_MESSAGE_TODO_NOT_YET_IMPLEMENTED))

        currencyRatesRepository.deleteCurrencyRates(
            params = params,
            deleteRemotely = IS_DELETE_REMOTELY_TRUE
        )
    }

}

private class BadParams : Params