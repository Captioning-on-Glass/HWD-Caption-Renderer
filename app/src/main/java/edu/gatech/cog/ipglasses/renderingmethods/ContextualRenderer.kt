package edu.gatech.cog.ipglasses.renderingmethods

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import edu.gatech.cog.ipglasses.CaptioningViewModel


/**
 * Renders the currently looked-at juror's captions in a primary position, but also renders a stream of captions in the top right corner.
 * TODO: Create a second text in the top right that shows all the words being spoken. Requires update to captioning server transmission process.
 * @param viewModel: The [CaptioningViewModel] to serve as the single source of truth for this rendering method.
 */
@Composable
fun ContextualRenderer(viewModel: CaptioningViewModel) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        Text(
            text = viewModel.globalCaptionMessages.value.joinToString(" ") { it.text },
            color = androidx.compose.ui.graphics.Color.Green
        )
    }
}