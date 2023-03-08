package hk.siggi.statues.nms.v1_8_R3;

import com.mojang.authlib.GameProfile;
import static hk.siggi.statues.nms.Reflection.setField;
import java.lang.reflect.Constructor;
import net.minecraft.server.v1_8_R3.ChatComponentText;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.PacketPlayOutAnimation;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntity;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityHeadRotation;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityTeleport;
import net.minecraft.server.v1_8_R3.PacketPlayOutPlayerInfo;
import net.minecraft.server.v1_8_R3.WorldSettings;

public class PacketFactory extends hk.siggi.statues.nms.PacketFactory {

	PacketFactory() {
	}

	private static Class pidclass;

	private static Class getPIDClass() {
		if (pidclass == null) {
			try {
				pidclass = Class.forName("net.minecraft.server.v1_8_R3.PacketPlayOutPlayerInfo.PlayerInfoData");
			} catch (Exception e) {
				try {
					pidclass = Class.forName("net.minecraft.server.v1_8_R3.PacketPlayOutPlayerInfo$PlayerInfoData");
				} catch (Exception e2) {
				}
			}
		}
		return pidclass;
	}

	private Object newPlayerInfoData(PacketPlayOutPlayerInfo packet, GameProfile profile, int i, WorldSettings.EnumGamemode byId, ChatComponentText chatComponentText) {
		try {
			Constructor ctor = getPIDClass().getDeclaredConstructor(PacketPlayOutPlayerInfo.class, GameProfile.class, int.class, WorldSettings.EnumGamemode.class, IChatBaseComponent.class);
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
