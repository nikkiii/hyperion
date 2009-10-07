package org.hyperion.rs2.model;

import java.util.LinkedList;
import java.util.List;

import org.hyperion.rs2.model.UpdateFlags.UpdateFlag;
import org.hyperion.rs2.model.region.Region;

/**
 * Represents a character in the game world, i.e. a <code>Player</code> or
 * an <code>NPC</code>.
 * @author Graham Edgecombe
 *
 */
public abstract class Entity {
	
	/**
	 * The default, i.e. spawn, location.
	 */
	public static final Location DEFAULT_LOCATION = Location.create(3200, 3200, 0);
	
	/**
	 * The index in the <code>EntityList</code>.
	 */
	private int index;
	
	/**
	 * The current location.
	 */
	private Location location;
	
	/**
	 * The entity's first stored hit for updates.
	 */
	private transient Damage damage = new Damage();
	
	/**
	 * The entity's state of life.
	 */
	private boolean isDead;
	
	/**
	 * The entity's combat state.
	 */
	private boolean isInCombat = false;
	
	/**
	 * Auto-retaliation setting.
	 */
	private boolean isAutoRetaliating;
	
	/**
	 * The teleportation target.
	 */
	private Location teleportTarget = null;
	
	/**
	 * The update flags.
	 */
	private final UpdateFlags updateFlags = new UpdateFlags();
	
	/**
	 * The entity's cooldowns.
	 */
	private final EntityCooldowns cooldowns = new EntityCooldowns();
	
	/**
	 * The list of local players.
	 */
	private final List<Player> localPlayers = new LinkedList<Player>();
	
	/**
	 * The list of local npcs.
	 */
	private final List<NPC> localNpcs = new LinkedList<NPC>();
	
	/**
	 * The teleporting flag.
	 */
	private boolean teleporting = false;
	
	/**
	 * The walking queue.
	 */
	private final WalkingQueue walkingQueue = new WalkingQueue(this);
	
	/**
	 * The sprites i.e. walk directions.
	 */
	private final Sprites sprites = new Sprites();
	
	/**
	 * The last known map region.
	 */
	private Location lastKnownRegion = this.getLocation();
	
	/**
	 * Map region changing flag.
	 */
	private boolean mapRegionChanging = false;
	
	/**
	 * The current animation.
	 */
	private Animation currentAnimation;
	
	/**
	 * The current graphic.
	 */
	private Graphic currentGraphic;
	
	/**
	 * The current region.
	 */
	private Region currentRegion;
	
	/**
	 * The interacting entity.
	 */
	private Entity interactingEntity;
	
	/**
	 * The face location.
	 */
	private Location face;
	
	/**
	 * Entity's combat aggressor state.
	 */
	private boolean isAggressor;
	
	/**
	 * Creates the entity.
	 */
	public Entity() {
		setLocation(DEFAULT_LOCATION);
		this.lastKnownRegion = location;
	}
	
	/**
	 * Set the entity's combat state.
	 * @param isInCombat This entity's combat state.
	 */
	public void setInCombat(boolean isInCombat) {
		this.isInCombat = isInCombat;
	}

	/**
	 * Returns the combat state of this entity.
	 * @return <code>boolean</code> The entity's combat state.
	 */
	public boolean isInCombat() {
		return isInCombat;
	}
	
	/**
	 * Gets the entity's aggressor state.
	 * @return boolean The entity's aggressor state.
	 */
	public boolean getAggressorState() {
		return isAggressor;
	}
	
	/**
	 * Sets the aggressor state for this entity.
	 */
	public void setAggressorState(boolean b) {
		isAggressor = b;
	}
	
	/**
	 * Set the entity's autoretaliation setting.
	 * @param b <code>true/false</code> Whether or not this entity will autoretaliate when attacked.
	 */
	public void setAutoRetaliating(boolean b) {
		this.isAutoRetaliating = b;
	}

