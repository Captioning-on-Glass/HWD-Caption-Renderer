package edu.gatech.cog.ipglasses

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import edu.gatech.cog.ipglasses.Renderers.CONTEXTUAL_RENDERER
import edu.gatech.cog.ipglasses.Renderers.DEFAULT_RENDERER


private val TAG = CaptioningViewModel::class.java.simpleName

/**
 * The [ViewModel] for captions. This ViewModel serves as the single source of truth for what should be rendered on the user's display at any given time.
 */
class CaptioningViewModel : ViewModel() {
    // MutableState is not compatible with mutable objects like ArrayLists: if you modify the
    // ArrayList by mutating it, the UI won't update to match. So we have to do something really
    // dirty to mimic ArrayList functionality: we have to
    //      1. allocate a new immutable list of a new size (if appending/shrinking)
    //      2. copy all of our elements from the original immutable list into the new one
    //      3. Replace the memory location of the old immutable list with the new immutable list
    // This is EXTREMELY bad use of memory, and will hit the garbage collector pretty hard, but
    // I'm not sure how to get around it. It will have to do for now.
    // TODO: Rework this to not operate with CaptionMessages, but rather track something more
    //  granular, like changes in speakerId or focusedId instead.
    val currentFocusedSpeakerCaptionMessages: MutableState<List<CaptionMessage>> =
        mutableStateOf(listOf())
    val globalCaptionMessages: MutableState<List<CaptionMessage>> = mutableStateOf(listOf())
    var renderingMethodToUse: Int = -1

    private fun updateCurrentFocusedSpeakerCaptionMessages(captionMessage: CaptionMessage) {
        if (captionMessage.speakerId != captionMessage.focusedId) {
            // The speaker isn't being focused by the participant. Clear the list.
            currentFocusedSpeakerCaptionMessages.value = listOf()
            return
        }
        if (currentFocusedSpeakerCaptionMessages.value.isEmpty()) {
            currentFocusedSpeakerCaptionMessages.value += listOf(captionMessage)
            return
        } else {
            if (captionMessage.speakerId != currentFocusedSpeakerCaptionMessages.value[0].speakerId) {
                // If the speaker has changed but the participant is focusing on the new speaker,
                // we need to reset the list to one element.
                currentFocusedSpeakerCaptionMessages.value = listOf(captionMessage)
                return
            }
            currentFocusedSpeakerCaptionMessages.value += listOf(captionMessage)
        }
    }

    private fun updateGlobalCaptions(captionMessage: CaptionMessage) {
        // When combined, (messageId, chunkId) uniquely identifies a piece of text.
        // So, if our current list of global captions doesn't already contain a matching
        // (messageId, chunkId), this is a duplicate caption (caused by, say, the participant
        // looking away and back really quickly) and we don't want to add it to our
        // "global captions" list.
        if (globalCaptionMessages.value.none { it.messageId == captionMessage.messageId && it.chunkId == captionMessage.chunkId }) {
            globalCaptionMessages.value += listOf(captionMessage)
        }
    }

    fun addMessage(captionMessage: CaptionMessage) {
        if (renderingMethodToUse == -1) {
            Log.w(TAG, "No rendering method was selected! Discarding this caption.")
            return
        }
        // To minimize impact on GC/performance, we want to minimize the amount of allocations/deallocations
        // we're doing. We can accomplish this by having the rendering method set ahead of time,
        // and selectively running non-performant code when necessary.
        when (renderingMethodToUse) {
            DEFAULT_RENDERER -> updateCurrentFocusedSpeakerCaptionMessages(captionMessage)
            CONTEXTUAL_RENDERER -> {
                updateCurrentFocusedSpeakerCaptionMessages(captionMessage)
                updateGlobalCaptions(captionMessage)
            }
            else -> {
                // We don't want to break the application if the programmer forgets to set
                // the rendering method, so I guess we have to run all the intensive code.
                updateCurrentFocusedSpeakerCaptionMessages(captionMessage)
                updateGlobalCaptions(captionMessage)
            }
        }
    }
}