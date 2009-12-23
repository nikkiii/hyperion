package org.hyperion.rs2.model;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

import org.hyperion.cache.Cache;
import org.hyperion.cache.InvalidCacheException;
import org.hyperion.cache.index.impl.MapIndex;
import org.hyperion.cache.index.impl.StandardIndex;
import org.hyperion.cache.map.LandscapeListener;
import org.hyperion.cache.map.LandscapeParser;
import org.hyperion.cache.obj.ObjectDefinitionListener;
import org.hyperion.cache.obj.ObjectDefinitionParser;

/**
 * Manages all of the in-game objects.
 * @author Graham Edgecombe
 *
 */
public class ObjectManager implements LandscapeListener, ObjectDefinitionListener {
	
	/**
	 * Logger instance.
	 */
	private static final Logger logger = Logger.getLogger(ObjectManager.class.getName());
	
	/**
	 * The number of definitions loaded.
	 */
	private int definitionCount = 0;
	
	/**
	 * The count of objects loaded.
	 */
	private int objectCount = 0;
	
	/**
	 * Loads the objects in the map.
	 * @throws IOException if an I/O error occurs.
	 * @throws InvalidCacheException if the cache is invalid.
	 */
	public void load() throws IOException, InvalidCacheException {
		Cache cache = new Cache(new File("./data/cache/"));
		try {
			logger.info("Loading definitions...");
			StandardIndex[] defIndices = cache.getIndexTable().getObjectDefinitionIndices();
			new ObjectDefinitionParser(cache, defIndices, this).parse();
			logger.info("Loaded " + definitionCount + " object definitions.");
			logger.info("Loading map...");
			MapIndex[] mapIndices = cache.getIndexTable().getMapIndices();
			for(MapIndex index : mapIndices) {
				new LandscapeParser(cache, index.getIdentifier(), this).parse();
			}
			logger.info("Loaded " + objectCount + " objects.");
		} finally {
			cache.close();
		}
	}

	@Override
	public void objectParsed(GameObject obj) {
		objectCount++;
		World.getWorld().getRegionManager().getRegionByLocation(obj.getLocation()).getGameObjects().add(obj);
		// if solid object
		switch(obj.getType()) {
		case 22:
			/*
			 * Floor decoration.
			 */
			World.getWorld().getRegionManager().flagClippingMask(obj.getLocation().getX(), obj.getLocation().getY(), 0x200000);
			break;
		case 0:
		case 1:
		case 2:
		case 3:
			/*
			 * Walls.
			 */
			flagWall(obj);
			break;
		case 9:
			/* 
			 * Diagonal wall
			 */
		case 10:
		case 11:
			/*
			 * Ground object.
			 */
		default:
			/*
			 * Anything else.
			 */
			int szX = 1;
			int szY = 1;
			if(obj.getRotation() == 1 || obj.getRotation() == 3) {
				//szX = sizey;
				//szY = sizex;
			} else {
				//szX = sizex;
				//szY = sizey;
			}
			int flags = 256;
			// if non walkable
			flags += 0x20000;
			for(int i = 0; i < szX; i++) {
				for(int j = 0; j < szY; j++) {
					World.getWorld().getRegionManager().flagClippingMask(obj.getLocation().getX() + i, obj.getLocation().getY() + j, flags);
				}
			}
			break;
		}
	}

