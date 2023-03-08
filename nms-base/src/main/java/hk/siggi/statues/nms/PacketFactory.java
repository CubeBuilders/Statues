package hk.siggi.statues.nms;

public abstract class PacketFactory<DW> {

	public abstract Object animate(int entityID, int animation);

	public abstract Object rotateHead(int entityID, byte rotation);

	public abstract Object move(int entityID, int x, int y, int z, boolean onGround);

	public abstract Object movelook(int entityID, int x, int y, int z, byte yaw, byte pitch, boolean onGround);

	public abstract Object look(int entityID, byte yaw, byte pitch, boolean onGround);

	public abstract Object teleport(int entityID, double x, double y, double z, byte yaw, byte pitch, boolean onGround);
}
