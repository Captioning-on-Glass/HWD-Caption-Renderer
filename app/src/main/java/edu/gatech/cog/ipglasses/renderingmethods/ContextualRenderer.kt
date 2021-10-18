package edu.gatech.cog.ipglasses.renderingmethods

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun ContextualRenderer(caption: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        Text(text = caption, color = androidx.compose.ui.graphics.Color.Green)
    }
}
//
//@Preview(showBackground = true)
//@Composable
//fun DefaultPreview() {
//    IPGlassesTheme {
//        ContextualRenderer("Android")
//    }
//}
