package org.hyperion.rs2.packet;

import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.net.Packet;

/**
 * A packet sent when the player enters a custom amount for banking etc.
 * @author Graham Edgecombe
 *
 */
public class EnterAmountPacketHandler implements PacketHandler {

	@Override
	public void handle(Player player, Packet packet) {
		int amount = packet.getInt();
		if(player.getInterfaceState().isEnterAmountInterfaceOpen()) {
			player.getInterfaceState().closeEnterAmountInterface(amount);
		}
	}

}
