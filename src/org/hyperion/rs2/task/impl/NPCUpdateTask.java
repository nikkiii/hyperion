package org.hyperion.rs2.task.impl;

import java.util.Iterator;

import org.hyperion.rs2.GameEngine;
import org.hyperion.rs2.model.Entity;
import org.hyperion.rs2.model.Location;
import org.hyperion.rs2.model.NPC;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.UpdateFlags;
import org.hyperion.rs2.model.World;
import org.hyperion.rs2.model.UpdateFlags.UpdateFlag;
import org.hyperion.rs2.net.Packet;
import org.hyperion.rs2.net.PacketBuilder;
import org.hyperion.rs2.task.Task;

/**
 * A task which creates and sends the NPC update block.
 * @author Graham Edgecombe
 *
 */
public class NPCUpdateTask implements Task {
	
	/**
	 * The player.
	 */
	private Player player;
	
	/**
	 * Creates an npc update task.
	 * @param player The player.
	 */
	public NPCUpdateTask(Player player) {
		this.player = player;
	}

	@Override
	public void execute(GameEngine context) {
		/*
		 * The update block holds the update masks and data, and is written
		 * after the main block.
		 */
		PacketBuilder updateBlock = new PacketBuilder();
		
		/*
		 * The main packet holds information about adding, moving and removing
		 * NPCs.
		 */
		PacketBuilder packet = new PacketBuilder(65, Packet.Type.VARIABLE_SHORT);
		packet.startBitAccess();
		
		/*
		 * Write the current size of the npc list.
		 */
		packet.putBits(8, player.getLocalNPCs().size());
		
		/*
		 * Iterate through the local npc list.
		 */
		for(Iterator<NPC> it$ = player.getLocalNPCs().iterator(); it$.hasNext();) {
			/*
			 * Get the next NPC.
			 */
			NPC npc = it$.next();
			
			/*
			 * If the NPC should still be in our list.
			 */
			if(World.getWorld().getNPCs().contains(npc) && !npc.isTeleporting() && npc.getLocation().isWithinDistance(player.getLocation())) {
				/*
				 * Update the movement.
				 */
				updateNPCMovement(packet, npc);
				
				/*
				 * Check if an update is required, and if so, send the update.
				 */
				if(npc.getUpdateFlags().isUpdateRequired()) {
					updateNPC(updateBlock, npc);
				}
			} else {
				/*
				 * Otherwise, remove the NPC from the list.
				 */
				it$.remove();
								
				/*
				 * Tell the client to remove the NPC from the list.
				 */
				packet.putBits(1, 1);
				packet.putBits(2, 3);
			}
		}
		
		/*
		 * Loop through all NPCs in the world.
		 */
		for(NPC npc : World.getWorld().getRegionManager().getLocalNpcs(player)) {
			/*
			 * Check if there is room left in the local list.
			 */
			if(player.getLocalNPCs().size() >= 255) {
				/*
				 * There is no more room left in the local list. We cannot add
				 * more NPCs, so we just ignore the extra ones. They will be
				 * added as other NPCs get removed.
				 */
				break;
			}
			
			/*
			 * If they should not be added ignore them.
			 */
			if(player.getLocalNPCs().contains(npc)) {
				continue;
			}
			
			/*
			 * Add the npc to the local list if it is within distance.
			 */
			player.getLocalNPCs().add(npc);
			
			/*
			 * Add the npc in the packet.
			 */
			addNewNPC(packet, npc);
			
			/*
			 * Check if an update is required.
			 */
			if(npc.getUpdateFlags().isUpdateRequired()) {
			
				/*
				 * If so, update the npc.
				 */
				updateNPC(updateBlock, npc);
			
			}
		}
		
		/*
		 * Check if the update block isn't empty.
		 */
		if(!updateBlock.isEmpty()) {
			/*
			 * If so, put a flag indicating that an update block follows.
			 */
			packet.putBits(14, 16383);
			packet.finishBitAccess();
			
			/*
			 * And append the update block.
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
	 * Adds a new NPC.
	 * @param packet The main packet.
	 * @param npc The npc to add.
	 */
	private void addNewNPC(PacketBuilder packet, NPC npc) {
		/*
		 * Write the NPC's index.
		 */
		packet.putBits(14, npc.getIndex());
		
		/*
		 * Calculate the x and y offsets.
		 */
		int yPos = npc.getLocation().getY() - player.getLocation().getY();
		int xPos = npc.getLocation().getX() - player.getLocation().getX();
		
		/*
		 * And write them.
		 */
		packet.putBits(5, yPos);
		packet.putBits(5, xPos);
		
		/*
		 * TODO unsure, but probably discards the client-side walk queue.
		 */
		packet.putBits(1, 0);
		
		/*
		 * We now write the NPC type id.
		 */
		packet.putBits(12, npc.getDefinition().getId());
		
		/*
		 * And indicate if an update is required.
		 */
		packet.putBits(1, npc.getUpdateFlags().isUpdateRequired() ? 1 : 0);
	}

	/**
	 * Update an NPC's movement.
	 * @param packet The main packet.
	 * @param npc The npc.
	 */
	private void updateNPCMovement(PacketBuilder packet, NPC npc) {		
		/*
		 * Check if the NPC is running.
		 */
		if(npc.getSprites().getSecondarySprite() == -1) {
			/*
			 * They are not, so check if they are walking.
			 */
			if(npc.getSprites().getPrimarySprite() == -1) {
				/*
				 * They are not walking, check if the NPC needs an update.
				 */
				if(npc.getUpdateFlags().isUpdateRequired()) {
					/*
					 * Indicate an update is required.
					 */
					packet.putBits(1, 1);
					
					/*
					 * Indicate we didn't move.
					 */
					packet.putBits(2, 0);
				} else {
					/*
					 * Indicate no update or movement is required.
					 */
					packet.putBits(1, 0);
				}
			} else {
				/*
				 * They are walking, so indicate an update is required.
				 */
				packet.putBits(1, 1);
				
				/*
				 * Indicate the NPC is walking 1 tile.
				 */
				packet.putBits(2, 1);
				
				/*
				 * And write the direction.
				 */
				packet.putBits(3, npc.getSprites().getPrimarySprite());
				
				/*
				 * And write the update flag.
				 */
				packet.putBits(1, npc.getUpdateFlags().isUpdateRequired() ? 1 : 0);
			}
		} else {
			/*
			 * They are running, so indicate an update is required.
			 */
			packet.putBits(1, 1);

			/*
			 * Indicate the NPC is running 2 tiles.
			 */
			packet.putBits(2, 2);
			
			/*
			 * And write the directions.
			 */
			packet.putBits(3, npc.getSprites().getPrimarySprite());
			packet.putBits(3, npc.getSprites().getSecondarySprite());
			
			/*
			 * And write the update flag.
			 */
			packet.putBits(1, npc.getUpdateFlags().isUpdateRequired() ? 1 : 0);
		}
	}
	
	/**
	 * Update an NPC.
	 * @param packet The update block.
	 * @param npc The npc.
	 */
	private void updateNPC(PacketBuilder packet, NPC npc) {
		/*
		 * Calculate the mask.
		 */
		int mask = 0;
		final UpdateFlags flags = npc.getUpdateFlags();
		
		if(flags.get(UpdateFlag.ANIMATION)) {
			mask |= 0x10;
		}
		if(flags.get(UpdateFlag.HIT)) {
			mask |= 0x8;
		}
		if(flags.get(UpdateFlag.GRAPHICS)) {
			mask |= 0x80;
		}
		if(flags.get(UpdateFlag.FACE_ENTITY)) {
			mask |= 0x20;
		}
		if(flags.get(UpdateFlag.FORCED_CHAT)) {
			mask |= 0x1;
		}
		if(flags.get(UpdateFlag.HIT_2)) {
			mask |= 0x40;
		}
		if(flags.get(UpdateFlag.TRANSFORM)) {
			mask |= 0x2;
		}
		if(flags.get(UpdateFlag.FACE_COORDINATE)) {
			mask |= 0x4;
		}
		
		/*
		 * And write the mask.
		 */
		packet.put((byte) mask);
		
		if(flags.get(UpdateFlag.ANIMATION)) {
			packet.putLEShort(npc.getCurrentAnimation().getId());
			packet.put((byte) npc.getCurrentAnimation().getDelay());
		}
		if(flags.get(UpdateFlag.HIT)) {
			
		}
		if(flags.get(UpdateFlag.GRAPHICS)) {
			packet.putShort(npc.getCurrentGraphic().getId());
			packet.putInt(npc.getCurrentGraphic().getDelay());
		}
		if(flags.get(UpdateFlag.FACE_ENTITY)) {
			Entity entity = npc.getInteractingEntity();
			packet.putShort(entity == null ? -1 : entity.getClientIndex());
		}
		if(flags.get(UpdateFlag.FORCED_CHAT)) {
			
		}
		if(flags.get(UpdateFlag.HIT_2)) {
			
		}
		if(flags.get(UpdateFlag.TRANSFORM)) {
			
		}
		if(flags.get(UpdateFlag.FACE_COORDINATE)) {
			Location loc = npc.getFaceLocation();
			if(loc == null) {
				packet.putLEShort(0);
				packet.putLEShort(0);
			} else {
				packet.putLEShort(loc.getX() * 2 + 1);
				packet.putLEShort(loc.getY() * 2 + 1);
			}
		}
	}

}
