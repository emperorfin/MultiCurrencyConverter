package emperorfin.android.multicurrencyconverter.ui.screen.currencyconverter.stateholder

import android.app.Application
import android.content.Context
import android.widget.Toast
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import emperorfin.android.multicurrencyconverter.R
import emperorfin.android.multicurrencyconverter.di.DefaultDispatcher
import emperorfin.android.multicurrencyconverter.di.IoDispatcher
import emperorfin.android.multicurrencyconverter.domain.datalayer.repository.ICurrencyRatesRepository
import emperorfin.android.multicurrencyconverter.domain.exception.CurrencyRateFailure
import emperorfin.android.multicurrencyconverter.domain.model.currencyrate.CurrencyRateModel
import emperorfin.android.multicurrencyconverter.domain.model.currencyrate.CurrencyRateModelMapper
import emperorfin.android.multicurrencyconverter.domain.uilayer.event.input.currencyrate.CurrencyRateParams
import emperorfin.android.multicurrencyconverter.domain.uilayer.event.output.ResultData
import emperorfin.android.multicurrencyconverter.domain.uilayer.event.output.succeeded
import emperorfin.android.multicurrencyconverter.ui.model.currencyrate.CurrencyRateUiModel
import emperorfin.android.multicurrencyconverter.ui.model.currencyrate.CurrencyRateUiModelMapper
import emperorfin.android.multicurrencyconverter.ui.util.InternetConnectivityUtil.hasInternetConnection
import emperorfin.android.multicurrencyconverter.ui.util.WhileUiSubscribed
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlin.math.roundToInt
import kotlin.properties.Delegates


