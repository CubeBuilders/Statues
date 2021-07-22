package hk.siggi.statues.v1_17_R1;

import com.mojang.authlib.GameProfile;
import static hk.siggi.statues.Reflection.getField;
import static hk.siggi.statues.Reflection.setField;
import java.lang.reflect.Constructor;
import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.chat.ChatComponentText;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.network.protocol.game.*;
import net.minecraft.network.syncher.DataWatcher;
import net.minecraft.world.level.EnumGamemode;
import org.bukkit.craftbukkit.v1_17_R1.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

public class PacketFactory extends hk.siggi.statues.PacketFactory<DataWatcher> {

	private static final int ADD_PLAYER = 0;
	private static final int UPDATE_GAMEMODE = 1;
	private static final int UPDATE_LATENCY = 2;
	private static final int UPDATE_DISPLAY_NAME = 3;
	private static final int REMOVE_PLAYER = 4;

	private static byte[] bytes = new byte[256];
	private static PacketDataSerializer zeroBuffer(int length) {
		return new PacketDataSerializer(Unpooled.wrappedBuffer(bytes, 0, length));
	}

	PacketFactory() {
	}

	@Override
	public PacketPlayOutPlayerInfo addPlayer(String displayName, GameProfile profile,
											 int ping, int gamemode) {
		try {
			PacketPlayOutPlayerInfo packet = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.a);
			List list = (List) getField(packet, "b");
			list.add(new PacketPlayOutPlayerInfo.PlayerInfoData(profile, ping, EnumGamemode.getById(gamemode), new ChatComponentText(displayName)));
			return packet;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public PacketPlayOutPlayerInfo removePlayer(String displayName, GameProfile profile) {
		try {
			PacketPlayOutPlayerInfo packet = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.e);
			List list = (List) getField(packet, "b");
			list.add(new PacketPlayOutPlayerInfo.PlayerInfoData(profile, 0, EnumGamemode.getById(0), new ChatComponentText(displayName)));
			return packet;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public PacketPlayOutNamedEntitySpawn spawn(int id, GameProfile profile,
											   double x, double y, double z, float yaw, float pitch,
											   ItemStack itemInHand, DataWatcher dataWatcher) {
		net.minecraft.world.item.ItemStack nmsItemStack = CraftItemStack.asNMSCopy((CraftItemStack) itemInHand);
		try {
			PacketPlayOutNamedEntitySpawn spawn = new PacketPlayOutNamedEntitySpawn(zeroBuffer(43));
			setField(spawn, "a", id);
			setField(spawn, "b", profile.getId());
			setField(spawn, "c", x);
			setField(spawn, "d", y);
			setField(spawn, "e", z);
			setField(spawn, "f", ((byte) (int) (yaw * 256.0F / 360.0F)));
			setField(spawn, "g", ((byte) (int) (pitch * 256.0F / 360.0F)));
			return spawn;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public PacketPlayOutEntityMetadata metadata(int id, DataWatcher dataWatcher) {
		return new PacketPlayOutEntityMetadata(id, dataWatcher, false);
	}

	@Override
	public PacketPlayOutEntityDestroy despawn(int id) {
		return new PacketPlayOutEntityDestroy(new int[]{id});
	}

	@Override
	public PacketPlayOutAnimation animate(int entityID, int animation) {
		try {
			PacketPlayOutAnimation packet = new PacketPlayOutAnimation(zeroBuffer(2));
			setField(packet, "a", entityID);
			setField(packet, "b", animation);
			return packet;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public PacketPlayOutEntityHeadRotation rotateHead(int entityID, byte rotation) {
		try {
			PacketPlayOutEntityHeadRotation packet = new PacketPlayOutEntityHeadRotation(zeroBuffer(2));
			setField(packet, "a", entityID);
			setField(packet, "b", rotation);
			return packet;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public PacketPlayOutEntity.PacketPlayOutRelEntityMove move(int entityID, int x, int y, int z, boolean onGround) {
		try {
			PacketPlayOutEntity.PacketPlayOutRelEntityMove packet = new PacketPlayOutEntity.PacketPlayOutRelEntityMove(
					entityID,
					(short) x,
					(short) y,
					(short) z,
					onGround
			);
			return packet;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public PacketPlayOutEntity.PacketPlayOutRelEntityMoveLook movelook(int entityID, int x, int y, int z, byte yaw, byte pitch, boolean onGround) {
		try {
			PacketPlayOutEntity.PacketPlayOutRelEntityMoveLook packet = new PacketPlayOutEntity.PacketPlayOutRelEntityMoveLook(
					entityID,
					(short) x,
					(short) y,
					(short) z,
					yaw,
					pitch,
					onGround
			);
			return packet;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public PacketPlayOutEntity.PacketPlayOutEntityLook look(int entityID, byte yaw, byte pitch, boolean onGround) {
		try {
			PacketPlayOutEntity.PacketPlayOutEntityLook packet = new PacketPlayOutEntity.PacketPlayOutEntityLook(
					entityID,
					yaw,
					pitch,
					onGround
			);
			return packet;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public PacketPlayOutEntityTeleport teleport(int entityID, double x, double y, double z, byte yaw, byte pitch, boolean onGround) {
		try {
			PacketPlayOutEntityTeleport packet = new PacketPlayOutEntityTeleport(zeroBuffer(28));
			setField(packet, "a", entityID);
			setField(packet, "b", x);
			setField(packet, "c", y);
			setField(packet, "d", z);
			setField(packet, "e", yaw);
			setField(packet, "f", pitch);
			setField(packet, "g", onGround);
			return packet;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
