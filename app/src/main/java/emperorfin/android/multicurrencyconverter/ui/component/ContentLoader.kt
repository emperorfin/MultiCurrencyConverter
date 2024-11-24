package emperorfin.android.multicurrencyconverter.ui.component

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.spring
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember


private const val ANIMATABLE_INITIAL_VALUE_0: Float = 0f
private const val ANIMATABLE_TARGET_VALUE_0: Float = 0f
private const val ANIMATABLE_TARGET_VALUE_1: Float = 1f

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContentLoader(
    loading: Boolean,
    empty: Boolean,
    emptyContent: @Composable () -> Unit,
    loadingIndicator: @Composable () -> Unit,
    onRefresh: () -> Unit,
    content: @Composable () -> Unit
) {
    if (empty) {
        emptyContent()
    } else if (loading) {
        loadingIndicator()
    } else {

        val pullToRefreshState = remember {
            object : PullToRefreshState {
                private val anim = Animatable(ANIMATABLE_INITIAL_VALUE_0, Float.VectorConverter)

                override val distanceFraction
                    get() = anim.value

                override suspend fun animateToThreshold() {
                    anim.animateTo(
                        ANIMATABLE_TARGET_VALUE_1,
                        spring(dampingRatio = Spring.DampingRatioHighBouncy)
                    )
                }

                override suspend fun animateToHidden() {
                    anim.animateTo(ANIMATABLE_TARGET_VALUE_0)
                }

                override suspend fun snapTo(targetValue: Float) {
                    anim.snapTo(targetValue)
                }
            }
        }

        PullToRefreshBox(
            isRefreshing = loading,
            onRefresh = onRefresh,
            state = pullToRefreshState,
        ) {
            content()
        }
    }

}