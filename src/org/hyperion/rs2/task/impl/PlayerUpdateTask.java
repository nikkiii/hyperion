package org.hyperion.rs2.task.impl;

import java.util.Iterator;

import org.hyperion.rs2.GameEngine;
import org.hyperion.rs2.model.Appearance;
import org.hyperion.rs2.model.ChatMessage;
import org.hyperion.rs2.model.Entity;
import org.hyperion.rs2.model.Item;
import org.hyperion.rs2.model.Location;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.UpdateFlags;
import org.hyperion.rs2.model.World;
import org.hyperion.rs2.model.UpdateFlags.UpdateFlag;
import org.hyperion.rs2.model.container.Container;
import org.hyperion.rs2.model.container.Equipment;
import org.hyperion.rs2.model.container.Equipment.EquipmentType;
import org.hyperion.rs2.net.Packet;
import org.hyperion.rs2.net.PacketBuilder;
import org.hyperion.rs2.task.Task;

/**
 * A task which creates and sends the player update block.
 * @author Graham Edgecombe
 *
 */
public class PlayerUpdateTask implements Task {
	
	/**
	 * The player.
	 */
	private Player player;
	
	/**
	 * Creates an update task.
	 * @param player The player.
	 */
	public PlayerUpdateTask(Player player) {
		this.player = player;
	}

	@Override
	public void execute(GameEngine context) {
		/*
		 * If the map region changed send the new one.
		 * We do this immediately as the client can begin loading it before the
		 * actual packet is received.
		 */
		if(player.isMapRegionChanging()) {
			player.getActionSender().sendMapRegion();
		}
		
		/*
		 * The update block packet holds update blocks and is send after the
		 * main packet. 
		 */
		PacketBuilder updateBlock = new PacketBuilder();
		
		/*
		 * The main packet is written in bits instead of bytes and holds
		 * information about the local list, players to add and remove,
		 * movement and which updates are required.
		 */
		PacketBuilder packet = new PacketBuilder(81, Packet.Type.VARIABLE_SHORT);
		packet.startBitAccess();
		
		/*
		 * Updates this player.
		 */
		updateThisPlayerMovement(packet);
		updatePlayer(updateBlock, player, false, true);
		
		/*
		 * Write the current size of the player list.
		 */
		packet.putBits(8, player.getLocalPlayers().size());
		
		/*
		 * Iterate through the local player list.
		 */
		for(Iterator<Player> it$ = player.getLocalPlayers().iterator(); it$.hasNext();) {
			/*
			 * Get the next player.
			 */
			Player otherPlayer = it$.next();
			
			/*
			 * If the player should still be in our list.
			 */
			if(World.getWorld().getPlayers().contains(otherPlayer) && !otherPlayer.isTeleporting() && otherPlayer.getLocation().isWithinDistance(player.getLocation())) {
				/*
				 * Update the movement.
				 */
				updatePlayerMovement(packet, otherPlayer);
				
				/*
				 * Check if an update is required, and if so, send the update.
				 */
				if(otherPlayer.getUpdateFlags().isUpdateRequired()) {
					updatePlayer(updateBlock, otherPlayer, false, false);
				}
			} else {
				/*
				 * Otherwise, remove the player from the list.
				 */
				it$.remove();
				
				/*
				 * Tell the client to remove the player from the list.
				 */
				packet.putBits(1, 1);
				packet.putBits(2, 3);
			}
		}
		
		/*
		 * Loop through every player.
		 */
		for(Player otherPlayer : World.getWorld().getRegionManager().getLocalPlayers(player)) {
			/*
			 * Check if there is room left in the local list.
			 */
			if(player.getLocalPlayers().size() >= 255) {
				/*
				 * There is no more room left in the local list. We cannot add
				 * more players, so we just ignore the extra ones. They will be
				 * added as other players get removed.
				 */
				break;
			}
			
			/*
			 * If they should not be added ignore them.
			 */
			if(otherPlayer == player || player.getLocalPlayers().contains(otherPlayer)) {
				continue;
			}
			
			/*
			 * Add the player to the local list if it is within distance.
			 */
			player.getLocalPlayers().add(otherPlayer);
			
			/*
			 * Add the player in the packet.
			 */
			addNewPlayer(packet, otherPlayer);
			
			/*
			 * Update the player, forcing the appearance flag.
			 */
			updatePlayer(updateBlock, otherPlayer, true, false);
		}
		
		/*
		 * Check if the update block is not empty.
		 */
		if(!updateBlock.isEmpty()) {
			/*
			 * Write a magic id indicating an update block follows.
			 */
			packet.putBits(11, 2047);
			packet.finishBitAccess();
			
			/*
			 * Add the update block at the end of this packet.
			 */
			packet.put(updateBlock.toPacket().getPayload());
		} else {
			/*
			 * Terminate the packet normally.
			 */
			packet.finishBitAccess();
		}
		
		/*
		 * Write the packet.
		 */
		player.write(packet.toPacket());
	}

