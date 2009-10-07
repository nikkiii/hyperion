package org.hyperion.rs2.packet;

import org.hyperion.rs2.Constants;
import org.hyperion.rs2.action.impl.AttackAction;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.World;
import org.hyperion.rs2.net.Packet;

public class PlayerOptionPacketHandler implements PacketHandler {

	@Override
	public void handle(Player player, Packet packet) {
		switch(packet.getOpcode()) {
		case 128:
			/*
			 * Option 1.
			 */
			option1(player, packet);
			break;
		case 37:
			/*
			 * Option 2.
			 */
			option2(player,  packet);
			break;
		case 227:
			/*
			 * Option 3.
			 */
			option3(player, packet);
			break;
		}
	}

	/**
	 * Handles the first option on a player option menu.
	 * @param player
	 * @param packet
	 */
	private void option1(final Player player, Packet packet) {
		int id = packet.getShort() & 0xFFFF;
		if(id < 0 || id >= Constants.MAX_PLAYERS) {
			return;
		}
		Player victim = (Player) World.getWorld().getPlayers().get(id);
		if(victim != null && player.getLocation().isWithinInteractionDistance(victim.getLocation())) {
			player.getActionQueue().addAction(new AttackAction(player, victim));
		}
	}
	
	/**
	 * Handles the second option on a player option menu.
	 * @param player
	 * @param packet
	 */
	private void option2(Player player, Packet packet) {
		int id = packet.getShort() & 0xFFFF;
		if(id < 0 || id >= Constants.MAX_PLAYERS) {
			return;
		}
	}
	
	/**
	 * Handles the third option on a player option menu.
	 * @param player
	 * @param packet
	 */
	private void option3(Player player, Packet packet) {
		int id = packet.getLEShortA() & 0xFFFF;
		if(id < 0 || id >= Constants.MAX_PLAYERS) {
			return;
		}
	}
		
}

