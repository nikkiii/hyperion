package org.hyperion.rs2.event;

/**
 * Represents a task that is executed in the future, once or periodically.
 * @author Graham Edgecombe
 *
 */
public abstract class Event {
	
	/**
	 * The delay, in milliseconds.
	 */
	private long delay;
	
	/**
	 * The running flag.
	 */
	private boolean running = true;
	
	/**
	 * Creates an event with the specified delay.
	 * @param delay The delay.
	 */
	public Event(long delay) {
		this.delay = delay;
	}
	
	/**
	 * Gets the event delay.
	 * @return The delay, in milliseconds.
	 */
	public long getDelay() {
		return delay;
	}
	
	/**
	 * Sets the event delay.
	 * @param delay The delay to set.
	 * @throws IllegalArgumentException if the delay is negative.
	 */
	public void setDelay(long delay) {
		if(delay < 0) {
			throw new IllegalArgumentException("Delay must be positive.");
		}
		this.delay = delay;
	}
	
	/**
	 * Checks if the event is running.
	 * @return <code>true</code> if the event is still running, <code>false</code> if not.
	 */
	public boolean isRunning() {
		return running;
	}
	
	/**
	 * Stops the event from running in the future.
	 */
	public void stop() {
		running = false;
	}
	
	/**
	 * The execute method is called when the event is run. The general contract
	 * of the execute method is that it may take any action whatsoever.
	 */
	public abstract void execute();

}
