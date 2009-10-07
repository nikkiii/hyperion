package org.hyperion.rs2.packet;

import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.net.Packet;

/**
 * A packet handler which takes no action i.e. it ignores the packet.
 * @author Graham Edgecombe
 *
 */
public class QuietPacketHandler implements PacketHandler {

	@Override
	public void handle(Player player, Packet packet) {
		
	}

}
