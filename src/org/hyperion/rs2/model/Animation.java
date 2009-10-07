package org.hyperion.rs2.model;

/**
 * Represents a single animation request.
 * @author Graham Edgecombe
 *
 */
public class Animation {

	/**
	 * Different animation constants.
	 */
	public final static Animation YES_EMOTE = create(855);
	public final static Animation NO_EMOTE = create(856);
	public final static Animation THINKING = create(857);
	public final static Animation BOW = create(858);
	public final static Animation ANGRY = create(859);
	public final static Animation CRY = create(860);
	public final static Animation LAUGH = create(861);
	public final static Animation CHEER = create(862);
	public final static Animation WAVE = create(863);
	public final static Animation BECKON = create(864);
	public final static Animation CLAP = create(865);
	public final static Animation DANCE = create(866);
	public final static Animation PANIC = create(2105);
	public final static Animation JIG = create(2106);
	public final static Animation SPIN = create(2107);
	public final static Animation HEADBANG = create(2108);
	public final static Animation JOYJUMP = create(2109);
	public final static Animation RASPBERRY = create(2110);
	public final static Animation YAWN = create(2111);
	public final static Animation SALUTE = create(2112);
	public final static Animation SHRUG = create(2113);
	public final static Animation BLOW_KISS = create(1368);
	public final static Animation GLASS_WALL = create(1128);
	public final static Animation LEAN = create(1129);
	public final static Animation CLIMB_ROPE = create(1130);
	public final static Animation GLASS_BOX = create(1131);
	public final static Animation GOBLIN_BOW = create(2127);
	public final static Animation GOBLIN_DANCE = create(2128);
	
	/**
	 * Creates an animation with no delay.
	 * @param id The id.
	 * @return The new animation object.
	 */
	public static Animation create(int id) {
		return create(id, 0);
	}
	
	/**
	 * Creates an animation.
	 * @param id The id.
	 * @param delay The delay.
	 * @return The new animation object.
	 */
	public static Animation create(int id, int delay) {
		return new Animation(id, delay);
	}
	
	/**
	 * The id.
	 */
	private int id;
	
	/**
	 * The delay.
	 */
	private int delay;
	
	/**
	 * Creates an animation.
	 * @param id The id.
	 * @param delay The delay.
	 */
	private Animation(int id, int delay) {
		this.id = id;
		this.delay = delay;
	}
	
	/**
	 * Gets the id.
	 * @return The id.
	 */
	public int getId() {
		return id;
	}
	
	/**
	 * Gets the delay.
	 * @return The delay.
	 */
	public int getDelay() {
		return delay;
	}

}
