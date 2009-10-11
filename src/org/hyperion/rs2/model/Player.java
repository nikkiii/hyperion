package org.hyperion.rs2.model;

import java.util.LinkedList;
import java.util.Queue;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.hyperion.data.Persistable;
import org.hyperion.rs2.action.ActionQueue;
import org.hyperion.rs2.action.impl.AttackAction;
import org.hyperion.rs2.event.impl.DeathEvent;
import org.hyperion.rs2.model.UpdateFlags.UpdateFlag;
import org.hyperion.rs2.model.container.Bank;
import org.hyperion.rs2.model.container.Container;
import org.hyperion.rs2.model.container.Equipment;
import org.hyperion.rs2.model.container.Inventory;
import org.hyperion.rs2.model.region.Region;
import org.hyperion.rs2.model.Damage.Hit;
import org.hyperion.rs2.model.Damage.HitType;
import org.hyperion.rs2.net.ActionSender;
import org.hyperion.rs2.net.ISAACCipher;
import org.hyperion.rs2.net.Packet;
import org.hyperion.rs2.util.IoBufferUtils;
import org.hyperion.rs2.util.NameUtils;

/**
 * Represents a player-controller character.
 * @author Graham Edgecombe
 *
 */
public class Player extends Entity implements Persistable {
	
	/**
	 * Represents the rights of a player.
	 * @author Graham Edgecombe
	 *
	 */
	public enum Rights {
		
		/**
		 * A standard account.
		 */
		PLAYER(0),
		
		/**
		 * A player-moderator account.
		 */
		MODERATOR(1),
		
		/**
		 * An administrator account.
		 */
		ADMINISTRATOR(2);
		
		/**
		 * The integer representing this rights level.
		 */
		private int value;
		
		/**
		 * Creates a rights level.
		 * @param value The integer representing this rights level.
		 */
		private Rights(int value) {
			this.value = value;
		}
		
		/**
		 * Gets an integer representing this rights level.
		 * @return An integer representing this rights level.
		 */
		public int toInteger() {
			return value;
		}

		/**
		 * Gets rights by a specific integer.
		 * @param value The integer returned by {@link #toInteger()}.
		 * @return The rights level.
		 */
		public static Rights getRights(int value) {
			if(value == 1) {
				return MODERATOR;
			} else if(value == 2) {
				return ADMINISTRATOR;
			} else {
				return PLAYER;
			}
		}
	}
	
	/*
	 * Attributes specific to our session.
	 */
	
	/**
	 * The <code>IoSession</code>.
	 */
	private final IoSession session;
	
	/**
	 * The ISAAC cipher for incoming data.
	 */
	private final ISAACCipher inCipher;
	
	/**
	 * The ISAAC cipher for outgoing data.
	 */
	private final ISAACCipher outCipher;
	
	/**
	 * The action sender.
	 */
	private final ActionSender actionSender = new ActionSender(this);
	
	/**
	 * A queue of pending chat messages.
	 */
	private final Queue<ChatMessage> chatMessages = new LinkedList<ChatMessage>();
	
	/**
	 * A queue of actions.
	 */
	private final ActionQueue actionQueue = new ActionQueue();
	
	/**
	 * The current chat message.
	 */
	private ChatMessage currentChatMessage;
	
	/**
	 * Active flag: if the player is not active certain changes (e.g. items)
	 * should not send packets as that indicates the player is still loading. 
	 */
	private boolean active = false;
	
	/**
	 * The interface state.
	 */
	private final InterfaceState interfaceState = new InterfaceState(this);
	
	/**
	 * A queue of packets that are pending.
	 */
	private final Queue<Packet> pendingPackets = new LinkedList<Packet>();
	
	/**
	 * The request manager which manages trading and duelling requests.
	 */
	private final RequestManager requestManager = new RequestManager(this);
	
	/*
	 * Core login details.
	 */
	
	/**
	 * The name.
	 */
	private String name;
	
	/**
	 * The name expressed as a long.
	 */
	private long nameLong;
	
	/**
	 * The UID, i.e. number in <code>random.dat</code>.
	 */
	private final int uid;
	
	/**
	 * The password.
	 */
	private String password;
	