	private void flagWall(GameObject obj) {
		final int objectType = obj.getType();
		final boolean flag = true; // if non walkable
		final int x = obj.getLocation().getX();
		final int y = obj.getLocation().getY();
		final int rotation = obj.getRotation();
		if(objectType == 0) {
			if(rotation == 0) {
				World.getWorld().getRegionManager().flagClippingMask(x, y, 128);
				World.getWorld().getRegionManager().flagClippingMask(x - 1, y, 8);
			} else if(rotation == 1) {
				World.getWorld().getRegionManager().flagClippingMask(x, y, 2);
				World.getWorld().getRegionManager().flagClippingMask(x, y + 1, 32);
			} else if(rotation == 2) {
				World.getWorld().getRegionManager().flagClippingMask(x, y, 8);
				World.getWorld().getRegionManager().flagClippingMask(x + 1, y, 128);
			} else if(rotation == 3) {
				World.getWorld().getRegionManager().flagClippingMask(x, y, 32);
				World.getWorld().getRegionManager().flagClippingMask(x, y - 1, 2);
			}
		} else if(objectType == 1 || objectType == 3) {
			if(rotation == 0) {
				World.getWorld().getRegionManager().flagClippingMask(x, y, 1);
				World.getWorld().getRegionManager().flagClippingMask(x - 1, y + 1, 16);
			} else if(rotation == 1) {
				World.getWorld().getRegionManager().flagClippingMask(x, y, 4);
				World.getWorld().getRegionManager().flagClippingMask(x + 1, y + 1, 64);
			} else if(rotation == 2) {
				World.getWorld().getRegionManager().flagClippingMask(x, y, 16);
				World.getWorld().getRegionManager().flagClippingMask(x + 1, y - 1, 1);
			} else if(rotation == 3) {
				World.getWorld().getRegionManager().flagClippingMask(x, y, 64);
				World.getWorld().getRegionManager().flagClippingMask(x - 1, y - 1, 4);
			}
		} else if(objectType == 2) {
			if(rotation == 0) {
				World.getWorld().getRegionManager().flagClippingMask(x, y, 130);
				World.getWorld().getRegionManager().flagClippingMask(x - 1, y, 8);
				World.getWorld().getRegionManager().flagClippingMask(x, y + 1, 32);
			} else if(rotation == 1) {
				World.getWorld().getRegionManager().flagClippingMask(x, y, 10);
				World.getWorld().getRegionManager().flagClippingMask(x, y + 1, 32);
				World.getWorld().getRegionManager().flagClippingMask(x + 1, y, 128);
			} else if(rotation == 2) {
				World.getWorld().getRegionManager().flagClippingMask(x, y, 40);
				World.getWorld().getRegionManager().flagClippingMask(x + 1, y, 128);
				World.getWorld().getRegionManager().flagClippingMask(x, y - 1, 2);
			} else if(rotation == 3) {
				World.getWorld().getRegionManager().flagClippingMask(x, y, 160);
				World.getWorld().getRegionManager().flagClippingMask(x, y - 1, 2);
				World.getWorld().getRegionManager().flagClippingMask(x - 1, y, 8);
			}
		}
		if(flag) {
			if(objectType == 0) {
				if(rotation == 0) {
					World.getWorld().getRegionManager().flagClippingMask(x, y, 0x10000);
					World.getWorld().getRegionManager().flagClippingMask(x - 1, y, 4096);
				} else if(rotation == 1) {
					World.getWorld().getRegionManager().flagClippingMask(x, y, 1024);
					World.getWorld().getRegionManager().flagClippingMask(x, y + 1, 16384);
				} else if(rotation == 2) {
					World.getWorld().getRegionManager().flagClippingMask(x, y, 4096);
					World.getWorld().getRegionManager().flagClippingMask(x + 1, y, 0x10000);
				} else if(rotation == 3) {
					World.getWorld().getRegionManager().flagClippingMask(x, y, 16384);
					World.getWorld().getRegionManager().flagClippingMask(x, y - 1, 1024);
				}
			} else if(objectType == 1 || objectType == 3) {
				if(rotation == 0) {
					World.getWorld().getRegionManager().flagClippingMask(x, y, 512);
					World.getWorld().getRegionManager().flagClippingMask(x - 1, y + 1, 8192);
				} else if(rotation == 1) {
					World.getWorld().getRegionManager().flagClippingMask(x, y, 2048);
					World.getWorld().getRegionManager().flagClippingMask(x + 1, y + 1, 32768);
				} else if(rotation == 2) {
					World.getWorld().getRegionManager().flagClippingMask(x, y, 8192);
					World.getWorld().getRegionManager().flagClippingMask(x + 1, y - 1, 512);
				} else if(rotation == 3) {
					World.getWorld().getRegionManager().flagClippingMask(x, y, 32768);
					World.getWorld().getRegionManager().flagClippingMask(x - 1, y - 1, 2048);
				}
			} else if(objectType == 2) {
				if(rotation == 0) {
					World.getWorld().getRegionManager().flagClippingMask(x, y, 0x10400);
					World.getWorld().getRegionManager().flagClippingMask(x - 1, y, 4096);
					World.getWorld().getRegionManager().flagClippingMask(x, y + 1, 16384);
				} else if(rotation == 1) {
					World.getWorld().getRegionManager().flagClippingMask(x, y, 5120);
					World.getWorld().getRegionManager().flagClippingMask(x, y + 1, 16384);
					World.getWorld().getRegionManager().flagClippingMask(x + 1, y, 0x10000);
				} else if(rotation == 2) {
					World.getWorld().getRegionManager().flagClippingMask(x, y, 20480);
					World.getWorld().getRegionManager().flagClippingMask(x + 1, y, 0x10000);
					World.getWorld().getRegionManager().flagClippingMask(x, y - 1, 1024);
				} else if(rotation == 3) {
					World.getWorld().getRegionManager().flagClippingMask(x, y, 0x14000);
					World.getWorld().getRegionManager().flagClippingMask(x, y - 1, 1024);
					World.getWorld().getRegionManager().flagClippingMask(x - 1, y, 4096);
				}
			}
		}
	}

	@Override
	public void objectDefinitionParsed(GameObjectDefinition def) {
		GameObjectDefinition.addDefinition(def);
		definitionCount++;
	}

}
