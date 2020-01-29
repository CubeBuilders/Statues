package hk.siggi.statues.v1_11_R1;

import com.mojang.authlib.GameProfile;
import static hk.siggi.statues.Reflection.getField;
import static hk.siggi.statues.Reflection.setField;
import java.lang.reflect.Constructor;
import java.util.List;
import net.minecraft.server.v1_11_R1.ChatComponentText;
import net.minecraft.server.v1_11_R1.DataWatcher;
import net.minecraft.server.v1_11_R1.EnumGamemode;
import net.minecraft.server.v1_11_R1.IChatBaseComponent;
import net.minecraft.server.v1_11_R1.PacketPlayOutAnimation;
import net.minecraft.server.v1_11_R1.PacketPlayOutEntity;
import net.minecraft.server.v1_11_R1.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_11_R1.PacketPlayOutEntityHeadRotation;
import net.minecraft.server.v1_11_R1.PacketPlayOutEntityMetadata;
import net.minecraft.server.v1_11_R1.PacketPlayOutEntityTeleport;
import net.minecraft.server.v1_11_R1.PacketPlayOutNamedEntitySpawn;
import net.minecraft.server.v1_11_R1.PacketPlayOutPlayerInfo;
import org.bukkit.craftbukkit.v1_11_R1.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

public class PacketFactory extends hk.siggi.statues.PacketFactory<DataWatcher> {

	private static final int ADD_PLAYER = 0;
	private static final int UPDATE_GAMEMODE = 1;
	private static final int UPDATE_LATENCY = 2;
	private static final int UPDATE_DISPLAY_NAME = 3;
	private static final int REMOVE_PLAYER = 4;

	PacketFactory() {
	}

	@Override
	public PacketPlayOutPlayerInfo addPlayer(String displayName, GameProfile profile,
			int ping, int gamemode) {
		try {
			PacketPlayOutPlayerInfo packet = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER);
			List list = (List) getField(packet, "b");
			list.add(newPlayerInfoData(packet, profile, ping, EnumGamemode.getById(gamemode), new ChatComponentText(displayName)));
			return packet;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public PacketPlayOutPlayerInfo removePlayer(String displayName, GameProfile profile) {
		try {
			PacketPlayOutPlayerInfo packet = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER);
			List list = (List) getField(packet, "b");
			list.add(newPlayerInfoData(packet, profile, 0, EnumGamemode.getById(0), new ChatComponentText(displayName)));
			return packet;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	private static Class pidclass;

	private static Class getPIDClass() {
		if (pidclass == null) {
			try {
				pidclass = Class.forName("net.minecraft.server.v1_11_R1.PacketPlayOutPlayerInfo.PlayerInfoData");
			} catch (Exception e) {
				try {
					pidclass = Class.forName("net.minecraft.server.v1_11_R1.PacketPlayOutPlayerInfo$PlayerInfoData");
				} catch (Exception e2) {
				}
			}
		}
		return pidclass;
	}

	private Object newPlayerInfoData(PacketPlayOutPlayerInfo packet, GameProfile profile, int i, EnumGamemode byId, ChatComponentText chatComponentText) {
		try {
			Constructor ctor = getPIDClass().getDeclaredConstructor(PacketPlayOutPlayerInfo.class, GameProfile.class, int.class, EnumGamemode.class, IChatBaseComponent.class);
			boolean oA = ctor.isAccessible();
			ctor.setAccessible(true);
			Object infoData = ctor.newInstance(packet, profile, i, byId, chatComponentText);
			ctor.setAccessible(oA);
			return infoData;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public PacketPlayOutNamedEntitySpawn spawn(int id, GameProfile profile,
			double x, double y, double z, float yaw, float pitch,
			ItemStack itemInHand, DataWatcher dataWatcher) {
		net.minecraft.server.v1_11_R1.ItemStack nmsItemStack = CraftItemStack.asNMSCopy((CraftItemStack) itemInHand);
		try {
			PacketPlayOutNamedEntitySpawn spawn = new PacketPlayOutNamedEntitySpawn();
			setField(spawn, "a", id);
			setField(spawn, "b", profile.getId());
			setField(spawn, "c", x);
			setField(spawn, "d", y);
			setField(spawn, "e", z);
			setField(spawn, "f", ((byte) (int) (yaw * 256.0F / 360.0F)));
			setField(spawn, "g", ((byte) (int) (pitch * 256.0F / 360.0F)));
			setField(spawn, "h", dataWatcher);
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
			PacketPlayOutAnimation packet = new PacketPlayOutAnimation();
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
			PacketPlayOutEntityHeadRotation packet = new PacketPlayOutEntityHeadRotation();
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
			PacketPlayOutEntity.PacketPlayOutRelEntityMove packet = new PacketPlayOutEntity.PacketPlayOutRelEntityMove();
			setField(PacketPlayOutEntity.class, packet, "a", entityID);
			setField(PacketPlayOutEntity.class, packet, "b", x);
			setField(PacketPlayOutEntity.class, packet, "c", y);
			setField(PacketPlayOutEntity.class, packet, "d", z);
			setField(PacketPlayOutEntity.class, packet, "g", onGround);
			return packet;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public PacketPlayOutEntity.PacketPlayOutRelEntityMoveLook movelook(int entityID, int x, int y, int z, byte yaw, byte pitch, boolean onGround) {
		try {
			PacketPlayOutEntity.PacketPlayOutRelEntityMoveLook packet = new PacketPlayOutEntity.PacketPlayOutRelEntityMoveLook();
			setField(PacketPlayOutEntity.class, packet, "a", entityID);
			setField(PacketPlayOutEntity.class, packet, "b", x);
			setField(PacketPlayOutEntity.class, packet, "c", y);
			setField(PacketPlayOutEntity.class, packet, "d", z);
			setField(PacketPlayOutEntity.class, packet, "e", yaw);
			setField(PacketPlayOutEntity.class, packet, "f", pitch);
			setField(PacketPlayOutEntity.class, packet, "g", onGround);
			setField(PacketPlayOutEntity.class, packet, "h", true);
			return packet;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public PacketPlayOutEntity.PacketPlayOutEntityLook look(int entityID, byte yaw, byte pitch, boolean onGround) {
		try {
			PacketPlayOutEntity.PacketPlayOutEntityLook packet = new PacketPlayOutEntity.PacketPlayOutEntityLook();
			setField(PacketPlayOutEntity.class, packet, "a", entityID);
			setField(PacketPlayOutEntity.class, packet, "e", yaw);
			setField(PacketPlayOutEntity.class, packet, "f", pitch);
			setField(PacketPlayOutEntity.class, packet, "g", onGround);
			setField(PacketPlayOutEntity.class, packet, "h", true);
			return packet;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public PacketPlayOutEntityTeleport teleport(int entityID, double x, double y, double z, byte yaw, byte pitch, boolean onGround) {
		try {
			PacketPlayOutEntityTeleport packet = new PacketPlayOutEntityTeleport();
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
