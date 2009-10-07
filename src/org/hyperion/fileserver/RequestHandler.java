package org.hyperion.fileserver;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel.MapMode;
import java.util.zip.CRC32;

import org.hyperion.Server;
import org.hyperion.cache.Cache;

/**
 * Handles update requests and creates a response.
 * @author Graham Edgecombe
 *
 */
public class RequestHandler {
	
	/**
	 * The absolute path of the files directory.
	 */
	public static final String FILES_DIRECTORY = new File("data/htdocs/").getAbsolutePath();
	
	/**
	 * The cached CRC table.
	 */
	private static ByteBuffer crcTable = null;
	
	/**
	 * The cache instance.
	 */
	private static Cache cache;
	
	/**
	 * Handles a single request.
	 * @param request The request.
	 * @return The response.
	 */
	public static synchronized Response handle(Request request) {
		if(cache == null) {
			try {
				cache = new Cache(new File("./data/cache/"));
			} catch(Exception e) {
				throw new RuntimeException(e);
			}
		}
		String path = request.getPath();
		if(path.equals("/")) {
			path = "/index.html";
		}
		String mime = getMimeType(path);
		try {
			if(crcTable == null) {
				crcTable = calculateCrcTable();
			}
			if(path.startsWith("/crc")) {
				return new Response(crcTable.asReadOnlyBuffer(), mime);
			} else if(path.startsWith("/title")) {
				return new Response(cache.getFile(0, 1).getBytes(), mime);
			} else if(path.startsWith("/config")) {
				return new Response(cache.getFile(0, 2).getBytes(), mime);
			} else if(path.startsWith("/interface")) {
				return new Response(cache.getFile(0, 3).getBytes(), mime);
			} else if(path.startsWith("/media")) {
				return new Response(cache.getFile(0, 4).getBytes(), mime);
			} else if(path.startsWith("/versionlist")) {
				return new Response(cache.getFile(0, 5).getBytes(), mime);
			} else if(path.startsWith("/textures")) {
				return new Response(cache.getFile(0, 6).getBytes(), mime);
			} else if(path.startsWith("/wordenc")) {
				return new Response(cache.getFile(0, 7).getBytes(), mime);
			} else if(path.startsWith("/sounds")) {
				return new Response(cache.getFile(0, 8).getBytes(), mime);
			}
			path = new File(FILES_DIRECTORY + path).getAbsolutePath();
			if(!path.startsWith(FILES_DIRECTORY)) {
				return null;
			}
			RandomAccessFile f = new RandomAccessFile(path, "r");
			try {
				MappedByteBuffer data = f.getChannel().map(MapMode.READ_ONLY, 0, f.length());
				return new Response(data, mime);
			} finally {
				f.close();
			}
		} catch(IOException ex) {
			return null;
		}
	}
	
	/*
	 * The following code is where it downloads the /crc file from JAGGRAB.
	 */
	/*
    DataInputStream datainputstream = method132("crc" + (int)(Math.random() * 99999999D) + "-" + 317);
    Class30_Sub2_Sub2 class30_sub2_sub2 = new Class30_Sub2_Sub2(new byte[40], 891);
    datainputstream.readFully(class30_sub2_sub2.aByteArray1405, 0, 40);
    datainputstream.close();
    // here the client reads every CRC value
    for(int i1 = 0; i1 < 9; i1++)
        anIntArray1090[i1] = class30_sub2_sub2.method413();

	// and the final hash the server produces
    int j1 = class30_sub2_sub2.method413();
    
    // and now it calculates its own hash
    int k1 = 1234;
    for(int l1 = 0; l1 < 9; l1++)
    	// the CRC values in anIntArray1090 are produced in the method below
        k1 = (k1 << 1) + anIntArray1090[l1];

	// and checks the two hashes (expected and received)
    if(j1 != k1)
    {
        s = "checksum problem";
        anIntArray1090[8] = 0;
    }
	 */

