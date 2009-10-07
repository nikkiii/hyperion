package org.hyperion.util.login;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoder;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;

/**
 * Login protocol encoding class.
 * @author Graham Edgecombe
 *
 */
public class LoginEncoder implements ProtocolEncoder {

	@Override
	public void encode(IoSession session, Object in, ProtocolEncoderOutput out) throws Exception {
		LoginPacket packet = (LoginPacket) in;
		IoBuffer buf = IoBuffer.allocate(1 + 2 + packet.getPayload().remaining());
		buf.put((byte) packet.getOpcode());
		buf.putShort((short) packet.getPayload().remaining());
		buf.put(packet.getPayload());
		buf.flip();
		out.write(buf);
	}

	@Override
	public void dispose(IoSession session) throws Exception {
		
	}
	
}
