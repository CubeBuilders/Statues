package hk.siggi.statues.v1_19_R2;

import com.mojang.authlib.GameProfile;
import hk.siggi.statues.Statue;
import net.minecraft.core.BlockPosition;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.*;
import net.minecraft.network.syncher.DataWatcherObject;
import net.minecraft.world.entity.player.EntityHuman;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_19_R2.CraftWorld;
import org.bukkit.entity.Player;

public class StatueEntity extends EntityHuman implements hk.siggi.statues.StatueEntity<Packet> {

	private final Statue statue;
	private final NMSUtil nmsUtil;
	private final PacketFactory packetFactory;

	public StatueEntity(GameProfile profile, Statue statue, NMSUtil nmsUtil, PacketFactory packetFactory) {
		super(((CraftWorld) Bukkit.getServer().getWorlds().get(0)).getHandle().getMinecraftWorld(), new BlockPosition(0.0, 0.0, 0.0), 0.0f, profile);
		this.statue = statue;
		this.nmsUtil = nmsUtil;
		this.packetFactory = packetFactory;
	}

	@Override
	public boolean B_() {
		// isSpectator()
		return false;
	}

	public static DataWatcherObject<Byte> getClientSettingsDataWatcher() {
		return bO;
	}

	public static DataWatcherObject<Byte> getMainHandDataWatcher() {
		return bP;
	}

	@Override
	protected void a_() {
		// initDatawatcher
		super.a_();
		//b(1, false, 16);
		al().b(getClientSettingsDataWatcher(), (byte) 127);
		al().b(getMainHandDataWatcher(), (byte) 0);
	}

	@Override
	public boolean f() {
		// isCreative
		return false;
	}

	@Override
	public Packet spawn() {
		al().b(getClientSettingsDataWatcher(), (byte) 127);
		al().b(getMainHandDataWatcher(), (byte) 0);
		return packetFactory.spawn(statue.entityID,
				statue.profile,
				statue.x, statue.y, statue.z,
				statue.yaw, statue.pitch,
				statue.itemInHand,
				al());
	}

	@Override
	public PacketPlayOutEntityMetadata metadata() {
		al().b(getClientSettingsDataWatcher(), (byte) 127);
		al().b(getMainHandDataWatcher(), (byte) 0);
		return packetFactory.metadata(statue.entityID, al());
	}

	@Override
	public PacketPlayOutAnimation swingArm() {
		return packetFactory.animate(statue.entityID, 0);
	}

	@Override
	public Packet[] face(Player p) {
		Location loc = p.getLocation();
		loc.setY(loc.getY() + nmsUtil.eyesHeight);
		return face(loc);
	}

	@Override
	public Packet[] face() {
		Packet[] packets = new Packet[2];
		byte yawByte = (byte) (statue.yaw * 256.0F / 360.0F);
		byte pitchByte = (byte) (statue.pitch * 256.0F / 360.0F);
		packets[0] = new PacketPlayOutEntity.PacketPlayOutEntityLook(statue.entityID, yawByte, pitchByte, true);
		packets[1] = packetFactory.rotateHead(statue.entityID, yawByte);
		return packets;
	}
	
	@Override
	public Packet[] move() {
		PacketPlayOutEntityTeleport teleport = packetFactory.teleport(statue.entityID, statue.x, statue.y, statue.z, (byte) (statue.yaw * 256.0F / 360.0F), (byte) (statue.pitch * 256.0F / 360.0F), false);

		Packet[] facePackets = face();
		Packet[] packets = new Packet[2];
		packets[0] = teleport;
		packets[1] = facePackets[1];
		return packets;
	}

	@Override
	public Packet[] face(Location loc) {
		Packet[] packets = new Packet[2];
		double diffX = loc.getX() - statue.x;
		double diffY = loc.getY() - (statue.y + nmsUtil.eyesHeight);
		double diffZ = loc.getZ() - statue.z;
		if (diffX == 0.0 && diffZ == 0.0) {
			diffZ = 1.0;
		}
		double distance = Math.sqrt(diffX * diffX + diffZ * diffZ);
		float faceYaw = (float) (Math.atan2(diffZ, diffX) * 180.0D / Math.PI) - 90.0F;
		float facePitch = (float) -(Math.atan2(diffY, distance) * 180.0D / Math.PI);
		byte yawByte = (byte) (faceYaw * 256.0F / 360.0F);
		byte pitchByte = (byte) (facePitch * 256.0F / 360.0F);
		packets[0] = new PacketPlayOutEntity.PacketPlayOutEntityLook(statue.entityID, yawByte, pitchByte, true);
		packets[1] = packetFactory.rotateHead(statue.entityID, yawByte);
		return packets;
	}

	@Override
	public PacketPlayOutEntityDestroy despawn() {
		return packetFactory.despawn(statue.entityID);
	}
}
