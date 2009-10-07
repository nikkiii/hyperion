package org.hyperion.rs2.model.container.impl;

import org.hyperion.rs2.model.Item;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.container.Container;
import org.hyperion.rs2.model.container.ContainerListener;

/**
 * A ContainerListener which updates a client-side interface to match the
 * server-side copy of the container.
 * @author Graham Edgecombe
 *
 */
public class InterfaceContainerListener implements ContainerListener {
	
	/**
	 * The player.
	 */
	private Player player;
	
	/**
	 * The interface id.
	 */
	private int interfaceId;
	
	/**
	 * Creates the container listener.
	 * @param player The player.
	 * @param interfaceId The interface id.
	 */
	public InterfaceContainerListener(Player player, int interfaceId) {
		this.player = player;
		this.interfaceId = interfaceId;
	}

	@Override
	public void itemChanged(Container container, int slot) {
		Item item = container.get(slot);
		player.getActionSender().sendUpdateItem(interfaceId, slot, item);
	}

	@Override
	public void itemsChanged(Container container) {
		player.getActionSender().sendUpdateItems(interfaceId, container.toArray());
	}

	@Override
	public void itemsChanged(Container container, int[] slots) {
		player.getActionSender().sendUpdateItems(interfaceId, slots, container.toArray());
	}

}
