package org.hyperion.rs2.action;

import java.util.LinkedList;
import java.util.Queue;

import org.hyperion.rs2.model.World;
import org.hyperion.rs2.model.container.Inventory;

/**
 * Stores a queue of pending actions.
 * @author blakeman8192
 * @author Graham Edgecombe
 *
 */
public class ActionQueue {
	
	/**
	 * The maximum number of actions allowed to be queued at once, deliberately
	 * set to the size of the player's inventory.
	 */
	public static final int MAXIMUM_SIZE = Inventory.SIZE;
	
	/**
	 * A queue of <code>Action</code> objects.
	 */
	private final Queue<Action> queuedActions = new LinkedList<Action>();
	
	/**
	 * The current action.
	 */
	private Action currentAction = null;
	
	/**
	 * Cancels all queued action events.
	 */
	public void cancelQueuedActions() {
		for(Action actionEvent : queuedActions) {
			actionEvent.stop();
		}
		queuedActions.clear();
		if(currentAction != null)
			currentAction.stop();
		currentAction = null;
	}
	
	/**
	 * Adds an <code>Action</code> to the queue.
	 * @param action The action.
	 */
	public void addAction(Action action) {
		if(queuedActions.size() >= MAXIMUM_SIZE) {
			return;
		}
		int queueSize = queuedActions.size() + (currentAction == null ? 0 : 1);
		switch(action.getQueuePolicy()) {
		case ALWAYS:
			break;
		case NEVER:
			if(queueSize > 0) {
				return;
			}
			break;
		}
		queuedActions.add(action);
		processNextAction();
	}
	
	/**
	 * Purges actions in the queue with a <code>WalkablePolicy</code> of <code>NON_WALKABLE</code>.
	 */
	public void clearNonWalkableActions() {
		if(currentAction != null) {
			switch(currentAction.getWalkablePolicy()) {
			case WALKABLE:
				break;
			case NON_WALKABLE:
				currentAction.stop();
				currentAction = null;
				break;
			case FOLLOW:
				currentAction.stop();
				currentAction = null;
				break;
			}
		}
		for(Action actionEvent : queuedActions) {
			switch(actionEvent.getWalkablePolicy()) {
			case WALKABLE:
				break;
			case NON_WALKABLE:
				actionEvent.stop();
				queuedActions.remove(actionEvent);
				break;
			case FOLLOW:
				actionEvent.stop();
				queuedActions.remove(actionEvent);
				break;
			}
		}
	}

	/**
	 * Processes this next action.
	 */
	public void processNextAction() {
		if(currentAction != null) {
			if(currentAction.isRunning()) {
				return;
			} else {
				currentAction = null;
			}
		}
		if(queuedActions.size() > 0) {
			currentAction = queuedActions.poll();
			World.getWorld().submit(currentAction);
		}
	}

}
