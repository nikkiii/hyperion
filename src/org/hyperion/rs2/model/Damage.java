package org.hyperion.rs2.model;

/**
 * Represents a single instance of damage.
 * @author Graham
 *
 */
public class Damage {
	
	/**
	 * Represents the four types of damage.
	 * @author Graham
	 *
	 */
	public static enum HitType {
		NO_DAMAGE		(0),			// blue
		NORMAL_DAMAGE	(1),		// red
		POISON_DAMAGE	(2),		// green
		DISEASE_DAMAGE	(3);	// orange
		
		private final int type;
		
		/**
		 * Constructor.
		 * @param type The corresponding integer for damage type.
		 */
		private HitType(int type) {
			this.type = type;
		}
		
		/**
		 * Get the damage type.
		 * @return The damage type, as an <code>int</code>.
		 */
		public int getType() {
			return this.type;
		}
	}
	
	/**
	 * Nested class <code>Hit</code>, handling a single hit.
	 * @author Graham
	 *
	 */
	public static class Hit {
		
		private HitType type;
		private int damage;
		
		public Hit(int damage, HitType type) {
			this.type = type;
			this.damage = damage;
		}
		
		public HitType getType() {
			return type;
		}
		
		public int getDamage() {
			return damage;
		}
	}
	
	/**
	 * Constructor method.
	 */
	public Damage() {
		hit1 = null;
		hit2 = null;
	}
	
	/**
	 * Represents a hit.
	 */
	private Hit hit1;
	private Hit hit2;
	
	/**
	 * Sets a hit.
	 * @param hit The hit to clone.
	 */
	public void setHit1(Hit hit) {
		this.hit1 = hit;
	}
	
	public void setHit2(Hit hit) {
		this.hit2 = hit;
	}

	/**
	 * Gets the hit damage.
	 * @return An <code>int</code> of the damage of this hit.
	 */
	public int getHitDamage1() {
		if(hit1 == null) {
			return 0;
		}
		return hit1.damage;
	}
	
	public int getHitDamage2() {
		if(hit2 == null) {
			return 0;
		}
		return hit2.damage;
	}
	
	/**
	 * Gets the hit type.
	 * @return The type of hit.
	 */
	public int getHitType1() {
		if(hit1 == null) {
			return HitType.NO_DAMAGE.getType();
		}
		return hit1.type.getType();
	}
	
	public int getHitType2() {
		if(hit2 == null) {
			return HitType.NO_DAMAGE.getType();
		}
		return hit2.type.getType();
	}
	
	/**
	 * Destroy this hit.
	 */
	public void clear() {
		hit1 = null;
	}

}
