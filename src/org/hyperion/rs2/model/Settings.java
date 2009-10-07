package org.hyperion.rs2.model;

/**
 * Contains client-side settings.
 * @author Graham Edgecombe
 *
 */
public class Settings {
	
	/**
	 * Withdraw as notes flag.
	 */
	private boolean withdrawAsNotes = false;
	
	/**
	 * Swapping flag.
	 */
	private boolean swapping = true;
	
	/**
	 * Sets the withdraw as notes flag.
	 * @param withdrawAsNotes The flag.
	 */
	public void setWithdrawAsNotes(boolean withdrawAsNotes) {
		this.withdrawAsNotes = withdrawAsNotes;
	}
	
	/**
	 * Sets the swapping flag.
	 * @param swapping The swapping flag.
	 */
	public void setSwapping(boolean swapping) {
		this.swapping = swapping;
	}
	
	/**
	 * Checks if the player is withdrawing as notes.
	 * @return The withdrawing as notes flag.
	 */
	public boolean isWithdrawingAsNotes() {
		return withdrawAsNotes;
	}
	
	/**
	 * Checks if the player is swapping.
	 * @return The swapping flag.
	 */
	public boolean isSwapping() {
		return swapping;
	}

}
