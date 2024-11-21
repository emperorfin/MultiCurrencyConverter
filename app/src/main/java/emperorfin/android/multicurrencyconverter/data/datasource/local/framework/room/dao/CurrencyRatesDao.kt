package emperorfin.android.multicurrencyconverter.data.datasource.local.framework.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import emperorfin.android.multicurrencyconverter.data.constant.StringConstants.ERROR_MESSAGE_NOT_YET_IMPLEMENTED
import emperorfin.android.multicurrencyconverter.data.datasource.local.framework.room.entity.currencyrate.CurrencyRateEntity
import emperorfin.android.multicurrencyconverter.data.datasource.local.framework.room.entity.currencyrate.CurrencyRateEntity.Companion.COLUMN_INFO_CURRENCY_SYMBOL_BASE
import emperorfin.android.multicurrencyconverter.data.datasource.local.framework.room.entity.currencyrate.CurrencyRateEntity.Companion.COLUMN_INFO_ID
import emperorfin.android.multicurrencyconverter.data.datasource.local.framework.room.entity.currencyrate.CurrencyRateEntity.Companion.TABLE_NAME
import emperorfin.android.multicurrencyconverter.domain.datalayer.dao.ICurrencyRatesDao

@Dao
interface CurrencyRatesDao : ICurrencyRatesDao {

    @Query("SELECT COUNT(*) FROM $TABLE_NAME")
    override suspend fun countAllCurrencyRates(): Int

    @Query("SELECT COUNT(*) FROM $TABLE_NAME WHERE $COLUMN_INFO_CURRENCY_SYMBOL_BASE = :currencySymbolBase")
    override suspend fun countCurrencyRates(currencySymbolBase: String): Int

    @Query("SELECT * FROM $TABLE_NAME WHERE $COLUMN_INFO_CURRENCY_SYMBOL_BASE = :currencySymbolBase ORDER BY $COLUMN_INFO_ID ASC")
    override suspend fun getCurrencyRates(currencySymbolBase: String): List<CurrencyRateEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    override suspend fun insertCurrencyRates(currencyRates: List<CurrencyRateEntity>): List<Long>

    @Query("DELETE FROM $TABLE_NAME WHERE $COLUMN_INFO_CURRENCY_SYMBOL_BASE = :currencySymbolBase")
    override suspend fun deleteCurrencyRates(currencySymbolBase: String): Int

    override suspend fun getCurrencyRates(currencySymbolBase: String, appId: String): Any =
        throw IllegalStateException(ERROR_MESSAGE_NOT_YET_IMPLEMENTED)

}