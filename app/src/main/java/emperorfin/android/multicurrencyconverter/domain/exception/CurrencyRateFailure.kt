package emperorfin.android.multicurrencyconverter.domain.exception

import androidx.annotation.StringRes
import emperorfin.android.multicurrencyconverter.R
import emperorfin. android. multicurrencyconverter.domain.exception.Failure.FeatureFailure

sealed class CurrencyRateFailure(
    @StringRes open val message: Int,
    open val cause: Throwable?
) : FeatureFailure() {

    class CurrencyRateListNotAvailableMemoryError(
        @StringRes override val message: Int = R.string.error_memory_currency_rate_list_not_available,
        override val cause: Throwable? = null
    ) : CurrencyRateFailure(message = message, cause = cause)

    class CurrencyRateListNotAvailableLocalError(
        @StringRes override val message: Int = R.string.error_local_currency_rate_list_not_available,
        override val cause: Throwable? = null
    ) : CurrencyRateFailure(message = message, cause = cause)

    class CurrencyRateListNotAvailableRemoteError(
        @StringRes override val message: Int = R.string.error_remote_currency_rate_list_not_available,
        override val cause: Throwable? = null
    ) : CurrencyRateFailure(message = message, cause = cause)

    class CurrencyRateMemoryError(
        @StringRes override val message: Int = R.string.error_memory_currency_rate,
        override val cause: Throwable? = null
    ) : CurrencyRateFailure(message = message, cause = cause)

    class CurrencyRateLocalError(
        @StringRes override val message: Int = R.string.error_local_currency_rate,
        override val cause: Throwable? = null
    ) : CurrencyRateFailure(message = message, cause = cause)

    class CurrencyRateRemoteError(
        @StringRes override val message: Int = R.string.error_remote_currency_rate,
        override val cause: Throwable? = null
    ) : CurrencyRateFailure(message = message, cause = cause)

    class GetCurrencyRateMemoryError(
        @StringRes override val message: Int = R.string.error_memory_get_currency_rate,
        override val cause: Throwable? = null
    ) : CurrencyRateFailure(message = message, cause = cause)

    class GetCurrencyRateLocalError(
        @StringRes override val message: Int = R.string.error_local_get_currency_rate,
        override val cause: Throwable? = null
    ) : CurrencyRateFailure(message = message, cause = cause)

    class GetCurrencyRateRemoteError(
        @StringRes override val message: Int = R.string.error_remote_get_currency_rate,
        override val cause: Throwable? = null
    ) : CurrencyRateFailure(message = message, cause = cause)

    class InsertCurrencyRateMemoryError(
        @StringRes override val message: Int = R.string.error_memory_insert_currency_rate,
        override val cause: Throwable? = null
    ) : CurrencyRateFailure(message = message, cause = cause)

    class InsertCurrencyRateLocalError(
        @StringRes override val message: Int = R.string.error_local_insert_currency_rate,
        override val cause: Throwable? = null
    ) : CurrencyRateFailure(message = message, cause = cause)

    class InsertCurrencyRateRemoteError(
        @StringRes override val message: Int = R.string.error_remote_insert_currency_rate,
        override val cause: Throwable? = null
    ) : CurrencyRateFailure(message = message, cause = cause)

    class UpdateCurrencyRateMemoryError(
        @StringRes override val message: Int = R.string.error_memory_update_currency_rate,
        override val cause: Throwable? = null
    ) : CurrencyRateFailure(message = message, cause = cause)

    class UpdateCurrencyRateLocalError(
        @StringRes override val message: Int = R.string.error_local_update_currency_rate,
        override val cause: Throwable? = null
    ) : CurrencyRateFailure(message = message, cause = cause)

    class UpdateCurrencyRateRemoteError(
        @StringRes override val message: Int = R.string.error_remote_update_currency_rate,
        override val cause: Throwable? = null
    ) : CurrencyRateFailure(message = message, cause = cause)

    class DeleteCurrencyRateMemoryError(
        @StringRes override val message: Int = R.string.error_memory_delete_currency_rate,
        override val cause: Throwable? = null
    ) : CurrencyRateFailure(message = message, cause = cause)

    class DeleteCurrencyRateLocalError(
        @StringRes override val message: Int = R.string.error_local_delete_currency_rate,
        override val cause: Throwable? = null
    ) : CurrencyRateFailure(message = message, cause = cause)

    class DeleteCurrencyRateRemoteError(
        @StringRes override val message: Int = R.string.error_remote_delete_currency_rate,
        override val cause: Throwable? = null
    ) : CurrencyRateFailure(message = message, cause = cause)

    class NonExistentCurrencyRateDataMemoryError(
        @StringRes override val message: Int = R.string.error_memory_non_existent_currency_rate_data,
        override val cause: Throwable? = null
    ) : CurrencyRateFailure(message = message, cause = cause)

    class NonExistentCurrencyRateDataLocalError(
        @StringRes override val message: Int = R.string.error_local_non_existent_currency_rate_data,
        override val cause: Throwable? = null
    ) : CurrencyRateFailure(message = message, cause = cause)

    class NonExistentCurrencyRateDataRemoteError(
        @StringRes override val message: Int = R.string.error_remote_non_existent_currency_rate_data,
        override val cause: Throwable? = null
    ) : CurrencyRateFailure(message = message, cause = cause)

    // For Repositories
    class GetCurrencyRateRepositoryError(
        @StringRes override val message: Int = R.string.error_repository_get_currency_rate,
        override val cause: Throwable? = null
    ) : CurrencyRateFailure(message = message, cause = cause)

    class InsertCurrencyRateRepositoryError(
        @StringRes override val message: Int = R.string.error_repository_insert_currency_rate,
        override val cause: Throwable? = null
    ) : CurrencyRateFailure(message = message, cause = cause)

    class DeleteCurrencyRateRepositoryError(
        @StringRes override val message: Int = R.string.error_repository_delete_currency_rate,
        override val cause: Throwable? = null
    ) : CurrencyRateFailure(message = message, cause = cause)

    class CurrencyRateListNotAvailableRepositoryError(
        @StringRes override val message: Int = R.string.error_repository_currency_rate_list_not_available,
        override val cause: Throwable? = null
    ) : CurrencyRateFailure(message = message, cause = cause)
}