	/**
	 * Updates a non-this player's movement.
	 * @param packet The packet.
	 * @param otherPlayer The player.
	 */
	public void updatePlayerMovement(PacketBuilder packet, Player otherPlayer) {
		/*
		 * Check which type of movement took place.
		 */
		if(otherPlayer.getSprites().getPrimarySprite() == -1) {
			/*
			 * If no movement did, check if an update is required.
			 */
			if(otherPlayer.getUpdateFlags().isUpdateRequired()) {
				/*
				 * Signify that an update happened.
				 */
				packet.putBits(1, 1);
				
				/*
				 * Signify that there was no movement.
				 */
				packet.putBits(2, 0);
			} else {
				/*
				 * Signify that nothing changed.
				 */
				packet.putBits(1, 0);
			}
		} else if(otherPlayer.getSprites().getSecondarySprite() == -1) {
			/*
			 * The player moved but didn't run. Signify that an update is
			 * required.
			 */
			packet.putBits(1, 1);
			
			/*
			 * Signify we moved one tile.
			 */
			packet.putBits(2, 1);
			
			/*
			 * Write the primary sprite (i.e. walk direction).
			 */
			packet.putBits(3, otherPlayer.getSprites().getPrimarySprite());
			
			/*
			 * Write a flag indicating if a block update happened.
			 */
			packet.putBits(1, otherPlayer.getUpdateFlags().isUpdateRequired() ? 1 : 0);
		} else {
			/*
			 * The player ran. Signify that an update happened.
			 */
			packet.putBits(1, 1);
			
			/*
			 * Signify that we moved two tiles.
			 */
			packet.putBits(2, 2);
			
			/*
			 * Write the primary sprite (i.e. walk direction).
			 */
			packet.putBits(3, otherPlayer.getSprites().getPrimarySprite());
			
			/*
			 * Write the secondary sprite (i.e. run direction).
			 */
			packet.putBits(3, otherPlayer.getSprites().getSecondarySprite());
			
			/*
			 * Write a flag indicating if a block update happened.
			 */
			packet.putBits(1, otherPlayer.getUpdateFlags().isUpdateRequired() ? 1 : 0);
		}
	}

	/**
	 * Adds a new player.
	 * @param packet The packet.
	 * @param otherPlayer The player.
	 */
	public void addNewPlayer(PacketBuilder packet, Player otherPlayer) {
		/*
		 * Write the player index.
		 */
		packet.putBits(11, otherPlayer.getIndex());
		
		/*
		 * Write two flags here: the first indicates an update is required
		 * (this is always true as we add the appearance after adding a player)
		 * and the second to indicate we should discard client-side walk
		 * queues.
		 */
		packet.putBits(1, 1);
		packet.putBits(1, 1);
		
		/*
		 * Calculate the x and y offsets.
		 */
		int yPos = otherPlayer.getLocation().getY() - player.getLocation().getY();
		int xPos = otherPlayer.getLocation().getX() - player.getLocation().getX();
		
		/*
		 * Write the x and y offsets.
		 */
		packet.putBits(5, yPos);
		packet.putBits(5, xPos);
	}
	
    private static void appendHit2Update(final Player p, final PacketBuilder updateBlock) {
    	updateBlock.put((byte) p.getDamage().getHitDamage2());
    	updateBlock.putByteS((byte) p.getDamage().getHitType2());
    	updateBlock.put((byte) p.getSkills().getLevel(3));
    	updateBlock.putByteC(p.getSkills().getLevelForExperience(3));
    }

    private static void appendHitUpdate(final Player p, final PacketBuilder updateBlock) {
    	updateBlock.put((byte) p.getDamage().getHitDamage1());
    	updateBlock.putByteA(p.getDamage().getHitType1());
    	updateBlock.putByteC(p.getSkills().getLevel(3));
    	updateBlock.put((byte) p.getSkills().getLevelForExperience(3));
    }

