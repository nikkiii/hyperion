package org.hyperion.rs2.event.impl;

import org.hyperion.rs2.event.Event;

/**
 * Handles all events related to combat.
 * @author Brett
 *
 */
public class CombatEvent extends Event {
	/**
	 * The cycle time, in milliseconds.
	 */
	public static final int CYCLE_TIME = 600;
	
	/**
	 * Creates the update event to cycle every 600 milliseconds.
	 */
	public CombatEvent() {
		super(CYCLE_TIME);
	}

	@Override
	public void execute() {
		// TODO Auto-generated method stub
		
	}
}
