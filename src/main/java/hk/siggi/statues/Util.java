package hk.siggi.statues;

import org.bukkit.Material;
import org.bukkit.Server;

public class Util {

	private Util() {
	}

	private static Material signPost;
	private static Material wallSign;

	static void init(Server server) {
		String name = server.getClass().getName();
		int versionIdx = name.indexOf(".v1_") + 4;
		int end = name.indexOf("_", versionIdx);
		int version = Integer.parseInt(name.substring(versionIdx, end));
		if (version < 13) {
			signPost = Material.valueOf("SIGN_POST");
			wallSign = Material.valueOf("WALL_SIGN");
		} else {
			signPost = Material.OAK_SIGN;
			wallSign = Material.OAK_WALL_SIGN;
		}
	}

	public static Material getSignPost() {
		return signPost;
	}

	public static Material getWallSign() {
		return wallSign;
	}
}
