package edu.gatech.cog.ipglasses

import com.google.gson.annotations.SerializedName


data class CaptionMessage(
    @SerializedName("message_id") val messageId: Int,
    @SerializedName("chunk_id") val chunkId: Int,
    val text: String,
    @SerializedName("speaker_id") val speakerId: String,
    @SerializedName("focused_id") val focusedId: String?

)
