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
		for(int x = (-radius-1); x <= (radius+1); x++) {
			for(int y = (-radius-1); y <= (radius+1); y++) {
				Location loc = centerPosition.transform(x, y, 0);
				coveredRegions.add(mgr.getRegionByLocation(loc));
			}
		}
		
		// calculate top left positions
		int topX = centerPosition.getX() - radius;
		int topY = centerPosition.getY() - radius;
		
		// now fills in the tile map
		for(Region region : coveredRegions) {
			for(GameObject obj : region.getGameObjects()) {
				
				if(!obj.getDefinition().isSolid()) {
					continue;
				}
				Location loc = obj.getLocation();
				if(loc.getZ() != centerPosition.getZ()) {
					continue;
				}

				int sizeX = obj.getDefinition().getSizeX();
				int sizeY = obj.getDefinition().getSizeY();
				// position in the tile map
				int posX = loc.getX() - topX;
				int posY = loc.getY() - topY;
				if(obj.getRotation() == 1 || obj.getRotation() == 3) {
					// switch sizes if rotated
					int temp = sizeX;
					sizeX = sizeY;
					sizeY = temp;
				}
				
				if((posX + sizeX) < 0 || (posY + sizeY) < 0 || (posX) >= tileMap.getWidth() || (posY) >= tileMap.getHeight()) {
					continue;
				}
				
				if(obj.getType() >= 0 && obj.getType() <= 3) {
					// walls
					if(posX >= 0 && posY >= 0 && posX < tileMap.getWidth() && posY < tileMap.getHeight()) {
						//int finalRotation = (obj.getType() + obj.getRotation()) % 4;
						int finalRotation = obj.getRotation();
						// finalRotation - 0 = west, 1 = north, 2 = east, 3 = south
						Tile t = tileMap.getTile(posX, posY);
						int flags = t.getTraversalMask();
						// clear flags
						if(finalRotation == 0) {
							flags &= ~Tile.WEST_TRAVERSAL_PERMITTED;
						} else if(finalRotation == 1) {
							flags &= ~Tile.NORTH_TRAVERSAL_PERMITTED;
						} else if(finalRotation == 2) {
							flags &= ~Tile.EAST_TRAVERSAL_PERMITTED;
						} else {
							flags &= ~Tile.SOUTH_TRAVERSAL_PERMITTED;
						}
						if(flags != t.getTraversalMask()) {
							tileMap.setTile(posX, posY, new Tile(flags));
						}
					}
				} else if(obj.getType() == 9) {
					// diagonal walls
					if(posX >= 0 && posY >= 0 && posX < tileMap.getWidth() && posY < tileMap.getHeight()) {
						tileMap.setTile(posX, posY, TileMap.SOLID_TILE);
					}
				} else if(obj.getType() == 10 || obj.getType() == 11) {
					// world objects
					for(int offX = 0; offX <= sizeX; offX++) {
						for(int offY = 0; offY <= sizeY; offY++) {
							int x = offX + posX;
							int y = offY + posY;
							if(x >= 0 && y >= 0 && x < tileMap.getWidth() && y < tileMap.getHeight()) {
								tileMap.setTile(x, y, TileMap.SOLID_TILE);
							}
						}
					}
				} else if(obj.getType() == 22) {
					// floor decoration
					if(obj.getDefinition().hasActions()) {
						if(posX >= 0 && posY >= 0 && posX < tileMap.getWidth() && posY < tileMap.getHeight()) {
							tileMap.setTile(posX, posY, TileMap.SOLID_TILE);
						}
					}
				} else {
					// 4-8 are wall decorations and 12-21 are roofs
					// we can ignore those
				}
			}
		}
		
		return tileMap;
	}

}
