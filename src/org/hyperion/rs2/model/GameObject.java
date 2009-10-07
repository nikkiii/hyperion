package org.hyperion.rs2.model;

/**
 * Represents a single game object.
 * @author Graham Edgecombe
 *
 */
public class GameObject {

	/**
	 * The location.
	 */
	private Location location;
	
	/**
	 * The definition.
	 */
	private GameObjectDefinition definition;
	
	/**
	 * The type.
	 */
	private int type;
	
	/**
	 * The rotation.
	 */
	private int rotation;
	
	/**
	 * Creates the game object.
	 * @param definition The definition.
	 * @param location The location.
	 * @param type The type.
	 * @param rotation The rotation.
	 */
	public GameObject(GameObjectDefinition definition, Location location, int type, int rotation) {
		this.definition = definition;
		this.location = location;
		this.type = type;
		this.rotation = rotation;
	}
	
	/**
	 * Gets the location.
	 * @return The location.
	 */
	public Location getLocation() {
		return location;
	}
	
	/**
	 * Gets the definition.
	 * @return The definition.
	 */
	public GameObjectDefinition getDefinition() {
		return definition;
	}
	
	/**
	 * Gets the type.
	 * @return The type.
	 */
	public int getType() {
		return type;
	}
	
	/**
	 * Gets the rotation.
	 * @return The rotation.
	 */
	public int getRotation() {
		return rotation;
	}

}
