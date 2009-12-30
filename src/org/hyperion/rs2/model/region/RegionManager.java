package org.hyperion.rs2.model.region;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.hyperion.rs2.model.Entity;
import org.hyperion.rs2.model.Location;
import org.hyperion.rs2.model.NPC;
import org.hyperion.rs2.model.Player;

/**
 * Manages the world regions.
 * @author Graham Edgecombe
 *
 */
public class RegionManager {
	
	/**
	 * The region size.
	 */
	public static final int REGION_SIZE = 32;
	
	/**
	 * The lower bound that splits the region in half.
	 */
	@SuppressWarnings("unused")
	private static final int LOWER_BOUND = REGION_SIZE / 2 - 1;
	
	/**
	 * The active (loaded) region map.
	 */
	private Map<RegionCoordinates, Region> activeRegions = new HashMap<RegionCoordinates, Region>();
	
	/**
	 * Gets the local players around an entity.
	 * @param entity The entity.
	 * @return The collection of local players.
	 */
	public Collection<Player> getLocalPlayers(Entity entity) {
		List<Player> localPlayers = new LinkedList<Player>();
		Region[] regions = getSurroundingRegions(entity.getLocation());
		for(Region region : regions) {
			for(Player player : region.getPlayers()) {
				if(player.getLocation().isWithinDistance(entity.getLocation())) {
					localPlayers.add(player);
				}
			}
		}
		return Collections.unmodifiableCollection(localPlayers);
	}
	
	/**
	 * Gets the local NPCs around an entity.
	 * @param entity The entity.
	 * @return The collection of local NPCs.
	 */
	public Collection<NPC> getLocalNpcs(Entity entity) {
		List<NPC> localPlayers = new LinkedList<NPC>();
		Region[] regions = getSurroundingRegions(entity.getLocation());
		for(Region region : regions) {
			for(NPC npc : region.getNpcs()) {
				if(npc.getLocation().isWithinDistance(entity.getLocation())) {
					localPlayers.add(npc);
				}
			}
		}
		return Collections.unmodifiableCollection(localPlayers);
	}
	
	/**
	 * Gets the regions surrounding a location.
	 * @param location The location.
	 * @return The regions surrounding the location.
	 */
	public Region[] getSurroundingRegions(Location location) {		
		int regionX = location.getX() / REGION_SIZE;
		int regionY = location.getY() / REGION_SIZE;
		
//		int regionPositionX = location.getX() % REGION_SIZE;
//		int regionPositionY = location.getY() % REGION_SIZE;
		
		Region[] surrounding = new Region[9];
		surrounding[0] = getRegion(regionX, regionY);
		surrounding[1] = getRegion(regionX - 1, regionY - 1);
		surrounding[2] = getRegion(regionX + 1, regionY + 1);
		surrounding[3] = getRegion(regionX - 1, regionY);
		surrounding[4] = getRegion(regionX, regionY - 1);
		surrounding[5] = getRegion(regionX + 1, regionY);
		surrounding[6] = getRegion(regionX, regionY + 1);
		surrounding[7] = getRegion(regionX - 1, regionY + 1);
		surrounding[8] = getRegion(regionX + 1, regionY - 1);

//		FIXME
//		if(regionPositionX <= LOWER_BOUND) {
//			if(regionPositionY <= LOWER_BOUND) {
//				surrounding[1] = getRegion(regionX - 1, regionY - 1);
//				surrounding[2] = getRegion(regionX - 1, regionY);
//				surrounding[3] = getRegion(regionX, regionY - 1);
//			} else {
//				surrounding[1] = getRegion(regionX + 1, regionY - 1);
//				surrounding[2] = getRegion(regionX + 1, regionY);
//				surrounding[3] = getRegion(regionX, regionY - 1);
//			}
//		} else {
//			if(regionPositionY <= LOWER_BOUND) {
//				surrounding[1] = getRegion(regionX - 1, regionY + 1);
//				surrounding[2] = getRegion(regionX - 1, regionY);
//				surrounding[3] = getRegion(regionX, regionY + 1);
//			} else {
//				surrounding[1] = getRegion(regionX + 1, regionY + 1);
//				surrounding[2] = getRegion(regionX + 1, regionY);
//				surrounding[3] = getRegion(regionX, regionY + 1);
//			}
//		}
		
		return surrounding;
	}

	/**
	 * Gets a region by location.
	 * @param location The location.
	 * @return The region.
	 */
	public Region getRegionByLocation(Location location) {
		return getRegion(location.getX() / REGION_SIZE, location.getY() / REGION_SIZE);
	}

	/**
	 * Gets a region by its x and y coordinates.
	 * @param x The x coordinate.
	 * @param y The y coordinate.
	 * @return The region.
	 */
	public Region getRegion(int x, int y) {
		RegionCoordinates key = new RegionCoordinates(x, y);
		if(activeRegions.containsKey(key)) {
			return activeRegions.get(key);
		} else {
			Region region = new Region(key);
			activeRegions.put(key, region);
			return region;
		}
	}

}
