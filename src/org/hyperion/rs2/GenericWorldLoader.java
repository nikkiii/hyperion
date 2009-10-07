package org.hyperion.rs2;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.apache.mina.core.buffer.IoBuffer;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.PlayerDetails;
import org.hyperion.rs2.util.NameUtils;
import org.hyperion.util.Streams;

/**
 * An implementation of the <code>WorldLoader</code> class that saves players
 * in binary, gzip-compressed files in the <code>data/players/</code>
 * directory.
 * @author Graham Edgecombe
 *
 */
public class GenericWorldLoader implements WorldLoader {

	@Override
	public LoginResult checkLogin(PlayerDetails pd) {
		Player player = null;
		int code = 2;
		File f = new File("data/savedGames/" + NameUtils.formatNameForProtocol(pd.getName()) + ".dat.gz");
		if(f.exists()) {
			try {
				InputStream is = new GZIPInputStream(new FileInputStream(f));
				String name = Streams.readRS2String(is);
				String pass = Streams.readRS2String(is);
				if(!name.equals(NameUtils.formatName(pd.getName()))) {
					code = 3;
				}
				if(!pass.equals(pd.getPassword())) {
					code = 3;
				}
			} catch(IOException ex) {
				code = 11;
			}
		}
		if(code == 2) {
			player = new Player(pd);
		}
		return new LoginResult(code, player);
	}

	@Override
	public boolean savePlayer(Player player) {
		try {
			OutputStream os = new GZIPOutputStream(new FileOutputStream("data/savedGames/" + NameUtils.formatNameForProtocol(player.getName()) + ".dat.gz"));
			IoBuffer buf = IoBuffer.allocate(1024);
			buf.setAutoExpand(true);
			player.serialize(buf);
			buf.flip();
			byte[] data = new byte[buf.limit()];
			buf.get(data);
			os.write(data);
			os.flush();
			os.close();
			return true;
		} catch(IOException ex) {
			return false;
		}
	}

	@Override
	public boolean loadPlayer(Player player) {
		try {
			File f = new File("data/savedGames/" + NameUtils.formatNameForProtocol(player.getName()) + ".dat.gz");
			InputStream is = new GZIPInputStream(new FileInputStream(f));
			IoBuffer buf = IoBuffer.allocate(1024);
			buf.setAutoExpand(true);
			while(true) {
				byte[] temp = new byte[1024];
				int read = is.read(temp, 0, temp.length);
				if(read == -1) {
					break;
				} else {
					buf.put(temp, 0, read);
				}
			}
			buf.flip();
			player.deserialize(buf);
			return true;
		} catch(IOException ex) {
			return false;
		}
	}

}
