package org.hyperion.rs2.event.impl;

import org.hyperion.rs2.event.Event;
import org.hyperion.rs2.model.World;
import org.hyperion.rs2.task.impl.CleanupTask;

/**
 * An event which runs periodically and performs tasks such as garbage
 * collection.
 * @author Graham Edgecombe
 *
 */
public class CleanupEvent extends Event {

	/**
	 * The delay in milliseconds between consecutive cleanups.
	 */
	public static final int CLEANUP_CYCLE_TIME = 300000;
	
	/**
	 * Creates the cleanup event to run every 5 minutes.
	 */
	public CleanupEvent() {
		super(CLEANUP_CYCLE_TIME);
	}

	@Override
	public void execute() {
		World.getWorld().submit(new CleanupTask());
	}

}
