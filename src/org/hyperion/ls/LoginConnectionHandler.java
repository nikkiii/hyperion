package org.hyperion.ls;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.hyperion.rs2.util.IoBufferUtils;
import org.hyperion.util.login.LoginCodecFactory;
import org.hyperion.util.login.LoginPacket;

/**
 * Handles the login server connections.
 * @author Graham Edgecombe
 *
 */
public class LoginConnectionHandler extends IoHandlerAdapter {
	
	/**
	 * The login server.
	 */
	private LoginServer server;
	
	/**
	 * Creates the connection handler.
	 * @param server The server.
	 */
	public LoginConnectionHandler(LoginServer server) {
		this.server = server;
	}

	@Override
	public void exceptionCaught(IoSession session, Throwable throwable) throws Exception {
		session.close(false);
		throwable.printStackTrace();
	}

	@Override
	public void messageReceived(final IoSession session, final Object message) throws Exception {
		server.pushTask(new Runnable() {
			public void run() {
				if(session.containsAttribute("node")) {
					((Node) session.getAttribute("node")).handlePacket((LoginPacket) message);
				} else {
					handlePreAuthenticationPacket(session, (LoginPacket) message);
				}
			}
		});
	}

	@Override
	public void sessionClosed(final IoSession session) throws Exception {
		server.pushTask(new Runnable() {
			public void run() {
				if(session.containsAttribute("node")) {
					NodeManager.getNodeManager().unregister((Node) session.getAttribute("node"));
				}
			}
		});
	}

	@Override
	public void sessionOpened(IoSession session) throws Exception {
		session.getFilterChain().addFirst("protocolCodecFilter", new ProtocolCodecFilter(new LoginCodecFactory()));
	}
	
	/**
	 * Handles the authentication packet.
	 * @param session The session.
	 * @param message
	 */
	private void handlePreAuthenticationPacket(IoSession session, LoginPacket message) {
		if(message.getOpcode() == LoginPacket.AUTH) {
			int node = message.getPayload().getUnsignedShort();
			String password = IoBufferUtils.getRS2String(message.getPayload());
			boolean valid = NodeManager.getNodeManager().isNodeAuthenticationValid(node, password);
			Node n = new Node(server, session, node);
			session.setAttribute("node", n);
			NodeManager.getNodeManager().register(n);
			int code = valid ? 0 : 1;
			IoBuffer resp = IoBuffer.allocate(1);
			resp.put((byte) code);
			resp.flip();
			session.write(new LoginPacket(LoginPacket.AUTH_RESPONSE, resp));
		} else {
			session.close(false);
		}
	}

}