	/*
	 * The following code is where various files in the cache are loaded, and
	 * the CRC values are produced here.
	 */
	/*
	public final Class44 method67(int i, String s, String s1, int j, byte byte0, int k)
    {
        byte abyte0[] = null;
        int l = 5;
        try
        {
            if(aClass14Array970[0] != null)
                abyte0 = aClass14Array970[0].method233(true, i);
        }
        catch(Exception _ex) { }
        if(abyte0 != null)
        {
            aCRC32_930.reset();
            aCRC32_930.update(abyte0);
            int i1 = (int)aCRC32_930.getValue();
            if(i1 != j)
                abyte0 = null;
        }
        if(abyte0 != null)
        {
            Class44 class44 = new Class44(44820, abyte0);
            return class44;
        }
        int j1 = 0;
        while(abyte0 == null) 
        {
            String s2 = "Unknown error";
            method13(k, (byte)4, "Requesting " + s);
            Object obj = null;
            try
            {
                int k1 = 0;
                DataInputStream datainputstream = method132(s1 + j);
                byte abyte1[] = new byte[6];
                datainputstream.readFully(abyte1, 0, 6);
                Class30_Sub2_Sub2 class30_sub2_sub2 = new Class30_Sub2_Sub2(abyte1, 891);
                class30_sub2_sub2.anInt1406 = 3;
                int i2 = class30_sub2_sub2.method412() + 6;
                int j2 = 6;
                abyte0 = new byte[i2];
                for(int k2 = 0; k2 < 6; k2++)
                    abyte0[k2] = abyte1[k2];

                while(j2 < i2) 
                {
                    int l2 = i2 - j2;
                    if(l2 > 1000)
                        l2 = 1000;
                    int j3 = datainputstream.read(abyte0, j2, l2);
                    if(j3 < 0)
                    {
                        s2 = "Length error: " + j2 + "/" + i2;
                        throw new IOException("EOF");
                    }
                    j2 += j3;
                    int k3 = (j2 * 100) / i2;
                    if(k3 != k1)
                        method13(k, (byte)4, "Loading " + s + " - " + k3 + "%");
                    k1 = k3;
                }
                datainputstream.close();
                try
                {
                    if(aClass14Array970[0] != null)
                        aClass14Array970[0].method234(abyte0.length, abyte0, (byte)2, i);
                }
                catch(Exception _ex)
                {
                    aClass14Array970[0] = null;
                }
                if(abyte0 != null)
                {
                    aCRC32_930.reset();
                    aCRC32_930.update(abyte0);
                    int i3 = (int)aCRC32_930.getValue();
                    if(i3 != j)
                    {
                        abyte0 = null;
                        j1++;
                        s2 = "Checksum error: " + i3;
                    }
                }
            }
            catch(IOException ioexception)
            {
                if(s2.equals("Unknown error"))
                    s2 = "Connection error";
                abyte0 = null;
            }
            catch(NullPointerException _ex)
            {
                s2 = "Null error";
                abyte0 = null;
                if(!signlink.reporterror)
                    return null;
            }
            catch(ArrayIndexOutOfBoundsException _ex)
            {
                s2 = "Bounds error";
                abyte0 = null;
                if(!signlink.reporterror)
                    return null;
            }
            catch(Exception _ex)
            {
                s2 = "Unexpected error";
                abyte0 = null;
                if(!signlink.reporterror)
                    return null;
            }
            if(abyte0 == null)
            {
                for(int l1 = l; l1 > 0; l1--)
                {
                    if(j1 >= 3)
                    {
                        method13(k, (byte)4, "Game updated - please reload page");
                        l1 = 10;
                    } else
                    {
                        method13(k, (byte)4, s2 + " - Retrying in " + l1);
                    }
                    try
                    {
                        Thread.sleep(1000L);
                    }
                    catch(Exception _ex) { }
                }

                l *= 2;
                if(l > 60)
                    l = 60;
                aBoolean872 = !aBoolean872;
            }
        }
        Class44 class44_1 = new Class44(44820, abyte0);
        if(byte0 != -41)
            throw new NullPointerException();
        else
            return class44_1;
    }
	 */
	
	/**
	 * <p>Calculates the crc table.</p>
	 * 
	 * <p>The following code is based on research into the client (above) and
	 * some forum post I found on the web archive.</p>
	 * @return The crc table.
	 * @throws IOException if an I/O error occurs.
	 */
	private static ByteBuffer calculateCrcTable() throws IOException {
		final CRC32 crc = new CRC32();
		int[] checksums = new int[9];
		
		/*
		 * Set the first checksum. As 0 is the CRC table itself (which we are
		 * calculating!), this is set to the client version instead.
		 */
		checksums[0] = Server.VERSION;
		
		/*
		 * Calculate the checksums.
		 */
		for(int i = 1; i < checksums.length; i++) {
			byte[] file = cache.getFile(0, i).getBytes(); // each of these maps to the files above
			crc.reset();
			crc.update(file, 0, file.length);
			checksums[i] = (int) crc.getValue();
		}
		
		/*
		 * This is some sort of overall hash of all the checksums themselves.
		 */
		int hash = 1234;
		
		/*
		 * Calculate the hash from every checksum.
		 */
		for(int i = 0; i < checksums.length; i++) {
			hash = (hash << 1) + checksums[i];
		}
		
		/*
		 * And write the table to a bytebuffer.
		 */
		ByteBuffer bb = ByteBuffer.allocate(4 * (checksums.length + 1));
		for(int i = 0; i < checksums.length; i++) {
			bb.putInt(checksums[i]);
		}
		bb.putInt(hash);
		bb.flip();
		return bb;
	}

	/**
	 * Gets the mime type of a file.
	 * @param path The path to the file.
	 * @return The mime type.
	 */
	private static String getMimeType(String path) {
		String mime = "application/octect-stream";
		if(path.endsWith(".htm") || path.endsWith(".html")) {
			mime = "text/html";
		} else if(path.endsWith(".jar")) {
			mime = "application/java-archive";
		}
		return mime;
	}

}
