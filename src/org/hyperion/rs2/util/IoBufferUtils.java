package org.hyperion.rs2.util;

import org.apache.mina.core.buffer.IoBuffer;

/**
 * A utility class for dealing with <code>IoBuffer</code>s.
 * @author Graham Edgecombe
 *
 */
public class IoBufferUtils {
	
	/**
	 * Reads a RuneScape string from a buffer.
	 * @param buf The buffer.
	 * @return The string.
	 */
	public static String getRS2String(IoBuffer buf) {
		StringBuilder bldr = new StringBuilder();
		byte b;
		while(buf.hasRemaining() && (b = buf.get()) != 10) {
			bldr.append((char) b);
		}
		return bldr.toString();
	}

	/**
	 * Writes a RuneScape string to a buffer.
	 * @param buf The buffer.
	 * @param string The string.
	 */
	public static void putRS2String(IoBuffer buf, String string) {
		for(char c : string.toCharArray()) {
			buf.put((byte) c);
		}
		buf.put((byte) 10);
	}

}
