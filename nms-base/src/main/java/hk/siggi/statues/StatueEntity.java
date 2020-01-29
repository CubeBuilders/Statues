package hk.siggi.statues;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public interface StatueEntity<PK> {

	public abstract PK spawn();

	public abstract PK metadata();

	public abstract PK swingArm();

	public abstract PK[] face(Player p);

	public abstract PK[] face();
	
	public abstract PK[] move();

	public abstract PK[] face(Location loc);

	public abstract PK despawn();
}
