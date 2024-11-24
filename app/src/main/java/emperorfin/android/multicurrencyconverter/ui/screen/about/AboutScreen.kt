package emperorfin.android.multicurrencyconverter.ui.screen.about

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import emperorfin.android.multicurrencyconverter.R
import emperorfin.android.multicurrencyconverter.ui.component.AboutTopAppBar


@Composable
fun AboutScreen(
    modifier: Modifier = Modifier,
    openDrawer: () -> Unit,
) {

    val snackBarHostState = remember { SnackbarHostState() }

    Scaffold(
        snackbarHost = { SnackbarHost(snackBarHostState) },
        topBar = {
            AboutTopAppBar(
                openDrawer = openDrawer
            )
        },
        modifier = modifier.fillMaxSize(),
    ) { paddingValues ->

        Content(
            modifier = Modifier.padding(paddingValues)
        )

    }
}

@Composable
fun Content(modifier: Modifier = Modifier) {

    Column(
        modifier = modifier
            .padding(
                horizontal = dimensionResource(id = R.dimen.horizontal_margin),
                vertical = dimensionResource(id = R.dimen.horizontal_margin)
            )
    ) {
        Text(text = stringResource(R.string.text_this_is_just_a_simple_about_screen))
    }

}

@Preview()
@Composable
private fun AboutScreenPreview() {
    AboutScreen(
        openDrawer = {}
    )
}