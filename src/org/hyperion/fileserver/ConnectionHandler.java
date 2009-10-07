package org.hyperion.fileserver;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;
import org.hyperion.fileserver.UpdateSession.Type;

/**
 * Handles connection events.
 * @author Graham Edgecombe
 *
 */
public class ConnectionHandler extends IoHandlerAdapter {

	/**
	 * Logger instance.
	 */
	private static final Logger logger = Logger.getLogger(ConnectionHandler.class.getName());
	
	/**
	 * The type of handler we are.
	 */
	private Type type;
	
	/**
	 * Creates the handler.
	 * @param type The type of handler.
	 */
	public ConnectionHandler(Type type) {
		this.type = type;
	}

	@Override
	public void exceptionCaught(IoSession session, Throwable throwable) throws Exception {
		logger.log(Level.SEVERE, "Error while handling request.", throwable);
	}

	@Override
	public void messageReceived(IoSession session, Object in) throws Exception {
		((UpdateSession) session.getAttribute("session")).readLine((String) in);
	}

	@Override
	public void sessionOpened(IoSession session) throws Exception {
		session.getFilterChain().addFirst("textFilter", new ProtocolCodecFilter(new TextLineCodecFactory()));
		session.setAttribute("session", new UpdateSession(type, session));
	}

}
