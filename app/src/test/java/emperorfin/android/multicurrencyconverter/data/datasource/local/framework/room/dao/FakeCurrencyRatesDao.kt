package emperorfin.android.multicurrencyconverter.data.datasource.local.framework.room.dao

import emperorfin.android.multicurrencyconverter.data.constant.StringConstants.ERROR_MESSAGE_NOT_YET_IMPLEMENTED
import emperorfin.android.multicurrencyconverter.data.datasource.local.framework.room.entity.currencyrate.CurrencyRateEntity
import emperorfin.android.multicurrencyconverter.data.datasource.util.CurrencyRateEntityDataGeneratorUtil
import emperorfin.android.multicurrencyconverter.data.datasource.util.CurrencyRateEntityDataGeneratorUtil.CURRENCY_SYMBOL_BASE_USD
import emperorfin.android.multicurrencyconverter.data.datasource.util.CurrencyRateEntityDataGeneratorUtil.CURRENCY_SYMBOL_OTHER_AED
import emperorfin.android.multicurrencyconverter.data.datasource.util.CurrencyRateEntityDataGeneratorUtil.CURRENCY_SYMBOL_OTHER_AFN
import emperorfin.android.multicurrencyconverter.data.datasource.util.CurrencyRateEntityDataGeneratorUtil.ID_AED
import emperorfin.android.multicurrencyconverter.data.datasource.util.CurrencyRateEntityDataGeneratorUtil.ID_AFN
import emperorfin.android.multicurrencyconverter.data.datasource.util.CurrencyRateEntityDataGeneratorUtil.RATE_AED
import emperorfin.android.multicurrencyconverter.data.datasource.util.CurrencyRateEntityDataGeneratorUtil.RATE_AFN
import emperorfin.android.multicurrencyconverter.domain.datalayer.dao.IFakeCurrencyRatesDao

internal data class FakeCurrencyRatesDao(
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

        val TABLE_ROW_IDS_TWO: List<Long> = listOf(1L, 2L)

        val CURRENCY_RATES_ENTITY: List<CurrencyRateEntity> =
            CurrencyRateEntityDataGeneratorUtil.getCurrencyRateEntityList()
    }

    override suspend fun countAllCurrencyRates(): Int {

        if (isCountException) throw Exception()

        return noOfCurrencyRates
    }

    override suspend fun countCurrencyRates(currencySymbolBase: String): Int {

        if (isCountException) throw Exception()

        return noOfCurrencyRates
    }

    override suspend fun getCurrencyRates(currencySymbolBase: String): List<CurrencyRateEntity> {

        if (isException) throw Exception()

        if (isEmptyList) return emptyList()

        val currencyRate1 = CurrencyRateEntity.newInstance(
            id = ID_AED,
            rate = RATE_AED,
            currencySymbolBase = CURRENCY_SYMBOL_BASE_USD,
            currencySymbolOther = CURRENCY_SYMBOL_OTHER_AED,
        )

        val currencyRate2 = CurrencyRateEntity.newInstance(
            id = ID_AFN,
            rate = RATE_AFN,
            currencySymbolBase = CURRENCY_SYMBOL_BASE_USD,
            currencySymbolOther = CURRENCY_SYMBOL_OTHER_AFN,
        )

        return listOf(currencyRate1, currencyRate2)
    }

    override suspend fun insertCurrencyRates(currencyRates: List<CurrencyRateEntity>): List<Long> =
        tableRowIds

    override suspend fun deleteCurrencyRates(currencySymbolBase: String): Int {

        if (isException) throw Exception()

        return noOfCurrencyRatesDeleted
    }

    override suspend fun getCurrencyRates(currencySymbolBase: String, appId: String): Any =
        throw IllegalStateException(ERROR_MESSAGE_NOT_YET_IMPLEMENTED)
}
