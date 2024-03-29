// automatically generated by the FlatBuffers compiler, do not modify

package edu.gatech.cog.ipglasses.cog;

import java.nio.*
import kotlin.math.sign
import com.google.flatbuffers.*

@Suppress("unused")
class OrientationMessage : Table() {

    fun __init(_i: Int, _bb: ByteBuffer)  {
        __reset(_i, _bb)
    }
    fun __assign(_i: Int, _bb: ByteBuffer) : OrientationMessage {
        __init(_i, _bb)
        return this
    }
    val azimuth : Float
        get() {
            val o = __offset(4)
            return if(o != 0) bb.getFloat(o + bb_pos) else 0.0f
        }
    val pitch : Float
        get() {
            val o = __offset(6)
            return if(o != 0) bb.getFloat(o + bb_pos) else 0.0f
        }
    val roll : Float
        get() {
            val o = __offset(8)
            return if(o != 0) bb.getFloat(o + bb_pos) else 0.0f
        }
    companion object {
        fun validateVersion() = Constants.FLATBUFFERS_2_0_0()
        fun getRootAsOrientationMessage(_bb: ByteBuffer): OrientationMessage = getRootAsOrientationMessage(_bb, OrientationMessage())
        fun getRootAsOrientationMessage(_bb: ByteBuffer, obj: OrientationMessage): OrientationMessage {
            _bb.order(ByteOrder.LITTLE_ENDIAN)
            return (obj.__assign(_bb.getInt(_bb.position()) + _bb.position(), _bb))
        }
        fun createOrientationMessage(builder: FlatBufferBuilder, azimuth: Float, pitch: Float, roll: Float) : Int {
            builder.startTable(3)
            addRoll(builder, roll)
            addPitch(builder, pitch)
            addAzimuth(builder, azimuth)
            return endOrientationMessage(builder)
        }
        fun startOrientationMessage(builder: FlatBufferBuilder) = builder.startTable(3)
        fun addAzimuth(builder: FlatBufferBuilder, azimuth: Float) = builder.addFloat(0, azimuth, 0.0)
        fun addPitch(builder: FlatBufferBuilder, pitch: Float) = builder.addFloat(1, pitch, 0.0)
        fun addRoll(builder: FlatBufferBuilder, roll: Float) = builder.addFloat(2, roll, 0.0)
        fun endOrientationMessage(builder: FlatBufferBuilder) : Int {
            val o = builder.endTable()
            return o
        }
        fun finishOrientationMessageBuffer(builder: FlatBufferBuilder, offset: Int) = builder.finish(offset)
        fun finishSizePrefixedOrientationMessageBuffer(builder: FlatBufferBuilder, offset: Int) = builder.finishSizePrefixed(offset)
    }
}
