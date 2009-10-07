package org.hyperion.rs2.task.impl;

import java.net.SocketAddress;
import java.util.logging.Logger;

import org.apache.mina.core.session.IoSession;
import org.hyperion.rs2.GameEngine;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.World;
import org.hyperion.rs2.task.Task;

/**
 * A task that is executed when a session is closed.
 * @author Graham Edgecombe
 *
 */
public class SessionClosedTask implements Task {

	/**
	 * Logger instance.
	 */
	private static final Logger logger = Logger.getLogger(SessionClosedTask.class.getName());
	
	/**
	 * The session that closed.
	 */
	private IoSession session;
	
	/**
	 * Creates the session closed task.
	 * @param session The session.
	 */
	public SessionClosedTask(IoSession session) {
		this.session = session;
	}

	@Override
	public void execute(GameEngine context) {
		SocketAddress address = (SocketAddress) session.getAttribute("remote");
		logger.fine("Session closed : " + address);
		if(session.containsAttribute("player")) {
			Player p = (Player) session.getAttribute("player");
			World.getWorld().unregister(p);
		}
	}

}
