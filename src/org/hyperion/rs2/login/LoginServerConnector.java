package org.hyperion.rs2.login;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.service.IoConnector;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.transport.socket.nio.NioSocketConnector;
import org.hyperion.rs2.WorldLoader.LoginResult;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.PlayerDetails;
import org.hyperion.rs2.model.World;
import org.hyperion.rs2.util.IoBufferUtils;
import org.hyperion.rs2.util.NameUtils;
import org.hyperion.util.CommonConstants;
import org.hyperion.util.login.LoginCodecFactory;
import org.hyperion.util.login.LoginPacket;

/**
 * <p>The <code>LoginServerConnector</code> manages the communication between
 * the game server and the login server.</p>
 * @author Graham Edgecombe
 *
 */
public class LoginServerConnector extends IoHandlerAdapter {
	
	/**
	 * Logger instance.
	 */
	private static final Logger logger = Logger.getLogger(LoginServerConnector.class.getName());
	
	/**
	 * The connector.
	 */
	private IoConnector connector = new NioSocketConnector();
	
	/**
	 * The login server address.
	 */
	private String address;
	
	/**
	 * The login server password.
	 */
	private String password;
	
	/**
	 * The world server node id.
	 */
	private int node;
	
	/**
	 * The client session.
	 */
	private IoSession session;
	
	/**
	 * Authenticated flag.
	 */
	private boolean authenticated = false;
	
	/**
	 * Creates the login server connector.
	 * @param address The address of the login server.
	 */
	public LoginServerConnector(String address) {
		this.address = address;
		connector.setHandler(this);
	}
	
	/**
	 * Checks if the client is connected.
	 * @return <code>true</code> if so, <code>false</code> if not.
	 */
	public boolean isConnected() {
		return session != null && session.isConnected();
	}
	
	/**
	 * Checks if the client is authenticated.
	 * @return <code>true</code> if so, <code>false</code> if not.
	 */
	public boolean isAuthenticated() {
		return isConnected() && authenticated;
	}

	/**
	 * Connects to the server.
	 * @param password The password.
	 * @param node The node id.
	 */
	public void connect(final String password, final int node) {
		this.password = password;
		this.node = node;
		logger.info("Connecting to login server : " + address + ":" + CommonConstants.LOGIN_PORT + "...");
		ConnectFuture cf = connector.connect(new InetSocketAddress(address, CommonConstants.LOGIN_PORT));
		cf.awaitUninterruptibly();
		if(!cf.isConnected() && (session == null || !session.isConnected())) {
			logger.severe("Connection to login server failed. Retrying...");
			// this stops stack overflow errors
			World.getWorld().getEngine().submitLogic(new Runnable() {
				public void run() {
					World.getWorld().getLoginServerConnector().connect(password, node);
				}
			});
		} else {
			this.session = cf.getSession();
			logger.info("Connected.");
			session.getFilterChain().addFirst("protocolCodecFilter", new ProtocolCodecFilter(new LoginCodecFactory()));
			// create and send auth packet
			IoBuffer buf = IoBuffer.allocate(16);
			buf.setAutoExpand(true);
			buf.putShort((short) node);
			IoBufferUtils.putRS2String(buf, password);
			buf.flip();
			this.session.write(new LoginPacket(LoginPacket.AUTH, buf));
		}
	}

	@Override
	public void exceptionCaught(IoSession session, Throwable throwable) throws Exception {
		session.close(false);
		throwable.printStackTrace();
	}

	@Override
	public void messageReceived(IoSession session, Object in) throws Exception {
		read((LoginPacket) in);
	}
	
	/**
	 * Write a packet.
	 * @param packet The packet to write.
	 */
	public void write(LoginPacket packet) {
		if(!this.isConnected()) {
			session.write(packet);
		} else {
			throw new IllegalStateException("Not connected.");
		}
	}

	/**
	 * Read and process a packet.
	 * @param packet The packet to read.
	 */
	private void read(LoginPacket packet) {
		final IoBuffer payload = packet.getPayload();
		switch(packet.getOpcode()) {
		case LoginPacket.AUTH_RESPONSE:
			{
				int code = payload.getUnsigned();
				if(code == 0) {
					authenticated = true;
					logger.info("Authenticated as node : World-" + node + ".");
				} else {
					session.close(false);
					logger.severe("Login server authentication error : " + code + ". Check your password and node id.");
				}
				break;
			}
		case LoginPacket.CHECK_LOGIN_RESPONSE:
			{
				String name = IoBufferUtils.getRS2String(payload);
				int returnCode = payload.getUnsigned();
				synchronized(checkLoginResults) {
					checkLoginResults.put(name, returnCode);
					checkLoginResults.notifyAll();
				}
				break;
			}
		case LoginPacket.LOAD_RESPONSE:
			{
				String name = IoBufferUtils.getRS2String(payload);
				int returnCode = payload.getUnsigned();
				if(returnCode == 1) {
					int dataLength = payload.getUnsignedShort();
					byte[] data = new byte[dataLength];
					payload.get(data);
					IoBuffer dataBuffer = IoBuffer.allocate(dataLength);
					dataBuffer.put(data);
					dataBuffer.flip();
					synchronized(playerLoadResults) {
						playerLoadResults.put(name, dataBuffer);
						playerLoadResults.notifyAll();
					}
				} else {
					synchronized(playerLoadResults) {
						playerLoadResults.put(name, null);
						playerLoadResults.notifyAll();
					}
				}
				break;
			}
		case LoginPacket.SAVE_RESPONSE:
			{
				String name = IoBufferUtils.getRS2String(payload);
				int success = payload.getUnsigned();
				synchronized(playerSaveResults) {
					playerSaveResults.put(name, success == 1 ? Boolean.TRUE : Boolean.FALSE);
					playerSaveResults.notifyAll();
				}
				break;
			}
		}
	}

