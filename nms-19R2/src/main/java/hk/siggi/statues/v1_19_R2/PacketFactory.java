package hk.siggi.statues.v1_19_R2;

import com.mojang.authlib.GameProfile;
import hk.siggi.statues.PacketBuildHelper;
import io.netty.buffer.Unpooled;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.UUID;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.network.chat.IChatMutableComponent;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoRemovePacket;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket;
import net.minecraft.network.protocol.game.PacketPlayOutAnimation;
import net.minecraft.network.protocol.game.PacketPlayOutEntity;
import net.minecraft.network.protocol.game.PacketPlayOutEntityDestroy;
import net.minecraft.network.protocol.game.PacketPlayOutEntityHeadRotation;
import net.minecraft.network.protocol.game.PacketPlayOutEntityMetadata;
import net.minecraft.network.protocol.game.PacketPlayOutEntityTeleport;
import net.minecraft.network.protocol.game.PacketPlayOutNamedEntitySpawn;
import net.minecraft.network.syncher.DataWatcher;
import net.minecraft.world.level.EnumGamemode;
import org.bukkit.craftbukkit.v1_19_R2.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;
import static hk.siggi.statues.Reflection.getField;

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
    public ClientboundPlayerInfoUpdatePacket addPlayer(String displayName, GameProfile profile,
                                                       int ping, int gamemode) {
        try {
            ClientboundPlayerInfoUpdatePacket packet = new ClientboundPlayerInfoUpdatePacket(
                EnumSet.of(
                    // everything except initialize chat
                    ClientboundPlayerInfoUpdatePacket.a.a,
                    ClientboundPlayerInfoUpdatePacket.a.c,
                    ClientboundPlayerInfoUpdatePacket.a.d,
                    ClientboundPlayerInfoUpdatePacket.a.e,
                    ClientboundPlayerInfoUpdatePacket.a.f
                ),
                Collections.emptySet());
            List list = (List) getField(packet, "b");
            IChatMutableComponent displayNameComponent = IChatBaseComponent.b(displayName);
            list.add(new ClientboundPlayerInfoUpdatePacket.b(profile.getId(), profile, false, ping, EnumGamemode.a(gamemode), displayNameComponent, null));
            return packet;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public ClientboundPlayerInfoRemovePacket removePlayer(String displayName, GameProfile profile) {
        try {
            return new ClientboundPlayerInfoRemovePacket(Arrays.asList(profile.getId()));
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
        return new PacketPlayOutEntityMetadata(id, dataWatcher.b());
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
