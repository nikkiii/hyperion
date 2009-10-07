package org.hyperion.fileserver;

/**
 * Represents a single request for a file on either a JAGGRAB or HTTP server.
 * @author Graham Edgecombe
 *
 */
public class Request {
	
	/**
	 * The path of the file.
	 */
	private String path;
	
	/**
	 * Creates the request.
	 * @param path The path of the file.
	 */
	public Request(String path) {
		if(path.startsWith("/runescape") && path.endsWith(".pack200")) {
			this.path = "/runescape.pack200";
		} else if(path.startsWith("/runescape") && path.endsWith(".js5")) {
			this.path = "/runescape.js5";
		} else if(path.startsWith("/unpackclass") && path.endsWith(".pack")) {
			this.path = "/unpackclass.pack";
		} else if(path.startsWith("/crc")) {
			this.path = "/crc";
		} else if(path.startsWith("/config")) {
			this.path = "/config";
		} else if(path.startsWith("/title")) {
			this.path = "/title";
		} else if(path.startsWith("/interface")) {
			this.path = "/interface";
		} else if(path.startsWith("/media")) {
			this.path = "/media";
		} else if(path.startsWith("/sounds")) {
			this.path = "/sounds";
		} else if(path.startsWith("/textures")) {
			this.path = "/textures";
		} else if(path.startsWith("/versionlist")) {
			this.path = "/versionlist";
		} else if(path.startsWith("/wordenc")) {
			this.path = "/wordenc";
		} else {
			this.path = path;
		}
	}
	
	/**
	 * Gets the path.
	 * @return The path.
	 */
	public String getPath() {
		return path;
	}

}
