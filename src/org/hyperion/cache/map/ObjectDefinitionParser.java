package org.hyperion.cache.map;

import java.io.IOException;
import java.nio.ByteBuffer;

import org.hyperion.cache.Archive;
import org.hyperion.cache.Cache;
import org.hyperion.cache.index.impl.StandardIndex;

/**
 * A class which parses object definitions in the game cache.
 * @author Graham Edgecombe
 *
 */
public class ObjectDefinitionParser {
	
	/**
	 * The cache.
	 */
	private Cache cache;
	
	/**
	 * The listener.
	 */
	private ObjectDefinitionListener listener;
	
	/**
	 * Creates the object definition parser.
	 * @param cache The cache.
	 * @param listener The object definition listener.
	 */
	public ObjectDefinitionParser(Cache cache, ObjectDefinitionListener listener) {
		this.cache = cache;
		this.listener = listener;
	}
	
	/**
	 * Parses the object definitions in the cache.
	 * @throws IOException if an I/O error occurs.
	 */
	public void parse() throws IOException {
		ByteBuffer buf = new Archive(cache.getFile(0, 2)).getFileAsByteBuffer("loc.dat");
		
		StandardIndex[] indices = cache.getIndexTable().getObjectDefinitionIndices();
		for(StandardIndex index : indices) {
			int id = index.getIdentifier();
			int offset = index.getFile(); // bad naming, should be getOffset()
			buf.position(offset);
			
			// TODO read the object definition now
			
		}
	}

}
