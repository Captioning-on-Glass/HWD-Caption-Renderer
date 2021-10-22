package edu.gatech.cog.ipglasses.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorPalette = darkColors(
    primary = Purple200,
    primaryVariant = Purple700,
    secondary = Teal200,
    background = Color.Black,
    onPrimary = Color.White,
    onSurface = Color.White,
)

/* Other default colors to override
background = Color.White,
surface = Color.White,
onPrimary = Color.White,
onSecondary = Color.Black,
onBackground = Color.Black,
onSurface = Color.Black,
*/

@Composable
fun IPGlassesTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable() () -> Unit) {
    val colors = DarkColorPalette

    MaterialTheme(
        colors = colors,
        typography = Typography,
        shapes = Shapes,
        content = content,
    )
}