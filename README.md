# MultiCurrency Converter

MultiCurrency Converter is a native Android app project that uses [Open Exchange Rates REST API](https://openexchangerates.org/api) to convert from one currency to another.

## Functionality (Summary):

* The app fetches exchange rates data from the open exchange rates service. 
* The app persists the data locally to permit it to be used offline after data has been fetched. 
* In order to limit bandwidth usage, the data can be refreshed from the API once every 30 minutes. 
* The user can select a currency from a list of currencies provided by open exchange rates. 
* The user can enter the desired amount for the selected currency. 
* The user is then shown a list showing the desired amount in the selected currency converted into amounts in each 
currency provided by open exchange rates. If exchange rates for the selected currency are not available via open exchange 
rates, the conversion is performed on the app side. 
* The project contain unit tests that ensure correct operation.

## Functionality (Details):

* When the app is newly installed and if the user opens it without an internet connectivity, the screen just shows a 
refresh button and an error message and allows the user to refresh the screen assuming they have an internet connectivity. 
A snack bar no internet connectivity error message is also shown which disappears after a short time. If the user tries 
refreshing without an internet connectivity, the above same thing happens. If there's an internet connectivity but it's 
not active, a refresh button with a different error message is shown but a snack bar no internet connectivity error 
message is not shown since there's an internet connectivity but just that it's not active. 
* The app fetches a list of currency rates (from the openexchangerates.org web service) when the user refreshes with an 
active internet connectivity. The list of currency rates is then displayed on the screen. This list of currency rates 
is also cached to the Room database. 
* Assuming the app opens for the first time with an active internet connectivity, the app fetches a list of currency 
rates (from the aforesaid web service). The currency rates is then displayed on the screen. This list of currency rates 
is also cached to the Room database. 
* Whenever the user subsequently visits the "Currency Converter" screen which is where the aforesaid fetched currency 
rates gets displayed, if without internet connectivity, the cached list of currency rates is displayed. But with internet 
connectivity (this must be active), a fresh list of currency rates is fetched from the web service and displayed on the 
screen. If there's an internet connectivity but is not active, rather than displaying a cached list of currency rates, a 
refresh button with a different error message is shown but a snack bar no internet connectivity error message is not shown.

## Project Tech-stack and Characteristics

* Android SDK
* Kotlin
* Jetpack Compose
* Material Design 3 Components
* ViewModel
* Kotlin Coroutine
* StateFlow
* MVVM Design Pattern
* Repository Pattern
* Navigation
* Offline Storage (via Room)
* Retrofit
* [Open Exchange Rates REST API](https://openexchangerates.org/api)
* Dependency Injection (via Hilt)
* Unit Testing

## Getting Started

### Command-line
Clone the project via the command-line.

### Project Setup
Add openExchangeRates.baseUrl="https://openexchangerates.org/api/" and openExchangeRates.apikey="<API_KEY>" 
to local.properties file at the root of this project. To get an API key, see [Authentication](https://docs.openexchangerates.org/reference/authentication). 

