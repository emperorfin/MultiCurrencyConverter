package emperorfin.android.multicurrencyconverter.data.repository.fake

import emperorfin.android.multicurrencyconverter.data.datasource.local.framework.room.entitysource.fake.FakeCurrencyRatesLocalDataSourceRoom
import emperorfin.android.multicurrencyconverter.data.datasource.remote.framework.retrofit.dtosource.fake.FakeCurrencyRatesRemoteDataSourceRetrofit
import emperorfin.android.multicurrencyconverter.domain.datalayer.repository.IFakeCurrencyRatesRepository
import emperorfin.android.multicurrencyconverter.domain.model.currencyrate.CurrencyRateModel
import emperorfin.android.multicurrencyconverter.domain.uilayer.event.output.ResultData
import emperorfin.android.multicurrencyconverter.domain.uilayer.event.output.currencyrate.Params


internal data class FakeCurrencyRatesRepository(
    val currencyRatesLocalDataSource: FakeCurrencyRatesLocalDataSourceRoom =
        FakeCurrencyRatesLocalDataSourceRoom(),
    val currencyRatesRemoteDataSource: FakeCurrencyRatesRemoteDataSourceRetrofit =
        FakeCurrencyRatesRemoteDataSourceRetrofit()
) : IFakeCurrencyRatesRepository {

    override suspend fun countAllCurrencyRates(
        params: Params,
        countRemotely: Boolean
    ): ResultData<Int> {
        return if (countRemotely) {
            currencyRatesRemoteDataSource.countAllCurrencyRates(params = params)
        } else {
            currencyRatesLocalDataSource.countAllCurrencyRates(params = params)
        }
    }

    override suspend fun countCurrencyRates(
        params: Params,
        countRemotely: Boolean
    ): ResultData<Int> {
        return if (countRemotely) {
            currencyRatesRemoteDataSource.countCurrencyRates(params = params)
        } else {
            currencyRatesLocalDataSource.countCurrencyRates(params = params)
        }
    }

    override suspend fun getCurrencyRates(
        params: Params,
        forceUpdate: Boolean
    ): ResultData<List<CurrencyRateModel>> {
        return if (forceUpdate) {
            currencyRatesRemoteDataSource.getCurrencyRates(params = params)
        } else {
            currencyRatesLocalDataSource.getCurrencyRates(params = params)
        }
    }

    override suspend fun saveCurrencyRates(
        currencyRatesModel: List<CurrencyRateModel>,
        saveRemotely: Boolean
    ): ResultData<List<Long>> {
        return if (saveRemotely) {
            currencyRatesRemoteDataSource.saveCurrencyRates(currencyRatesModel = currencyRatesModel)
        } else {
            currencyRatesLocalDataSource.saveCurrencyRates(currencyRatesModel = currencyRatesModel)
        }
    }

    override suspend fun deleteCurrencyRates(
        params: Params,
        deleteRemotely: Boolean
    ): ResultData<Int> {
        return if (deleteRemotely) {
            currencyRatesRemoteDataSource.deleteCurrencyRates(params = params)
        } else {
            currencyRatesLocalDataSource.deleteCurrencyRates(params = params)
        }
    }
}
