package org.hyperion.rs2.model;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel.MapMode;
import java.util.logging.Logger;

import org.hyperion.util.Buffers;

/**
 * The item definition manager.
 * @author Vastico
 * @author Graham Edgecombe
 *
 */
public class ItemDefinition {
	
	/**
	 * Logger instance.
	 */
	private static final Logger logger = Logger.getLogger(ItemDefinition.class.getName());
	
	/**
	 * The definition array.
	 */
	private static ItemDefinition[] definitions;
	
	/**
	 * Gets a definition for the specified id.
	 * @param id The id.
	 * @return The definition.
	 */
	public static ItemDefinition forId(int id) {
		return definitions[id];
	}
	
	/**
	 * Loads the item definitions.
	 * @throws IOException if an I/O error occurs.
	 * @throws IllegalStateException if the definitions have been loaded already.
	 */
	public static void init() throws IOException {
		if(definitions != null) {
			throw new IllegalStateException("Definitions already loaded.");
		}
		logger.info("Loading definitions...");
		RandomAccessFile raf = new RandomAccessFile("data/itemDefinitions.bin", "r");
		try {
			ByteBuffer buffer = raf.getChannel().map(MapMode.READ_ONLY, 0, raf.length());
			int count = buffer.getShort() & 0xFFFF;
			definitions = new ItemDefinition[count];
			for(int i = 0; i < count; i++) {
				String name = Buffers.readString(buffer);
				String examine = Buffers.readString(buffer);
				boolean noted = buffer.get() == 1 ? true : false;
				int parentId = buffer.getShort() & 0xFFFF;
				if(parentId == 65535) {
					parentId = -1;
				}
				boolean noteable = buffer.get() == 1 ? true : false;
				int notedId = buffer.getShort() & 0xFFFF;
				if(notedId == 65535) {
					notedId = -1;
				}
				boolean stackable = buffer.get() == 1 ? true : false;
				boolean members = buffer.get() == 1 ? true : false;
				boolean prices = buffer.get() == 1 ? true : false;
				int shop = -1;
				int highAlc = -1;
				int lowAlc = -1;
				if(prices) {
					shop = buffer.getInt();
					highAlc = (int) (shop * 0.6D);
					lowAlc = (int) (shop * 0.4D);
				}
				definitions[i] = new ItemDefinition(i, name, examine, noted, noteable, stackable, parentId, notedId, members, shop, highAlc, lowAlc);
			}
			logger.info("Loaded " + definitions.length + " definitions.");
		} finally {
			raf.close();
		}
	}
	
	/**
	 * Id.
	 */
	private final int id;
	
	/**
	 * Name.
	 */
	private final String name;
	
	/**
	 * Description.
	 */
	private final String examine;
	
	/**
	 * Noted flag.
	 */
	private final boolean noted;
	
	/**
	 * Noteable flag.
	 */
	private final boolean noteable;
	
	/**
	 * Stackable flag.
	 */
	private final boolean stackable;
	
	/**
	 * Non-noted id.
	 */
	private final int parentId;
	
	/**
	 * Noted id.
	 */
	private final int notedId;
	
	/**
	 * Members flag.
	 */
	private final boolean members;
	
	/**
	 * Shop value.
	 */
	private final int shopValue;
	
	/**
	 * High alc value.
	 */
	private final int highAlcValue;
	
	/**
	 * Low alc value.
	 */
	private final int lowAlcValue;
	
	/**
	 * Creates the item definition.
	 * @param id The id.
	 * @param name The name.
	 * @param examine The description.
	 * @param noted The noted flag.
	 * @param noteable The noteable flag.
	 * @param stackable The stackable flag.
	 * @param parentId The non-noted id.
	 * @param notedId The noted id.
	 * @param members The members flag.
	 * @param shopValue The shop price.
	 * @param highAlcValue The high alc value.
	 * @param lowAlcValue The low alc value.
	 */
	private ItemDefinition(int id, String name, String examine, boolean noted, boolean noteable, boolean stackable, int parentId, int notedId, boolean members, int shopValue, int highAlcValue, int lowAlcValue) {
		this.id = id;
		this.name = name;
		this.examine = examine;
		this.noted = noted;
		this.noteable = noteable;
		this.stackable = stackable;
		this.parentId = parentId;
		this.notedId = notedId;
		this.members = members;
		this.shopValue = shopValue;
		this.highAlcValue = highAlcValue;
		this.lowAlcValue = lowAlcValue;
	}
	
	/**
	 * Gets the id.
	 * @return The id.
	 */
	public int getId() {
		return id;
	}
	
	/**
	 * Gets the name.
	 * @return The name.
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Gets the description.
	 * @return The description.
	 */
	public String getDescription() {
		return examine;
	}
	
	/**
	 * Gets the noted flag.
	 * @return The noted flag.
	 */
	public boolean isNoted() {
		return noted;
	}
	
	/**
	 * Gets the noteable flag.
	 * @return The noteable flag.
	 */
	public boolean isNoteable() {
		return noteable;
	}
	
	/**
	 * Gets the stackable flag.
	 * @return The stackable flag.
	 */
	public boolean isStackable() {
		return stackable || noted;
	}
	
	/**
	 * Gets the normal id.
	 * @return The normal id.
	 */
	public int getNormalId() {
		return parentId;
	}
	
	/**
	 * Gets the noted id.
	 * @return The noted id.
	 */
	public int getNotedId() {
		return notedId;
	}
	
	/**
	 * Gets the members only flag.
	 * @return The members only flag.
	 */
	public boolean isMembersOnly() {
		return members;
	}
	
	/**
	 * Gets the value.
	 * @return The value.
	 */
	public int getValue() {
		return shopValue;
	}
	
	/**
	 * Gets the low alc value.
	 * @return The low alc value.
	 */
	public int getLowAlcValue() {
		return lowAlcValue;
	}
	
	/**
	 * Gets the high alc value.
	 * @return The high alc value.
	 */
	public int getHighAlcValue() {
		return highAlcValue;
	}

}
