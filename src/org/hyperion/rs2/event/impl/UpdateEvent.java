package org.hyperion.rs2.event.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.hyperion.rs2.event.Event;
import org.hyperion.rs2.model.NPC;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.World;
import org.hyperion.rs2.task.ConsecutiveTask;
import org.hyperion.rs2.task.ParallelTask;
import org.hyperion.rs2.task.Task;
import org.hyperion.rs2.task.impl.NPCResetTask;
import org.hyperion.rs2.task.impl.NPCTickTask;
import org.hyperion.rs2.task.impl.NPCUpdateTask;
import org.hyperion.rs2.task.impl.PlayerResetTask;
import org.hyperion.rs2.task.impl.PlayerTickTask;
import org.hyperion.rs2.task.impl.PlayerUpdateTask;

/**
 * An event which starts player update tasks.
 * @author Graham Edgecombe
 *
 */
public class UpdateEvent extends Event {

	/**
	 * The cycle time, in milliseconds.
	 */
	public static final int CYCLE_TIME = 600;
	
	/**
	 * Creates the update event to cycle every 600 milliseconds.
	 */
	public UpdateEvent() {
		super(CYCLE_TIME);
	}

	@Override
	public void execute() {
		List<Task> tickTasks = new ArrayList<Task>();
		List<Task> updateTasks = new ArrayList<Task>();
		List<Task> resetTasks = new ArrayList<Task>();
		
		for(NPC npc : World.getWorld().getNPCs()) {
			tickTasks.add(new NPCTickTask(npc));
			resetTasks.add(new NPCResetTask(npc));
		}
		
		Iterator<Player> it$ = World.getWorld().getPlayers().iterator();
		while(it$.hasNext()) {
			Player player = it$.next();
			if(!player.getSession().isConnected()) {
				it$.remove();
			} else {
				tickTasks.add(new PlayerTickTask(player));
				updateTasks.add(new ConsecutiveTask(new PlayerUpdateTask(player), new NPCUpdateTask(player)));
				resetTasks.add(new PlayerResetTask(player));
			}
		}
		
		// ticks can no longer be parallel due to region code
		Task tickTask = new ConsecutiveTask(tickTasks.toArray(new Task[0]));
		Task updateTask = new ParallelTask(updateTasks.toArray(new Task[0]));
		Task resetTask = new ParallelTask(resetTasks.toArray(new Task[0]));
		
		World.getWorld().submit(new ConsecutiveTask(tickTask, updateTask, resetTask));
	}

}
