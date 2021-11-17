package edu.gatech.cog.ipglasses.renderingmethods

import androidx.compose.foundation.layout.*
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.datasource.LoremIpsum
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import edu.gatech.cog.ipglasses.CaptionMessage
import edu.gatech.cog.ipglasses.CaptioningViewModel
import edu.gatech.cog.ipglasses.Renderers
import edu.gatech.cog.ipglasses.Speakers
import edu.gatech.cog.ipglasses.ui.theme.IPGlassesTheme
import edu.gatech.cog.ipglasses.ui.theme.Typography


private const val TAG = "FocusedSpeakerRenderer"

@Preview(showBackground = false, widthDp = 480, heightDp = 480)
@Composable
fun FocusedSpeakerPreview() {
    val viewModel = CaptioningViewModel()
    viewModel.renderingMethodToUse = Renderers.FOCUSED_SPEAKER_ONLY
    val lipsum = LoremIpsum()
    for ((i, word) in lipsum.values.first().split("\\s+".toRegex()).withIndex()) {
        viewModel.addMessage(
            CaptionMessage(
                messageId = 0,
                chunkId = i,
                text = word,
                speakerId = Speakers.JUROR_A,
                focusedId = Speakers.JUROR_A
            )
        )
    }
    IPGlassesTheme {
        // A surface container using the 'background' color from the theme
        Surface(
            modifier = Modifier.fillMaxSize()
        ) {
            FocusedSpeakerRenderer(viewModel)
        }
    }
}

/**
 * Renders the currently-focused juror's words in  a primary position.
 * @param viewModel: The [CaptioningViewModel] to use as a single source of truth for captions.
 */
@Composable
fun FocusedSpeakerRenderer(viewModel: CaptioningViewModel) {
    Box(
        modifier = Modifier
            .width(480.dp)
            .height(480.dp)
    ) {
        LimitedText(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth(),
            maxBottomLines = MAX_LINES,
            text = viewModel.currentFocusedSpeakerCaptionMessages.value.joinToString(" ") { message -> message.text },
        )
    }
}