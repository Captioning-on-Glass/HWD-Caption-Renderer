package edu.gatech.cog.ipglasses

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import edu.gatech.cog.CaptionMessage
import edu.gatech.cog.Juror
import java.util.*
import kotlin.Comparator
import kotlin.collections.ArrayList


private val TAG = CaptioningViewModel::class.java.simpleName


/**
 * The [ViewModel] for captions. This ViewModel serves as the single source of truth for what should be rendered on the user's display at any given time.
 */
class CaptioningViewModel : ViewModel() {

    private class CaptionMessageComparator : Comparator<CaptionMessage> {
        // When combined, (messageId, chunkId) uniquely identifies a piece of text.
        override fun compare(o1: CaptionMessage, o2: CaptionMessage): Int {
            if (o1.messageId != o2.messageId) {
                return o1.messageId.compareTo(o2.messageId)
            }
            return o1.chunkId.compareTo(o2.chunkId)
        }
    }

    private val comparator = CaptionMessageComparator()
    val globalCaptionMessages: SortedSet<CaptionMessage> = sortedSetOf(comparator)
    val currentText: MutableState<String> = mutableStateOf("")
    var renderingMethodToUse: Int = -1

    private fun updateGlobalCaptions(captionMessage: CaptionMessage) {
        globalCaptionMessages.add(captionMessage)
        val sortedMessagesMap = globalCaptionMessages.groupBy { it.messageId }
            .toSortedMap() // Group all the captions we have so far by messageId
        currentText.value =
            sortedMessagesMap.values.map { list -> list.joinToString(" ") { message -> message.text.toString() } }
                .joinToString("\n")
    }

    fun addMessage(captionMessage: CaptionMessage) {
        if (renderingMethodToUse == -1) {
            Log.w(TAG, "No rendering method was selected! Discarding this caption.")
            return
        }
        // To minimize impact on GC/performance, we want to minimize the amount of allocations/deallocations
        // we're doing. We can accomplish this by having the rendering method set ahead of time,
        // and running non-performant code when necessary.
        when (renderingMethodToUse) {
            Renderers.LIVE_TRANSCRIBE_SIMULATION -> updateGlobalCaptions(captionMessage)
            else -> {
            }
        }
    }
}