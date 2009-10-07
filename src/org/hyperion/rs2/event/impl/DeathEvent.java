package org.hyperion.rs2.event.impl;

import org.hyperion.rs2.event.Event;
import org.hyperion.rs2.model.Entity;
import org.hyperion.rs2.model.NPC;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.Skills;
import org.hyperion.rs2.model.World;

/**
 * The death event handles player and npc deaths. Drops loot, does animation, teleportation, etc.
 * @author Graham
 *
 */
@SuppressWarnings("unused")
public class DeathEvent extends Event {
	
	private Entity entity;

	/**
	 * Creates the death event for the specified entity.
	 * @param entity The player or npc whose death has just happened.
	 */
	public DeathEvent(Entity entity) {
		super(3500);
		this.entity = entity;
	}

	@Override
	public void execute() {
		if(entity instanceof Player) {
			Player p = (Player) entity;
			p.getSkills().setLevel(Skills.HITPOINTS, p.getSkills().getLevelForExperience(Skills.HITPOINTS));
			entity.setDead(false);
			entity.setTeleportTarget(Entity.DEFAULT_LOCATION);
			p.getActionSender().sendMessage("Oh dear, you are dead!");
			this.stop();
		}
	}

}