@HiltViewModel
data class CurrencyConverterViewModel @Inject constructor(
    val applicationContext: Application,
    private val currencyRatesRepository: ICurrencyRatesRepository,
    private val currencyRateModelMapper: CurrencyRateModelMapper,
    private val currencyRateUiModelMapper: CurrencyRateUiModelMapper,
    @IoDispatcher private val coroutineDispatcherIo: CoroutineDispatcher,
    @DefaultDispatcher private val coroutineDispatcherDefault: CoroutineDispatcher,
) : ViewModel() {

    companion object {

        const val ERROR_MESSAGE_ON_CONVERT_RATES_PARAM_CANT_BE_NULL_WHEN_WOULD_RECONVERT_RATES_PARAM_IS_TRUE: String =
            "When the parameter wouldReconvertRates is true, the parameter onConvertRates " +
                    "must not be null."

        private const val NUM_OF_CURRENCY_RATES_MINUS_1: Int = -1
        private const val NUM_OF_CURRENCY_RATES_0: Int = 0

        private const val REFRESH_TIME_MILLIS_0: Long = 0
        private const val REFRESH_TIME_MINUTES_30: Int = 30

        private const val CURRENCY_SYMBOL_USD: String = "USD"

        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("pref_data_store_screen_currency_conversion")

        private val PREF_KEY_REFRESH_TIME_MILLIS_START: Preferences.Key<Long> =
            longPreferencesKey("refresh_time_millis_start")

    }

    private var initCurrencyRates = true
    private var isRefresh = false

    private var currencyRatesWithFlagsDefault = listOf<CurrencyRateUiModel>()

    private val _isLoading = MutableStateFlow(false)

    private val _errorMessage: MutableStateFlow<Int?> = MutableStateFlow(null)

    private val _currencyRatesWithFlags: MutableStateFlow<ResultData<List<CurrencyRateUiModel>>> =
        MutableStateFlow(ResultData.Loading)
    private val currencyRatesWithFlags: StateFlow<ResultData<List<CurrencyRateUiModel>>> =
        _currencyRatesWithFlags

    private val _messageSnackBar: MutableStateFlow<Int?> = MutableStateFlow(null)
    private val messageSnackBar: StateFlow<Int?> = _messageSnackBar

    private val savedRefreshTimeMillisStart: Flow<Long> = applicationContext.dataStore.data
        .map { preferences ->
            preferences[PREF_KEY_REFRESH_TIME_MILLIS_START] ?: REFRESH_TIME_MILLIS_0
        }

    private var refreshTimeMillisStart: Long = REFRESH_TIME_MILLIS_0
    private var refreshTimeMillisCurrent: Long = REFRESH_TIME_MILLIS_0

    init {
        initCurrencyRates()
    }

    fun convert(
        isFreeOpenExchangeRatesAccount: Boolean = true,
        baseAmount: Int,
        baseCurrencySymbol: String
    ) {

        val currencyRatesWithFlagsResultData: ResultData<List<CurrencyRateUiModel>> =
            _currencyRatesWithFlags.value

        if (currencyRatesWithFlagsResultData is ResultData.Error) {
            _currencyRatesWithFlags.value = currencyRatesWithFlagsResultData

            return
        }

        val currencyRatesWithFlags: List<CurrencyRateUiModel> = currencyRatesWithFlagsDefault

        if (currencyRatesWithFlags.isEmpty()) {
            _currencyRatesWithFlags.value = ResultData.Error(
                failure = CurrencyRateFailure.CurrencyRateMemoryError(message = R.string.error_unexpected_during_conversion)
            )

            return
        }

        _currencyRatesWithFlags.value = ResultData.Loading

        val currencyRatesWithFlagsNew = mutableListOf<CurrencyRateUiModel>()

        currencyRatesWithFlags.forEach {

            val currencyRateWithFlag: CurrencyRateUiModel =
                currencyRatesWithFlags.single { currencyRateWithFlag ->
                    currencyRateWithFlag.currencySymbolOther == baseCurrencySymbol
                }

            val baseCurrencySymbolValue: Double = currencyRateWithFlag.rate

            val currencyRateValue: Double = it.rate

            if (baseCurrencySymbol == CURRENCY_SYMBOL_USD || !isFreeOpenExchangeRatesAccount) {
                val newRate = currencyRateValue * baseAmount
                val rateToThreeDecimalPlace = roundToThreeDecimalPlaces(newRate)

                val currencyRateWithFlagNew = CurrencyRateUiModel.newInstance(
                    id = it.id,
                    currencySymbolBase = baseCurrencySymbol,
                    currencySymbolOther = it.currencySymbolOther,
                    rate = rateToThreeDecimalPlace,
                    currencySymbolOtherFlag = it.currencySymbolOtherFlag
                )

                currencyRatesWithFlagsNew.add(currencyRateWithFlagNew)
            } else {

                val newRate = (currencyRateValue / baseCurrencySymbolValue) * baseAmount
                val rateToThreeDecimalPlace = roundToThreeDecimalPlaces(newRate)

                val currencyRateWithFlagNew = CurrencyRateUiModel.newInstance(
                    id = it.id,
                    currencySymbolBase = baseCurrencySymbol,
                    currencySymbolOther = it.currencySymbolOther,
                    rate = rateToThreeDecimalPlace,
                    currencySymbolOtherFlag = it.currencySymbolOtherFlag
                )

                currencyRatesWithFlagsNew.add(currencyRateWithFlagNew)
            }
        }

        initCurrencyRates = false

        _currencyRatesWithFlags.value = ResultData.Success(data = currencyRatesWithFlagsNew)

    }

    fun initCurrencyRates(
        params: CurrencyRateParams = CurrencyRateParams(currencySymbolBase = CURRENCY_SYMBOL_USD),
        isRefresh: Boolean = false,
        wouldReconvertRates: Boolean = false,
        onConvertRates: (() -> Unit)? = null
    ) {
        loadCurrencyRates(
            params = params,
            isRefresh = isRefresh,
            wouldReconvertRates = wouldReconvertRates,
            onConvertRates = onConvertRates
        )

    }

    fun refreshCurrencyRates(
        isFreeOpenExchangeRatesAccount: Boolean = true,
        baseAmount: Int,
        baseCurrencySymbol: String
    ) {
        if (isFreeOpenExchangeRatesAccount && baseCurrencySymbol != CURRENCY_SYMBOL_USD) return

        refreshTimeMillisCurrent = System.currentTimeMillis()

        if (refreshTimeMillisStart > REFRESH_TIME_MILLIS_0) {
            val refreshTimeElapsedMillis: Long = refreshTimeMillisCurrent - refreshTimeMillisStart

            val refreshTimeElapsedMinutes: Int =
                TimeUnit.MILLISECONDS.toMinutes(refreshTimeElapsedMillis).toInt()

            if (refreshTimeElapsedMinutes < REFRESH_TIME_MINUTES_30) {
                Toast.makeText(
                    applicationContext,
                    R.string.message_refresh_in_30_minutes,
                    Toast.LENGTH_LONG
                ).show()

                return
            }
        }

        isRefresh = true

        val params = CurrencyRateParams(currencySymbolBase = baseCurrencySymbol)

        initCurrencyRates(
            params = params,
            isRefresh = true,
            wouldReconvertRates = true,
            onConvertRates = {
                convert(baseAmount = baseAmount, baseCurrencySymbol = baseCurrencySymbol)
            }
        )
    }

    val uiState: StateFlow<CurrencyConverterUiState> = combine(
        _isLoading,
        _errorMessage,
        currencyRatesWithFlags,
        messageSnackBar,
        savedRefreshTimeMillisStart
    ) { isLoading, errorMessage, currencyRatesWithFlags, messageSnackBar, savedRefreshTimeMillisStart ->
        when (currencyRatesWithFlags) {
            ResultData.Loading -> {
                CurrencyConverterUiState(isLoading = true)
            }

            is ResultData.Error -> {
                CurrencyConverterUiState(
                    errorMessage = (currencyRatesWithFlags.failure as CurrencyRateFailure).message,
                    messageSnackBar = messageSnackBar,
                    initRates = initCurrencyRates
                )
            }

            is ResultData.Success<List<CurrencyRateUiModel>> -> {

                val currencyRatesWithFlagsOld: List<CurrencyRateUiModel> =
                    currencyRatesWithFlags.data
                val currencyRatesWithFlagsNew: MutableList<CurrencyRateUiModel> = mutableListOf()

                val mapOfCurrencySymbolsToFlag = mutableMapOf<String, String?>()

                currencyRatesWithFlagsOld.forEach {

                    if (it.currencySymbolBase != it.currencySymbolOther) {
                        currencyRatesWithFlagsNew.add(it)
                    }

                    mapOfCurrencySymbolsToFlag[it.currencySymbolOther] = it.currencySymbolOtherFlag
                }

                val currencyRatesWithFlagsSorted = currencyRatesWithFlagsNew.sortedBy {
                    it.currencySymbolOther
                }

                val mapOfCurrencySymbolsToFlagSorted = mapOfCurrencySymbolsToFlag.toSortedMap()

                if (isRefresh) {

                    val refreshTimeElapsedMillis =
                        refreshTimeMillisCurrent - savedRefreshTimeMillisStart

                    val refreshTimeElapsedMinutes: Int =
                        TimeUnit.MILLISECONDS.toMinutes(refreshTimeElapsedMillis).toInt()

                    if (savedRefreshTimeMillisStart > REFRESH_TIME_MILLIS_0 && refreshTimeElapsedMinutes < REFRESH_TIME_MINUTES_30) {
                        refreshTimeMillisStart = savedRefreshTimeMillisStart
                    } else {
                        refreshTimeMillisStart = System.currentTimeMillis()

                        saveStartRefreshTimeMillis(refreshTimeMillisStart = refreshTimeMillisStart)
                    }

                } else {
                    refreshTimeMillisStart = savedRefreshTimeMillisStart
                }

                isRefresh = false

                CurrencyConverterUiState(
                    items = currencyRatesWithFlagsSorted,
                    mapOfCurrencySymbolsToFlag = mapOfCurrencySymbolsToFlagSorted,
                    isLoading = isLoading,
                    errorMessage = errorMessage,
                    messageSnackBar = messageSnackBar
                )
            }
        }
    }
        .stateIn(
            scope = viewModelScope,
            started = WhileUiSubscribed,
            initialValue = CurrencyConverterUiState(isLoading = true)
        )

    private fun roundToThreeDecimalPlaces(rateValue: Double): Double {
        return (rateValue * 1000.0).roundToInt() / 1000.0
    }

    fun snackBarMessageShown() {
        _messageSnackBar.value = null
    }

    private suspend fun saveStartRefreshTimeMillis(refreshTimeMillisStart: Long) {
        applicationContext.dataStore.edit { preferences ->
            preferences[PREF_KEY_REFRESH_TIME_MILLIS_START] = refreshTimeMillisStart
        }
    }

    private fun getDatabaseCurrencyRatesViaRepository(
        params: CurrencyRateParams,
        isRefresh: Boolean,
        wouldReconvertRates: Boolean,
        onConvertRates: (() -> Unit)? = null
    ) = viewModelScope.launch(context = coroutineDispatcherIo) {

        _currencyRatesWithFlags.value = ResultData.Loading

        val currencyRatesResultData: ResultData<List<CurrencyRateModel>> =
            currencyRatesRepository.getCurrencyRates(params = params, forceUpdate = isRefresh)

        if (currencyRatesResultData.succeeded) {
            val currencyRatesEntity = (currencyRatesResultData as ResultData.Success).data

            val currencyConverterModelMapper = currencyRateModelMapper
            val currencyConverterUiModelMapper = currencyRateUiModelMapper

            val currencyRatesUiModel: List<CurrencyRateUiModel> = currencyRatesEntity.map {
                currencyConverterModelMapper.transform(it)
            }.map {
                currencyConverterUiModelMapper.transform(it)
            }

            initCurrencyRates = false

            currencyRatesWithFlagsDefault = currencyRatesUiModel

            if (!wouldReconvertRates) {
                _currencyRatesWithFlags.value = ResultData.Success(data = currencyRatesUiModel)
            } else {

                if (onConvertRates == null) {
                    throw IllegalArgumentException(
                        ERROR_MESSAGE_ON_CONVERT_RATES_PARAM_CANT_BE_NULL_WHEN_WOULD_RECONVERT_RATES_PARAM_IS_TRUE
                    )
                }

                onConvertRates.invoke()
            }
        } else {
            val error: ResultData.Error = (currencyRatesResultData as ResultData.Error)
            _currencyRatesWithFlags.value = error
        }

    }

    private fun loadCurrencyRates(
        params: CurrencyRateParams,
        isRefresh: Boolean,
        wouldReconvertRates: Boolean,
        onConvertRates: (() -> Unit)? = null
    ) {
        viewModelScope.launch {

            var currencyRatesCount by Delegates.notNull<Int>()

            val currencyRatesCountDataResultEvent =
                currencyRatesRepository.countCurrencyRates(params = params)

            currencyRatesCount = if (currencyRatesCountDataResultEvent.succeeded)
                (currencyRatesCountDataResultEvent as ResultData.Success).data
            else
                NUM_OF_CURRENCY_RATES_MINUS_1

            if (currencyRatesCount > NUM_OF_CURRENCY_RATES_0 || isRefresh) {

                if (hasInternetConnection(applicationContext)) {

                    getCurrencyRatesViaRepository(
                        params = params,
                        isRefresh = true,
                        wouldReconvertRates = wouldReconvertRates,
                        onConvertRates = onConvertRates
                    )
                } else {

                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            applicationContext,
                            R.string.message_no_internet_loading_cached_currency_rates,
                            Toast.LENGTH_LONG
                        ).show()
                    }

                    getCurrencyRatesViaRepository(
                        params = params,
                        isRefresh = false,
                        wouldReconvertRates = wouldReconvertRates,
                        onConvertRates = onConvertRates
                    )
                }
            } else {

                if (hasInternetConnection(applicationContext)) {
                    getCurrencyRatesViaRepository(
                        params = params,
                        isRefresh = true,
                        wouldReconvertRates = wouldReconvertRates,
                        onConvertRates = onConvertRates
                    )
                } else {

                    _messageSnackBar.value = R.string.message_no_internet_connectivity

                    _currencyRatesWithFlags.value = ResultData.Error(
                        failure = CurrencyRateFailure.CurrencyRateRemoteError(
                            message = R.string.message_no_internet_connectivity
                        )
                    )
                }
            }

        }
    }

    private fun getCurrencyRatesViaRepository(
        params: CurrencyRateParams,
        isRefresh: Boolean,
        wouldReconvertRates: Boolean,
        onConvertRates: (() -> Unit)? = null
    ) {
        getDatabaseCurrencyRatesViaRepository(
            params = params,
            isRefresh = isRefresh,
            wouldReconvertRates = wouldReconvertRates,
            onConvertRates = onConvertRates
        )
    }

}