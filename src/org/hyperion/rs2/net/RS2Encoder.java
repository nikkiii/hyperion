package org.hyperion.rs2.net;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoder;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;
import org.hyperion.rs2.model.Player;

/**
 * Game protocol encoding class.
 * @author Graham Edgecombe
 *
 */
public class RS2Encoder implements ProtocolEncoder {

	@Override
	public void encode(IoSession session, Object in, ProtocolEncoderOutput out) throws Exception {
		Packet p = (Packet) in;
		
		/*
		 * Check what type the packet is.
		 */
		if(p.isRaw()) {
			/*
			 * If the packet is raw, send its payload.
			 */
			out.write(p.getPayload());
		} else {
			/*
			 * If not, get the out ISAAC cipher.
			 */
			ISAACCipher outCipher = ((Player) session.getAttribute("player")).getOutCipher();
			
			/*
			 * Get the packet attributes.
			 */
			int opcode = p.getOpcode();
			Packet.Type type = p.getType();
			int length = p.getLength();
			
			/*
			 * Encrypt the packet opcode.
			 */
			opcode += outCipher.getNextValue();
			
			/*
			 * Compute the required size for the buffer.
			 */
			int finalLength = length + 1;
			switch(type) {
			case VARIABLE:
				finalLength += 1;
				break;
			case VARIABLE_SHORT:
				finalLength += 2;
				break;
			}
			
			/*
			 * Create the buffer and write the opcode (and length if the
			 * packet is variable-length).
			 */
			IoBuffer buffer = IoBuffer.allocate(finalLength);
			buffer.put((byte) opcode);
			switch(type) {
			case VARIABLE:
				buffer.put((byte) length);
				break;
			case VARIABLE_SHORT:
				buffer.putShort((short) length);
				break;
			}
			
			/*
			 * Write the payload itself.
			 */
			buffer.put(p.getPayload());
			
			/*
			 * Flip and dispatch the packet.
			 */
			out.write(buffer.flip());
		}
	}

	@Override
	public void dispose(IoSession session) throws Exception {
		
	}
	
}
