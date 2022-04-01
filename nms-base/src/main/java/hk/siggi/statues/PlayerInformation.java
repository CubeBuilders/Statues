package hk.siggi.statues;

import org.bukkit.World;
import org.bukkit.entity.Player;
import java.util.HashSet;
import java.util.Set;

public class PlayerInformation {

	public final Player player;
	public final Set<Statue> shownStatues;
	public final Set<Statue> nearStatues;
	public final Set<Statue> listedStatues;
	public World lastWorld;

	public PlayerInformation(Player player) {
		this.player = player;
		this.lastWorld = player.getWorld();
		shownStatues = new HashSet<Statue>();
		nearStatues = new HashSet<Statue>();
		listedStatues = new HashSet<Statue>();
	}
}
