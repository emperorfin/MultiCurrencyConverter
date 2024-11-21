package emperorfin.android.multicurrencyconverter.data.repository

import emperorfin.android.multicurrencyconverter.R
import emperorfin.android.multicurrencyconverter.di.CurrencyRatesLocalDataSource
import emperorfin.android.multicurrencyconverter.di.CurrencyRatesRemoteDataSource
import emperorfin.android.multicurrencyconverter.di.IoDispatcher
import emperorfin.android.multicurrencyconverter.domain.datalayer.datasource.CurrencyRatesDataSource
import emperorfin.android.multicurrencyconverter.domain.datalayer.repository.ICurrencyRatesRepository
import emperorfin.android.multicurrencyconverter.domain.exception.CurrencyRateFailure.CurrencyRateRemoteError
import emperorfin.android.multicurrencyconverter.domain.exception.CurrencyRateFailure.GetCurrencyRateRepositoryError
import emperorfin.android.multicurrencyconverter.domain.exception.CurrencyRateFailure.CurrencyRateLocalError
import emperorfin.android.multicurrencyconverter.domain.exception.CurrencyRateFailure.GetCurrencyRateRemoteError
import emperorfin.android.multicurrencyconverter.domain.model.currencyrate.CurrencyRateModel
import emperorfin.android.multicurrencyconverter.domain.uilayer.event.output.ResultData
import emperorfin.android.multicurrencyconverter.domain.uilayer.event.output.ResultData.Success
import emperorfin.android.multicurrencyconverter.domain.uilayer.event.output.ResultData.Error
import emperorfin.android.multicurrencyconverter.domain.uilayer.event.output.currencyrate.Params
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap
import javax.inject.Inject

