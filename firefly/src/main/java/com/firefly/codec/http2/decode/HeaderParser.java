package com.firefly.codec.http2.decode;

import java.nio.ByteBuffer;

import com.firefly.codec.http2.frame.Frame;

/**
 * <p>
 * The parser for the frame header of HTTP/2 frames.
 * </p>
 *
 * @see Parser
 */
public class HeaderParser {
	private State state = State.LENGTH;
	private int cursor;

	private int length;
	private int type;
	private int flags;
	private int streamId;

	protected void reset() {
		state = State.LENGTH;
		cursor = 0;
		
		length = 0;
		type = 0;
		flags = 0;
		streamId = 0;
	}

	/**
	 * <p>
	 * Parses the header bytes in the given {@code buffer}; only the header
	 * bytes are consumed, therefore when this method returns, the buffer may
	 * contain unconsumed bytes.
	 * </p>
	 *
	 * @param buffer
	 *            the buffer to parse
	 * @return true if the whole header bytes were parsed, false if not enough
	 *         header bytes were present in the buffer
	 */
	public boolean parse(ByteBuffer buffer) {
		while (buffer.hasRemaining()) {
			switch (state) {
			case LENGTH: {
				int octet = buffer.get() & 0xFF;
				length = (length << 8) + octet;
				if (++cursor == 3) {
					length &= Frame.MAX_MAX_LENGTH;
					state = State.TYPE;
				}
				break;
			}
			case TYPE: {
				type = buffer.get() & 0xFF;
				state = State.FLAGS;
				break;
			}
			case FLAGS: {
				flags = buffer.get() & 0xFF;
				state = State.STREAM_ID;
				break;
			}
			case STREAM_ID: {
				if (buffer.remaining() >= 4) {
					streamId = buffer.getInt();
					// Most significant bit MUST be ignored as per specification.
					streamId &= 0x7F_FF_FF_FF;
					return true;
				} else {
					state = State.STREAM_ID_BYTES;
					cursor = 4;
				}
				break;
			}
			case STREAM_ID_BYTES: {
				int currByte = buffer.get() & 0xFF;
				--cursor;
				streamId += currByte << (8 * cursor);
				if (cursor == 0) {
					// Most significant bit MUST be ignored as per specification.
					streamId &= 0x7F_FF_FF_FF;
					return true;
				}
				break;
			}
			default: {
				throw new IllegalStateException();
			}
			}
		}
		return false;
	}

	public int getLength() {
		return length;
	}

	public int getFrameType() {
		return type;
	}

	public boolean hasFlag(int bit) {
		return (flags & bit) == bit;
	}

	public int getStreamId() {
		return streamId;
	}

	private enum State {
		LENGTH, TYPE, FLAGS, STREAM_ID, STREAM_ID_BYTES
	}
}
