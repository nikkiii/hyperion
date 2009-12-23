package org.hyperion.rs2.model;

/**
 * Represents a single type of object.
 * @author Graham Edgecombe
 *
 */
public class GameObjectDefinition {
	
	/**
	 * The maximum number of object definitions
	 */
	public static final int MAX_DEFINITIONS = 9399;
	
	/**
	 * The definitions array.
	 */
	private static GameObjectDefinition[] definitions = new GameObjectDefinition[MAX_DEFINITIONS];

	/**
	 * Adds a definition. TODO better way?
	 * @param def The definition.
	 */
	static void addDefinition(GameObjectDefinition def) {
		definitions[def.getId()] = def;
	}
	
	/**
	 * Gets an object definition by its id.
	 * @param id The id.
	 * @return The definition.
	 */
	public static GameObjectDefinition forId(int id) {
		return definitions[id];
	}
	
	/**
	 * The id.
	 */
	private int id;
	
	/**
	 * The name.
	 */
	private String name;
	
	/**
	 * The description.
	 */
	private String desc;
	
	/**
	 * X size.
	 */
	private int sizeX;
	
	/**
	 * Y size.
	 */
	private int sizeY;
	
	/**
	 * Creates the definition.
	 * @param id The id.
	 * @param name The name of the object.
	 * @param desc The description of the object.
	 * @param sizeX The x size of the object.
	 * @param sizeY The y size of the object.
	 */
	public GameObjectDefinition(int id, String name, String desc, int sizeX, int sizeY) {
		this.id = id;
		this.name = name;
		this.desc = desc;
		this.sizeX = sizeX;
		this.sizeY = sizeY;
	}
	
	/**
	 * Gets the id.
	 * @return The id.
	 */
	public int getId() {
		return this.id;
	}
	
	/**
	 * Gets the name.
	 * @return The name.
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Gets the description.
	 * @return The description.
	 */
	public String getDescription() {
		return desc;
	}
	
	/**
	 * Gets the x size.
	 * @return The x size.
	 */
	public int getSizeX() {
		return sizeX;
	}
	
	/**
	 * Gets the y size.
	 * @return The y size.
	 */
	public int getSizeY() {
		return sizeY;
	}
	
}
