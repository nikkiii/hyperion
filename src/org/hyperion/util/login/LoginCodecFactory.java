package org.hyperion.util.login;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolEncoder;

/**
 * A factory which produces codecs for the login server protocol.
 * @author Graham Edgecombe
 *
 */
public class LoginCodecFactory implements ProtocolCodecFactory {
	
	/**
	 * The login encoder.
	 */
	private static final LoginEncoder ENCODER = new LoginEncoder();
	
	/**
	 * The login decoder.
	 */
	private LoginDecoder decoder = new LoginDecoder();

	@Override
	public ProtocolDecoder getDecoder(IoSession arg0) throws Exception {
		return decoder;
	}

	@Override
	public ProtocolEncoder getEncoder(IoSession arg0) throws Exception {
		return ENCODER;
	}

}
