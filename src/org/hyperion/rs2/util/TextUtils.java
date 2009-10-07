package org.hyperion.rs2.util;

import org.hyperion.rs2.Constants;

/**
 * Text utility class.
 * @author Graham Edgecombe
 *
 */
public class TextUtils {
	
	/**
	 * Unpacks text.
	 * @param packedData The packet text.
	 * @param size The length.
	 * @return The string.
	 */
	public static String textUnpack(byte packedData[], int size) {
		byte[] decodeBuf = new byte[4096];
		int idx = 0, highNibble = -1;
		for(int i = 0; i < size * 2; i++) {
			int val = packedData[i / 2] >> (4 - 4 * (i % 2)) & 0xf;
			if(highNibble == -1) {
				if(val < 13) {
					decodeBuf[idx++] = (byte) Constants.XLATE_TABLE[val];
				} else {
					highNibble = val;
				}
			} else {
				decodeBuf[idx++] = (byte) Constants.XLATE_TABLE[((highNibble << 4) + val) - 195];
				highNibble = -1;
			}
		}
		return new String(decodeBuf, 0, idx);
	}
	
	/**
	 * Optimises text.
	 * @param text The text to optimise.
	 * @return The text.
	 */
	public static String optimizeText(String text) {
		char buf[] = text.toCharArray();
		boolean endMarker = true;
		for(int i = 0; i < buf.length; i++) {
			char c = buf[i];
			if(endMarker && c >= 'a' && c <= 'z') {
				buf[i] -= 0x20;
				endMarker = false;
			}
			if(c == '.' || c == '!' || c == '?') {
				endMarker = true;
			}
		}
		return new String(buf, 0, buf.length);
	}

	/**
	 * Packs text. 
	 * @param packedData The destination of the packed text.
	 * @param text The unpacked text.
	 */
	public static void textPack(byte packedData[], String text) {
		if(text.length() > 80) {
			text = text.substring(0, 80);
		}
		text = text.toLowerCase();
		int carryOverNibble = -1;
		int ofs = 0;
		for(int idx = 0; idx < text.length(); idx++) {
			char c = text.charAt(idx);
			int tableIdx = 0;
			for(int i = 0; i < Constants.XLATE_TABLE.length; i++) {
				if(c == (byte) Constants.XLATE_TABLE[i]) {
					tableIdx = i;
					break;
				}
			}
			if(tableIdx > 12) {
				tableIdx += 195;
			}
			if(carryOverNibble == -1) {
				if(tableIdx < 13) {
					carryOverNibble = tableIdx;
				} else {
					packedData[ofs++] = (byte) (tableIdx);
				}
			} else if (tableIdx < 13) {
				packedData[ofs++] = (byte) ((carryOverNibble << 4) + tableIdx);
				carryOverNibble = -1;
			} else {
				packedData[ofs++] = (byte) ((carryOverNibble << 4) + (tableIdx >> 4));
				carryOverNibble = tableIdx & 0xf;
			}
		}
		if(carryOverNibble != -1) {
			packedData[ofs++] = (byte) (carryOverNibble << 4);
		}
	}

	/**
	 * Filters invalid characters out of a string.
	 * @param s The string.
	 * @return The filtered string.
	 */
	public static String filterText(String s) {
		StringBuilder bldr = new StringBuilder();
		for(char c : s.toLowerCase().toCharArray()) {
			boolean valid = false;
			for(char validChar : Constants.XLATE_TABLE) {
				if(validChar == c) {
					valid = true;
				}
			}
			if(valid) {
				bldr.append((char) c);
			}
		}
		return bldr.toString();
	}

}