	@Override
	public void sessionClosed(IoSession session) throws Exception {
		if(this.session == session) {
			logger.info("Disconnected. Retrying...");
			connect(password, node);
			this.session = null;
		}
	}
	
	/**
	 * Check login results.
	 */
	private Map<String, Integer> checkLoginResults = new HashMap<String, Integer>();
	
	/**
	 * Player load results.
	 */
	private Map<String, IoBuffer> playerLoadResults = new HashMap<String, IoBuffer>();
	
	/**
	 * Player save results.
	 */
	private Map<String, Boolean> playerSaveResults = new HashMap<String, Boolean>();

	/**
	 * Checks the login of a player.
	 * @param pd The player details.
	 * @return The login result.
	 */
	public LoginResult checkLogin(PlayerDetails pd) {
		IoBuffer buf = IoBuffer.allocate(16);
		buf.setAutoExpand(true);
		IoBufferUtils.putRS2String(buf, pd.getName());
		IoBufferUtils.putRS2String(buf, pd.getPassword());
		buf.flip();
		session.write(new LoginPacket(LoginPacket.CHECK_LOGIN, buf));
		synchronized(checkLoginResults) {
			while(!checkLoginResults.containsKey(NameUtils.formatNameForProtocol(pd.getName()))) {
				try {
					checkLoginResults.wait();
				} catch(InterruptedException e) {
					continue;
				}
			}
			int code = checkLoginResults.remove(NameUtils.formatNameForProtocol(pd.getName()));
			if(code == 2) {
				return new LoginResult(code, new Player(pd));
			} else {
				return new LoginResult(code);
			}
		}
	}

	/**
	 * Loads a player.
	 * @param player The player.
	 * @return <code>true</code> on success, <code>false</code> on error.
	 */
	public boolean loadPlayer(Player player) {
		IoBuffer buf = IoBuffer.allocate(16);
		buf.setAutoExpand(true);
		IoBufferUtils.putRS2String(buf, NameUtils.formatNameForProtocol(player.getName()));
		buf.flip();
		session.write(new LoginPacket(LoginPacket.LOAD, buf));
		synchronized(playerLoadResults) {
			while(!playerLoadResults.containsKey(NameUtils.formatNameForProtocol(player.getName()))) {
				try {
					playerLoadResults.wait();
				} catch(InterruptedException e) {
					continue;
				}
			}
			IoBuffer playerData = playerLoadResults.remove(NameUtils.formatNameForProtocol(player.getName()));
			if(playerData == null) {
				return false;
			} else {
				player.deserialize(playerData);
			}
		}
		return true;
	}

	/**
	 * Saves a player.
	 * @param player The player.
	 * @return <code>true</code> on success, <code>false</code> on error.
	 */
	public boolean savePlayer(Player player) {
		IoBuffer buf = IoBuffer.allocate(16);
		buf.setAutoExpand(true);
		IoBufferUtils.putRS2String(buf, NameUtils.formatNameForProtocol(player.getName()));
		IoBuffer data = IoBuffer.allocate(1024);
		data.setAutoExpand(true);
		player.serialize(data);
		data.flip();
		buf.putShort((short) data.remaining());
		buf.put(data);
		buf.flip();
		session.write(new LoginPacket(LoginPacket.SAVE, buf));
		synchronized(playerSaveResults) {
			while(!playerSaveResults.containsKey(NameUtils.formatNameForProtocol(player.getName()))) {
				try {
					playerSaveResults.wait();
				} catch(InterruptedException e) {
					continue;
				}
			}
			return playerSaveResults.remove(NameUtils.formatNameForProtocol(player.getName())).booleanValue();
		}
	}

	/**
	 * Sends a notification of player disconnection to the login server.
	 * @param name The player name.
	 */
	public void disconnected(String name) {
		IoBuffer buf = IoBuffer.allocate(16);
		buf.setAutoExpand(true);
		IoBufferUtils.putRS2String(buf, NameUtils.formatNameForProtocol(name));
		buf.flip();
		session.write(new LoginPacket(LoginPacket.DISCONNECT, buf));
	}

}
