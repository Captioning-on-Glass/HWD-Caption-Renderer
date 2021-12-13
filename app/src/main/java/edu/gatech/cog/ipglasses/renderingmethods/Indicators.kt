package edu.gatech.cog.ipglasses.renderingmethods

import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import edu.gatech.cog.ipglasses.R
import edu.gatech.cog.ipglasses.Speakers
import edu.gatech.cog.ipglasses.ui.theme.IPGlassesTheme

val JURORS_MAP = mapOf(
    Speakers.JUROR_C to 0,
    Speakers.JUROR_B to 1,
    Speakers.JUROR_A to 2,
    Speakers.JURY_FOREMAN to 3
)

@Preview(showBackground = false, widthDp = DISPLAY_WIDTH, heightDp = DISPLAY_HEIGHT)
@Composable
fun IndicatorsPreview() {
    val focusedSpeaker = Speakers.JUROR_C
    val currentSpeaker = Speakers.JURY_FOREMAN
    IPGlassesTheme {
        // A surface container using the 'background' color from the theme
        Surface(
            modifier = Modifier.fillMaxSize()
        ) {
            Indicators(focusedSpeaker, currentSpeaker)
        }
    }
}

@Composable
fun Indicators(focusedSpeaker: String?, currentSpeaker: String) {
    if (focusedSpeaker == null) {
        return
    }
    if (focusedSpeaker == currentSpeaker) {
        return
    }
    val shouldPointLeft = JURORS_MAP[currentSpeaker]!! < JURORS_MAP[focusedSpeaker]!!
    Box(modifier = Modifier.fillMaxWidth()) {
        if (shouldPointLeft) {
            Icon(
                painter = painterResource(id = R.drawable.ic_baseline_arrow_left_24),
                contentDescription = "Look left",
                modifier = Modifier
                    .size(108.dp)
                    .align(Alignment.TopStart)
                    .padding(8.dp)
            )
        } else {
            Icon(
                painter = painterResource(id = R.drawable.ic_baseline_arrow_right_24),
                contentDescription = "Look right",
                modifier = Modifier
                    .size(108.dp)
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
            )
        }
    }
}