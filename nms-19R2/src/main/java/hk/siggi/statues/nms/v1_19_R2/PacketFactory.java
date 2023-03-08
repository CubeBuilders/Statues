package hk.siggi.statues.nms.v1_19_R2;

import hk.siggi.statues.nms.PacketBuildHelper;
import io.netty.buffer.Unpooled;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.protocol.game.PacketPlayOutAnimation;
import net.minecraft.network.protocol.game.PacketPlayOutEntity;
import net.minecraft.network.protocol.game.PacketPlayOutEntityHeadRotation;
import net.minecraft.network.protocol.game.PacketPlayOutEntityTeleport;

public class PacketFactory extends hk.siggi.statues.nms.PacketFactory {

    private static PacketDataSerializer wrap(PacketBuildHelper helper) {
        byte[] bytes = helper.toByteArray();
        return new PacketDataSerializer(Unpooled.wrappedBuffer(bytes, 0, bytes.length));
    }

    PacketFactory() {
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
