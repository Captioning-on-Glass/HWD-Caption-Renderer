package edu.gatech.cog.ipglasses.renderingmethods

import androidx.compose.foundation.layout.*
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.datasource.LoremIpsum
import androidx.compose.ui.unit.dp
import com.google.flatbuffers.FlatBufferBuilder
import edu.gatech.cog.ipglasses.CaptioningViewModel
import edu.gatech.cog.ipglasses.Renderers
import edu.gatech.cog.ipglasses.cog.CaptionMessage
import edu.gatech.cog.ipglasses.cog.Juror
import edu.gatech.cog.ipglasses.ui.theme.IPGlassesTheme


private const val TAG = "FocusedSpeakerRenderer"

@Preview(showBackground = false, widthDp = DISPLAY_WIDTH, heightDp = DISPLAY_HEIGHT)
@Composable
fun FocusedSpeakerPreview() {
    val viewModel = CaptioningViewModel()
    viewModel.renderingMethodToUse = Renderers.FOCUSED_SPEAKER_ONLY
    val lipsum = LoremIpsum()

    for ((i, word) in lipsum.values.first().split("\\s+".toRegex()).withIndex()) {
        val builder = FlatBufferBuilder(1024)
        val text = builder.createString(word)
        val captionMessageOffset = CaptionMessage.createCaptionMessage(builder, text, Juror.JurorA, Juror.JurorA, 0, i)
        builder.finish(captionMessageOffset)
        val buf = builder.dataBuffer()
        val captionMessage = CaptionMessage.getRootAsCaptionMessage(buf)
        viewModel.addMessage(
captionMessage
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
            text = viewModel.currentFocusedSpeakerCaptionMessages.value.joinToString(" ") { message -> message.text() },
        )
    }
}