	/**
	 * Updates a player.
	 * @param packet The packet.
	 * @param otherPlayer The other player.
	 * @param forceAppearance The force appearance flag.
	 * @param noChat Indicates chat should not be relayed to this player.
	 */
	public void updatePlayer(PacketBuilder packet, Player otherPlayer, boolean forceAppearance, boolean noChat) {
		/*
		 * If no update is required and we don't have to force an appearance
		 * update, don't write anything.
		 */
		if(!otherPlayer.getUpdateFlags().isUpdateRequired() && !forceAppearance) {
			return;
		}
		
		/*
		 * We can used the cached update block!
		 */
		synchronized(otherPlayer) {
			if(otherPlayer.hasCachedUpdateBlock() && otherPlayer != player && !forceAppearance && !noChat) {
				packet.put(otherPlayer.getCachedUpdateBlock().getPayload().flip());
				return;
			}
			
			/*
			 * We have to construct and cache our own block.
			 */
			PacketBuilder block = new PacketBuilder();
			
			/*
			 * Calculate the bitmask.
			 */
			int mask = 0;
			final UpdateFlags flags = otherPlayer.getUpdateFlags();
			
			// TODO mask 0x400
			if(flags.get(UpdateFlag.GRAPHICS)) {
				mask |= 0x100;
			}
			if(flags.get(UpdateFlag.ANIMATION)) {
				mask |= 0x8;
			}
			if(flags.get(UpdateFlag.FORCED_CHAT)) {
				mask |= 0x4;
			}
			if(flags.get(UpdateFlag.CHAT) && !noChat) {
				mask |= 0x80;
			}
			if(flags.get(UpdateFlag.FACE_ENTITY)) {
				mask |= 0x1;
			}
			if(flags.get(UpdateFlag.APPEARANCE) || forceAppearance) {
				mask |= 0x10;
			}
			if(flags.get(UpdateFlag.FACE_COORDINATE)) {
				mask |= 0x2;
			}
			if(flags.get(UpdateFlag.HIT)) {
				mask |= 0x20;
			}
			if(flags.get(UpdateFlag.HIT_2)) {
				mask |= 0x200;
			}
			
			/*
			 * Check if the bitmask would overflow a byte.
			 */
			if(mask >= 0x100) {
				/*
				 * Write it as a short and indicate we have done so.
				 */
				mask |= 0x40;
				block.put((byte) (mask & 0xFF));
				block.put((byte) (mask >> 8));
			} else {
				/*
				 * Write it as a byte.
				 */
				block.put((byte) (mask));
			}
			
			/*
			 * Append the appropriate updates.
			 */
			if(flags.get(UpdateFlag.GRAPHICS)) {
				appendGraphicsUpdate(block, otherPlayer);
			}
			if(flags.get(UpdateFlag.ANIMATION)) {
				appendAnimationUpdate(block, otherPlayer);
			}
			if(flags.get(UpdateFlag.FORCED_CHAT)) {
				
			}
			if(flags.get(UpdateFlag.CHAT) && !noChat) {
				appendChatUpdate(block, otherPlayer);
			}
			if(flags.get(UpdateFlag.FACE_ENTITY)) {
				Entity entity = otherPlayer.getInteractingEntity();
				block.putLEShort(entity == null ? -1 : entity.getClientIndex());
			}
			if(flags.get(UpdateFlag.APPEARANCE) || forceAppearance) {
				appendPlayerAppearanceUpdate(block, otherPlayer);
			}
			if(flags.get(UpdateFlag.FACE_COORDINATE)) {
				Location loc = otherPlayer.getFaceLocation();
				if(loc == null) {
					block.putLEShortA(0);
					block.putLEShort(0);
				} else {
					block.putLEShortA(loc.getX() * 2 + 1);
					block.putLEShort(loc.getY() * 2 + 1);
				}
			}
			if(flags.get(UpdateFlag.HIT)) {
				appendHitUpdate(otherPlayer, block);
			}
			if(flags.get(UpdateFlag.HIT_2)) {
				appendHit2Update(otherPlayer, block);
			}
			
			/*
			 * Convert the block builder to a packet.
			 */
			Packet blockPacket = block.toPacket();
			
			/*
			 * Now it is over, cache the block if we can.
			 */
			if(otherPlayer != player && !forceAppearance && !noChat) {
				otherPlayer.setCachedUpdateBlock(blockPacket);
			}
		
			/*
			 * And finally append the block at the end.
			 */
			packet.put(blockPacket.getPayload());
		}
	}
	
