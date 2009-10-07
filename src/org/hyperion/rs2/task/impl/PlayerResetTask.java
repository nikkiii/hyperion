package org.hyperion.rs2.task.impl;

import org.hyperion.rs2.GameEngine;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.task.Task;

/**
 * A task which resets a player after an update cycle.
 * @author Graham Edgecombe
 *
 */
public class PlayerResetTask implements Task {
	
	/**
	 * The player to reset.
	 */
	private Player player;
	
	/**
	 * Creates a reset task.
	 * @param player The player to reset.
	 */
	public PlayerResetTask(Player player) {
		this.player = player;
	}

	@Override
	public void execute(GameEngine context) {
		player.getUpdateFlags().reset();
		player.setTeleporting(false);
		player.setMapRegionChanging(false);
		player.resetTeleportTarget();
		player.resetCachedUpdateBlock();
		player.reset();
	}

}
