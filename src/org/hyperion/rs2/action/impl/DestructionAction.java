package org.hyperion.rs2.action.impl;

import org.hyperion.rs2.action.Action;
import org.hyperion.rs2.model.Player;

/**
 * <p>A destruction action is one where an item is used and lost forever, such
 * as bone burying (for the prayer skill).</p>
 * 
 * <p>The destruction action class handles functionality common to all
 * destruction-type skills.</p>
 * 
 * <p>The individual skills handle specific functionality.</p>
 * @author Graham Edgecombe
 *
 */
public abstract class DestructionAction extends Action {

	/**
	 * Creates the destruction action for the specified player.
	 * @param player The player to create the action for.
	 */
	public DestructionAction(Player player) {
		super(player, 0);
	}
	
	@Override
	public QueuePolicy getQueuePolicy() {
		return QueuePolicy.NEVER;
	}
	
	@Override
	public WalkablePolicy getWalkablePolicy() {
		return WalkablePolicy.WALKABLE;
	}
	
	/**
	 * Gets the destruction delay.
	 * @return The delay between consecutive destructions.
	 */
	public abstract long getDestructionDelay();

	@Override
	public void execute() {
		if(this.getDelay() == 0) {
			this.setDelay(getDestructionDelay());
		} else {
			
		}
	}

}
