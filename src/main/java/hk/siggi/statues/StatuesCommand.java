package hk.siggi.statues;

import com.mojang.authlib.GameProfile;
import java.util.UUID;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class StatuesCommand implements CommandExecutor {

	private final Statues plugin;

	StatuesCommand(Statues plugin) {
		this.plugin = plugin;
	}

	private boolean checkPermission(Player p, String permission) {
		if (p == null) {
			return true;
		}
		return p.hasPermission(permission);
	}

	public boolean onCommand(CommandSender sender, Command command, String label, String[] split) {
		Player player = null;
		String senderName = "<CONSOLE>";
		if (sender instanceof Player) {
			player = (Player) sender;
			senderName = player.getName();
		}
		if (split.length == 0) {
			sender.sendMessage(ChatColor.AQUA + "/statue create|delete");
			return true;
		}
		if (split[0].equalsIgnoreCase("create")) {
			if (!checkPermission(player, "hk.siggi.statues.create")) {
				player.sendMessage(ChatColor.RED + "You are not allowed to use this command.");
				return true;
			}
			if (player == null) {
				sender.sendMessage(ChatColor.RED + "This command can only be used by in-game players.");
				return true;
			}
			String username = split[1];
			UUID uuid = plugin.pluginCB.getUUIDCache().getUUIDFromName(username);
			if (uuid == null) {
				player.sendMessage(ChatColor.RED + "Player not found: " + username);
				return true;
			}
			GameProfile profile = plugin.getProfile(uuid);
			boolean facePlayer = false;
			boolean standStraight = false;
			if (split.length >= 3) {
				facePlayer = Boolean.parseBoolean(split[2]);
			}
			if (split.length >= 4) {
				standStraight = Boolean.parseBoolean(split[3]);
			}
			Statue statue = new Statue(profile, player.getLocation());
			statue.facePlayer = facePlayer;
			statue.standStraight = standStraight;
			plugin.add(statue);
			plugin.save();
		} else if (split[0].equalsIgnoreCase("delete")) {
			if (!checkPermission(player, "hk.siggi.statues.delete")) {
				player.sendMessage(ChatColor.RED + "You are not allowed to use this command.");
				return true;
			}
			if (player == null) {
				sender.sendMessage(ChatColor.RED + "This command can only be used by in-game players.");
				return true;
			}
			Statue closest = null;
			double distance = 0.0;
			World world = player.getWorld();
			Location myLocation = player.getLocation();
			String currentWorldName = world.getName();
			for (Statue statue : plugin.statues) {
				if (currentWorldName.equals(statue.world)) {
					double dist = Statues.getLocation(statue).distanceSquared(myLocation);
					if (dist < distance || closest == null) {
						distance = dist;
						closest = statue;
					}
				}
			}
			if (closest == null || distance >= 16.0) {
				sender.sendMessage(ChatColor.RED + "Statue not found, or you are too far from the statue!");
				return true;
			}
			closest.delete();
			plugin.save();
		} else if (split[0].equalsIgnoreCase("reload")) {
			if (!checkPermission(player, "hk.siggi.statues.reload")) {
				player.sendMessage(ChatColor.RED + "You are not allowed to use this command.");
				return true;
			}
			plugin.load();
		} else if (split[0].equalsIgnoreCase("resave")) {
			if (!checkPermission(player, "hk.siggi.statues.resave")) {
				player.sendMessage(ChatColor.RED + "You are not allowed to use this command.");
				return true;
			}
			plugin.save();
		} else {
			sender.sendMessage(ChatColor.AQUA + "/statue create|delete");
		}
		return true;
	}

}
