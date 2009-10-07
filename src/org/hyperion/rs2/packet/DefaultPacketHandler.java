package org.hyperion.rs2.packet;

import java.util.logging.Logger;

import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.net.Packet;

/**
 * Reports information about unhandled packets.
 * @author Graham Edgecombe
 *
 */
public class DefaultPacketHandler implements PacketHandler {

	/**
	 * The logger instance.
	 */
	private static final Logger logger = Logger.getLogger(DefaultPacketHandler.class.getName());
	
	@Override
	public void handle(Player player, Packet packet) {
		logger.info("Packet : [opcode=" + packet.getOpcode() + " length=" + packet.getLength() + " payload=" + packet.getPayload() + "]");
	}

}
