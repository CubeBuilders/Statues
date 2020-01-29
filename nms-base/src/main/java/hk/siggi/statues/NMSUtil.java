package hk.siggi.statues;

import com.mojang.authlib.GameProfile;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public abstract class NMSUtil<PKF extends PacketFactory, SE extends StatueEntity, PK> {

	public final double eyesHeight;

	public NMSUtil(double eyesHeight) {
		this.eyesHeight = eyesHeight;
	}

	public abstract PKF getPacketFactory();

	public abstract SE newStatueEntity(GameProfile profile, Statue statue);

	public abstract GameProfile getGameProfile(Player p);

	public abstract int getPing(Player p);

	public abstract int getGamemode(Player p);

	public abstract void sendPacket(Player p, PK packet);

	public abstract int getNextEntityID();
	
	public abstract Material getSignItem();
	
	public abstract Material getSignPost();
	
	public abstract Material getWallSign();
}
