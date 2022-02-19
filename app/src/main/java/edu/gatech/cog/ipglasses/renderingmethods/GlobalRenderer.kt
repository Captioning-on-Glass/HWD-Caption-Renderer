package edu.gatech.cog.ipglasses.renderingmethods

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.datasource.LoremIpsum
import edu.gatech.cog.ipglasses.CaptionMessage
import edu.gatech.cog.ipglasses.CaptioningViewModel
import edu.gatech.cog.ipglasses.Renderers
import edu.gatech.cog.ipglasses.Speakers
import edu.gatech.cog.ipglasses.ui.theme.IPGlassesTheme


private const val TAG = "GlobalRenderer"

@Preview(showBackground = false, widthDp = DISPLAY_WIDTH, heightDp = DISPLAY_HEIGHT)
@Composable
fun GlobalPreview() {
    val viewModel = CaptioningViewModel()
    viewModel.renderingMethodToUse = Renderers.GLOBAL_ONLY
    val lipsum = LoremIpsum()
    for ((i, chunk) in lipsum.values.first().split("\\s+".toRegex()).withIndex()) {
        viewModel.addMessage(
            CaptionMessage(
                messageId = i,
                chunkId = 0,
                text = chunk,
                speakerId = Speakers.JUROR_A,
                focusedId = Speakers.JUROR_B
            )
        )
    }

    IPGlassesTheme {
        // A surface container using the 'background' color from the theme
        Surface(
            modifier = Modifier.fillMaxSize()
        ) {
            GlobalRenderer(viewModel)
        }
    }
}

/**
 * Renders all spoken text.
 * @param viewModel: The [CaptioningViewModel] to use as a single source of truth for captions.
 */
@Composable
fun GlobalRenderer(viewModel: CaptioningViewModel) {
    val globalCaptionMessages = viewModel.globalCaptionMessages.value
    val textToDisplay = globalCaptionMessages.joinToString(" ") { message -> message.text }
    Box(
        modifier = Modifier.fillMaxHeight().fillMaxWidth(0.8f)
    ) {
        LimitedText(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .fillMaxWidth(0.8f),
            maxBottomLines = MAX_LINES,
            text = textToDisplay,
        )
    }
}
