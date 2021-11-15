package edu.gatech.cog.ipglasses.renderingmethods

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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


/**
 * Renders the currently looked-at juror's captions in a primary position, but also renders a stream of captions in the top right corner.
 * @param viewModel: The [CaptioningViewModel] to serve as the single source of truth for this rendering method.
 */
@Composable
fun FocusedSpeakerAndGlobalRenderer(viewModel: CaptioningViewModel) {
    Box(
        modifier = Modifier
            .padding(30.dp)
            .fillMaxSize()
    ) {
        LimitedText(
            modifier = Modifier.align(Alignment.TopEnd).width(240.dp),
            maxBottomLines = MAX_LINES,
            fontSize = 24.sp,
            text = viewModel.globalCaptionMessages.value.joinToString(" ") { message -> message.text },
            color = Color.White,
        )
        LimitedText(
            modifier = Modifier.align(Alignment.BottomStart),
            maxBottomLines = MAX_LINES,
            fontSize = 28.sp,
            text = viewModel.currentFocusedSpeakerCaptionMessages.value.joinToString(" ") { message -> message.text },
            color = Color.White
        )
    }
}

@Preview(showBackground = false, widthDp = 480, heightDp = 480)
@Composable
fun FocusedSpeakerAndGlobalPreview() {
    val viewModel = CaptioningViewModel()
    viewModel.renderingMethodToUse = Renderers.FOCUSED_SPEAKER_AND_GLOBAL
    val lipsum = LoremIpsum()
    for ((i, word) in lipsum.values.first().split(" ").withIndex()) {
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
            FocusedSpeakerAndGlobalRenderer(viewModel)
        }
    }
}