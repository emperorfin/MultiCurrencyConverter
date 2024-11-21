package emperorfin.android.multicurrencyconverter.data.datasource.remote.framework.retrofit.webservice.openexchangerates.endpoint.currencyrates


data class CurrencyRatesResponse(
    val disclaimer: String,
    val license: String,
    val timestamp: Long,
    val base: String,
    val rates: Map<String, Double>
)
