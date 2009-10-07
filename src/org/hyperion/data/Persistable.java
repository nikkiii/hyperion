package org.hyperion.data;

import org.apache.mina.core.buffer.IoBuffer;

/**
 * Represents a type that can be converted to and from an <code>IoBuffer</code>
 * allowing it to be transferred between the login and world server.
 * @author Graham Edgecombe
 *
 */
public interface Persistable {
	
	/**
	 * Serializes the class into the specified buffer.
	 * @param buf The buffer.
	 */
	public void serialize(IoBuffer buf);
	
	/**
	 * Deserializes the class from the specified buffer.
	 * @param buf The buffer.
	 */
	public void deserialize(IoBuffer buf);

}
