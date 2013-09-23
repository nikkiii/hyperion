package org.hyperion.rs2.event;

import java.util.concurrent.TimeUnit;

import org.hyperion.rs2.GameEngine;

/**
 * A class that manages <code>Event</code>s for a specific
 * <code>GameEngine</code>.
 * @author Graham Edgecombe
 *
 */
public class EventManager {
	
	/**
	 * The <code>GameEngine</code> to manager events for.
	 */
	private GameEngine engine;

	/**
	 * Creates an <code>EventManager</code> for the specified
	 * <code>GameEngine</code>.
	 * @param engine The game engine the manager is managing events for.
	 */
	public EventManager(GameEngine engine) {
		this.engine = engine;
	}
	
	/**
	 * Submits a new event to the <code>GameEngine</code>.
	 * @param event The event to submit.
	 */
	public void submit(final Event event) {
		submit(event, event.getDelay());
	}
	
	/**
	 * Schedules an event to run after the specified delay.
	 * @param event The event.
	 * @param delay The delay.
	 */
	private void submit(final Event event, final long delay) {
		engine.scheduleLogic(new Runnable() {
			@Override
			public void run() {
				long start = System.currentTimeMillis();
				if(event.isRunning()) {
					event.execute();
				} else {
					return;
				}
				long elapsed = System.currentTimeMillis() - start;
				long remaining = event.getDelay() - elapsed;
				if(remaining <= 0) {
					remaining = 0;
				}
				submit(event, remaining);
			}
		}, delay, TimeUnit.MILLISECONDS);
	}

}
