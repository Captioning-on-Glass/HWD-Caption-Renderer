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
import edu.gatech.cog.ipglasses.Speakers
import edu.gatech.cog.ipglasses.cog.CaptionMessage
import edu.gatech.cog.ipglasses.cog.Juror
import edu.gatech.cog.ipglasses.ui.theme.IPGlassesTheme


private const val TAG = "WhoSaidWhatRenderer"

@Preview(showBackground = false, widthDp = DISPLAY_WIDTH, heightDp = DISPLAY_HEIGHT)
@Composable
fun WhoSaidWhatPreview() {
    val viewModel = CaptioningViewModel()
    viewModel.renderingMethodToUse = Renderers.WHO_SAID_WHAT
    val lipsum = LoremIpsum(10)
    val jurorIds: List<Byte> =
        listOf(Juror.JurorA, Juror.JurorB, Juror.JurorC, Juror.JuryForeman)
    for ((i, chunk) in lipsum.values.take(4).iterator().withIndex()) {
        val builder = FlatBufferBuilder(1024)
        val text = builder.createString(chunk)
        val speakerId = jurorIds[i]
        val focusedId = jurorIds[i]
        val captionMessageOffset = CaptionMessage.createCaptionMessage(builder, text, speakerId, focusedId, i, 0)
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
            WhoSaidWhatRenderer(viewModel)
        }
    }
}

/**
 * Renders all spoken text, labeling each chunk with who said what.
 * @param viewModel: The [CaptioningViewModel] to use as a single source of truth for captions.
 */
@Composable
fun WhoSaidWhatRenderer(viewModel: CaptioningViewModel) {
    val globalCaptionMessages = viewModel.globalCaptionMessages.value
    val textToDisplay = if (globalCaptionMessages.isEmpty()) {
        ""
    } else {
        val sortedMessagesMap = globalCaptionMessages.groupBy { it.messageId() }
            .toSortedMap() // Group all the captions we have so far by messageId
        val latestMessage: List<CaptionMessage> =
            sortedMessagesMap[sortedMessagesMap.lastKey()]!!
        val speakerName = when (latestMessage.first().speakerId()) {
            Juror.JurorA -> "Juror A"
            Juror.JurorB -> "Juror B"
            Juror.JurorC -> "Juror C"
            Juror.JuryForeman -> "Jury Foreman"
            else -> ""
        }
        latestMessage.sortedBy { captionMessage -> captionMessage.chunkId() }
            .joinToString(" ", prefix = "$speakerName: ") { message -> message.text() }

    }
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
            text = textToDisplay,
        )
    }
}
