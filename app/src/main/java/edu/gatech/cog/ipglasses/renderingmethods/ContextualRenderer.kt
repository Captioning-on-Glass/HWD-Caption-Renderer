package edu.gatech.cog.ipglasses.renderingmethods

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import edu.gatech.cog.ipglasses.CaptioningViewModel

@Composable
fun ContextualRenderer(viewModel: CaptioningViewModel) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        Text(text = viewModel.caption.value, color = androidx.compose.ui.graphics.Color.Green)
    }
}