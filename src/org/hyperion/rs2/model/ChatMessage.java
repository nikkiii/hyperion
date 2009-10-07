package org.hyperion.rs2.model;

/**
 * Represents a single chat message.
 * @author Graham Edgecombe
 *
 */
public class ChatMessage {
	
	/**
	 * The colour.
	 */
	private int colour;
	
	/**
	 * The effects.
	 */
	private int effects;
	
	/**
	 * The packed chat text.
	 */
	private byte[] text;
	
	/**
	 * Creates a new chat message.
	 * @param colour The message colour.
	 * @param effects The message effects.
	 * @param text The packed chat text.
	 */
	public ChatMessage(int colour, int effects, byte[] text) {
		this.colour = colour;
		this.effects = effects;
		this.text = text;
	}
	
	/**
	 * Gets the message colour.
	 * @return The message colour.
	 */
	public int getColour() {
		return colour;
	}
	
	/**
	 * Gets the message effects.
	 * @return The message effects.
	 */
	public int getEffects() {
		return effects;
	}
	
	/**
	 * Gets the packed message text.
	 * @return The packed message text.
	 */
	public byte[] getText() {
		return text;
	}

}
