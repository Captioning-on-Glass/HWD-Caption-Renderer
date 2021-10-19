package edu.gatech.cog.ipglasses

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class CaptioningViewModel : ViewModel() {
    var caption = mutableStateOf("")
        private set

    fun appendText(newText: String) {
        caption.value += " $newText"
    }

    fun replaceCaption(newText: String) {
        caption.value = newText
    }

    fun clearText() {
        caption.value = ""
    }
}