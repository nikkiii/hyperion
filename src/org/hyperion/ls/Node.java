package org.hyperion.ls;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.hyperion.rs2.WorldLoader.LoginResult;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.PlayerDetails;
import org.hyperion.rs2.util.IoBufferUtils;
import org.hyperion.rs2.util.NameUtils;
import org.hyperion.util.login.LoginPacket;

/**
 * Manages a single node (world).
 * @author Graham Edgecombe
 *
 */
public class Node {
	
	/**
	 * The login server.
	 */
	private LoginServer server;
	
	/**
	 * The session.
	 */
	private IoSession session;
	
	/**
	 * The id.
	 */
	private int id;
	
	/**
	 * A map of players.
	 */
	private Map<String, PlayerData> players = new HashMap<String, PlayerData>();
	
	/**
	 * Creates a node.
	 * @param server The server.
	 * @param session The session.
	 * @param id The id.
	 */
	public Node(LoginServer server, IoSession session, int id) {
		this.server = server;
		this.session = session;
		this.id = id;
	}
	
	/**
	 * Registers a new player.
	 * @param player The player to add.
	 */
	public void register(PlayerData player) {
		players.put(player.getName(), player);
	}
	
	/**
	 * Removes an old player.
	 * @param player The player to remove.
	 */
	public void unregister(PlayerData player) {
		players.remove(player.getName());
	}
	
	/**
	 * Gets a player by their name.
	 * @param name The player name.
	 * @return The player.
	 */
	public PlayerData getPlayer(String name) {
		return players.get(name);
	}
	
	/**
	 * Gets the players in this node.
	 * @return The players in this node.
	 */
	public Collection<PlayerData> getPlayers() {
		return players.values();
	}

	/**
	 * Gets the session.
	 * @return The session.
	 */
	public IoSession getSession() {
		return session;
	}
	
	/**
	 * Gets the id.
	 * @return The id.
	 */
	public int getId() {
		return id;
	}

	/**
	 * Handles an incoming packet.
	 * @param packet The incoming packet.
	 */
	public void handlePacket(LoginPacket packet) {
		final IoBuffer buf = packet.getPayload();
		switch(packet.getOpcode()) {
		case LoginPacket.CHECK_LOGIN:
			{
				String name = NameUtils.formatNameForProtocol(IoBufferUtils.getRS2String(buf));
				String password = IoBufferUtils.getRS2String(buf);
				LoginResult res = server.getLoader().checkLogin(new PlayerDetails(null, name, password, 0, null, null));
				if(res.getReturnCode() == 2) {
					PlayerData pd = new PlayerData(name, res.getPlayer().getRights().toInteger());
					NodeManager.getNodeManager().register(pd, this);
				}
				IoBuffer resp = IoBuffer.allocate(16);
				resp.setAutoExpand(true);
				IoBufferUtils.putRS2String(resp, name);
				resp.put((byte) res.getReturnCode());
				resp.flip();
				session.write(new LoginPacket(LoginPacket.CHECK_LOGIN_RESPONSE, resp));
				break;
			}
		case LoginPacket.LOAD:
			{
				String name = NameUtils.formatNameForProtocol(IoBufferUtils.getRS2String(buf));
				Player p = new Player(new PlayerDetails(null, name, "", 0, null, null));
				int code = server.getLoader().loadPlayer(p) ? 1 : 0;
				IoBuffer resp = IoBuffer.allocate(1024);
				resp.setAutoExpand(true);
				IoBufferUtils.putRS2String(resp, name);
				resp.put((byte) code);
				if(code == 1) {
					IoBuffer data = IoBuffer.allocate(16);
					data.setAutoExpand(true);
					p.serialize(data);
					data.flip();
					resp.putShort((short) data.remaining());
					resp.put(data);
				}
				resp.flip();
				session.write(new LoginPacket(LoginPacket.LOAD_RESPONSE, resp));
				break;
			}
		case LoginPacket.SAVE:
			{
				String name = NameUtils.formatNameForProtocol(IoBufferUtils.getRS2String(buf));
				int dataLength = buf.getUnsignedShort();
				byte[] data = new byte[dataLength];
				buf.get(data);
				IoBuffer dataBuffer = IoBuffer.allocate(dataLength);
				dataBuffer.put(data);
				dataBuffer.flip();
				Player p = new Player(new PlayerDetails(null, name, "", 0, null, null));
				p.deserialize(dataBuffer);
				int code = server.getLoader().savePlayer(p) ? 1 : 0;
				IoBuffer resp = IoBuffer.allocate(16);
				resp.setAutoExpand(true);
				IoBufferUtils.putRS2String(resp, name);
				resp.put((byte) code);
				resp.flip();
				session.write(new LoginPacket(LoginPacket.SAVE_RESPONSE, resp));
				break;
			}
		case LoginPacket.DISCONNECT:
			{
				String name = NameUtils.formatNameForProtocol(IoBufferUtils.getRS2String(buf));
				PlayerData p = NodeManager.getNodeManager().getPlayer(name);
				if(p != null) {
					NodeManager.getNodeManager().unregister(p);
				}
			}
			break;
		}
	}

}
