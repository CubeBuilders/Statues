package hk.siggi.statues;

import com.mojang.authlib.GameProfile;
import org.bukkit.inventory.ItemStack;

public abstract class PacketFactory<DW> {

	public abstract Object addPlayer(String displayName, GameProfile profile,
			int ping, int gamemode);

	public abstract Object removePlayer(String displayName, GameProfile profile);

	public abstract Object spawn(int id, GameProfile profile,
			double x, double y, double z, float yaw, float pitch,
			ItemStack itemInHand, DW dataWatcher);

	public abstract Object metadata(int id, DW dataWatcher);

	public abstract Object despawn(int id);

	public abstract Object animate(int entityID, int animation);

	public abstract Object rotateHead(int entityID, byte rotation);

	public abstract Object move(int entityID, int x, int y, int z, boolean onGround);

	public abstract Object movelook(int entityID, int x, int y, int z, byte yaw, byte pitch, boolean onGround);

	public abstract Object look(int entityID, byte yaw, byte pitch, boolean onGround);

	public abstract Object teleport(int entityID, double x, double y, double z, byte yaw, byte pitch, boolean onGround);
}
