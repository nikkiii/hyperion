package org.hyperion.rs2.model.container;

import org.hyperion.rs2.model.Item;
import org.hyperion.rs2.model.ItemDefinition;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.container.impl.InterfaceContainerListener;

/**
 * Banking utility class.
 * @author Graham Edgecombe
 *
 */
public class Bank {
	
	/**
	 * The bank size.
	 */
	public static final int SIZE = 352;
	
	/**
	 * The player inventory interface.
	 */
	public static final int PLAYER_INVENTORY_INTERFACE = 5064;

	/**
	 * The bank inventory interface.
	 */
	public static final int BANK_INVENTORY_INTERFACE = 5382;
	
	/**
	 * Opens the bank for the specified player.
	 * @param player The player to open the bank for.
	 */
	public static void open(Player player) {
		player.getBank().shift();
		player.getActionSender().sendInterfaceInventory(5292, 5063);
		player.getInterfaceState().addListener(player.getBank(), new InterfaceContainerListener(player, 5382));
		player.getInterfaceState().addListener(player.getInventory(), new InterfaceContainerListener(player, 5064));
	}
	
	/**
	 * Withdraws an item.
	 * @param player The player.
	 * @param slot The slot in the player's inventory.
	 * @param id The item id.
	 * @param amount The amount of the item to deposit.
	 */
	public static void withdraw(Player player, int slot, int id, int amount) {
		Item item = player.getBank().get(slot);
		if(item == null) {
			return; // invalid packet, or client out of sync
		}
		if(item.getId() != id) {
			return; // invalid packet, or client out of sync
		}
		int transferAmount = item.getCount();
		if(transferAmount >= amount) {
			transferAmount = amount;
		} else if(transferAmount == 0) {
			return; // invalid packet, or client out of sync
		}
		int newId = item.getId(); // TODO deal with withdraw as notes!
		if(player.getSettings().isWithdrawingAsNotes()) {
			if(item.getDefinition().isNoteable()) {
				newId = item.getDefinition().getNotedId();
			}
		}
		ItemDefinition def = ItemDefinition.forId(newId);
		if(def.isStackable()) {
			if(player.getInventory().freeSlots() <= 0 && player.getInventory().getById(newId) == null) {
				player.getActionSender().sendMessage("You don't have enough inventory space to withdraw that many."); // this is the real message
			}
		} else {
			int free = player.getInventory().freeSlots();
			if(transferAmount > free) {
				player.getActionSender().sendMessage("You don't have enough inventory space to withdraw that many."); // this is the real message
				transferAmount = free;
			}
		}
		// now add it to inv
		if(player.getInventory().add(new Item(newId, transferAmount))) {
			// all items in the bank are stacked, makes it very easy!
			int newAmount = item.getCount() - transferAmount;
			if(newAmount <= 0) {
				player.getBank().set(slot, null);
			} else {
				player.getBank().set(slot, new Item(item.getId(), newAmount));
			}
		} else {
			player.getActionSender().sendMessage("You don't have enough inventory space to withdraw that many."); // this is the real message
		}
	}
	
	/**
	 * Deposits an item.
	 * @param player The player.
	 * @param slot The slot in the player's inventory.
	 * @param id The item id.
	 * @param amount The amount of the item to deposit.
	 */
	public static void deposit(Player player, int slot, int id, int amount) {
		boolean inventoryFiringEvents = player.getInventory().isFiringEvents();
		player.getInventory().setFiringEvents(false);
		try {
			Item item = player.getInventory().get(slot);
			if(item == null) {
				return; // invalid packet, or client out of sync
			}
			if(item.getId() != id) {
				return; // invalid packet, or client out of sync
			}
			int transferAmount = player.getInventory().getCount(id);
			if(transferAmount >= amount) {
				transferAmount = amount;
			} else if(transferAmount == 0) {
				return; // invalid packet, or client out of sync
			}
			boolean noted = item.getDefinition().isNoted();
			if(item.getDefinition().isStackable() || noted) {
				int bankedId = noted ? item.getDefinition().getNormalId() : item.getId();
				if(player.getBank().freeSlots() < 1 && player.getBank().getById(bankedId) == null) {
					player.getActionSender().sendMessage("You don't have enough space in your bank account."); // this is the real message
				}
				// we only need to remove from one stack
				int newInventoryAmount = item.getCount() - transferAmount;
				Item newItem;
				if(newInventoryAmount <= 0) {
					newItem = null;
				} else {
					newItem = new Item(item.getId(), newInventoryAmount);
				}
				if(!player.getBank().add(new Item(bankedId, transferAmount))) {
					player.getActionSender().sendMessage("You don't have enough space in your bank account."); // this is the real message
				} else {
					player.getInventory().set(slot, newItem);
					player.getInventory().fireItemsChanged();
					player.getBank().fireItemsChanged();
				}
			} else {
				if(player.getBank().freeSlots() < transferAmount) {
					player.getActionSender().sendMessage("You don't have enough space in your bank account."); // this is the real message
				}
				if(!player.getBank().add(new Item(item.getId(), transferAmount))) {
					player.getActionSender().sendMessage("You don't have enough space in your bank account."); // this is the real message
				} else {
					// we need to remove multiple items
					for(int i = 0; i < transferAmount; i++) {
						/* if(i == 0) {
							player.getInventory().set(slot, null);
						} else { */
							player.getInventory().set(player.getInventory().getSlotById(item.getId()), null);
						// }
					}
					player.getInventory().fireItemsChanged();
				}
			}
		} finally {
			player.getInventory().setFiringEvents(inventoryFiringEvents);
		}
	}

}
