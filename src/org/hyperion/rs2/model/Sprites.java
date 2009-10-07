package org.hyperion.rs2.model;

/**
 * Represents walking and running directions.
 * @author Graham Edgecombe
 *
 */
public class Sprites {
	
	/**
	 * The walking direction.
	 */
	private int primary = -1;
	
	/**
	 * The running direction.
	 */
	private int secondary = -1;
	
	/**
	 * Gets the primary sprite.
	 * @return The primary sprite.
	 */
	public int getPrimarySprite() {
		return primary;
	}
	
	/**
	 * Gets the secondary sprite.
	 * @return The secondary sprite.
	 */
	public int getSecondarySprite() {
		return secondary;
	}
	
	/**
	 * Sets the sprites.
	 * @param primary The primary sprite.
	 * @param secondary The secondary sprite.
	 */
	public void setSprites(int primary, int secondary) {
		this.primary = primary;
		this.secondary = secondary;
	}

}
