// automatically generated by the FlatBuffers compiler, do not modify

package edu.gatech.cog.ipglasses.cog;

import java.nio.*
import kotlin.math.sign
import com.google.flatbuffers.*

@Suppress("unused")
class CaptionMessage : Table() {

    fun __init(_i: Int, _bb: ByteBuffer)  {
        __reset(_i, _bb)
    }
    fun __assign(_i: Int, _bb: ByteBuffer) : CaptionMessage {
        __init(_i, _bb)
        return this
    }
    val text : String?
        get() {
            val o = __offset(4)
            return if (o != 0) __string(o + bb_pos) else null
        }
    val textAsByteBuffer : ByteBuffer get() = __vector_as_bytebuffer(4, 1)
    fun textInByteBuffer(_bb: ByteBuffer) : ByteBuffer = __vector_in_bytebuffer(_bb, 4, 1)
    val speakerId : Byte
        get() {
            val o = __offset(6)
            return if(o != 0) bb.get(o + bb_pos) else 0
        }
    val focusedId : Byte
        get() {
            val o = __offset(8)
            return if(o != 0) bb.get(o + bb_pos) else 0
        }
    val messageId : Int
        get() {
            val o = __offset(10)
            return if(o != 0) bb.getInt(o + bb_pos) else 0
        }
    val chunkId : Int
        get() {
            val o = __offset(12)
            return if(o != 0) bb.getInt(o + bb_pos) else 0
        }
    companion object {
        fun validateVersion() = Constants.FLATBUFFERS_2_0_0()
        fun getRootAsCaptionMessage(_bb: ByteBuffer): CaptionMessage = getRootAsCaptionMessage(_bb, CaptionMessage())
        fun getRootAsCaptionMessage(_bb: ByteBuffer, obj: CaptionMessage): CaptionMessage {
            _bb.order(ByteOrder.LITTLE_ENDIAN)
            return (obj.__assign(_bb.getInt(_bb.position()) + _bb.position(), _bb))
        }
        fun createCaptionMessage(builder: FlatBufferBuilder, textOffset: Int, speakerId: Byte, focusedId: Byte, messageId: Int, chunkId: Int) : Int {
            builder.startTable(5)
            addChunkId(builder, chunkId)
            addMessageId(builder, messageId)
            addText(builder, textOffset)
            addFocusedId(builder, focusedId)
            addSpeakerId(builder, speakerId)
            return endCaptionMessage(builder)
        }
        fun startCaptionMessage(builder: FlatBufferBuilder) = builder.startTable(5)
        fun addText(builder: FlatBufferBuilder, text: Int) = builder.addOffset(0, text, 0)
        fun addSpeakerId(builder: FlatBufferBuilder, speakerId: Byte) = builder.addByte(1, speakerId, 0)
        fun addFocusedId(builder: FlatBufferBuilder, focusedId: Byte) = builder.addByte(2, focusedId, 0)
        fun addMessageId(builder: FlatBufferBuilder, messageId: Int) = builder.addInt(3, messageId, 0)
        fun addChunkId(builder: FlatBufferBuilder, chunkId: Int) = builder.addInt(4, chunkId, 0)
        fun endCaptionMessage(builder: FlatBufferBuilder) : Int {
            val o = builder.endTable()
            return o
        }
        fun finishCaptionMessageBuffer(builder: FlatBufferBuilder, offset: Int) = builder.finish(offset)
        fun finishSizePrefixedCaptionMessageBuffer(builder: FlatBufferBuilder, offset: Int) = builder.finishSizePrefixed(offset)
    }
}
