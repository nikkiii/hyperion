package org.hyperion.fileserver;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.logging.Logger;

import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;
import org.hyperion.fileserver.UpdateSession.Type;

/**
 * <p>An implementation of the JAGGRAB and HTTP server.</p>
 * 
 * <p>A lot of the work in the JAGGRAB server is based on some code
 * Miss Silabsoft re-released, originally authored by Winterlove.</p>
 * @author Graham Edgecombe
 *
 */
public class FileServer {
	
	/**
	 * The HTTP port to listen on.
	 */
	public static final int HTTP_PORT = 8080;
	
	/**
	 * The JAGGRAB port to listen on.
	 */
	public static final int JAGGRAB_PORT = 43595;
	
	/**
	 * Logger instance.
	 */
	private static final Logger logger = Logger.getLogger(FileServer.class.getName());
	
	/**
	 * The <code>IoAcceptor</code> instance.
	 */
	private final IoAcceptor jaggrabAcceptor = new NioSocketAcceptor();
	
	/**
	 * The <code>IoAcceptor</code> instance.
	 */
	private final IoAcceptor httpAcceptor = new NioSocketAcceptor();

	/**
	 * Creates the jaggrab server.
	 */
	public FileServer() {
		jaggrabAcceptor.setHandler(new ConnectionHandler(Type.JAGGRAB));
		httpAcceptor.setHandler(new ConnectionHandler(Type.HTTP));
	}
	
	/**
	 * Binds the server to the ports.
	 * @return The server instance, for chaining.
	 * @throws IOException 
	 */
	public FileServer bind() throws IOException {
		logger.info("Binding to port : " + JAGGRAB_PORT + "...");
		jaggrabAcceptor.bind(new InetSocketAddress(JAGGRAB_PORT));
		logger.info("Binding to port : " + HTTP_PORT + "...");
		httpAcceptor.bind(new InetSocketAddress(HTTP_PORT));
		return this;
	}

	/**
	 * Starts the jaggrab server.
	 */
	public void start() {
		logger.info("Ready");
	}

}
