package org.hyperion.rs2.login;

import org.hyperion.rs2.WorldLoader;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.PlayerDetails;
import org.hyperion.rs2.model.World;

/**
 * A <code>WorldLoader</code> which loads from the login server.
 * @author Graham Edgecombe
 *
 */
public class LoginServerWorldLoader implements WorldLoader {

	@Override
	public LoginResult checkLogin(PlayerDetails pd) {
		if(!World.getWorld().getLoginServerConnector().isAuthenticated()) {
			return new LoginResult(8);
		} else {
			return World.getWorld().getLoginServerConnector().checkLogin(pd);
		}
	}

	@Override
	public boolean loadPlayer(Player player) {
		return World.getWorld().getLoginServerConnector().loadPlayer(player);
	}

	@Override
	public boolean savePlayer(Player player) {
		return World.getWorld().getLoginServerConnector().savePlayer(player);
	}

}