	/**
	 * Appends an animation update.
	 * @param block The update block.
	 * @param otherPlayer The player.
	 */
	private void appendAnimationUpdate(PacketBuilder block, Player otherPlayer) {
		block.putLEShort(otherPlayer.getCurrentAnimation().getId());
		block.putByteC(otherPlayer.getCurrentAnimation().getDelay());
	}

	/**
	 * Appends a graphics update.
	 * @param block The update block.
	 * @param otherPlayer The player.
	 */
	private void appendGraphicsUpdate(PacketBuilder block, Player otherPlayer) {
		block.putLEShort(otherPlayer.getCurrentGraphic().getId());
		block.putInt(otherPlayer.getCurrentGraphic().getDelay());
	}

	/**
	 * Appends a chat text update.
	 * @param packet The packet.
	 * @param otherPlayer The player.
	 */
	private void appendChatUpdate(PacketBuilder packet, Player otherPlayer) {
		ChatMessage cm = otherPlayer.getCurrentChatMessage();
		
		byte[] bytes = cm.getText();
		
		packet.putLEShort(((cm.getColour() & 0xFF) << 8) | (cm.getEffects() & 0xFF));
		packet.put((byte) otherPlayer.getRights().toInteger());
		packet.putByteC(bytes.length);
		for(int ptr = bytes.length -1; ptr >= 0; ptr--) {
			packet.put(bytes[ptr]);
		}
	}

	/**
	 * Appends an appearance update.
	 * @param packet The packet.
	 * @param otherPlayer The player.
	 */
	private void appendPlayerAppearanceUpdate(PacketBuilder packet, Player otherPlayer) {
		Appearance app = otherPlayer.getAppearance();
		Container eq = otherPlayer.getEquipment();
		
		PacketBuilder playerProps = new PacketBuilder();
		playerProps.put((byte) app.getGender()); // gender
		playerProps.put((byte) 0); // skull icon
		
		for(int i = 0; i < 4; i++) {
			if(eq.isSlotUsed(i)) {
				playerProps.putShort((short) 0x200 + eq.get(i).getId());
			} else {
				playerProps.put((byte) 0);
			}
		}
		if(eq.isSlotUsed(Equipment.SLOT_CHEST)) {
			playerProps.putShort((short) 0x200 + eq.get(Equipment.SLOT_CHEST).getId());
		} else {
			playerProps.putShort((short) 0x100 + app.getChest()); // chest
		}
		if(eq.isSlotUsed(Equipment.SLOT_SHIELD)){
			playerProps.putShort((short) 0x200 + eq.get(Equipment.SLOT_SHIELD).getId());
		} else {
			playerProps.put((byte) 0);
		}
		Item chest = eq.get(Equipment.SLOT_CHEST);
		if(chest != null) {
			if(!Equipment.is(EquipmentType.PLATEBODY, chest)) {
				playerProps.putShort((short) 0x100 + app.getArms());
			} else {
				playerProps.putShort((short) 0x200 + chest.getId());
			}
		} else {
			playerProps.putShort((short) 0x100 + app.getArms());
		}
		if(eq.isSlotUsed(Equipment.SLOT_BOTTOMS)) {
			playerProps.putShort((short) 0x200 + eq.get(Equipment.SLOT_BOTTOMS).getId());
		} else {
			playerProps.putShort((short) 0x100 + app.getLegs());
		}
		Item helm = eq.get(Equipment.SLOT_HELM);
		if(helm != null) {
			if(!Equipment.is(EquipmentType.FULL_HELM, helm) && !Equipment.is(EquipmentType.FULL_MASK, helm)) {
				playerProps.putShort((short) 0x100 + app.getHead());
			} else {
				playerProps.put((byte) 0);
			}
		} else {
			playerProps.putShort((short) 0x100 + app.getHead());
		}
		if(eq.isSlotUsed(Equipment.SLOT_GLOVES)) {
			playerProps.putShort((short) 0x200 + eq.get(Equipment.SLOT_GLOVES).getId());
		} else {
			playerProps.putShort((short) 0x100 + app.getHands());
		}
		if(eq.isSlotUsed(Equipment.SLOT_BOOTS)) {
			playerProps.putShort((short) 0x200 + eq.get(Equipment.SLOT_BOOTS).getId());
		} else {
			playerProps.putShort((short) 0x100 + app.getFeet());
		}
		boolean fullHelm = false;
		if(helm != null) {
			fullHelm = !Equipment.is(EquipmentType.FULL_HELM, helm);
		}
		if(fullHelm || app.getGender() == 1) {
			playerProps.put((byte) 0);
		} else {
			playerProps.putShort((short) 0x100 + app.getBeard());
		}
		
		playerProps.put((byte) app.getHairColour()); // hairc
		playerProps.put((byte) app.getTorsoColour()); // torsoc
		playerProps.put((byte) app.getLegColour()); // legc
		playerProps.put((byte) app.getFeetColour()); // feetc
		playerProps.put((byte) app.getSkinColour()); // skinc
		
		playerProps.putShort((short) 0x328); // stand
		playerProps.putShort((short) 0x337); // stand turn
		playerProps.putShort((short) 0x333); // walk
		playerProps.putShort((short) 0x334); // turn 180
		playerProps.putShort((short) 0x335); // turn 90 cw
		playerProps.putShort((short) 0x336); // turn 90 ccw
		playerProps.putShort((short) 0x338); // run
		
		playerProps.putLong(otherPlayer.getNameAsLong()); // player name
		playerProps.put((byte) otherPlayer.getSkills().getCombatLevel()); // combat level
		playerProps.putShort(0); // (skill-level instead of combat-level) otherPlayer.getSkills().getTotalLevel()); // total level
		
		Packet propsPacket = playerProps.toPacket();
		
		packet.putByteC(propsPacket.getLength());
		packet.put(propsPacket.getPayload());
	}

