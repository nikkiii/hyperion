package org.hyperion.rs2.packet;

import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.container.Bank;
import org.hyperion.rs2.model.container.Inventory;
import org.hyperion.rs2.net.Packet;

/**
 * Switch item packet handler.
 * @author Graham Edgecombe
 *
 */
public class SwitchItemPacketHandler implements PacketHandler {

	@Override
	public void handle(Player player, Packet packet) {
		int interfaceId = packet.getLEShortA();
		packet.getByteC();
		int fromSlot = packet.getLEShortA();
		int toSlot = packet.getLEShort();
		
		switch(interfaceId) {
		case Bank.PLAYER_INVENTORY_INTERFACE:
		case Inventory.INTERFACE:
			if(fromSlot >= 0 && fromSlot < Inventory.SIZE && toSlot >= 0 && toSlot < Inventory.SIZE && toSlot != fromSlot) {
				player.getInventory().swap(fromSlot, toSlot);
			}
			break;
		case Bank.BANK_INVENTORY_INTERFACE:
			if(fromSlot >= 0 && fromSlot < Bank.SIZE && toSlot >= 0 && toSlot < Bank.SIZE && toSlot != fromSlot) {
				if(player.getSettings().isSwapping()) {
					player.getBank().swap(fromSlot, toSlot);
				} else {
					player.getBank().insert(fromSlot, toSlot);
				}
			}
			break;
		}
	}

}
