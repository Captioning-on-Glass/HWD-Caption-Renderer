package edu.gatech.cog.ipglasses.renderingmethods

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import edu.gatech.cog.ipglasses.ui.theme.IPGlassesTheme

@Composable
fun DefaultRenderer(caption: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        Text(text = caption)
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    IPGlassesTheme {
        DefaultRenderer("Android")
    }
}
