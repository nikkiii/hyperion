package org.hyperion.util;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.junit.Test;

public class TestStreams {

	@Test
	public void testWriteString() throws IOException {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		try {
			Streams.writeString(os, "Hello, World.");
			byte[] expected = new byte[] { 'H', 'e', 'l', 'l', 'o', ',', ' ', 'W', 'o', 'r', 'l', 'd', '.', 0 };
			assertArrayEquals(expected, os.toByteArray());
		} finally {
			os.close();
		}
	}

	@Test
	public void testReadString() throws IOException {
		ByteArrayInputStream is = new ByteArrayInputStream(new byte[] { 'H', 'e', 'l', 'l', 'o', ',', ' ', 'W', 'o', 'r', 'l', 'd', '.', 0 });
		try {
			assertEquals("Hello, World.", Streams.readString(is));
		} finally {
			is.close();
		}
	}

	@Test
	public void testWriteLine() throws IOException {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		try {
			Streams.writeLine(os, "Hello, World.");
			byte[] expected = new byte[] { 'H', 'e', 'l', 'l', 'o', ',', ' ', 'W', 'o', 'r', 'l', 'd', '.', 10 };
			assertArrayEquals(expected, os.toByteArray());
		} finally {
			os.close();
		}
	}

	@Test
	public void testReadLine() throws IOException {
		ByteArrayInputStream is = new ByteArrayInputStream(new byte[] { 'H', 'e', 'l', 'l', 'o', ',', ' ', 'W', 'o', 'r', 'l', 'd', '.', 10 });
		try {
			assertEquals("Hello, World.", Streams.readLine(is));
		} finally {
			is.close();
		}
	}

}
