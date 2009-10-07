package org.hyperion.cache.util;

import java.nio.ByteBuffer;

/**
 * A utility class for byte buffers.
 * @author Graham Edgecombe
 *
 */
public class ByteBufferUtils {
	
	/**
	 * Gets a smart.
	 * @param buf The buffer.
	 * @return The smart.
	 */
	public static int getSmart(ByteBuffer buf) {
		int peek = buf.get(buf.position()) & 0xFF;
		if(peek < 128) {
			return buf.get() & 0xFF;
		} else {
			return (buf.getShort() & 0xFFFF) - 32768;
		}
	}

}