	/**
	 * The rights level.
	 */
	private Rights rights = Rights.PLAYER;
	
	/**
	 * The members flag.
	 */
	private boolean members = true;
	
	/*
	 * Attributes.
	 */
	
	/**
	 * The player's appearance information.
	 */
	private final Appearance appearance = new Appearance();
	
	/**
	 * The player's equipment.
	 */
	private final Container equipment = new Container(Container.Type.STANDARD, Equipment.SIZE);
	
	/**
	 * The player's skill levels.
	 */
	private final Skills skills = new Skills(this);
	
	/**
	 * The player's inventory.
	 */
	private final Container inventory = new Container(Container.Type.STANDARD, Inventory.SIZE);
	
	/**
	 * The player's bank.
	 */
	private final Container bank = new Container(Container.Type.ALWAYS_STACK, Bank.SIZE);
	
	/**
	 * The player's settings.
	 */
	private final Settings settings = new Settings();
	
	/*
	 * Cached details.
	 */
	/**
	 * The cached update block.
	 */
	private Packet cachedUpdateBlock;
	
	/**
	 * Creates a player based on the details object.
	 * @param details The details object.
	 */
	public Player(PlayerDetails details) {
		super();
		this.session = details.getSession();
		this.inCipher = details.getInCipher();
		this.outCipher = details.getOutCipher();
		this.name = details.getName();
		this.nameLong = NameUtils.nameToLong(this.name);
		this.password = details.getPassword();
		this.uid = details.getUID();
		this.getUpdateFlags().flag(UpdateFlag.APPEARANCE);
		this.setTeleporting(true);
	}
	
	/**
	 * Gets the request manager.
	 * @return The request manager.
	 */
	public RequestManager getRequestManager() {
		return requestManager;
	}
	
	/**
	 * Gets the player's name expressed as a long.
	 * @return The player's name expressed as a long.
	 */
	public long getNameAsLong() {
		return nameLong;
	}
	
	/**
	 * Gets the player's settings.
	 * @return The player's settings.
	 */
	public Settings getSettings() {
		return settings;
	}
	
	/**
	 * Writes a packet to the <code>IoSession</code>. If the player is not
	 * yet active, the packets are queued.
	 * @param packet The packet.
	 */
	public void write(Packet packet) {
		synchronized(this) {
			if(!active) {
				pendingPackets.add(packet);
			} else {
				for(Packet pendingPacket : pendingPackets) {
					session.write(pendingPacket);
				}
				pendingPackets.clear();
				session.write(packet);
			}
		}
	}
	
	/**
	 * Gets the player's bank.
	 * @return The player's bank.
	 */
	public Container getBank() {
		return bank;
	}
	
	/**
	 * Gets the interface state.
	 * @return The interface state.
	 */
	public InterfaceState getInterfaceState() {
		return interfaceState;
	}
	
	/**
	 * Checks if there is a cached update block for this cycle.
	 * @return <code>true</code> if so, <code>false</code> if not.
	 */
	public boolean hasCachedUpdateBlock() {
		return cachedUpdateBlock != null;
	}
	
	/**
	 * Sets the cached update block for this cycle.
	 * @param cachedUpdateBlock The cached update block.
	 */
	public void setCachedUpdateBlock(Packet cachedUpdateBlock) {
		this.cachedUpdateBlock = cachedUpdateBlock;
	}
	
	/**
	 * Gets the cached update block.
	 * @return The cached update block.
	 */
	public Packet getCachedUpdateBlock() {
		return cachedUpdateBlock;
	}
	
	/**
	 * Resets the cached update block.
	 */
	public void resetCachedUpdateBlock() {
		cachedUpdateBlock = null;
	}
	
	/**
	 * Gets the current chat message.
	 * @return The current chat message.
	 */
	public ChatMessage getCurrentChatMessage() {
		return currentChatMessage;
	}
	
	/**
	 * Sets the current chat message.
	 * @param currentChatMessage The current chat message to set.
	 */
	public void setCurrentChatMessage(ChatMessage currentChatMessage) {
		this.currentChatMessage = currentChatMessage;
	}
	
