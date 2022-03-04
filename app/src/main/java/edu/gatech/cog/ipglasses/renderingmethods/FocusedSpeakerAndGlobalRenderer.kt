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
import edu.gatech.cog.ipglasses.ui.theme.Typography


/**
 * Renders the currently looked-at juror's captions in a primary position, but also renders a stream of captions in the top right corner.
 * @param viewModel: The [CaptioningViewModel] to serve as the single source of truth for this rendering method.
 */
@Composable
fun FocusedSpeakerAndGlobalRenderer(viewModel: CaptioningViewModel) {
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
            text = viewModel.globalCaptionMessages.value.joinToString(" ") { message -> message.text.toString() },
            style = Typography.body2
        )
        LimitedText(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth(),
            maxBottomLines = MAX_LINES,
            text = viewModel.currentFocusedSpeakerCaptionMessages.value.joinToString(" ") { message -> message.text.toString() },
            style = Typography.body1
        )
    }
}

@Preview(showBackground = false, widthDp = DISPLAY_WIDTH, heightDp = DISPLAY_HEIGHT)
@Composable
fun FocusedSpeakerAndGlobalPreview() {
    val viewModel = CaptioningViewModel()
    viewModel.renderingMethodToUse = Renderers.FOCUSED_SPEAKER_AND_GLOBAL
    val lipsum = LoremIpsum()
    for ((i, word) in lipsum.values.first().split("\\s+".toRegex()).withIndex()) {
        val builder = FlatBufferBuilder(1024)
        val text = builder.createString(word)
        val captionMessageOffset = CaptionMessage.createCaptionMessage(builder, text, Juror.JurorA, Juror.JurorA, 0, i)
        builder.finish(captionMessageOffset)
        val buf = builder.dataBuffer()
        val captionMessage = CaptionMessage.getRootAsCaptionMessage(buf)
        viewModel.addMessage(
            captionMessage = captionMessage
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