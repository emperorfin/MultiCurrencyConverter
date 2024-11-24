package emperorfin.android.multicurrencyconverter.data.datasource.local.framework.room.entitysource.fake

import emperorfin.android.multicurrencyconverter.R
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
import emperorfin.android.multicurrencyconverter.domain.exception.CurrencyRateFailure.CurrencyRateListNotAvailableLocalError
import emperorfin.android.multicurrencyconverter.domain.exception.CurrencyRateFailure.CurrencyRateLocalError
import emperorfin.android.multicurrencyconverter.domain.exception.CurrencyRateFailure.DeleteCurrencyRateLocalError
import emperorfin.android.multicurrencyconverter.domain.exception.CurrencyRateFailure.InsertCurrencyRateLocalError
import emperorfin.android.multicurrencyconverter.domain.exception.CurrencyRateFailure.NonExistentCurrencyRateDataLocalError
import emperorfin.android.multicurrencyconverter.domain.model.currencyrate.CurrencyRateModel
import emperorfin.android.multicurrencyconverter.domain.uilayer.event.input.currencyrate.CurrencyRateParams
import emperorfin.android.multicurrencyconverter.domain.uilayer.event.input.currencyrate.None
import emperorfin.android.multicurrencyconverter.domain.uilayer.event.output.ResultData
import emperorfin.android.multicurrencyconverter.domain.uilayer.event.output.currencyrate.Params
import emperorfin.android.multicurrencyconverter.domain.uilayer.event.output.succeeded


internal data class FakeCurrencyRatesLocalDataSourceRoom(
    private val isCountAllCurrencyRatesException: Boolean = false,
    private val countAllCurrencyRatesResultData: ResultData.Success<Int> =
        ResultData.Success(NUM_OF_CURRENCY_RATES_0),
    private val isCountCurrencyRatesException: Boolean = false,
    private val countCurrencyRatesResultData: ResultData.Success<Int> =
        ResultData.Success(NUM_OF_CURRENCY_RATES_0),
    private val isGetCurrencyRatesException: Boolean = false,
    private val getCurrencyRatesResultData: ResultData.Success<List<CurrencyRateModel>> =
        ResultData.Success(CURRENCY_RATES_MODEL),
    private val isSaveCurrencyRatesError: Boolean = false,
    private val saveCurrencyRatesResultData: ResultData<List<Long>> =
        ResultData.Success(listOf(1L, 2L)),
    private val isDeleteCurrencyRatesException: Boolean = false,
    private val isDeleteCurrencyRatesErrorDuringRatesCount: Boolean = false,
    private val isDeleteCurrencyRatesErrorWhileDeletingAllRates: Boolean = false,
    private val deleteCurrencyRatesResultData: ResultData.Success<Int> =
        ResultData.Success(NUM_OF_CURRENCY_RATES_DELETED_1),
) : FakeCurrencyRatesDataSource {

    private companion object {

        const val NUM_OF_CURRENCY_RATES_0: Int = 0
        const val NUM_OF_CURRENCY_RATES_DELETED_1: Int = 1

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

        when (params) {
            is None -> {

                if (isCountAllCurrencyRatesException)
                    return ResultData.Error(failure = CurrencyRateLocalError())

                if (countAllCurrencyRatesResultData.data == NUM_OF_CURRENCY_RATES_0)
                    return ResultData.Error(failure = NonExistentCurrencyRateDataLocalError())

                if (countAllCurrencyRatesResultData.data < NUM_OF_CURRENCY_RATES_0)
                    return ResultData.Error(failure = CurrencyRateLocalError())

                return countAllCurrencyRatesResultData

            }

            is CurrencyRateParams -> {
                throw IllegalArgumentException(ERROR_MESSAGE_INAPPROPRIATE_ARGUMENT_PASSED)
            }

            else -> throw NotImplementedError(ERROR_MESSAGE_NOT_YET_IMPLEMENTED)
        }
    }

    override suspend fun countCurrencyRates(params: Params): ResultData<Int> {

        when (params) {
            is None -> {
                throw IllegalArgumentException(ERROR_MESSAGE_INAPPROPRIATE_ARGUMENT_PASSED)
            }

            is CurrencyRateParams -> {

                if (isCountCurrencyRatesException)
                    return ResultData.Error(failure = CurrencyRateLocalError())

                if (countCurrencyRatesResultData.data == NUM_OF_CURRENCY_RATES_0)
                    return ResultData.Error(failure = NonExistentCurrencyRateDataLocalError())

                if (countCurrencyRatesResultData.data < NUM_OF_CURRENCY_RATES_0)
                    return ResultData.Error(failure = CurrencyRateLocalError())

                return countCurrencyRatesResultData
            }

            else -> throw NotImplementedError(ERROR_MESSAGE_NOT_YET_IMPLEMENTED)
        }
    }

    override suspend fun getCurrencyRates(params: Params): ResultData<List<CurrencyRateModel>> {
        when (params) {
            is None -> {
                throw IllegalArgumentException(ERROR_MESSAGE_INAPPROPRIATE_ARGUMENT_PASSED)
            }

            is CurrencyRateParams -> {

                if (isGetCurrencyRatesException)
                    return ResultData.Error(failure = CurrencyRateLocalError())

                if (getCurrencyRatesResultData.data.isEmpty())
                    return ResultData.Error(failure = CurrencyRateListNotAvailableLocalError())

                return getCurrencyRatesResultData
            }

            else -> throw NotImplementedError(ERROR_MESSAGE_NOT_YET_IMPLEMENTED)
        }
    }

    override suspend fun saveCurrencyRates(currencyRatesModel: List<CurrencyRateModel>): ResultData<List<Long>> {

        if (currencyRatesModel.isEmpty())
            return ResultData.Error(
                failure = InsertCurrencyRateLocalError(message = R.string.error_cant_save_empty_currency_rate_list)
            )

        if (isSaveCurrencyRatesError)
            return ResultData.Error(
                failure = InsertCurrencyRateLocalError(message = R.string.error_all_currency_rates_not_saved)
            )

        if (saveCurrencyRatesResultData.succeeded && (saveCurrencyRatesResultData as ResultData.Success).data.isEmpty())
            throw IllegalArgumentException(
                "The property saveCurrencyRatesResultData must be of type ResultData.Success<List<Long>> " +
                        "where saveCurrencyRatesResultData.data must not be empty"
            )

        return saveCurrencyRatesResultData
    }

    override suspend fun deleteCurrencyRates(params: Params): ResultData<Int> {

        when (params) {
            is None -> {
                throw IllegalArgumentException(ERROR_MESSAGE_INAPPROPRIATE_ARGUMENT_PASSED)
            }

            is CurrencyRateParams -> {

                if (isDeleteCurrencyRatesException)
                    return ResultData.Error(failure = DeleteCurrencyRateLocalError())

                if (isDeleteCurrencyRatesErrorDuringRatesCount)
                    return ResultData.Error(failure = DeleteCurrencyRateLocalError())

                if (isDeleteCurrencyRatesErrorWhileDeletingAllRates)
                    return ResultData.Error(failure = DeleteCurrencyRateLocalError(R.string.error_deleting_currency_rates))

                if (deleteCurrencyRatesResultData.data < 1)
                    throw IllegalArgumentException("deleteCurrencyRatesResultData.data must be greater than 0.")

                return deleteCurrencyRatesResultData
            }

            else -> throw NotImplementedError(ERROR_MESSAGE_NOT_YET_IMPLEMENTED)
        }

    }
}