	/**
	 * Get this entity's autoretaliation setting.
	 * @return <code>true</code> if autoretaliation is on, <code>false</code> if not.
	 */
	public boolean isAutoRetaliating() {
		return isAutoRetaliating;
	}

	/**
	 * Set the entity's state of life.
	 * @param isDead Boolean
	 */
	public void setDead(boolean isDead) {
		this.isDead = isDead;
	}

	/**
	 * Is the entity dead?
	 * @return
	 */
	public boolean isDead() {
		return isDead;
	}

	/**
	 * Makes this entity face a location.
	 * @param location The location to face.
	 */
	public void face(Location location) {
		this.face = location;
		this.updateFlags.flag(UpdateFlag.FACE_COORDINATE);
	}
	
	/**
	 * Checks if this entity is facing a location.
	 * @return The entity face flag.
	 */
	public boolean isFacing() {
		return face != null;
	}
	
	/**
	 * Resets the facing location.
	 */
	public void resetFace() {
		this.face = null;
		this.updateFlags.flag(UpdateFlag.FACE_COORDINATE);
	}
	
	/**
	 * Gets the face location.
	 * @return The face location, or <code>null</code> if the entity is not
	 * facing.
	 */
	public Location getFaceLocation() {
		return face;
	}
	
	/**
	 * Checks if this entity is interacting with another entity.
	 * @return The entity interaction flag.
	 */
	public boolean isInteracting() {
		return interactingEntity != null;
	}
	
	/**
	 * Sets the interacting entity.
	 * @param entity The new entity to interact with.
	 */
	public void setInteractingEntity(Entity entity) {
		this.interactingEntity = entity;
		this.updateFlags.flag(UpdateFlag.FACE_ENTITY);
	}
	
	/**
	 * Resets the interacting entity.
	 */
	public void resetInteractingEntity() {
		this.interactingEntity = null;
		this.updateFlags.flag(UpdateFlag.FACE_ENTITY);
	}
	
	/**
	 * Gets the interacting entity.
	 * @return The entity to interact with.
	 */
	public Entity getInteractingEntity() {
		return interactingEntity;
	}
	
	/**
	 * Gets the current region.
	 * @return The current region.
	 */
	public Region getRegion() {
		return currentRegion;
	}
	
	/**
	 * Gets the current animation.
	 * @return The current animation;
	 */
	public Animation getCurrentAnimation() {
		return currentAnimation;
	}
	
	/**
	 * Gets the current graphic.
	 * @return The current graphic.
	 */
	public Graphic getCurrentGraphic() {
		return currentGraphic;
	}
	
	/**
	 * Resets attributes after an update cycle.
	 */
	public void reset() {
		this.currentAnimation = null;
		this.currentGraphic = null;
	}
	
	/**
	 * Animates the entity.
	 * @param animation The animation.
	 */
	public void playAnimation(Animation animation) {
		this.currentAnimation = animation;
		this.getUpdateFlags().flag(UpdateFlag.ANIMATION);
	}
	
	/**
	 * Plays graphics.
	 * @param graphic The graphics.
	 */
	public void playGraphics(Graphic graphic) {
		this.currentGraphic = graphic;
		this.getUpdateFlags().flag(UpdateFlag.GRAPHICS);
	}
	
	/**
	 * Gets the walking queue.
	 * @return The walking queue.
	 */
	public WalkingQueue getWalkingQueue() {
		return walkingQueue;
	}
	
	/**
	 * Sets the last known map region.
	 * @param lastKnownRegion The last known map region.
	 */
	public void setLastKnownRegion(Location lastKnownRegion) {
		this.lastKnownRegion = lastKnownRegion;
	}
	
	/**
	 * Gets the last known map region.
	 * @return The last known map region.
	 */
	public Location getLastKnownRegion() {
		return lastKnownRegion;
	}
	
	/**
	 * Checks if the map region has changed in this cycle.
	 * @return The map region changed flag.
	 */
	public boolean isMapRegionChanging() {
		return mapRegionChanging;
	}
	
