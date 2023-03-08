package hk.siggi.statues.nms.v1_8_R3;

import com.mojang.authlib.GameProfile;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.Packet;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class NMSUtil extends hk.siggi.statues.nms.NMSUtil {

	public NMSUtil() {
		super(1.620);
	}

	private PacketFactory packetFactory;

	@Override
	public PacketFactory getPacketFactory() {
		if (packetFactory == null) {
			packetFactory = new PacketFactory();
		}
		return packetFactory;
	}

	@Override
	public GameProfile getGameProfile(Player p) {
		CraftPlayer pl = (CraftPlayer) p;
		return pl.getProfile();
	}

	@Override
	public int getPing(Player p) {
		CraftPlayer pl = ((CraftPlayer) p);
		EntityPlayer nmsPlayer = ((EntityPlayer) pl.getHandle());
		return nmsPlayer.ping;
	}

	@Override
	public int getGamemode(Player p) {
		CraftPlayer pl = ((CraftPlayer) p);
		EntityPlayer nmsPlayer = ((EntityPlayer) pl.getHandle());
		return nmsPlayer.playerInteractManager.getGameMode().getId();
	}

	@Override
	public void sendPacket(Player p, Object packet) {
		EntityPlayer entityPlayer = ((CraftPlayer) p).getHandle();
		entityPlayer.playerConnection.sendPacket((Packet<?>) packet);
	}

	@Override
	public Material getSignItem() {
		return Material.SIGN;
	}

	@Override
	public Material getSignPost() {
		return Material.SIGN_POST;
	}

	@Override
	public Material getWallSign() {
		return Material.WALL_SIGN;
	}
}
