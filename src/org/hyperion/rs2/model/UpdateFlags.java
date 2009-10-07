package org.hyperion.rs2.model;

import java.util.BitSet;

/**
 * Holds update flags.
 * @author Graham Edgecombe
 *
 */
public class UpdateFlags {
	
	/**
	 * The bitset (flag data).
	 */
	private BitSet flags = new BitSet();
	
	/**
	 * Represents a single type of update flag.
	 * @author Graham Edgecombe
	 *
	 */
	public enum UpdateFlag {
		
		/**
		 * Appearance update.
		 */
		APPEARANCE,
		
		/**
		 * Chat update.
		 */
		CHAT,
		
		/**
		 * Graphics update.
		 */
		GRAPHICS,
		
		/**
		 * Animation update.
		 */
		ANIMATION,
		
		/**
		 * Forced chat update.
		 */
		FORCED_CHAT,
		
		/**
		 * Interacting entity update.
		 */
		FACE_ENTITY,
		
		/**
		 * Face coordinate entity update.
		 */
		FACE_COORDINATE,
		
		/**
		 * Hit update.
		 */
		HIT,
		
		/**
		 * Hit 2 update/
		 */
		HIT_2,
		
		/**
		 * Update flag used to transform npc to another.
		 */
		TRANSFORM,
	}
	
	/**
	 * Checks if an update required.
	 * @return <code>true</code> if 1 or more flags are set,
	 * <code>false</code> if not.
	 */
	public boolean isUpdateRequired() {
		return !flags.isEmpty();
	}
	
	/**
	 * Flags (sets to true) a flag.
	 * @param flag The flag to flag.
	 */
	public void flag(UpdateFlag flag) {
		flags.set(flag.ordinal(), true);
	}
	
	/**
	 * Sets a flag.
	 * @param flag The flag.
	 * @param value The value.
	 */
	public void set(UpdateFlag flag, boolean value) {
		flags.set(flag.ordinal(), value);
	}
	
	/**
	 * Gets the value of a flag.
	 * @param flag The flag to get the value of.
	 * @return The flag value.
	 */
	public boolean get(UpdateFlag flag) {
		return flags.get(flag.ordinal());
	}
	
	/**
	 * Resest all update flags.
	 */
	public void reset() {
		flags.clear();
	}

}
