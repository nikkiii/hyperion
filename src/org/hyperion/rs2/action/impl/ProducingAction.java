package org.hyperion.rs2.action.impl;

import org.hyperion.rs2.action.Action;
import org.hyperion.rs2.model.Player;

/**
 * <p>A harvesting action is an action where on item is transformed into
 * another, typically this is in skills such as smithing and crafting.</p>
 * 
 * <p>This class implements code related to all production-type skills, such as
 * dealing with the action itself, replacing the items and checking levels.</p>
 * 
 * <p>The individual crafting, smithing, and other skills implement
 * functionality specific to them such as random events.</p>
 * @author Graham Edgecombe
 *
 */
public abstract class ProducingAction extends Action {

	/**
	 * Creates the producing action.
	 * @param player The player to create the action for.
	 */
	public ProducingAction(Player player) {
		super(player, 0);
	}

	@Override
	public QueuePolicy getQueuePolicy() {
		return QueuePolicy.ALWAYS;
	}
	
	@Override
	public WalkablePolicy getWalkablePolicy() {
		return WalkablePolicy.NON_WALKABLE;
	}
	
	/**
	 * Gets the production delay.
	 * @return The delay between consecutive productions.
	 */
	public abstract long getProductionDelay();

	@Override
	public void execute() {
		if(this.getDelay() == 0) {
			this.setDelay(getProductionDelay());
		} else {
			
		}
	}

}