	/**
	 * Gets the queue of pending chat messages.
	 * @return The queue of pending chat messages.
	 */
	public Queue<ChatMessage> getChatMessageQueue() {
		return chatMessages;
	}
	
	/**
	 * Gets the player's appearance.
	 * @return The player's appearance.
	 */
	public Appearance getAppearance() {
		return appearance;
	}
	
	/**
	 * Gets the player's equipment.
	 * @return The player's equipment.
	 */
	public Container getEquipment() {
		return equipment;
	}
	
	/**
	 * Gets the player's skills.
	 * @return The player's skills.
	 */
	public Skills getSkills() {
		return skills;
	}
	
	/**
	 * Gets the action sender.
	 * @return The action sender.
	 */
	public ActionSender getActionSender() {
		return actionSender;
	}
	
	/**
	 * Gets the incoming ISAAC cipher.
	 * @return The incoming ISAAC cipher.
	 */
	public ISAACCipher getInCipher() {
		return inCipher;
	}
	
	/**
	 * Gets the outgoing ISAAC cipher.
	 * @return The outgoing ISAAC cipher.
	 */
	public ISAACCipher getOutCipher() {
		return outCipher;
	}
	
	/**
	 * Gets the player's name.
	 * @return The player's name.
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Gets the player's password.
	 * @return The player's password.
	 */
	public String getPassword() {
		return password;
	}
	
	/**
	 * Sets the player's password.
	 * @param pass The password.
	 */
	public void setPassword(String pass) {
		this.password = pass;
	}
	
	/**
	 * Gets the player's UID.
	 * @return The player's UID.
	 */
	public int getUID() {
		return uid;
	}
	
	/**
	 * Gets the <code>IoSession</code>.
	 * @return The player's <code>IoSession</code>.
	 */
	public IoSession getSession() {
		return session;
	}
	
	/**
	 * Sets the rights.
	 * @param rights The rights level to set.
	 */
	public void setRights(Rights rights) {
		this.rights = rights;
	}

	/**
	 * Gets the rights.
	 * @return The player's rights.
	 */
	public Rights getRights() {
		return rights;
	}

	/**
	 * Checks if this player has a member's account.
	 * @return <code>true</code> if so, <code>false</code> if not.
	 */
	public boolean isMembers() {
		return members;
	}
	
	/**
	 * Sets the members flag.
	 * @param members The members flag.
	 */
	public void setMembers(boolean members) {
		this.members = members;
	}
	
	@Override
	public String toString() {
		return Player.class.getName() + " [name=" + name + " rights=" + rights + " members=" + members + " index=" + this.getIndex() + "]";
	}
	
	/**
	 * Sets the active flag.
	 * @param active The active flag.
	 */
	public void setActive(boolean active) {
		synchronized(this) {
			this.active = active;
		}
	}

	/**
	 * Gets the active flag.
	 * @return The active flag.
	 */
	public boolean isActive() {
		synchronized(this) {
			return active;
		}
	}
	
	/**
	 * Gets the action queue.
	 * @return The action queue.
	 */
	public ActionQueue getActionQueue() {
		return actionQueue;
	}

	/**
	 * Gets the inventory.
	 * @return The inventory.
	 */
	public Container getInventory() {
		return inventory;
	}
	
	/**
	 * Updates the players' options when in a PvP area.
	 */
	public void updatePlayerAttackOptions(boolean enable) {
		if(enable) {
			actionSender.sendInteractionOption("Attack", 1, true);
			//actionSender.sendOverlay(381);
		} else {
			
		}
	}
	
	/**
	 * Manages updateflags and HP modification when a hit occurs.
	 * @param source The Entity dealing the blow.
	 */
	public void inflictDamage(Hit inc, Entity source) {
		if(!getUpdateFlags().get(UpdateFlag.HIT)) {
			getDamage().setHit1(inc);
			getUpdateFlags().flag(UpdateFlag.HIT);
		} else {
			if(!getUpdateFlags().get(UpdateFlag.HIT_2)) {
				getDamage().setHit2(inc);
				getUpdateFlags().flag(UpdateFlag.HIT_2);
			}
		}
		skills.detractLevel(Skills.HITPOINTS, inc.getDamage());
		if((source instanceof Entity) && (source != null)) {
			this.setInCombat(true);
			this.setAggressorState(false);
			if(this.isAutoRetaliating()) {
				this.face(source.getLocation());
				this.getActionQueue().addAction(new AttackAction(this, source));
			}
		}
		if(skills.getLevel(Skills.HITPOINTS) <= 0) {
			if(!this.isDead()) {
				World.getWorld().submit(new DeathEvent(this));
			}
			this.setDead(true);
		}
	}
	
