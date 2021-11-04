package edu.gatech.cog.ipglasses.renderingmethods

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import edu.gatech.cog.ipglasses.CaptionMessage
import edu.gatech.cog.ipglasses.CaptioningViewModel
import edu.gatech.cog.ipglasses.ui.theme.IPGlassesTheme
import kotlinx.coroutines.launch


private const val TAG = "DefaultRenderer"

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    val viewModel = CaptioningViewModel()
    viewModel.renderingMethodToUse = 1
    viewModel.addMessage(
        CaptionMessage(
            messageId = 0,
            chunkId = 0,
            text = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Condimentum id venenatis a condimentum vitae sapien pellentesque habitant morbi. Ac orci phasellus egestas tellus.",
            speakerId = "juror-a",
            focusedId = "juror-a"
        )
    )
    IPGlassesTheme {
        // A surface container using the 'background' color from the theme
        Surface(
            modifier = Modifier.fillMaxSize(),
        ) {
            DefaultRenderer(viewModel)
        }
    }
}

/**
 * Renders the currently-focused juror's words in  a primary position.
 * @param viewModel: The [CaptioningViewModel] to use as a single source of truth for captions.
 */
@Composable
fun DefaultRenderer(viewModel: CaptioningViewModel) {
    Box(
        contentAlignment = Alignment.BottomStart,
        modifier = Modifier
            .padding(30.dp)
            .fillMaxSize()
            .wrapContentSize(Alignment.BottomStart)
    ) {
        Text(
            fontSize = 20.sp,
            text = viewModel.currentFocusedSpeakerCaptionMessages.value.joinToString(" ") { message -> message.text },
            color = Color.White,
        )
    }
}
