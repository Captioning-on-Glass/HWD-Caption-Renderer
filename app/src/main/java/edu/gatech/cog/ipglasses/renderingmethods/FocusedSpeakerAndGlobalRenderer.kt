package edu.gatech.cog.ipglasses.renderingmethods

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import edu.gatech.cog.ipglasses.CaptionMessage
import edu.gatech.cog.ipglasses.CaptioningViewModel
import edu.gatech.cog.ipglasses.Renderers
import edu.gatech.cog.ipglasses.ui.theme.IPGlassesTheme


/**
 * Renders the currently looked-at juror's captions in a primary position, but also renders a stream of captions in the top right corner.
 * TODO: Create a second text in the top right that shows all the words being spoken. Requires update to captioning server transmission process.
 * @param viewModel: The [CaptioningViewModel] to serve as the single source of truth for this rendering method.
 */
@Composable
fun FocusedSpeakerAndGlobalRenderer(viewModel: CaptioningViewModel) {
    Box(
        modifier = Modifier
            .padding(30.dp)
            .fillMaxSize()
    ) {
        Text(
            modifier = Modifier.align(Alignment.BottomStart),
            fontSize = 28.sp,
            text = viewModel.currentFocusedSpeakerCaptionMessages.value.joinToString(" ") { message -> message.text },
            color = Color.White,
        )
        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .fillMaxWidth()
                .height(320.dp)
                .background(
                    Color.Black
                )
        )
    }
}

@Preview(showBackground = false, widthDp = 480, heightDp = 480)
@Composable
fun FocusedSpeakerAndGlobalPreview() {
    val viewModel = CaptioningViewModel()
    viewModel.renderingMethodToUse = Renderers.FOCUSED_SPEAKER_AND_GLOBAL
    viewModel.addMessage(
        CaptionMessage(
            messageId = 0,
            chunkId = 0,
            text = "Lorem ipsum dolor sit amet.",
            speakerId = "juror-a",
            focusedId = "juror-a"
        )
    )
    IPGlassesTheme {
        // A surface container using the 'background' color from the theme
        Surface(
            modifier = Modifier.fillMaxSize()
        ) {
            FocusedSpeakerAndGlobalRenderer(viewModel)
        }
    }
}