	/**
	 * Updates this player's movement.
	 * @param packet The packet.
	 */
	private void updateThisPlayerMovement(PacketBuilder packet) {
		/*
		 * Check if the player is teleporting.
		 */
		if(player.isTeleporting() || player.isMapRegionChanging()) {			
			/*
			 * They are, so an update is required.
			 */
			packet.putBits(1, 1);
			
			/*
			 * This value indicates the player teleported.
			 */
			packet.putBits(2, 3);
			
			/*
			 * This is the new player height.
			 */
			packet.putBits(2, player.getLocation().getZ());
			
			/*
			 * This indicates that the client should discard the walking queue.
			 */
			packet.putBits(1, player.isTeleporting() ? 1 : 0);
			
			/*
			 * This flag indicates if an update block is appended.
			 */
			packet.putBits(1, player.getUpdateFlags().isUpdateRequired() ? 1 : 0);
			
			/*
			 * These are the positions.
			 */
			packet.putBits(7, player.getLocation().getLocalY(player.getLastKnownRegion()));
			packet.putBits(7, player.getLocation().getLocalX(player.getLastKnownRegion()));
		} else {
			/*
			 * Otherwise, check if the player moved.
			 */
			if(player.getSprites().getPrimarySprite() == -1) {
				/*
				 * The player didn't move. Check if an update is required.
				 */
				if(player.getUpdateFlags().isUpdateRequired()) {
					/*
					 * Signifies an update is required.
					 */
					packet.putBits(1, 1);
					
					/*
					 * But signifies that we didn't move.
					 */
					packet.putBits(2, 0);
				} else {
					/*
					 * Signifies that nothing changed.
					 */
					packet.putBits(1, 0);
				}
			} else {
				/*
				 * Check if the player was running.
				 */
				if(player.getSprites().getSecondarySprite() == -1) {
					/*
					 * The player walked, an update is required.
					 */
					packet.putBits(1, 1);
					
					/*
					 * This indicates the player only walked.
					 */
					packet.putBits(2, 1);
					
					/*
					 * This is the player's walking direction.
					 */
					packet.putBits(3, player.getSprites().getPrimarySprite());
					
					/*
					 * This flag indicates an update block is appended.
					 */
					packet.putBits(1, player.getUpdateFlags().isUpdateRequired() ? 1 : 0);
				} else {
					/*
					 * The player ran, so an update is required.
					 */
					packet.putBits(1, 1);
					
					/*
					 * This indicates the player ran.
					 */
					packet.putBits(2, 2);
					
					/*
					 * This is the walking direction.
					 */
					packet.putBits(3, player.getSprites().getPrimarySprite());
					
					/*
					 * And this is the running direction.
					 */
					packet.putBits(3, player.getSprites().getSecondarySprite());
					
					/*
					 * And this flag indicates an update block is appended.
					 */
					packet.putBits(1, player.getUpdateFlags().isUpdateRequired() ? 1 : 0);
				}
			}
		}
	}

}
