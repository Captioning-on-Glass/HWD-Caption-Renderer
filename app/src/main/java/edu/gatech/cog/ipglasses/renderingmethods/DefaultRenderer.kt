package edu.gatech.cog.ipglasses.renderingmethods

import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import edu.gatech.cog.ipglasses.CaptioningViewModel
import edu.gatech.cog.ipglasses.ui.theme.IPGlassesTheme

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    val viewModel = CaptioningViewModel()
    viewModel.replaceText("Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Condimentum id venenatis a condimentum vitae sapien pellentesque habitant morbi. Ac orci phasellus egestas tellus.")
    IPGlassesTheme {
        DefaultRenderer(viewModel)
    }
}

/**
 * Renders the currently-focused juror's words in  a primary position.
 * @param viewModel: The [CaptioningViewModel] to use as a single source of truth for captions.
 */
@Composable
fun DefaultRenderer(viewModel: CaptioningViewModel) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .padding(30.dp)
            .fillMaxWidth()
            .fillMaxHeight()
            .wrapContentSize(Alignment.Center)
    ) {
        Text(text = viewModel.caption.value)
    }
}
