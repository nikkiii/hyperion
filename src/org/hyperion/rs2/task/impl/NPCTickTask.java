package org.hyperion.rs2.task.impl;

import org.hyperion.rs2.GameEngine;
import org.hyperion.rs2.model.NPC;
import org.hyperion.rs2.task.Task;

/**
 * A task which performs pre-update tasks for an NPC.
 * @author Graham Edgecombe
 *
 */
public class NPCTickTask implements Task {
	
	/**
	 * The npc who we are performing pre-update tasks for.
	 */
	private NPC npc;
	
	/**
	 * Creates the tick task.
	 * @param npc The npc.
	 */
	public NPCTickTask(NPC npc) {
		this.npc = npc;
	}

	@Override
	public void execute(GameEngine context) {
		/*
		 * If the map region changed set the last known region.
		 */
		if(npc.isMapRegionChanging()) {
			npc.setLastKnownRegion(npc.getLocation());
		}
		
		/*
		 * Process the next movement in the NPC's walking queue.
		 */
		npc.getWalkingQueue().processNextMovement();
	}

}
