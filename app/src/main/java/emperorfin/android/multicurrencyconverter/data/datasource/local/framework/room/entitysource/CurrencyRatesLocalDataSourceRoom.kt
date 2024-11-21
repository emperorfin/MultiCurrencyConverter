package emperorfin.android.multicurrencyconverter.data.datasource.local.framework.room.entitysource

import emperorfin.android.multicurrencyconverter.data.datasource.local.framework.room.entity.currencyrate.CurrencyRateEntityMapper
import emperorfin.android.multicurrencyconverter.di.LocalCurrencyRatesDao
import emperorfin.android.multicurrencyconverter.di.IoDispatcher
import emperorfin.android.multicurrencyconverter.domain.datalayer.datasource.CurrencyRatesDataSource
import emperorfin.android.multicurrencyconverter.domain.model.currencyrate.CurrencyRateModelMapper
import emperorfin.android.multicurrencyconverter.domain.uilayer.event.output.ResultData
import emperorfin.android.multicurrencyconverter.domain.uilayer.event.output.ResultData.Success
import emperorfin.android.multicurrencyconverter.domain.uilayer.event.output.ResultData.Error
import emperorfin.android.multicurrencyconverter.domain.uilayer.event.output.currencyrate.Params
import emperorfin.android.multicurrencyconverter.domain.datalayer.dao.ICurrencyRatesDao
import emperorfin.android.multicurrencyconverter.domain.exception.CurrencyRateFailure.CurrencyRateLocalError
import emperorfin.android.multicurrencyconverter.domain.exception.CurrencyRateFailure.NonExistentCurrencyRateDataLocalError
import emperorfin.android.multicurrencyconverter.domain.exception.CurrencyRateFailure.CurrencyRateListNotAvailableLocalError
import emperorfin.android.multicurrencyconverter.domain.exception.CurrencyRateFailure.DeleteCurrencyRateLocalError
import emperorfin.android.multicurrencyconverter.domain.exception.CurrencyRateFailure.InsertCurrencyRateLocalError
import emperorfin.android.multicurrencyconverter.R
import emperorfin.android.multicurrencyconverter.data.constant.StringConstants.ERROR_MESSAGE_INAPPROPRIATE_ARGUMENT_PASSED
import emperorfin.android.multicurrencyconverter.data.constant.StringConstants.ERROR_MESSAGE_NOT_YET_IMPLEMENTED
import emperorfin.android.multicurrencyconverter.data.datasource.local.framework.room.entity.currencyrate.CurrencyRateEntity
import emperorfin.android.multicurrencyconverter.domain.model.currencyrate.CurrencyRateModel
import emperorfin.android.multicurrencyconverter.domain.uilayer.event.input.currencyrate.CurrencyRateParams
import emperorfin.android.multicurrencyconverter.domain.uilayer.event.input.currencyrate.None
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