	public void inflictDamage(Hit inc) {
		this.inflictDamage(inc, null);
	}

	@Override
	public void deserialize(IoBuffer buf) {
		this.name = IoBufferUtils.getRS2String(buf);
		this.nameLong = NameUtils.nameToLong(this.name);
		this.password = IoBufferUtils.getRS2String(buf);
		this.rights = Player.Rights.getRights(buf.getUnsigned());
		this.members = buf.getUnsigned() == 1 ? true : false;
		setLocation(Location.create(buf.getUnsignedShort(), buf.getUnsignedShort(), buf.getUnsigned()));
		int[] look = new int[13];
		for(int i = 0; i < 13; i++) {
			look[i] = buf.getUnsigned();
		}
		appearance.setLook(look);
		for(int i = 0; i < Equipment.SIZE; i++) {
			int id = buf.getUnsignedShort();
			if(id != 65535) {
				int amt = buf.getInt();
				Item item = new Item(id, amt);
				equipment.set(i, item);
			}
		}
		for(int i = 0; i < Skills.SKILL_COUNT; i++) {
			skills.setSkill(i, buf.getUnsigned(), buf.getDouble());
		}
		for(int i = 0; i < Inventory.SIZE; i++) {
			int id = buf.getUnsignedShort();
			if(id != 65535) {
				int amt = buf.getInt();
				Item item = new Item(id, amt);
				inventory.set(i, item);
			}
		}
		if(buf.hasRemaining()) { // backwards compat
			for(int i = 0; i < Bank.SIZE; i++) {
				int id = buf.getUnsignedShort();
				if(id != 65535) {
					int amt = buf.getInt();
					Item item = new Item(id, amt);
					bank.set(i, item);
				}
			}
		}
	}

	@Override
	public void serialize(IoBuffer buf) {
		IoBufferUtils.putRS2String(buf, NameUtils.formatName(name));
		IoBufferUtils.putRS2String(buf, password);
		buf.put((byte) rights.toInteger());
		buf.put((byte) (members ? 1 : 0));
		buf.putShort((short) getLocation().getX());
		buf.putShort((short) getLocation().getY());
		buf.put((byte) getLocation().getZ());
		int[] look = appearance.getLook();
		for(int i = 0; i < 13; i++) {
			buf.put((byte) look[i]);
		}
		for(int i = 0; i < Equipment.SIZE; i++) {
			Item item = equipment.get(i);
			if(item == null) {
				buf.putShort((short) 65535);
			} else {
				buf.putShort((short) item.getId());
				buf.putInt(item.getCount());
			}
		}
		for(int i = 0; i < Skills.SKILL_COUNT; i++) {
			buf.put((byte) skills.getLevel(i));
			buf.putDouble((double) skills.getExperience(i));
		}
		for(int i = 0; i < Inventory.SIZE; i++) {
			Item item = inventory.get(i);
			if(item == null) {
				buf.putShort((short) 65535);
			} else {
				buf.putShort((short) item.getId());
				buf.putInt(item.getCount());
			}
		}
		for(int i = 0; i < Bank.SIZE; i++) {
			Item item = bank.get(i);
			if(item == null) {
				buf.putShort((short) 65535);
			} else {
				buf.putShort((short) item.getId());
				buf.putInt(item.getCount());
			}
		}
	}

	@Override
	public void addToRegion(Region region) {
		region.addPlayer(this);
	}

	@Override
	public void removeFromRegion(Region region) {
		region.removePlayer(this);
	}

	@Override
	public int getClientIndex() {
		return this.getIndex() + 32768;
	}

	@Override
	public void inflictDamage(int damage, HitType type) {
		// TODO Auto-generated method stub
		
	}
	
}
