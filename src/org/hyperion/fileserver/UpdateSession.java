package org.hyperion.fileserver;

import java.nio.ByteBuffer;
import java.util.logging.Logger;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.future.IoFuture;
import org.apache.mina.core.future.IoFutureListener;
import org.apache.mina.core.session.IoSession;

/**
 * Represents a single update session.
 * @author Graham Edgecombe
 *
 */
public class UpdateSession {
	
	/**
	 * Logger instance.
	 */
	private static final Logger logger = Logger.getLogger(UpdateSession.class.getName());
	
	/**
	 * An enum which describes the type of session.
	 * @author Graham Edgecombe
	 *
	 */
	public enum Type {
		
		/**
		 * A plain HTTP session (which the loader will fall back to if port 443
		 * cannot be used).
		 */
		HTTP,
		
		/**
		 * A JAGGRAB session (which is the primary choice of the loader).
		 */
		JAGGRAB;
	}

	/**
	 * The <code>IoSession</code> we are serving.
	 */
	private IoSession session;
	
	/**
	 * The type of session we are.
	 */
	private Type type;
	
	/**
	 * The request we are serving.
	 */
	private Request request;
	
	/**
	 * Creates the update session.
	 * @param type The type of session.
	 * @param session The <code>IoSession</code>.
	 */
	public UpdateSession(Type type, IoSession session) {
		this.type = type;
		this.session = session;
	}

	/**
	 * Reads a line of input data.
	 * @param line The line.
	 */
	public void readLine(String line) {
		if(request == null) {
			switch(type) {
			case JAGGRAB:
				readJaggrabPath(line);
				break;
			case HTTP:
				readHttpPath(line);
				break;
			}
		} else {
			if(type == Type.HTTP) {
				if(line.length() == 0) {
					serve();
				}
			}
		}
	}

	/**
	 * Servers the requested file.
	 */
	private void serve() {
		if(request == null) {
			session.close(false);
			return;
		}
		logger.fine("Serving " + type + " request : " + request.getPath());
		Response resp = RequestHandler.handle(request);
		if(resp == null) {
			session.close(false);
			return;
		}
		
		StringBuilder header = new StringBuilder();
		if(type == Type.HTTP) {
			header.append("HTTP/1.0 200 OK\r\n");
			header.append("Content-Length: ").append(resp.getFileData().remaining()).append("\r\n");
			header.append("Connection: close\r\n");
			header.append("Server: JaGeX/3.1\r\n");
			header.append("Content-Type: " + resp.getMimeType() + "\r\n");
			header.append("\r\n");
		}
		byte[] headerBytes = header.toString().getBytes();
		
		ByteBuffer bb = resp.getFileData();
		IoBuffer ib = IoBuffer.allocate(bb.remaining() + headerBytes.length);
		ib.put(headerBytes).put(bb);
		ib.flip();
		session.write(ib).addListener(new IoFutureListener<IoFuture>() {
			@Override
			public void operationComplete(IoFuture arg0) {
				session.close(false);
			}
		});
	}
	
	/**
	 * Reads the path from a HTTP request line.
	 * @param line The request line.
	 */
	private void readHttpPath(String line) {
		String[] parts = line.split(" ");
		if(parts.length != 3) {
			session.close(false);
		} else {
			request = new Request(parts[1].trim());
		}
	}

	/**
	 * Reads the path from a JAGGRAB request line.
	 * @param line The request line.
	 */
	private void readJaggrabPath(String line) {
		final String START = "JAGGRAB ";
		if(line.startsWith(START)) {
			request = new Request(line.substring(START.length()).trim());
		} else {
			session.close(false);
		}
		serve();
	}
	
}
