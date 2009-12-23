package org.hyperion.rs2.net;

import java.security.SecureRandom;
import java.util.logging.Logger;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.CumulativeProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;
import org.hyperion.Server;
import org.hyperion.rs2.model.PlayerDetails;
import org.hyperion.rs2.model.World;
import org.hyperion.rs2.net.ondemand.OnDemandPool;
import org.hyperion.rs2.net.ondemand.OnDemandRequest;
import org.hyperion.rs2.util.IoBufferUtils;
import org.hyperion.rs2.util.NameUtils;

/**
 * Login protocol decoding class.
 * @author Graham Edgecombe
 *
 */
public class RS2LoginDecoder extends CumulativeProtocolDecoder {

	/**
	 * Logger instance.
	 */
	private static final Logger logger = Logger.getLogger(RS2LoginDecoder.class.getName());
	
	/**
	 * Opcode stage.
	 */
	public static final int STATE_OPCODE = 0;
	
	/**
	 * Login stage.
	 */
	public static final int STATE_LOGIN = 1;
	
	/**
	 * Precrypted stage.
	 */
	public static final int STATE_PRECRYPTED = 2;
	
	/**
	 * Crypted stage.
	 */
	public static final int STATE_CRYPTED = 3;
	
	/**
	 * Update stage.
	 */
	public static final int STATE_UPDATE = -1;
	
	/**
	 * Game opcode.
	 */
	public static final int OPCODE_GAME = 14;
	
	/**
	 * Update opcode.
	 */
	public static final int OPCODE_UPDATE = 15;
	
	/**
	 * Secure random number generator.
	 */
	private static final SecureRandom RANDOM = new SecureRandom();
	
	/**
	 * Initial login response.
	 */
	private static final byte[] INITIAL_RESPONSE = new byte[] {
		0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0
	};
		
