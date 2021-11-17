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


private const val TAG = "WhoSaidWhatRenderer"

@Preview(showBackground = false, widthDp = 480, heightDp = 480)
@Composable
fun WhoSaidWhatPreview() {
    val viewModel = CaptioningViewModel()
    viewModel.renderingMethodToUse = Renderers.WHO_SAID_WHAT
    val lipsum = LoremIpsum(10)
    val jurorIds: List<String> =
        listOf(Speakers.JUROR_A, Speakers.JUROR_B, Speakers.JUROR_C, Speakers.JURY_FOREMAN)
    for ((i, chunk) in lipsum.values.take(4).iterator().withIndex()) {
        viewModel.addMessage(
            CaptionMessage(
                messageId = i,
                chunkId = 0,
                text = chunk,
                speakerId = jurorIds[i],
                focusedId = jurorIds[i]
            )
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
        val sortedMessagesMap = globalCaptionMessages.groupBy { it.messageId }
            .toSortedMap() // Group all the captions we have so far by messageId
        val latestMessage: List<CaptionMessage> =
            sortedMessagesMap[sortedMessagesMap.lastKey()]!!
        val speakerName = latestMessage.first().speakerId.split("-")
            .joinToString(" ") { word -> word.replaceFirstChar { it.uppercase() } }
        latestMessage.sortedBy { captionMessage -> captionMessage.chunkId }
            .joinToString(" ", prefix = "$speakerName: ") { message -> message.text }

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
