import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import edu.gatech.cog.ipglasses.CaptioningViewModel
import edu.gatech.cog.ipglasses.renderingmethods.LimitedText
import edu.gatech.cog.ipglasses.renderingmethods.MAX_LINES

@Composable
fun LiveTranscribeRenderer(viewModel: CaptioningViewModel) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        LimitedText(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .fillMaxSize(),
            maxBottomLines = MAX_LINES,
            text = viewModel.currentText.value,
        )
    }
}