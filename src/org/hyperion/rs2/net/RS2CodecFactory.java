package org.hyperion.rs2.net;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolEncoder;

/**
 * A factory which produces codecs for the RuneScape protocol.
 * @author Graham Edgecombe
 *
 */
public class RS2CodecFactory implements ProtocolCodecFactory {
	
	/**
	 * The login decoder.
	 */
	private static final RS2LoginDecoder LOGIN_DECODER = new RS2LoginDecoder();
	
	/**
	 * The game decoder.
	 */
	private static final RS2Decoder DECODER = new RS2Decoder();
	
	/**
	 * The encoder.
	 */
	private static final RS2Encoder ENCODER = new RS2Encoder();
	
	/**
	 * The login codec factory.
	 */
	public static final RS2CodecFactory LOGIN = new RS2CodecFactory(false);
	
	/**
	 * The game codec factory.
	 */
	public static final RS2CodecFactory GAME = new RS2CodecFactory(true);
	
	/**
	 * Login complete flag.
	 */
	private boolean loginComplete;

	/**
	 * Creates the codec factory.
	 * @param loginComplete Login complete flag.
	 */
	private RS2CodecFactory(boolean loginComplete) {
		this.loginComplete = loginComplete;
	}

	@Override
	public ProtocolDecoder getDecoder(IoSession session) throws Exception {
		return loginComplete ? DECODER : LOGIN_DECODER;
	}

	@Override
	public ProtocolEncoder getEncoder(IoSession session) throws Exception {
		return ENCODER;
	}

}
