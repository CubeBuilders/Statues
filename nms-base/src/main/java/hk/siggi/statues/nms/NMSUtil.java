package hk.siggi.statues.nms;

import com.mojang.authlib.GameProfile;
import java.lang.reflect.Constructor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public abstract class NMSUtil {

	// <editor-fold desc="Setup" defaultstate="collapsed">
	private static NMSUtil nmsUtil = null;

	public static NMSUtil get() {
		if (nmsUtil == null) {
			try {
				Class<NMSUtil> clazz = (Class<NMSUtil>) Class.forName("hk.siggi.statues.nms." + getVersion() + ".NMSUtil");
				Constructor<NMSUtil> newNMSUtil = clazz.getConstructor();
				nmsUtil = newNMSUtil.newInstance();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return nmsUtil;
	}

	private static String getVersion() {
		String name = Bukkit.getServer().getClass().getName();
		String version = name.substring(name.indexOf(".v") + 1);
		version = version.substring(0, version.indexOf("."));
		return version;
	}
	// </editor-fold>

	public final double eyesHeight;

	public NMSUtil(double eyesHeight) {
		this.eyesHeight = eyesHeight;
	}

	public abstract PacketFactory getPacketFactory();

	public abstract GameProfile getGameProfile(Player p);

	public abstract int getPing(Player p);

	public abstract int getGamemode(Player p);

	public abstract void sendPacket(Player p, Object packet);

	public abstract Material getSignItem();

	public abstract Material getSignPost();

	public abstract Material getWallSign();
}
