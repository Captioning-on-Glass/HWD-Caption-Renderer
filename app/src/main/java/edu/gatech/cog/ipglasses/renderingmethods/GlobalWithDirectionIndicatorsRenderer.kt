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


private const val TAG = "GlobalWithDirectionIndicatorsRenderer"

@Preview(showBackground = false, widthDp = 480, heightDp = 480)
@Composable
fun GlobalWithDirectionIndicatorsPreview() {
    val viewModel = CaptioningViewModel()
    viewModel.renderingMethodToUse = Renderers.GLOBAL_WITH_DIRECTION_INDICATORS
    val lipsum = LoremIpsum()
    for ((i, word) in lipsum.values.first().split("\\s+".toRegex()).withIndex()) {
        viewModel.addMessage(
            CaptionMessage(
                messageId = 0,
                chunkId = i,
                text = word,
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
            GlobalWithDirectionIndicatorsRenderer(viewModel)
        }
    }
}

/**
 * Renders the currently-focused juror's words in a primary position. If the current speaker is not focused,
 * renders an indicator showing which direction the participant should turn their head to see the correct speaker.
 * @param viewModel: The [CaptioningViewModel] to use as a single source of truth for captions.
 */
@Composable
fun GlobalWithDirectionIndicatorsRenderer(viewModel: CaptioningViewModel) {
    Box(
        modifier = Modifier
            .width(480.dp)
            .height(480.dp)
    ) {
        LimitedText(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .width(240.dp),
            maxBottomLines = MAX_LINES,
            text = viewModel.globalCaptionMessages.value.joinToString(" ") { message -> message.text },
            style = Typography.body2
        )
        Box(modifier = Modifier
            .fillMaxWidth()
            .align(Alignment.BottomStart)) {
            Indicators(
                viewModel.currentFocusedId,
                viewModel.currentSpeakerId
            )
        }
    }
}