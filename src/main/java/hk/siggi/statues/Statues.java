package hk.siggi.statues;

import com.mojang.authlib.GameProfile;
import hk.siggi.bukkit.plugcubebuildersin.PlugCubeBuildersIn;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.UUID;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.MemoryNPCDataStore;
import net.citizensnpcs.api.npc.NPCRegistry;
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
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.event.world.WorldUnloadEvent;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class Statues extends JavaPlugin implements Listener {

	PlugCubeBuildersIn pluginCB = null;
	private static Statues instance = null;
	final ArrayList<Statue> statues = new ArrayList<>();
	NPCRegistry npcRegistry = null;
	private Economy economy = null;

	public void add(Statue statue) {
		statues.add(statue);
		statue.createNpc();
		statue.trySpawnEntity();
	}

	private boolean startedLoop = false;

	public static Statues getInstance() {
		return instance;
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
		Util.init(getServer());
		startedLoop = false;
		npcRegistry = CitizensAPI.createNamedNPCRegistry("Statues", new MemoryNPCDataStore());
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvents(this, this);
		getCommand("statue").setExecutor(new StatuesCommand(this));
		pluginCB = (PlugCubeBuildersIn) pm.getPlugin("PlugCubeBuildersIn");
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
							add(statue);
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
		CitizensAPI.removeNamedNPCRegistry("Statues");
	}

	@EventHandler
	public void login(final PlayerJoinEvent event) {
		if (!startedLoop) {
			startedLoop = true;
			startLoop();
		}
		for (Statue statue : statues) {
			if (statue.onlyVisibleTo != null && statue.playerEntity != null) {
				event.getPlayer().hidePlayer(Statues.getInstance(), statue.playerEntity);
			}
		}
	}

	@EventHandler
	public void worldLoadEvent(WorldLoadEvent event) {
		String name = event.getWorld().getName();
		new BukkitRunnable(){
			@Override
			public void run() {
				for (Statue statue : statues) {
					if (statue.world.equals(name)) {
						statue.trySpawnEntity();
					}
				}
			}
		}.runTaskLater(this, 1L);
	}

	@EventHandler
	public void worldUnloadEvent(WorldUnloadEvent event) {
		String name = event.getWorld().getName();
		for (Statue statue : statues) {
			if (statue.world.equals(name)) {
				statue.despawnEntity();
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
		if (clicked.getType() == Util.getSignPost() || clicked.getType() == Util.getWallSign()) {
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
		new BukkitRunnable() {
			@Override
			public void run() {
				boolean shouldSave = false;
				for (Iterator<Statue> it = statues.iterator(); it.hasNext(); ) {
					Statue statue = it.next();
					if (statue.shouldAutoDelete()) {
						statue.delete();
						shouldSave = true;
					}
					if (statue.deleted) {
						it.remove();
						shouldSave = true;
					}
				}
				if (shouldSave)
					save();
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
