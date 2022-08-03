package hk.siggi.statues.v1_19_R1;

import com.mojang.authlib.GameProfile;
import static hk.siggi.statues.Reflection.getField;
import java.util.List;

import hk.siggi.statues.PacketBuildHelper;
import io.netty.buffer.Unpooled;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.network.chat.IChatMutableComponent;
import net.minecraft.network.protocol.game.*;
import net.minecraft.network.syncher.DataWatcher;
import net.minecraft.world.level.EnumGamemode;
import org.bukkit.craftbukkit.v1_19_R1.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

public class PacketFactory extends hk.siggi.statues.PacketFactory<DataWatcher> {

	private static final int ADD_PLAYER = 0;
	private static final int UPDATE_GAMEMODE = 1;
	private static final int UPDATE_LATENCY = 2;
	private static final int UPDATE_DISPLAY_NAME = 3;
	private static final int REMOVE_PLAYER = 4;

	private static PacketDataSerializer wrap(PacketBuildHelper helper) {
		byte[] bytes = helper.toByteArray();
		return new PacketDataSerializer(Unpooled.wrappedBuffer(bytes, 0, bytes.length));
	}

	PacketFactory() {
	}

	@Override
	public PacketPlayOutPlayerInfo addPlayer(String displayName, GameProfile profile,
											 int ping, int gamemode) {
		try {
			PacketPlayOutPlayerInfo packet = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.a);
			List list = (List) getField(packet, "b");
			IChatMutableComponent displayNameComponent = IChatBaseComponent.b(displayName);
			list.add(new PacketPlayOutPlayerInfo.PlayerInfoData(profile, ping, EnumGamemode.a(gamemode), displayNameComponent, null));
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
			IChatMutableComponent displayNameComponent = IChatBaseComponent.b(displayName);
			list.add(new PacketPlayOutPlayerInfo.PlayerInfoData(profile, 0, EnumGamemode.a(0), displayNameComponent, null));
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
			PacketBuildHelper helper = new PacketBuildHelper();
			helper.writeVarInt(id);
			helper.writeUUID(profile.getId());
			helper.dataOut.writeDouble(x);
			helper.dataOut.writeDouble(y);
			helper.dataOut.writeDouble(z);
			helper.dataOut.writeByte((byte) (int) (yaw * 256.0F / 360.0F));
			helper.dataOut.writeByte((byte) (int) (pitch * 256.0F / 360.0F));
			PacketPlayOutNamedEntitySpawn spawn = new PacketPlayOutNamedEntitySpawn(wrap(helper));
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
			PacketBuildHelper helper = new PacketBuildHelper();
			helper.writeVarInt(entityID);
			helper.dataOut.writeByte(animation);
			PacketPlayOutAnimation packet = new PacketPlayOutAnimation(wrap(helper));
			return packet;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public PacketPlayOutEntityHeadRotation rotateHead(int entityID, byte rotation) {
		try {
			PacketBuildHelper helper = new PacketBuildHelper();
			helper.writeVarInt(entityID);
			helper.dataOut.writeByte(rotation);
			PacketPlayOutEntityHeadRotation packet = new PacketPlayOutEntityHeadRotation(wrap(helper));
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
			PacketBuildHelper helper = new PacketBuildHelper();
			helper.writeVarInt(entityID);
			helper.dataOut.writeDouble(x);
			helper.dataOut.writeDouble(y);
			helper.dataOut.writeDouble(z);
			helper.dataOut.writeByte(yaw);
			helper.dataOut.writeByte(pitch);
			helper.dataOut.writeBoolean(onGround);
			PacketPlayOutEntityTeleport packet = new PacketPlayOutEntityTeleport(wrap(helper));
			return packet;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
