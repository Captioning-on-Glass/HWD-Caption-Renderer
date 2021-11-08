package edu.gatech.cog.ipglasses.renderingmethods

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.datasource.LoremIpsum
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import edu.gatech.cog.ipglasses.CaptionMessage
import edu.gatech.cog.ipglasses.CaptioningViewModel
import edu.gatech.cog.ipglasses.Renderers
import edu.gatech.cog.ipglasses.ui.theme.IPGlassesTheme


private const val TAG = "FocusedSpeakerOnlyRenderer"

@Preview(showBackground = false, widthDp = 480, heightDp = 480)
@Composable
fun FocusedSpeakerOnlyPreview() {
    val viewModel = CaptioningViewModel()
    viewModel.renderingMethodToUse = Renderers.FOCUSED_SPEAKER_ONLY
    val lipsum = LoremIpsum()
    for ((i, word) in lipsum.values.first().split(" ").withIndex()) {
        viewModel.addMessage(
            CaptionMessage(
                messageId = 0,
                chunkId = i,
                text = word,
                speakerId = "juror-a",
                focusedId = "juror-a"
            )
        )
    }
    IPGlassesTheme {
        // A surface container using the 'background' color from the theme
        Surface(
            modifier = Modifier.fillMaxSize()
        ) {
            FocusedSpeakerOnlyRenderer(viewModel)
        }
    }
}

/**
 * Renders the currently-focused juror's words in  a primary position.
 * @param viewModel: The [CaptioningViewModel] to use as a single source of truth for captions.
 */
@Composable
fun FocusedSpeakerOnlyRenderer(viewModel: CaptioningViewModel) {
    Box(
        modifier = Modifier
            .padding(30.dp)
            .fillMaxSize()
    ) {
        LimitedText(
            modifier = Modifier.align(Alignment.BottomStart),
            maxBottomLines = MAX_LINES,
            fontSize = 28.sp,
            text = viewModel.currentFocusedSpeakerCaptionMessages.value.joinToString(" ") { message -> message.text },
            color = Color.White,
        )
    }
}