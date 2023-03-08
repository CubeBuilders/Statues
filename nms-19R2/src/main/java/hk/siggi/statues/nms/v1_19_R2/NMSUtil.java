package hk.siggi.statues.nms.v1_19_R2;

import com.mojang.authlib.GameProfile;

import net.minecraft.network.protocol.Packet;
import net.minecraft.server.level.EntityPlayer;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_19_R2.entity.CraftPlayer;
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
		return nmsPlayer.e;
	}

	@Override
	public int getGamemode(Player p) {
		CraftPlayer pl = ((CraftPlayer) p);
		EntityPlayer nmsPlayer = ((EntityPlayer) pl.getHandle());
		return nmsPlayer.d.b().a();
	}

	@Override
	public void sendPacket(Player p, Object packet) {
		EntityPlayer entityPlayer = ((CraftPlayer) p).getHandle();
		entityPlayer.b.a((Packet<?>) packet);
	}

	@Override
	public Material getSignItem() {
		return Material.OAK_SIGN;
	}

	@Override
	public Material getSignPost() {
		return Material.OAK_SIGN;
	}

	@Override
	public Material getWallSign() {
		return Material.OAK_WALL_SIGN;
	}
}
