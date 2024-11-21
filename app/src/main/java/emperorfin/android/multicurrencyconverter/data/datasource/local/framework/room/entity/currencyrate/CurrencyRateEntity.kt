package emperorfin.android.multicurrencyconverter.data.datasource.local.framework.room.entity.currencyrate

import androidx.room.ColumnInfo
import androidx.room.Entity
import emperorfin.android.multicurrencyconverter.data.datasource.local.framework.room.entity.currencyrate.CurrencyRateEntity.Companion.COLUMN_INFO_ID
import emperorfin.android.multicurrencyconverter.data.datasource.local.framework.room.entity.currencyrate.CurrencyRateEntity.Companion.TABLE_NAME
import emperorfin.android.multicurrencyconverter.domain.uilayer.event.output.currencyrate.CurrencyRateEntityParams

@Entity(
    tableName = TABLE_NAME,
    primaryKeys = [COLUMN_INFO_ID]
)
data class CurrencyRateEntity(
    @ColumnInfo(name = COLUMN_INFO_ID)
    override val id: String,
    @ColumnInfo(name = COLUMN_INFO_CURRENCY_SYMBOL_BASE)
    override val currencySymbolBase: String,
    @ColumnInfo(name = COLUMN_INFO_CURRENCY_SYMBOL_OTHER)
    override val currencySymbolOther: String,
    @ColumnInfo(name = COLUMN_INFO_RATE)
    override val rate: Double
) : CurrencyRateEntityParams {

    companion object {

        const val TABLE_NAME = "table_currency_rates"

        const val COLUMN_INFO_ID = "id"
        const val COLUMN_INFO_CURRENCY_SYMBOL_BASE = "currency_symbol_base"
        const val COLUMN_INFO_CURRENCY_SYMBOL_OTHER = "currency_symbol_other"
        const val COLUMN_INFO_RATE = "rate"

        fun newInstance(
            id: String,
            currencySymbolBase: String,
            currencySymbolOther: String,
            rate: Double
        ): CurrencyRateEntity {
            return CurrencyRateEntity(
                id = id,
                currencySymbolBase = currencySymbolBase,
                currencySymbolOther = currencySymbolOther,
                rate = rate
            )
        }

    }

}
