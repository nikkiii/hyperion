package org.hyperion.fileserver;

import java.nio.ByteBuffer;

/**
 * Represents a response to either a JAGGRAB or HTTP request.
 * @author Graham Edgecombe
 *
 */
public class Response {
	
	/**
	 * The data in the file.
	 */
	private ByteBuffer fileData;
	
	/**
	 * The MIME type.
	 */
	private String mimeType;
	
	/**
	 * Creates the response.
	 * @param bytes The data.
	 * @param mimeType The MIME type.
	 */
	public Response(byte[] bytes, String mimeType) {
		ByteBuffer buf = ByteBuffer.allocate(bytes.length);
		buf.put(bytes);
		buf.flip();
		fileData = buf;
		this.mimeType = mimeType;
	}
	
	/**
	 * Creates the response.
	 * @param fileData The file data.
	 * @param mimeType The MIME type.
	 */
	public Response(ByteBuffer fileData, String mimeType) {
		this.fileData = fileData;
		this.mimeType = mimeType;
	}
	
	/**
	 * Gets the file data.
	 * @return The file dtaa.
	 */
	public ByteBuffer getFileData() {
		return fileData;
	}

	/**
	 * Gets the MIME type.
	 * @return The MIME type.
	 */
	public String getMimeType() {
		return mimeType;
	}

}
