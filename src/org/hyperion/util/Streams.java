package org.hyperion.util;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;

/**
 * A utility class that assists in dealing with <code>InputStream</code> and
 * <code>OutputStream</code> classes.
 * @author Graham Edgecombe
 *
 */
public class Streams {

	/**
	 * Writes a null-terminated string to the specified
	 * <code>OutputStream</code>.
	 * @param out The output stream.
	 * @param string The string.
	 * @throws IOException if an I/O error occurs, such as the stream closing.
	 */
	public static void writeString(OutputStream out, String string) throws IOException {
		for(char c : string.toCharArray()) {
			out.write((byte) c);
		}
		out.write(0);
	}
	
	/**
	 * Reads a RuneScape string from the specified
	 * <code>InputStream</code>.
	 * @param in The input stream.
	 * @return The string.
	 * @throws IOException if an I/O error occurs, such as the stream closing.
	 */
	public static String readRS2String(InputStream in) throws IOException {
		StringBuilder bldr = new StringBuilder();
		while(true) {
			int b = in.read();
			if(b == -1 || b == 10) {
				break;
			} else {
				bldr.append((char) ((byte) b));
			}
		}
		return bldr.toString();
	}

	/**
	 * Reads a null-terminated string from the specified
	 * <code>InputStream</code>.
	 * @param in The input stream.
	 * @return The string.
	 * @throws IOException if an I/O error occurs, such as the stream closing.
	 */
	public static String readString(InputStream in) throws IOException {
		StringBuilder bldr = new StringBuilder();
		byte b;
		while((b = (byte) in.read()) != 0) {
			bldr.append((char) b);
		}
		return bldr.toString();
	}
	
	/**
	 * Writes a line to the specified <code>OutputStream</code>.
	 * @param out The output stream.
	 * @param line The line.
	 * @throws IOException if an I/O error occurs, such as the stream closing.
	 */
	public static void writeLine(OutputStream out, String line) throws IOException {
		out.write((line + "\n").getBytes());
	}

	/**
	 * Reads a line from the specified <code>InputStream</code>.
	 * @param in The input stream.
	 * @return The line.
	 * @throws IOException if an I/O error occurs, such as the stream closing.
	 */
	public static String readLine(InputStream in) throws IOException {
		StringBuilder bldr = new StringBuilder();
		byte b;
		while((b = (byte) in.read()) != '\n') {
			bldr.append((char) b);
		}
		return bldr.toString().trim();
	}

}