data class CurrencyRatesLocalDataSourceRoom @Inject internal constructor(
    @LocalCurrencyRatesDao private val currencyRatesDao: ICurrencyRatesDao,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    private val currencyRateEntityMapper: CurrencyRateEntityMapper,
    private val currencyRateModelMapper: CurrencyRateModelMapper
) : CurrencyRatesDataSource {

    private companion object {
        const val NUM_OF_CURRENCY_RATES_0: Int = 0
    }

    override suspend fun countAllCurrencyRates(params: Params): ResultData<Int> = withContext(ioDispatcher) {
        when(params){
            is None -> {
                return@withContext try {

                    val numOfAllCurrencyRates: Int = currencyRatesDao.countAllCurrencyRates()

                    if (numOfAllCurrencyRates > NUM_OF_CURRENCY_RATES_0) {
                        return@withContext Success(data = numOfAllCurrencyRates)
                    } else if (numOfAllCurrencyRates == NUM_OF_CURRENCY_RATES_0) {
                        return@withContext Error(failure = NonExistentCurrencyRateDataLocalError())
                    }

                    return@withContext Error(failure = CurrencyRateLocalError())

                } catch (e: Exception){
                    return@withContext Error(failure = CurrencyRateLocalError(cause = e))
                }
            }
            is CurrencyRateParams -> {
                throw IllegalArgumentException(ERROR_MESSAGE_INAPPROPRIATE_ARGUMENT_PASSED)
            }
            else -> throw NotImplementedError(ERROR_MESSAGE_NOT_YET_IMPLEMENTED)
        }


    }

    override suspend fun countCurrencyRates(params: Params): ResultData<Int> = withContext(ioDispatcher) {
        when(params){
            is None -> {
                throw IllegalArgumentException(ERROR_MESSAGE_INAPPROPRIATE_ARGUMENT_PASSED)
            }
            is CurrencyRateParams -> {
                return@withContext try {

                    val numOfCurrencyRates: Int = currencyRatesDao.countCurrencyRates(params.currencySymbolBase!!)

                    if (numOfCurrencyRates > NUM_OF_CURRENCY_RATES_0) {
                        return@withContext Success(data = numOfCurrencyRates)
                    } else if (numOfCurrencyRates == NUM_OF_CURRENCY_RATES_0) {
                        return@withContext Error(failure = NonExistentCurrencyRateDataLocalError())
                    }

                    return@withContext Error(failure = CurrencyRateLocalError())

                } catch (e: Exception){
                    return@withContext Error(failure = CurrencyRateLocalError(cause = e))
                }
            }
            else -> throw NotImplementedError(ERROR_MESSAGE_NOT_YET_IMPLEMENTED)
        }
    }

    override suspend fun getCurrencyRates(
        params: Params
    ): ResultData<List<CurrencyRateModel>> = withContext(ioDispatcher) {

        when(params){
            is None -> {
                throw IllegalArgumentException(ERROR_MESSAGE_INAPPROPRIATE_ARGUMENT_PASSED)
            }
            is CurrencyRateParams -> {
                return@withContext try {
                    val currencyRatesEntity: List<CurrencyRateEntity> =
                        currencyRatesDao.getCurrencyRates(params.currencySymbolBase!!)

                    if (currencyRatesEntity.isEmpty())
                        return@withContext Error(failure = CurrencyRateListNotAvailableLocalError())

                    val currencyRatesModel = currencyRatesEntity.map {
                        currencyRateModelMapper.transform(it)
                    }

                    return@withContext Success(currencyRatesModel)

                } catch (e: Exception){
                    return@withContext Error(failure = CurrencyRateLocalError(cause = e))
                }
            }
            else -> throw NotImplementedError(ERROR_MESSAGE_NOT_YET_IMPLEMENTED)
        }
    }

    override suspend fun saveCurrencyRates(
        currencyRatesModel: List<CurrencyRateModel>
    ): ResultData<List<Long>> = withContext(ioDispatcher){

        if (currencyRatesModel.isEmpty())
            return@withContext Error(
                failure = InsertCurrencyRateLocalError(message = R.string.error_cant_save_empty_currency_rate_list)
            )

        val currencyRatesEntity = currencyRatesModel.map {
            currencyRateEntityMapper.transform(it)
        }

        val tableRowIds: List<Long> = currencyRatesDao.insertCurrencyRates(currencyRatesEntity)

        if (tableRowIds.size != currencyRatesEntity.size)
            return@withContext Error(
                InsertCurrencyRateLocalError(message = R.string.error_all_currency_rates_not_saved)
            )

        return@withContext Success(tableRowIds)
    }

    override suspend fun deleteCurrencyRates(params: Params): ResultData<Int> = withContext(ioDispatcher) {
        when(params){
            is None -> {
                throw IllegalArgumentException(ERROR_MESSAGE_INAPPROPRIATE_ARGUMENT_PASSED)
            }
            is CurrencyRateParams -> {
                return@withContext try {

                    val numOfCurrencyRatesResultData: ResultData<Int> = countCurrencyRates(params)

                    val numOfCurrencyRates: Int = if(numOfCurrencyRatesResultData is Error &&
                        numOfCurrencyRatesResultData.failure is CurrencyRateLocalError){
                        return@withContext Error(failure = DeleteCurrencyRateLocalError())
                    } else if(numOfCurrencyRatesResultData is Error &&
                        numOfCurrencyRatesResultData.failure is NonExistentCurrencyRateDataLocalError) {
                        NUM_OF_CURRENCY_RATES_0
                    } else {
                        (numOfCurrencyRatesResultData as Success).data
                    }

                    val numOfCurrencyRatesDeleted: Int = currencyRatesDao.deleteCurrencyRates(params.currencySymbolBase!!)

                    if (numOfCurrencyRatesDeleted > NUM_OF_CURRENCY_RATES_0 && numOfCurrencyRatesDeleted != numOfCurrencyRates) {
                        return@withContext Error(failure = DeleteCurrencyRateLocalError(R.string.error_deleting_currency_rates))
                    }

                    return@withContext Success(numOfCurrencyRatesDeleted)

                } catch (e: Exception){
                    return@withContext Error(failure = DeleteCurrencyRateLocalError(cause = e))
                }
            }
            else -> throw NotImplementedError(ERROR_MESSAGE_NOT_YET_IMPLEMENTED)
        }
    }
}
