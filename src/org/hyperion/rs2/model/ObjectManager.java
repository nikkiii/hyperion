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
	}

	@Override
	public void objectDefinitionParsed(GameObjectDefinition def) {
		definitionCount++;
		GameObjectDefinition.addDefinition(def);
	}

}