	@Override
	protected boolean doDecode(IoSession session, IoBuffer in, ProtocolDecoderOutput out) throws Exception {
		int state = (Integer) session.getAttribute("state", STATE_OPCODE);
		switch(state) {
		case STATE_UPDATE:
			if(in.remaining() >= 4) {
				/*
				 * Here we read the cache id (idx file), file id and priority.
				 */
				int cacheId = in.get() & 0xFF;
				int fileId = ((in.get() & 0xFF) << 8) | (in.get() & 0xFF);
				int priority = in.get() & 0xFF;

				/*
				 * We push the request into the ondemand pool so it can be served.
				 */
				OnDemandPool.getOnDemandPool().pushRequest(new OnDemandRequest(session, cacheId, fileId, priority));
				return true;
			} else {
				in.rewind();
				return false;
			}
		case STATE_OPCODE:
			if(in.remaining() >= 1) {
				/*
				 * Here we read the first opcode which indicates the type
				 * of connection.
				 * 
				 * 14 = game
				 * 15 = update
				 * 
				 * Updating is disabled in the vast majority of 317
				 * clients.
				 */
				int opcode = in.get() & 0xFF;
				switch(opcode) {
				case OPCODE_GAME:
					session.setAttribute("state", STATE_LOGIN);
					return true;
				case OPCODE_UPDATE:
					session.setAttribute("state", STATE_UPDATE);
					session.write(new PacketBuilder().put(INITIAL_RESPONSE).toPacket());
					return true;
				default:
					logger.info("Invalid opcode : " + opcode);
					session.close(false);
					break;
				}
			} else {
				in.rewind();
				return false;
			}
			break;
		case STATE_LOGIN:
			if(in.remaining() >= 1) {
				/*
				 * The name hash is a simple hash of the name which is
				 * suspected to be used to select the appropriate login
				 * server.
				 */
				@SuppressWarnings("unused")
				int nameHash = in.get() & 0xFF;
				
				/*
				 * We generated the server session key using a SecureRandom
				 * class for security.
				 */
				long serverKey = RANDOM.nextLong();
				
				/*
				 * The initial response is just 0s which the client is set
				 * to ignore (probably some sort of modification).
				 */
				session.write(new PacketBuilder().put(INITIAL_RESPONSE).put((byte) 0).putLong(serverKey).toPacket());
				session.setAttribute("state", STATE_PRECRYPTED);
				session.setAttribute("serverKey", serverKey);
				return true;
			}
			break;
		case STATE_PRECRYPTED:
			if(in.remaining() >= 2) {
				/*
				 * We read the type of login.
				 * 
				 * 16 = normal
				 * 18 = reconnection
				 */
				int loginOpcode = in.get() & 0xFF;
				if(loginOpcode != 16 && loginOpcode != 18) {
					logger.info("Invalid login opcode : " + loginOpcode);
					session.close(false);
					in.rewind();
					return false;
				}
				
				/*
				 * We read the size of the login packet.
				 */
				int loginSize = in.get() & 0xFF;
				
				/*
				 * And calculated how long the encrypted block will be.
				 */
				int loginEncryptSize = loginSize - (36 + 1 + 1 + 2);
				
				/*
				 * This could be invalid so if it is we ignore it.
				 */
				if(loginEncryptSize <= 0) {
					logger.info("Encrypted packet size zero or negative : " + loginEncryptSize);
					session.close(false);
					in.rewind();
					return false;
				}
				session.setAttribute("state", STATE_CRYPTED);
				session.setAttribute("size", loginSize);
				session.setAttribute("encryptSize", loginEncryptSize);
				return true;
			}
			break;
		case STATE_CRYPTED:
			int size = (Integer) session.getAttribute("size");
			int encryptSize = (Integer) session.getAttribute("encryptSize");
			if(in.remaining() >= size) {
				/*
				 * We read the magic ID which is 255 (0xFF) which indicates
				 * this is the real login packet.
				 */
				int magicId = in.get() & 0xFF;
				if(magicId != 255) {
					logger.info("Incorrect magic id : " + magicId);
					session.close(false);
					in.rewind();
					return false;
				}
				
				/*
				 * We now read a short which is the client version and
				 * check if it equals 317.
				 */
				int version = in.getShort() & 0xFFFF;
				if(version != Server.VERSION) {
					logger.info("Incorrect version : " + version);
					session.close(false);
					in.rewind();
					return false;
				}
				
				/*
				 * The following byte indicates if we are using a low
				 * memory version.
				 */
				@SuppressWarnings("unused")
				boolean lowMemoryVersion = (in.get() & 0xFF) == 1;
				
				/*
				 * We know read the cache indices.
				 */
				for(int i = 0; i < 9; i++) {
					in.getInt();
				}
				
				/*
				 * The encrypted size includes the size byte which we don't
				 * need.
				 */
				encryptSize--;
				
				/*
				 * We check if there is a mismatch in the sizing.
				 */
				int reportedSize = in.get() & 0xFF;
				if(reportedSize != encryptSize) {
					logger.info("Packet size mismatch (expected : " + encryptSize + ", reported : " + reportedSize + ")");
					session.close(false);
					in.rewind();
					return false;
				}
				
				/*
				 * We now read the encrypted block opcode (although in most
				 * 317 clients and this server the RSA is disabled) and
				 * check it is equal to 10.
				 */
				int blockOpcode = in.get() & 0xFF;
				if(blockOpcode != 10) {
					logger.info("Invalid login block opcode : " + blockOpcode);
					session.close(false);
					in.rewind();
					return false;
				}

				/*
				 * We read the client's session key.
				 */
				long clientKey = in.getLong();
				
				/*
				 * And verify it has the correct server session key.
				 */
				long serverKey = (Long) session.getAttribute("serverKey");
				long reportedServerKey = in.getLong();
				if(reportedServerKey != serverKey) {
					logger.info("Server key mismatch (expected : " + serverKey + ", reported : " + reportedServerKey + ")");
					session.close(false);
					in.rewind();
					return false;
				}
				
				/*
				 * The UID, found in random.dat in newer clients and
				 * uid.dat in older clients is a way of identifying a
				 * computer.
				 * 
				 * However, some clients send a hardcoded or random UID,
				 * making it useless in the private server scene.
				 */
				int uid = in.getInt();
				
				/*
				 * We read and format the name and passwords.
				 */
				String name = NameUtils.formatName(IoBufferUtils.getRS2String(in));
				String pass = IoBufferUtils.getRS2String(in);
				logger.info("Login request : username=" + name + " password=" + pass);
				
				/*
				 * And setup the ISAAC cipher which is used to encrypt and
				 * decrypt opcodes.
				 * 
				 * However, without RSA, this is rendered useless anyway.
				 */
				int[] sessionKey = new int[4];
				sessionKey[0] = (int) (clientKey >> 32);
				sessionKey[1] = (int) clientKey;
				sessionKey[2] = (int) (serverKey >> 32);
				sessionKey[3] = (int) serverKey;
				
				session.removeAttribute("state");
				session.removeAttribute("serverKey");
				session.removeAttribute("size");
				session.removeAttribute("encryptSize");
				
				ISAACCipher inCipher = new ISAACCipher(sessionKey);
				for(int i = 0; i < 4; i++) {
					sessionKey[i] += 50;
				}
				ISAACCipher outCipher = new ISAACCipher(sessionKey);
				
				/*
				 * Now, the login has completed, and we do the appropriate
				 * things to fire off the chain of events which will load
				 * and check the saved games etc.
				 */
				session.getFilterChain().remove("protocol");
				session.getFilterChain().addFirst("protocol", new ProtocolCodecFilter(RS2CodecFactory.GAME));
				
				PlayerDetails pd = new PlayerDetails(session, name, pass, uid, inCipher, outCipher);
				World.getWorld().load(pd);
			}
			break;
		}
		in.rewind();
		return false;
	}

}
