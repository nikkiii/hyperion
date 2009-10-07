package org.hyperion.rs2.model;

/**
 * <p>Represents a type of NPC.</p>
 * @author Graham Edgecombe
 *
 */
public class NPCDefinition {
	
	/**
	 * Gets an npc definition by its id.
	 * @param id The id.
	 * @return The definition.
	 */
	public static NPCDefinition forId(int id) {
		return new NPCDefinition(id);
	}
	
	/**
	 * The id.
	 */
	private int id;
	
	/**
	 * Creates the definition.
	 * @param id The id.
	 */
	private NPCDefinition(int id) {
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
