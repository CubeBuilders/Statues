package hk.siggi.statues;

import com.mojang.authlib.GameProfile;
import hk.siggi.bukkit.plugcubebuildersin.PlugCubeBuildersIn;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class Statues extends JavaPlugin implements Listener {

	PlugCubeBuildersIn pluginCB = null;
	private static Statues instance = null;
	final ArrayList<Statue> statues = new ArrayList<>();
	private Economy economy = null;

	public void add(Statue statue) {
		statues.add(statue);
	}

	private final HashMap<String, PlayerInformation> playerInfo = new HashMap<>();

	private boolean startedLoop = false;

	public static Statues getInstance() {
		return instance;
	}

	private NMSUtil nmsUtil = null;

	public NMSUtil getNMSUtil() {
		if (nmsUtil == null) {
			try {
				Class<NMSUtil> clazz = (Class<NMSUtil>) Class.forName("hk.siggi.statues." + getVersion() + ".NMSUtil");
				Constructor<NMSUtil> newNMSUtil = clazz.getConstructor();
				nmsUtil = newNMSUtil.newInstance();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return nmsUtil;
	}

	private static String getVersion() {
		String name = Bukkit.getServer().getClass().getName();
		String version = name.substring(name.indexOf(".v") + 1);
		version = version.substring(0, version.indexOf("."));
		return version;
	}

	private boolean setupEconomy() {
		economy = null;
		RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
		if (economyProvider != null) {
			economy = economyProvider.getProvider();
		}
		return (economy != null);
	}

	@Override
	public void onEnable() {
		instance = this;
		startedLoop = false;
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvents(this, this);
		getCommand("statue").setExecutor(new StatuesCommand(this));
		pluginCB = (PlugCubeBuildersIn) pm.getPlugin("PlugCubeBuildersIn");
		for (Player p : getServer().getOnlinePlayers()) {
			playerInfo.put(p.getName(), new PlayerInformation(p));
		}
		setupEconomy();
		load();
	}

	public void load() {
		ArrayList<Statue> toDelete = new ArrayList<Statue>();
		for (Statue statue : statues) {
			if (statue.save) {
				statue.deleted = true;
				toDelete.add(statue);
			}
		}
		for (Statue statue : toDelete) {
			statues.remove(statue);
		}
		File dataFolder = getDataFolder();
		File file = new File(dataFolder, "statues.txt");
		File file2 = new File(dataFolder, "statues.txt.save");
		if (!file.exists() && file2.exists()) {
			file2.renameTo(file);
		}
		if (file.exists()) {
			try {
				BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
				String line;
				while ((line = reader.readLine()) != null) {
					if (line.startsWith("statue=")) {
						Statue statue = Statue.deserialize(line.substring(7));
						if (statue != null) {
							statues.add(statue);
						}
					}
				}
				reader.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void save() {
		// save
		File dataFolder = getDataFolder();
		if (!dataFolder.exists()) {
			dataFolder.mkdirs();
		}
		File file = new File(dataFolder, "statues.txt");
		File file2 = new File(dataFolder, "statues.txt.save");
		try {
			FileOutputStream fos = new FileOutputStream(file2);
			for (Statue statue : statues) {
				if (!statue.save) {
					continue;
				}
				fos.write(("statue=" + statue.serialize() + "\n").getBytes());
			}
			fos.close();
			if (file.exists()) {
				file.delete();
			}
			file2.renameTo(file);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (file2.exists()) {
			file2.delete();
		}
	}

	@Override
	public void onDisable() {
		for (PlayerInformation pi : playerInfo.values()) {
			Player p = pi.player;
			for (Statue statue : pi.shownStatues) {
				getNMSUtil().sendPacket(p, statue.statueEntity.despawn());
			}
		}
		playerInfo.clear();
	}

	@EventHandler
	public void login(final PlayerJoinEvent event) {
		if (!startedLoop) {
			startedLoop = true;
			startLoop();
		}
	}

	private Object addToPlayerListPacket(Statue statue) {
		return getNMSUtil().getPacketFactory().addPlayer(statue.profile.getName(), statue.profile, 10, 0);
	}

	private Object removeFromPlayerListPacket(Player p, Statue statue) {
		GameProfile correctProfile = null;
		int correctPing = 10;
		int correctGameMode = 0;
		Player player = getServer().getPlayer(statue.profile.getId());
		if (player != null) {
			if (p == player || p.canSee(player)) {
				correctProfile = getNMSUtil().getGameProfile(player);
				correctPing = getNMSUtil().getPing(player);
				correctGameMode = getNMSUtil().getGamemode(player);
			}
		}
		if (correctProfile == null) {
			return getNMSUtil().getPacketFactory().removePlayer(statue.profile.getName(), statue.profile);
		} else {
			return getNMSUtil().getPacketFactory().addPlayer(correctProfile.getName(), correctProfile, correctPing, correctGameMode);
		}
	}

	public void spawnStatue(final Player p, final Statue statue) {
		final PlayerInformation pi = playerInfo.get(p.getName());
		if (pi == null) {
			return;
		}
		if (pi.shownStatues.contains(statue)) {
			return;
		}
		pi.shownStatues.add(statue);
		if (!statue.alwaysShownOnPlayerList) {
			getNMSUtil().sendPacket(p, addToPlayerListPacket(statue));
		}
		new BukkitRunnable() {
			@Override
			public void run() {
				if (pi.shownStatues.contains(statue)) {
					getNMSUtil().sendPacket(p, statue.statueEntity.spawn());
				}
			}
		}.runTaskLater(this, 10L);
		new BukkitRunnable() {
			@Override
			public void run() {
				if (pi.shownStatues.contains(statue)) {
					getNMSUtil().sendPacket(p, statue.statueEntity.metadata());
				}
			}
		}.runTaskLater(this, 20L);
		new BukkitRunnable() {
			@Override
			public void run() {
				if (pi.shownStatues.contains(statue)) {
					Object[] facePackets = statue.statueEntity.face();
					for (Object packet : facePackets) {
						getNMSUtil().sendPacket(p, packet);
					}
				}
				if (!statue.alwaysShownOnPlayerList) {
					getNMSUtil().sendPacket(p, removeFromPlayerListPacket(p, statue));
				}
			}
		}.runTaskLater(this, 40L);
		if (statue.standStraight) {
			for (int i = 0; i < 3; i++) {
				new BukkitRunnable() {
			@Override
					public void run() {
						if (pi.shownStatues.contains(statue)) {
							getNMSUtil().sendPacket(p, statue.statueEntity.swingArm());
						}
					}
				}.runTaskLater(this, 45L + (i * 5L));
			}
		}
	}

	@EventHandler
	public void logout(PlayerQuitEvent event) {
		playerInfo.remove(event.getPlayer().getName());
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void playerRespawned(PlayerRespawnEvent event) {
		PlayerInformation info = playerInfo.get(event.getPlayer().getName());
		if (info == null) {
			return;
		}
		info.shownStatues.clear();
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void playerMoved(PlayerMoveEvent event) {
		Player p = event.getPlayer();
		PlayerInformation info = playerInfo.get(p.getName());
		if (info == null) {
			playerInfo.put(p.getName(), info = new PlayerInformation(p));
		}
		if (info.lastWorld != event.getTo().getWorld()) {
			playerInfo.remove(p.getName());
			return;
		}
		for (Statue statue : info.shownStatues) {
			Location statuePos = getLocation(statue);
			double distanceSquared = statuePos.distanceSquared(p.getLocation());
			if (statue.facePlayer) {
				if (distanceSquared <= (10.0 * 10.0)) {
					Location loc = event.getTo().clone();
					loc.setY(loc.getY() + getNMSUtil().eyesHeight);
					Object[] facePackets = statue.statueEntity.face(loc);
					for (Object packet : facePackets) {
						getNMSUtil().sendPacket(p, packet);
					}
				}
			}
			if (!statue.alwaysShownOnPlayerList && !info.nearStatues.contains(statue) && distanceSquared < (16.0 * 16.0)) {
				info.nearStatues.add(statue);
				getNMSUtil().sendPacket(p, addToPlayerListPacket(statue));
				new BukkitRunnable() {
					@Override
					public void run() {
						if (!statue.alwaysShownOnPlayerList) {
							getNMSUtil().sendPacket(p, removeFromPlayerListPacket(p, statue));
						}
					}
				}.runTaskLater(this, 40L);
			}
		}
		info.nearStatues.retainAll(info.shownStatues);
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void playerTeleported(PlayerTeleportEvent event) {
		if (event.getFrom().getWorld() != event.getTo().getWorld()) {
			PlayerInformation info = playerInfo.get(event.getPlayer().getName());
			if (info == null) {
				return;
			}
			if (info.lastWorld != event.getTo().getWorld()) {
				playerInfo.remove(event.getPlayer().getName());
			}
		}
	}

	@EventHandler
	public void rightClickBlock(PlayerInteractEvent event) {
		if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
			return;
		}
		Block clicked = event.getClickedBlock();
		if (clicked == null) {
			return;
		}
		NMSUtil nmsUtil = Statues.getInstance().getNMSUtil();
		if (clicked.getType() == nmsUtil.getSignPost() || clicked.getType() == nmsUtil.getWallSign()) {
			Sign sign = (Sign) clicked.getState();
			if (ChatColor.stripColor(sign.getLine(0)).equalsIgnoreCase("[Statue]")
					|| ChatColor.stripColor(sign.getLine(0)).equalsIgnoreCase("<Statue>")) {
				event.setCancelled(true);
				double cost = 0.0;
				String costLine = ChatColor.stripColor(sign.getLine(1));
				if (costLine.equalsIgnoreCase("Free")) {
					cost = 0.0;
				} else {
					int multiplier = 1;
					if (costLine.startsWith("$")) {
						costLine = costLine.substring(1);
					}
					if (costLine.endsWith("K")) {
						costLine = costLine.substring(0, costLine.length() - 1);
						multiplier = 1000;
					} else if (costLine.endsWith("M")) {
						costLine = costLine.substring(0, costLine.length() - 1);
						multiplier = 1000000;
					}
					cost = Double.parseDouble(costLine) * (double) multiplier;
				}
				Player player = event.getPlayer();
				EconomyResponse response = null;
				if (cost > 0) {
					response = economy.withdrawPlayer(player, cost);
				}
				if (response == null || response.transactionSuccess()) {
					Statue statue = Statue.create(event.getPlayer().getUniqueId(), clicked);
					if (statue == null) {
						if (cost > 0) {
							economy.depositPlayer(player, cost);
						}
						event.getPlayer().sendMessage(ChatColor.RED + "An error has occurred. :/");
					} else {
						clicked.setType(Material.AIR);
						statues.add(statue);
						save();
					}
				} else {
					event.getPlayer().sendMessage(ChatColor.RED + "You don't have enough money to place your statue here!");
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void changedSign(SignChangeEvent event) {
		String firstLine = ChatColor.stripColor(event.getLine(0)).toLowerCase().replaceAll("&[A-Za-z0-9]", "");
		if (firstLine.equalsIgnoreCase("[Statue]") || firstLine.equalsIgnoreCase("<Statue>")) {
			if (!event.getPlayer().hasPermission("hk.siggi.statues.createsign")) {
				event.setCancelled(true);
				event.getBlock().breakNaturally();
				event.getPlayer().sendMessage(ChatColor.RED + "You are not allowed to create a [Statue] sign.");
			}
		}
	}

	private void startLoop() {
		final double disappearDistance = 80.0 * 80.0;
		final double appearDistance = 64.0 * 64.0;
		new BukkitRunnable() {
			@Override
			public void run() {
				boolean shouldSave = false;
				Collection<? extends Player> onlinePlayers = getServer().getOnlinePlayers();
				for (Player p : onlinePlayers) {
					String playerCurrentWorld = p.getWorld().getName();
					try {
						Location myLocation = p.getLocation();
						PlayerInformation info = playerInfo.get(p.getName());
						if (info == null) {
							continue;
						}
						List<UUID> deletedStatues = new ArrayList<>();
						for (Iterator<Statue> it = info.listedStatues.iterator(); it.hasNext();) {
							Statue statue = it.next();
							UUID uuid = statue.profile.getId();
							boolean del = deletedStatues.contains(uuid);
							if (statue.deleted || del || !statue.canShow(p) || !statue.alwaysShownOnPlayerList) {
								if (!del) {
									getNMSUtil().sendPacket(p, removeFromPlayerListPacket(p, statue));
									deletedStatues.add(uuid);
								}
								it.remove();
							}
						}
						for (Iterator<Statue> it = info.shownStatues.iterator(); it.hasNext();) {
							Statue statue = it.next();
							Location loc = getLocation(statue);
							if (statue.deleted || !playerCurrentWorld.equals(statue.world) || loc.distanceSquared(myLocation) > disappearDistance) {
								getNMSUtil().sendPacket(p, statue.statueEntity.despawn());
								it.remove();
							} else {
								boolean updateMove = statue.x != statue.prevX || statue.y != statue.prevY || statue.z != statue.prevZ;
								boolean updateLook = statue.yaw != statue.prevYaw || statue.prevPitch != statue.prevPitch;
								boolean distanceGreaterThan4 = updateMove && (Math.max(Math.abs(statue.x - statue.prevX), Math.max(Math.abs(statue.y - statue.prevY), Math.abs(statue.z - statue.prevZ))) > 4.0);
								int deltaX = 0;
								int deltaY = 0;
								int deltaZ = 0;
								if (updateMove && !distanceGreaterThan4) {
									deltaX = (int) ((statue.x * 32.0 - statue.prevX * 32.0) * 128.0);
									deltaY = (int) ((statue.y * 32.0 - statue.prevY * 32.0) * 128.0);
									deltaZ = (int) ((statue.z * 32.0 - statue.prevZ * 32.0) * 128.0);
								}
								NMSUtil nmsUtil = getNMSUtil();
								PacketFactory packetFactory = nmsUtil.getPacketFactory();
								if (distanceGreaterThan4) {
									nmsUtil.sendPacket(p, packetFactory.teleport(statue.entityID, statue.x, statue.y, statue.z, (byte) (statue.yaw * 256.0F / 360.0F), (byte) (statue.pitch * 256.0F / 360.0F), true));
									nmsUtil.sendPacket(p, packetFactory.rotateHead(statue.entityID, (byte) (statue.yaw * 256.0F / 360.0F)));
								} else if (updateMove && updateLook) {
									nmsUtil.sendPacket(p, packetFactory.movelook(statue.entityID, deltaX, deltaY, deltaZ, (byte) (statue.yaw * 256.0F / 360.0F), (byte) (statue.pitch * 256.0F / 360.0F), true));
									nmsUtil.sendPacket(p, packetFactory.rotateHead(statue.entityID, (byte) (statue.yaw * 256.0F / 360.0F)));
								} else if (updateMove) {
									nmsUtil.sendPacket(p, packetFactory.move(statue.entityID, deltaX, deltaY, deltaZ, true));
								} else if (updateLook) {
									nmsUtil.sendPacket(p, packetFactory.look(statue.entityID, (byte) (statue.yaw * 256.0F / 360.0F), (byte) (statue.pitch * 256.0F / 360.0F), true));
									nmsUtil.sendPacket(p, packetFactory.rotateHead(statue.entityID, (byte) (statue.yaw * 256.0F / 360.0F)));
								}
								if (statue.armswing) {
									nmsUtil.sendPacket(p, statue.statueEntity.swingArm());
								}
								if (statue.metadataTick == 0) {
									nmsUtil.sendPacket(p, statue.statueEntity.metadata());
								}
							}
						}
						for (Iterator<Statue> it = statues.iterator(); it.hasNext();) {
							Statue statue = it.next();
							if (statue.deleted) {
								it.remove();
								if (statue.save) {
									shouldSave = true;
								}
								continue;
							} else if (statue.shouldAutoDelete()) {
								statue.delete();
								continue;
							}
							if (statue.canShow(p)) {
								if (statue.alwaysShownOnPlayerList && !info.listedStatues.contains(statue)) {
									getNMSUtil().sendPacket(p, addToPlayerListPacket(statue));
									info.listedStatues.add(statue);
								}
								if (!info.shownStatues.contains(statue) && playerCurrentWorld.equals(statue.world)) {
									Location loc = getLocation(statue);
									if (loc.distanceSquared(myLocation) <= appearDistance) {
										spawnStatue(p, statue);
									}
								}
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				for (Statue statue : statues) {
					// clear any flags that were set.
					statue.prevX = statue.x;
					statue.prevY = statue.y;
					statue.prevZ = statue.z;
					statue.prevPitch = statue.pitch;
					statue.prevYaw = statue.yaw;
					statue.armswing = false;
					if (statue.metadataTick >= 99) {
						statue.metadataTick = 0;
					} else {
						statue.metadataTick += 1;
					}
				}
				if (shouldSave) {
					save();
				}
			}
		}.runTaskTimer(this, 1, 1);
	}

	public static Location getLocation(Statue statue) {
		return new Location(Bukkit.getWorld(statue.world), statue.x, statue.y, statue.z, statue.yaw, statue.pitch);
	}

	public GameProfile getProfile(UUID player) {
		if (player == null) {
			return null;
		}
		String name = pluginCB.getUUIDCache().getNameFromUUID(player);
		if (name == null) {
			return null;
		}
		GameProfile profile = new GameProfile(player, name);
		profile = pluginCB.getGameProfile(profile);
		return profile;
	}

	static UUID uuid(String uuid) {
		return UUID.fromString(uuid.replaceAll("-", "").replaceAll("([0-9A-Fa-f]{8})([0-9A-Fa-f]{4})([0-9A-Fa-f]{4})([0-9A-Fa-f]{4})([0-9A-Fa-f]{12})", "$1-$2-$3-$4-$5"));
	}
}
