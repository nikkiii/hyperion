package org.hyperion.rs2.pf;

import java.util.HashSet;
import java.util.Set;

import org.hyperion.rs2.model.GameObject;
import org.hyperion.rs2.model.Location;
import org.hyperion.rs2.model.World;
import org.hyperion.rs2.model.region.Region;
import org.hyperion.rs2.model.region.RegionManager;

/**
 * A class which assist in building <code>TileMap</code>s from a collection of
 * <code>GameObject</code>s.
 * @author Graham Edgecombe
 *
 */
public class TileMapBuilder {
	
	/**
	 * The tile map being built.
	 */
	private final TileMap tileMap;
	
	/**
	 * The center position.
	 */
	private final Location centerPosition;
	
	/**
	 * The radius.
	 */
	private final int radius;
	
	/**
	 * Sets up the tile map builder with the specified radius, and center
	 * position.
	 * @param position The center position.
	 * @param radius The radius.
	 */
	public TileMapBuilder(Location position, int radius) {
		this.centerPosition = position;
		this.tileMap = new TileMap(radius * 2 + 1, radius * 2 + 1);
		this.radius = radius;
	}
	
	/**
	 * Builds the tile map.
	 * @return The built tile map.
	 */
	public TileMap build() {
		// the region manager
		RegionManager mgr = World.getWorld().getRegionManager();
		// a set of regions covered by our center position + radius
		Set<Region> coveredRegions = new HashSet<Region>();
		
		// populates the set of covered regions
		for(int x = -radius; x <= radius; x++) {
			for(int y = -radius; y <= radius; y++) {
				Location loc = centerPosition.transform(x, y, 0);
				coveredRegions.add(mgr.getRegionByLocation(loc));
			}
		}
		
		// now fills in the tile map
		for(Region region : coveredRegions) {
			for(GameObject obj : region.getGameObjects()) {
				Location loc = obj.getLocation();
				if(loc.getX() >= (centerPosition.getX() - radius)
					&& loc.getY() <= (centerPosition.getY() + radius)
					&& loc.getY() >= (centerPosition.getY() - radius)
					&& loc.getY() <= (centerPosition.getY() + radius)) {
					// object is in range, so add its info to the tile map
					switch(obj.getType()) {
					case 5:
						/* side decorations on walls */
						break;
					case 10:
					case 11:
						/* solid objects */
						
						break;
					default:
						System.out.println(obj.getLocation() + " : " + obj.getType() + " (r" + obj.getRotation() + ")");
						break;
					}
				}
			}
		}
		
		return tileMap;
	}

}