data class CurrencyRatesRepository @Inject constructor(
    @CurrencyRatesLocalDataSource private val currencyRatesLocalDataSource: CurrencyRatesDataSource,
    @CurrencyRatesRemoteDataSource private val currencyRatesRemoteDataSource: CurrencyRatesDataSource,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : ICurrencyRatesRepository {

    private var cachedCurrencyRates: ConcurrentMap<String, List<CurrencyRateModel>>? = null

    override suspend fun countAllCurrencyRates(
        params: Params,
        countRemotely: Boolean
    ): ResultData<Int> = withContext(ioDispatcher) {
        if (countRemotely) {
            return@withContext currencyRatesRemoteDataSource.countAllCurrencyRates(params = params)
        } else {
            return@withContext currencyRatesLocalDataSource.countAllCurrencyRates(params = params)
        }
    }

    override suspend fun countCurrencyRates(
        params: Params,
        countRemotely: Boolean
    ): ResultData<Int> = withContext(ioDispatcher) {

        if (countRemotely) {
            return@withContext currencyRatesRemoteDataSource.countCurrencyRates(params = params)
        } else {
            return@withContext currencyRatesLocalDataSource.countCurrencyRates(params = params)
        }
    }

    override suspend fun getCurrencyRates(
        params: Params,
        forceUpdate: Boolean
    ): ResultData<List<CurrencyRateModel>> = withContext(ioDispatcher) {

        if (!forceUpdate) {
            cachedCurrencyRates?.let {
                val cachedCurrencyRates: MutableCollection<List<CurrencyRateModel>> = it.values

                if (cachedCurrencyRates.isNotEmpty()) {
                    return@withContext Success(cachedCurrencyRates.first())
                }
            }
        }

        val newCurrencyRates: ResultData<List<CurrencyRateModel>> =
            fetchCurrencyRatesFromRemoteOrLocal(params = params, forceUpdate = forceUpdate)

        (newCurrencyRates as? Success)?.let { refreshCache(it.data) }

        cachedCurrencyRates?.values?.let {
            val currencyRates: MutableCollection<List<CurrencyRateModel>> = it

            if (currencyRates.isNotEmpty()) {
                return@withContext Success(currencyRates.first())
            }
        }

        (newCurrencyRates as? Success)?.let {
            return@withContext it
        }

        return@withContext newCurrencyRates as Error
    }

    override suspend fun saveCurrencyRates(
        currencyRatesModel: List<CurrencyRateModel>,
        saveRemotely: Boolean
    ): ResultData<List<Long>> = withContext(ioDispatcher) {

        if (saveRemotely) {
            return@withContext currencyRatesRemoteDataSource.saveCurrencyRates(currencyRatesModel = currencyRatesModel)
        } else {
            return@withContext currencyRatesLocalDataSource.saveCurrencyRates(currencyRatesModel = currencyRatesModel)
        }

    }

    override suspend fun deleteCurrencyRates(
        params: Params,
        deleteRemotely: Boolean
    ): ResultData<Int> = withContext(ioDispatcher) {

        if (deleteRemotely) {
            return@withContext currencyRatesRemoteDataSource.deleteCurrencyRates(params = params)
        } else {
            return@withContext currencyRatesLocalDataSource.deleteCurrencyRates(params = params)
        }
    }

    private suspend fun fetchCurrencyRatesFromRemoteOrLocal(
        params: Params, forceUpdate: Boolean
    ): ResultData<List<CurrencyRateModel>> {
        var isRemoteException = false

        if (forceUpdate) {
            when (val currencyRatesRemote = currencyRatesRemoteDataSource.getCurrencyRates(params = params)) {
                is Error -> {
                    if (currencyRatesRemote.failure is CurrencyRateRemoteError)
                        isRemoteException = true
                }
                is Success -> {
                    refreshLocalDataSource(params = params, currencyRates = currencyRatesRemote.data)

                    return currencyRatesRemote
                }
                else -> {}
            }
        }

        if (forceUpdate) {
            if (isRemoteException)
                return Error(
                    GetCurrencyRateRepositoryError(
                        message = R.string.exception_occurred_remote
                    )
                )

            return Error(
                GetCurrencyRateRemoteError(
                    message = R.string.error_cant_force_refresh_currency_rates_remote_data_source_unavailable
                )
            )
        }

        val currencyRatesLocal = currencyRatesLocalDataSource.getCurrencyRates(params = params)

        if (currencyRatesLocal is Success) return currencyRatesLocal

        if ((currencyRatesLocal as Error).failure is CurrencyRateLocalError)
            return Error(
                GetCurrencyRateRepositoryError(
                    R.string.exception_occurred_local
                )
            )

        return Error(
            GetCurrencyRateRepositoryError(
                R.string.error_fetching_from_remote_and_local
            )
        )
    }

    private fun refreshCache(currencyRates: List<CurrencyRateModel>) {
        cachedCurrencyRates?.clear()

        currencyRates.sortedBy { it.currencySymbolOther }.apply {
            cacheAndPerform(currencyRates = this) {}
        }
    }

    private suspend fun refreshLocalDataSource(params: Params, currencyRates: List<CurrencyRateModel>) {
        currencyRatesLocalDataSource.deleteCurrencyRates(params = params)

        currencyRatesLocalDataSource.saveCurrencyRates(currencyRatesModel = currencyRates)
    }

    private fun cacheCurrencyRates(currencyRates: List<CurrencyRateModel>): List<CurrencyRateModel> {

        val cachedCurrencyRatesNew = mutableListOf<CurrencyRateModel>()

        currencyRates.forEach {
            val currencyRate = CurrencyRateModel.newInstance(
                currencySymbolBase = it.currencySymbolBase,
                currencySymbolOther = it.currencySymbolOther,
                rate = it.rate,
                id = it.id,
            )

            cachedCurrencyRatesNew.add(currencyRate)
        }

        if (cachedCurrencyRates == null) {
            cachedCurrencyRates = ConcurrentHashMap()
        }
        cachedCurrencyRates?.put(cachedCurrencyRatesNew.first().currencySymbolBase, cachedCurrencyRatesNew)

        return cachedCurrencyRatesNew
    }

    private inline fun cacheAndPerform(currencyRates: List<CurrencyRateModel>, perform: (List<CurrencyRateModel>) -> Unit) {
        val cachedCurrencyRates = cacheCurrencyRates(currencyRates = currencyRates)

        perform(cachedCurrencyRates)
    }
}
