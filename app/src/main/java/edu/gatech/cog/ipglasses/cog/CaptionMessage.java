// automatically generated by the FlatBuffers compiler, do not modify

package edu.gatech.cog.ipglasses.cog;

import java.nio.*;
import java.lang.*;
import java.util.*;
import com.google.flatbuffers.*;

@SuppressWarnings("unused")
public final class CaptionMessage extends Table {
  public static void ValidateVersion() { Constants.FLATBUFFERS_2_0_0(); }
  public static CaptionMessage getRootAsCaptionMessage(ByteBuffer _bb) { return getRootAsCaptionMessage(_bb, new CaptionMessage()); }
  public static CaptionMessage getRootAsCaptionMessage(ByteBuffer _bb, CaptionMessage obj) { _bb.order(ByteOrder.LITTLE_ENDIAN); return (obj.__assign(_bb.getInt(_bb.position()) + _bb.position(), _bb)); }
  public void __init(int _i, ByteBuffer _bb) { __reset(_i, _bb); }
  public CaptionMessage __assign(int _i, ByteBuffer _bb) { __init(_i, _bb); return this; }

  public String text() { int o = __offset(4); return o != 0 ? __string(o + bb_pos) : null; }
  public ByteBuffer textAsByteBuffer() { return __vector_as_bytebuffer(4, 1); }
  public ByteBuffer textInByteBuffer(ByteBuffer _bb) { return __vector_in_bytebuffer(_bb, 4, 1); }
  public byte speakerId() { int o = __offset(6); return o != 0 ? bb.get(o + bb_pos) : 0; }
  public byte focusedId() { int o = __offset(8); return o != 0 ? bb.get(o + bb_pos) : 0; }
  public int messageId() { int o = __offset(10); return o != 0 ? bb.getInt(o + bb_pos) : 0; }
  public int chunkId() { int o = __offset(12); return o != 0 ? bb.getInt(o + bb_pos) : 0; }

  public static int createCaptionMessage(FlatBufferBuilder builder,
      int textOffset,
      byte speakerId,
      byte focusedId,
      int messageId,
      int chunkId) {
    builder.startTable(5);
    CaptionMessage.addChunkId(builder, chunkId);
    CaptionMessage.addMessageId(builder, messageId);
    CaptionMessage.addText(builder, textOffset);
    CaptionMessage.addFocusedId(builder, focusedId);
    CaptionMessage.addSpeakerId(builder, speakerId);
    return CaptionMessage.endCaptionMessage(builder);
  }

  public static void startCaptionMessage(FlatBufferBuilder builder) { builder.startTable(5); }
  public static void addText(FlatBufferBuilder builder, int textOffset) { builder.addOffset(0, textOffset, 0); }
  public static void addSpeakerId(FlatBufferBuilder builder, byte speakerId) { builder.addByte(1, speakerId, 0); }
  public static void addFocusedId(FlatBufferBuilder builder, byte focusedId) { builder.addByte(2, focusedId, 0); }
  public static void addMessageId(FlatBufferBuilder builder, int messageId) { builder.addInt(3, messageId, 0); }
  public static void addChunkId(FlatBufferBuilder builder, int chunkId) { builder.addInt(4, chunkId, 0); }
  public static int endCaptionMessage(FlatBufferBuilder builder) {
    int o = builder.endTable();
    return o;
  }
  public static void finishCaptionMessageBuffer(FlatBufferBuilder builder, int offset) { builder.finish(offset); }
  public static void finishSizePrefixedCaptionMessageBuffer(FlatBufferBuilder builder, int offset) { builder.finishSizePrefixed(offset); }

  public static final class Vector extends BaseVector {
    public Vector __assign(int _vector, int _element_size, ByteBuffer _bb) { __reset(_vector, _element_size, _bb); return this; }

    public CaptionMessage get(int j) { return get(new CaptionMessage(), j); }
    public CaptionMessage get(CaptionMessage obj, int j) {  return obj.__assign(__indirect(__element(j), bb), bb); }
  }
}

