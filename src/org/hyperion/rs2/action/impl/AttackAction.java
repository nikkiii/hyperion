package org.hyperion.rs2.action.impl;

import org.hyperion.rs2.action.Action;
import org.hyperion.rs2.model.Entity;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.Combat;
import org.hyperion.rs2.model.Combat.AttackType;
import org.hyperion.rs2.model.EntityCooldowns.CooldownFlags;

/**
 * Handles an action for an attacking player.
 * @author Brett
 *
 */
public class AttackAction extends Action {
	
	/**
	 * The victim of this attack action.
	 */
	private Entity victim;
	
	/**
	 * The type of attack.
	 */
	private AttackType type = AttackType.MELEE;
	
	/**
	 * Constructor method for this action.
	 * @param player The attacker.
	 * @param victim The attacked.
	 * @param type The type of attack.
	 */
	public AttackAction(Player player, Entity victim) {
		super(player, 300);
		this.victim = victim;
	}

	@Override
	public QueuePolicy getQueuePolicy() {
		return QueuePolicy.NEVER;
	}

	@Override
	public WalkablePolicy getWalkablePolicy() {
		return WalkablePolicy.FOLLOW;
	}

	
	@Override
	public void execute() {
		final Player player = getPlayer();
		if(Combat.canAttack(player, victim)) {
			if(!player.getEntityCooldowns().get(CooldownFlags.MELEE_SWING)) {
				player.setInCombat(true);
				player.setAggressorState(true);
				Combat.doAttack(player, victim, type);
				player.getEntityCooldowns().flag(CooldownFlags.MELEE_SWING, Combat.getAttackSpeed(player), player);
			}
		} else {
			this.stop();
		}
	}

}
