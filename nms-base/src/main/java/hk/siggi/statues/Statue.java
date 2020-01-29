package hk.siggi.statues;

import com.mojang.authlib.GameProfile;
import java.lang.ref.WeakReference;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Statue {

	public Statue(GameProfile profile, Location location) {
		this.profile = profile;
		if (location != null) {
			x = location.getX();
			y = location.getY();
			z = location.getZ();
			pitch = location.getPitch();
			yaw = location.getYaw();
			world = location.getWorld().getName();
		} else {
			world = Bukkit.getWorlds().get(0).getName();
		}
		statueEntity = Statues.getInstance().getNMSUtil().newStatueEntity(profile, this);
		entityID = Statues.getInstance().getNMSUtil().getNextEntityID();
	}

	public Statue(GameProfile profile, Location location, Player onlyVisibleTo) {
		this(profile, location);
		this.onlyVisibleTo = new LinkedList<WeakReference<Player>>();
		this.onlyVisibleTo.add(new WeakReference(onlyVisibleTo));
	}

	public Statue(GameProfile profile, Location location, Player[] onlyVisibleTo) {
		this(profile, location);
		this.onlyVisibleTo = new LinkedList<WeakReference<Player>>();
		for (Player p : onlyVisibleTo) {
			this.onlyVisibleTo.add(new WeakReference(p));
		}
	}

	public boolean shouldAutoDelete() {
		if (onlyVisibleTo == null) {
			return false;
		}
		for (Iterator<WeakReference<Player>> it = onlyVisibleTo.iterator(); it.hasNext();) {
			WeakReference<Player> pp = it.next();
			if (pp == null) {
				it.remove();
				continue;
			}
			Player p = pp.get();
			if (p == null) {
				it.remove();
				continue;
			}
			if (!p.isOnline()) {
				it.remove();
				continue;
			}
		}
		return onlyVisibleTo.isEmpty();
	}
	private List<WeakReference<Player>> onlyVisibleTo = null;

	public boolean canShow(Player p) {
		if (onlyVisibleTo == null) {
			return true;
		}
		boolean result = false;
		for (Iterator<WeakReference<Player>> it = onlyVisibleTo.iterator(); it.hasNext();) {
			WeakReference<Player> pp = it.next();
			if (pp == null) {
				it.remove();
				continue;
			}
			Player ppp = pp.get();
			if (ppp == null) {
				it.remove();
				continue;
			}
			if (!ppp.isOnline()) {
				it.remove();
				continue;
			}
			if (ppp == p) {
				result = true;
			}
		}
		return result;
	}
	public int entityID = 0;
	public String world;

	public double x = 0.0;
	public double y = 0.0;
	public double z = 0.0;
	public float pitch = 0.0f; // up & down
	public float yaw = 0.0f; // left & right

	public double prevX = 0.0;
	public double prevY = 0.0;
	public double prevZ = 0.0;
	public float prevPitch = 0.0f;
	public float prevYaw = 0.0f;

	public boolean sendPositionUpdate = false;
	public boolean armswing = false;

	public ItemStack itemInHand = null;
	public GameProfile profile;
	public StatueEntity statueEntity;
	public boolean facePlayer = false;
	public boolean standStraight = false;
	public boolean deleted = false;
	public String price = null;
	public boolean wasWallSign = false;
	public String face = null;
	public boolean save = true;

	public String serialize() {
		return ("VER1," + world + "," + x + "," + y + "," + z + "," + pitch + "," + yaw + "," + profile.getId() + "," + facePlayer + "," + standStraight + "," + price + "," + wasWallSign + "," + face);
	}

	public static Statue deserialize(String statueString) {
		String[] pieces = statueString.split(",");
		if (pieces[0].equalsIgnoreCase("VER1")) {
			try {
				String world = pieces[1];
				double x = Double.parseDouble(pieces[2]);
				double y = Double.parseDouble(pieces[3]);
				double z = Double.parseDouble(pieces[4]);
				float pitch = Float.parseFloat(pieces[5]);
				float yaw = Float.parseFloat(pieces[6]);
				UUID uuid = Statues.uuid(pieces[7]);
				boolean facePlayer = Boolean.parseBoolean(pieces[8]);
				boolean standStraight = Boolean.parseBoolean(pieces[9]);
				String price = pieces[10];
				boolean wasWallSign = Boolean.parseBoolean(pieces[11]);
				String face = pieces[12];
				GameProfile profile = Statues.getInstance().getProfile(uuid);
				Statue statue = new Statue(profile, null);
				statue.x = x;
				statue.y = y;
				statue.z = z;
				statue.yaw = yaw;
				statue.pitch = pitch;
				statue.world = world;
				statue.facePlayer = facePlayer;
				statue.standStraight = standStraight;
				statue.price = price;
				statue.wasWallSign = wasWallSign;
				statue.face = face;
				return statue;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	public static Statue create(UUID player, Block block) {
		if (player == null || block == null) {
			return null;
		}
		BlockState state = block.getState();
		if (!(state instanceof Sign)) {
			return null;
		}
		Sign sign = (Sign) state;
		String firstLine = sign.getLine(0);
		boolean facePlayer;
		boolean standStraight;
		BlockFace face = ((org.bukkit.material.Sign) sign.getData()).getFacing();
		if (firstLine.equalsIgnoreCase("<Statue>")) {
			facePlayer = false;
			standStraight = true;
		} else {
			facePlayer = true;
			standStraight = false;
		}
		GameProfile profile = Statues.getInstance().getProfile(player);
		if (profile == null) {
			return null;
		}
		World world = block.getWorld();
		double x = ((double) block.getX()) + 0.5;
		double y = (double) block.getY();
		double z = ((double) block.getZ()) + 0.5;

		double lookAtX = x + (float) face.getModX();
		double lookAtY = y + (float) face.getModY();
		double lookAtZ = z + (float) face.getModZ();

		double diffX = lookAtX - x;
		double diffY = lookAtY - y;
		double diffZ = lookAtZ - z;
		if (diffX == 0.0 && diffZ == 0.0) {
			diffZ = 1.0;
		}
		double distance = Math.sqrt(diffX * diffX + diffZ * diffZ);
		float yaw = (float) (Math.atan2(diffZ, diffX) * 180.0D / Math.PI) - 90.0F;
		float pitch = (float) -(Math.atan2(diffY, distance) * 180.0D / Math.PI);

		Location location = new Location(world, x, y, z, yaw, pitch);
		Statue statue = new Statue(profile, location);
		statue.facePlayer = facePlayer;
		statue.standStraight = standStraight;
		statue.price = sign.getLine(1);
		statue.wasWallSign = block.getType() == Statues.getInstance().getNMSUtil().getWallSign();
		statue.face = face.toString();
		return statue;
	}

	public void delete() {
		deleted = true;
		if (price != null && face != null) {
			try {
				Location location = Statues.getLocation(this);
				Block block = location.getBlock();
				NMSUtil nmsUtil = Statues.getInstance().getNMSUtil();
				block.setType(wasWallSign ? nmsUtil.getWallSign() : nmsUtil.getSignPost());
				BlockState state = block.getState();
				org.bukkit.block.Sign signState = (org.bukkit.block.Sign) state;
				org.bukkit.material.Sign signMaterial = (org.bukkit.material.Sign) signState.getData();
				try {
					signMaterial.setFacingDirection(BlockFace.valueOf(face));
				} catch (Exception e) {
				}
				signState.setData(signMaterial);
				if (facePlayer) {
					signState.setLine(0, "[Statue]");
				} else {
					signState.setLine(0, "<Statue>");
				}
				signState.setLine(1, price);
				if (price.toLowerCase().endsWith(" spot")) {
					signState.setLine(2, "");
					signState.setLine(3, "");
				} else if (price.equalsIgnoreCase("Free")) {
					signState.setLine(2, "Right click");
					signState.setLine(3, "to claim!");
				} else {
					signState.setLine(2, "Right click");
					signState.setLine(3, "to buy!");
				}
				signState.update();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void addVisibleTo(Player p) {
		if (onlyVisibleTo == null) {
			onlyVisibleTo = new LinkedList<WeakReference<Player>>();
		}
		for (Iterator<WeakReference<Player>> it = onlyVisibleTo.iterator(); it.hasNext();) {
			WeakReference<Player> pp = it.next();
			Player ppp = pp.get();
			if (ppp == null) {
				it.remove();
			} else if (ppp == p) {
				return;
			}
		}
		onlyVisibleTo.add(new WeakReference<Player>(p));
	}

	public void removeVisibleTo(Player p) {
		if (onlyVisibleTo == null) {
			return;
		}
		for (Iterator<WeakReference<Player>> it = onlyVisibleTo.iterator(); it.hasNext();) {
			WeakReference<Player> pp = it.next();
			Player ppp = pp.get();
			if (ppp == null || ppp == p) {
				it.remove();
			}
		}
	}

	public void armswing() {
		armswing = true;
	}

	public void move(double x, double y, double z, float pitch, float yaw) {
		move(null, x, y, z, pitch, yaw);
	}

	public void move(String world, double x, double y, double z, float pitch, float yaw) {
		this.world = world == null ? this.world : world;
		this.x = x;
		this.y = y;
		this.z = z;
		this.pitch = pitch;
		this.yaw = yaw;
	}

	public boolean alwaysShownOnPlayerList = false;
}