	/**
	 * Sets the map region changing flag.
	 * @param mapRegionChanging The map region changing flag.
	 */
	public void setMapRegionChanging(boolean mapRegionChanging) {
		this.mapRegionChanging = mapRegionChanging;
	}
	
	/**
	 * Checks if this entity has a target to teleport to.
	 * @return <code>true</code> if so, <code>false</code> if not.
	 */
	public boolean hasTeleportTarget() {
		return teleportTarget != null;
	}
	
	/**
	 * Gets the teleport target.
	 * @return The teleport target.
	 */
	public Location getTeleportTarget() {
		return teleportTarget;
	}
	
	/**
	 * Sets the teleport target.
	 * @param teleportTarget The target location.
	 */
	public void setTeleportTarget(Location teleportTarget) {
		this.teleportTarget = teleportTarget;
	}
	
	/**
	 * Resets the teleport target.
	 */
	public void resetTeleportTarget() {
		this.teleportTarget = null;
	}
	
	/**
	 * Gets the sprites.
	 * @return The sprites.
	 */
	public Sprites getSprites() {
		return sprites;
	}
	
	/**
	 * Checks if this player is teleporting.
	 * @return <code>true</code> if so, <code>false</code> if not.
	 */
	public boolean isTeleporting() {
		return teleporting;
	}
	
	/**
	 * Sets the teleporting flag.
	 * @param teleporting The teleporting flag.
	 */
	public void setTeleporting(boolean teleporting) {
		this.teleporting = teleporting;
	}
	
	/**
	 * Gets the list of local players.
	 * @return The list of local players.
	 */
	public List<Player> getLocalPlayers() {
		return localPlayers;
	}
	
	/**
	 * Gets the list of local npcs.
	 * @return The list of local npcs.
	 */
	public List<NPC> getLocalNPCs() {
		return localNpcs;
	}
	
	/**
	 * Sets the entity's index.
	 * @param index The index.
	 */
	public void setIndex(int index) {
		this.index = index;
	}
	
	/**
	 * Gets the entity's index.
	 * @return The index.
	 */
	public int getIndex() {
		return index;
	}
	
	/**
	 * Sets the current location.
	 * @param location The current location.
	 */
	public void setLocation(Location location) {
		this.location = location;
		
		Region newRegion = World.getWorld().getRegionManager().getRegionByLocation(location);
		if(newRegion != currentRegion) {
			if(currentRegion != null) {
				removeFromRegion(currentRegion);
			}
			currentRegion = newRegion;
			addToRegion(currentRegion);
		}
	}
	
	/**
	 * Destroys this entity.
	 */
	public void destroy() {
		removeFromRegion(currentRegion);
	}
	
	/**
	 * Deal a hit to the entity.
	 * @param damage The damage to be done.
	 * @param type The type of damage we are inflicting.
	 */
	public abstract void inflictDamage(int damage, Damage.HitType type);
	
	/**
	 * Removes this entity from the specified region.
	 * @param region The region.
	 */
	public abstract void removeFromRegion(Region region);
	
	/**
	 * Adds this entity to the specified region.
	 * @param region The region.
	 */
	public abstract void addToRegion(Region region);
	
	/**
	 * Gets the current location.
	 * @return The current location.
	 */
	public Location getLocation() {
		return location;
	}

	/**
	 * Gets the update flags.
	 * @return The update flags.
	 */
	public UpdateFlags getUpdateFlags() {
		return updateFlags;
	}
	
	/**
	 * Gets the cooldown flags.
	 * @return The cooldown flags.
	 */
	public EntityCooldowns getEntityCooldowns() {
		return cooldowns;
	}

	/**
	 * Get this entity's hit1.
	 * @return The entity's hits as <code>Hit</code> type.
	 */
	public Damage getDamage() {	
		return damage;
	}

	/**
	 * Gets the client-side index of an entity.
	 * @return The client-side index.
	 */
	public abstract int getClientIndex();


}
