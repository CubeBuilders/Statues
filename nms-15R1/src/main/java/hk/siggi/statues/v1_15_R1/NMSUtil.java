package hk.siggi.statues.v1_15_R1;

import com.mojang.authlib.GameProfile;
import static hk.siggi.statues.Reflection.getField;
import hk.siggi.statues.Statue;
import java.util.concurrent.atomic.AtomicInteger;
import net.minecraft.server.v1_15_R1.EntityPlayer;
import net.minecraft.server.v1_15_R1.Packet;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class NMSUtil extends hk.siggi.statues.NMSUtil<PacketFactory, StatueEntity, Packet> {

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
	public StatueEntity newStatueEntity(GameProfile profile, Statue statue) {
		return new StatueEntity(profile, statue, this, getPacketFactory());
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
	public void sendPacket(Player p, Packet packet) {
		EntityPlayer entityPlayer = ((CraftPlayer) p).getHandle();
		entityPlayer.playerConnection.sendPacket(packet);
	}

	@Override
	public int getNextEntityID() {
		try {
			Class clazz = net.minecraft.server.v1_15_R1.Entity.class;
			AtomicInteger entityID = (AtomicInteger) getField(clazz, null, "entityCount");
			return entityID.getAndIncrement();
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
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
