package org.hyperion.rs2.model;

/**
 * Represents a single type of object.
 * @author Graham Edgecombe
 *
 */
public class GameObjectDefinition {

	/**
	 * Gets an object definition by its id.
	 * @param id The id.
	 * @return The definition.
	 */
	public static GameObjectDefinition forId(int id) {
		return new GameObjectDefinition(id);
	}
	
	/**
	 * The id.
	 */
	private int id;
	
	/**
	 * Creates the definition.
	 * @param id The id.
	 */
	private GameObjectDefinition(int id) {
		this.id = id;
	}
	
	/**
	 * Gets the id.
	 * @return The id.
	 */
	public int getId() {
		return this.id;
	}
	
}
