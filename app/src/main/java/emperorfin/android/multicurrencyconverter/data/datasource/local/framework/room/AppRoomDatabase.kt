package emperorfin.android.multicurrencyconverter.data.datasource.local.framework.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import emperorfin.android.multicurrencyconverter.data.datasource.local.framework.room.dao.CurrencyRatesDao
import emperorfin.android.multicurrencyconverter.data.datasource.local.framework.room.entity.currencyrate.CurrencyRateEntity

@Database(entities = [CurrencyRateEntity::class], version = 1, exportSchema = false)
abstract class AppRoomDatabase : RoomDatabase() {

    abstract val mCurrencyRatesDao: CurrencyRatesDao

    companion object {

        private const val DATABASE_NAME = "database_app"

        @Volatile
        private var INSTANCE: AppRoomDatabase? = null

        fun getInstance(context: Context): AppRoomDatabase{

            synchronized(this) {
                var instance = INSTANCE

                if(instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        AppRoomDatabase::class.java,
                        DATABASE_NAME
                    )
                        .fallbackToDestructiveMigration()
                        .build()

                    INSTANCE = instance
                }

                return instance
            }

        }

    }
}