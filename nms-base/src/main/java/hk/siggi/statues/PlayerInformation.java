package hk.siggi.statues;

import java.util.ArrayList;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class PlayerInformation {

	public final Player player;
	public final ArrayList<Statue> shownStatues;
	public final ArrayList<Statue> listedStatues;
	public World lastWorld;

	public PlayerInformation(Player player) {
		this.player = player;
		this.lastWorld = player.getWorld();
		shownStatues = new ArrayList<Statue>();
		listedStatues = new ArrayList<Statue>();
	}
}
