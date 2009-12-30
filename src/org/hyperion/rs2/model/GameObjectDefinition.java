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
	 * Solid flag.
	 */
	private boolean solid;
	
	/**
	 * Walkable flag.
	 */
	private boolean walkable;
	
	/**
	 * 'Has actions' flag.
	 */
	private boolean hasActions;
	
	/**
	 * Creates the definition.
	 * @param id The id.
	 * @param name The name of the object.
	 * @param desc The description of the object.
	 * @param sizeX The x size of the object.
	 * @param sizeY The y size of the object.
	 * @param isSolid Solid flag.
	 * @param isWalkable  Walkable flag.
	 * @param hasActions Flag which indicates if this object has any actions.
	 */
	public GameObjectDefinition(int id, String name, String desc, int sizeX, int sizeY, boolean isSolid, boolean isWalkable, boolean hasActions) {
		this.id = id;
		this.name = name;
		this.desc = desc;
		this.sizeX = sizeX;
		this.sizeY = sizeY;
		this.solid = isSolid;
		this.walkable = isWalkable;
		this.hasActions = hasActions;
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
	
	/**
	 * Checks if this object is solid.
	 * @return The solid flag.
	 */
	public boolean isSolid() {
		return solid;
	}
	
	/**
	 * Checks if this object is walkable.
	 * @return The walkable flag.
	 */
	public boolean isWalkable() {
		return walkable;
	}
	
	/**
	 * Checks if this object has any actions.
	 * @return A flag indicating that this object has some actions.
	 */
	public boolean hasActions() {
		return hasActions;
	}
	
}
