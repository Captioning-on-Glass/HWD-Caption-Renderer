package edu.gatech.cog.ipglasses

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

/**
 * The [ViewModel] for captions. This ViewModel serves as the single source of truth for what should be rendered on the user's display at any given time.
 */
class CaptioningViewModel : ViewModel() {
    var caption =
        mutableStateOf("") // "mutableStateOf" tells all @Composables to watch for changes to this variable's value.
        private set

    /**
     * Appends the given text to the caption.
     */
    fun appendText(newText: String) {
        caption.value += " $newText"
    }

    /**
     * Replaces the caption's current text with new text.
     */
    fun replaceText(newText: String) {
        caption.value = newText
    }

    /**
     * Clears the caption's current text.
     */
    fun clearText() {
        caption.value = ""
    }
}