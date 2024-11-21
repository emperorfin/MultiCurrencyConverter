package emperorfin.android.multicurrencyconverter.di

import android.content.Context
import com.skydoves.sandwich.coroutines.CoroutinesResponseCallAdapterFactory
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import emperorfin.android.multicurrencyconverter.BuildConfig
import emperorfin.android.multicurrencyconverter.data.datasource.local.framework.room.AppRoomDatabase
import emperorfin.android.multicurrencyconverter.data.datasource.local.framework.room.entitysource.CurrencyRatesLocalDataSourceRoom
import emperorfin.android.multicurrencyconverter.data.datasource.remote.framework.retrofit.dtosource.CurrencyRatesRemoteDataSourceRetrofit
import emperorfin.android.multicurrencyconverter.data.datasource.remote.framework.retrofit.webservice.openexchangerates.api.CurrencyRatesApi
import emperorfin.android.multicurrencyconverter.domain.datalayer.dao.ICurrencyRatesDao
import emperorfin.android.multicurrencyconverter.domain.datalayer.datasource.CurrencyRatesDataSource
import emperorfin.android.multicurrencyconverter.domain.datalayer.repository.ICurrencyRatesRepository
import emperorfin.android.multicurrencyconverter.data.repository.CurrencyRatesRepository
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Qualifier
import javax.inject.Singleton


@Retention(AnnotationRetention.RUNTIME)
@Qualifier
annotation class CurrencyRatesLocalDataSource

@Retention(AnnotationRetention.RUNTIME)
@Qualifier
annotation class CurrencyRatesRemoteDataSource

@Retention(AnnotationRetention.RUNTIME)
@Qualifier
annotation class LocalCurrencyRatesDao

@Retention(AnnotationRetention.RUNTIME)
@Qualifier
annotation class RemoteCurrencyRatesDao

@Module
@InstallIn(SingletonComponent::class)
abstract class DataSourceModule {

    @Singleton
    @Binds
    @CurrencyRatesLocalDataSource
    abstract fun bindCurrencyRatesLocalDataSourceRoom(dataSource: CurrencyRatesLocalDataSourceRoom): CurrencyRatesDataSource

    @Singleton
    @Binds
    @CurrencyRatesRemoteDataSource
    abstract fun bindCurrencyRatesRemoteDataSourceRetrofit(dataSource: CurrencyRatesRemoteDataSourceRetrofit): CurrencyRatesDataSource
}

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Singleton
    @Binds
    abstract fun bindCurrencyRatesRepository(repository: CurrencyRatesRepository): ICurrencyRatesRepository
}

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Singleton
    @Provides
    fun providesAppDatabase(
        @ApplicationContext context: Context
    ): AppRoomDatabase {
        return AppRoomDatabase.getInstance(context)
    }

    @Provides
    @LocalCurrencyRatesDao
    fun provideCurrencyRatesDao(database: AppRoomDatabase): ICurrencyRatesDao = database.mCurrencyRatesDao
}

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.OPEN_EXCHANGE_RATES_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(CoroutinesResponseCallAdapterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    @RemoteCurrencyRatesDao
    fun provideCurrencyRatesApi(retrofit: Retrofit): ICurrencyRatesDao {
        return retrofit.create(CurrencyRatesApi::class.java)
    }
}