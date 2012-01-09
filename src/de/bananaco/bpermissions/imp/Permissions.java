package de.bananaco.bpermissions.imp;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.Listener;
import org.bukkit.event.Event.Priority;
import org.bukkit.plugin.java.JavaPlugin;

import de.bananaco.bpermissions.api.World;
import de.bananaco.bpermissions.api.WorldManager;
import de.bananaco.bpermissions.api.util.Calculable;
import de.bananaco.bpermissions.api.util.CalculableType;
import de.bananaco.permissions.interfaces.PromotionTrack;

public class Permissions extends JavaPlugin {
	
	private final Map<String, String> mirrors = new HashMap<String, String>();
	private final Mirrors mrs = new Mirrors(mirrors);
	
	public SuperPermissionHandler handler;
	private Listener loader;
	private Map<CommandSender, Commands> commands;
	private WorldManager wm;
	private DefaultWorld world;
	private Config config;
	de.bananaco.permissions.Permissions oldPermissions;
	
	@Override
	public void onDisable() {
		for(World world : wm.getAllWorlds())
			world.save();
		System.out.println(blankFormat("Disabled"));
	}

	@Override
	public void onEnable() {
		// Only happens after onEnable(), prevent NPE's
		config = new Config();
		wm = WorldManager.getInstance();
		oldPermissions = new de.bananaco.permissions.Permissions(this);
		handler = new SuperPermissionHandler(this);
		loader = new WorldLoader(this, mirrors);
		world = new DefaultWorld(this);
		// Set the default world to our defaults
		wm.setDefaultWorld(world);
		// Load the default users.yml and groups.yml
		world.load();
		// Load the default Map for Commands
		commands = new HashMap<CommandSender, Commands>();
		// Load the world mirroring setup
		mrs.load();
		// Load the config.yml
		config.load();
		// Register world loading
		getServer().getPluginManager().registerEvent(Event.Type.WORLD_INIT, loader, Priority.Normal, this);
		// Register player login
		getServer().getPluginManager().registerEvent(Event.Type.PLAYER_LOGIN, handler, Priority.Lowest, this);
		// Register player chat
		getServer().getPluginManager().registerEvent(Event.Type.PLAYER_CHAT, handler, Priority.Lowest, this);
		// Register world changing
		getServer().getPluginManager().registerEvent(Event.Type.PLAYER_CHANGED_WORLD, handler, Priority.Normal, this);
		// And print a nice little message ;)
		System.out.println(blankFormat("Enabled"));
	}
	
	public static String blankFormat(String message) {
		return "[bPermissions] "+message;
	}
	
	public static String format(String message) {
		ChatColor vary = ChatColor.GREEN;
		if(message.contains("!")) {
			vary = ChatColor.RED;
		} else if(message.contains(":")) {
			vary = ChatColor.AQUA;
		}
		return ChatColor.BLUE+"[bPermissions] "+vary+message;
	}
	
	public static boolean hasPermission(Player player, String node) {
		return WorldManager.getInstance().getWorld(player.getWorld().getName()).getUser(player.getName()).hasPermission(node);
	}
	
	public void sendMessage(CommandSender sender, String message) {
		sender.sendMessage(format(message));
	}
	
	public boolean has(CommandSender sender, String perm) {
		if(sender instanceof Player)
			return sender.hasPermission(perm);
		else
		return true;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {
		boolean allowed = true;

		if (sender instanceof Player)
			allowed = hasPermission((Player) sender, "bPermissions.admin")
					|| sender.isOp();
		
		/*
		 * Promote/Demote shizzledizzle
		 */
		if(args.length > 0 && (command.getName().equalsIgnoreCase("promote") || command.getName().equalsIgnoreCase("demote"))) {
			// Define some global variables
			String player = args[0];
			String name = "default";
			String world = null;
			// Check for permission
			if(!has(sender, "tracks."+name)) {
				sendMessage(sender, "You don't have permission to use promotion tracks!");
				return true;
			}
			if(command.getName().equalsIgnoreCase("promote")) {
				PromotionTrack track = config.getPromotionTrack();
				if(args.length > 1)
					name = args[1];
				if(args.length > 2)
					world = args[2];
				if(track.containsTrack(name)) {
					track.promote(player, name, world);
					sendMessage(sender, "Promoted along the track: "+name+" in "+(world==null?"all worlds":"world: "+world));
				} else {
					sendMessage(sender, "That track ("+name+") does not exist");
				}
			}
			else if(command.getName().equalsIgnoreCase("demote")) {
				PromotionTrack track = config.getPromotionTrack();
				if(args.length > 1)
					name = args[1];
				if(args.length > 2)
					world = args[2];
				if(track.containsTrack(name)) {
					track.demote(player, name, world);
					sendMessage(sender, "Demoted along the track: "+name+" in "+(world==null?"all worlds":"world: "+world));
				} else {
					sendMessage(sender, "That track ("+name+") does not exist");
				}
			}
			return true;
		}

		if (!allowed) {
			sendMessage(sender, "You're not allowed to do that!");
			return true;
		}
		/*
		 * Create an entry in the commands selection if one does not exist
		 */
		if (!commands.containsKey(sender))
			commands.put(sender, new Commands(sender));

		Commands cmd = commands.get(sender);
		/*
		 * Selecting and displaying the currently selected world
		 */
		if (command.getName().equalsIgnoreCase("world")) {
			World world = cmd.getWorld();
			if (args.length == 0) {
				if (world == null) {
					sendMessage(sender, "No world selected.");
				} else {
					sendMessage(sender, "Currently selected world: " + world.getName());
				}
			} else if (args.length == 1) {
				cmd.setWorld(args[0]);
			} else {
				sendMessage(sender, "Too many arguments.");
			}
			return true;
		}
		/*
		 * User/group
		 * 
		 * Much is repeated here
		 */
		if (command.getName().equalsIgnoreCase("user") || command.getName().equalsIgnoreCase("group")) {
			Calculable calc = cmd.getCalculable();
			CalculableType type = command.getName().equalsIgnoreCase("user")?CalculableType.USER:CalculableType.GROUP;
			CalculableType opposite = !command.getName().equalsIgnoreCase("user")?CalculableType.USER:CalculableType.GROUP;
			/*
			 * Selecting, displaying, and executing commands on the Calculable
			 */
			if(args.length == 0) {
				if(calc == null) {
					sendMessage(sender, "Nothing is selected!");
				} else {
					sendMessage(sender, "Currently selected "+calc.getType().getName()+": "+calc.getName());
				}
			} else if(args.length == 1) {
				cmd.setCalculable(type, args[0]);
			} else if(args.length == 2) {
				if(calc == null) {
					sendMessage(sender, "Nothing is selected!");
				} else if (calc.getType() != type) {
					sendMessage(sender, "Please select a "+type.getName()+", you currently have a "+opposite.getName()+" selected.");
				} else {
					String action = args[0];
					String value = args[1];
					if(action.equalsIgnoreCase("addgroup")) {
						cmd.addGroup(value);
					} else if(action.equalsIgnoreCase("rmgroup")) {
						cmd.removeGroup(value);
					} else if(action.equalsIgnoreCase("setgroup")) {
						cmd.setGroup(value);
					}
					else if(action.equalsIgnoreCase("list")) {
						value = value.toLowerCase();
						if(value.equalsIgnoreCase("groups") || value.equalsIgnoreCase("group") || value.equalsIgnoreCase("g")) {
							cmd.listGroups();
						} else if(value.startsWith("perm") || value.equalsIgnoreCase("p")) {
							cmd.listPermissions();
						}
					} else if(action.equalsIgnoreCase("meta")) {
						cmd.showValue(value);
					}
					else if(action.equalsIgnoreCase("addperm")) {
						cmd.addPermission(value);
					} else if(action.equalsIgnoreCase("rmperm")) {
						cmd.removePermission(value);
					} else if(action.equals("has")) {
						cmd.hasPermission(value);
					} else {
						sendMessage(sender, "Please consult the command documentation!");
					}
				}
			} else if(args.length == 3 && args[0].equalsIgnoreCase("meta")) {
				if(calc == null) {
					sendMessage(sender, "Nothing is selected!");
				} else if (calc.getType() != type) {
					sendMessage(sender, "Please select a "+type.getName()+", you currently have a "+opposite.getName()+" selected.");
				} else {
					cmd.setValue(args[1], args[2]);
				}
			}
			else {
				sendMessage(sender, "Too many arguments.");
			}
			return true;
		}
		
		/*
		 * And now your standard "permissions" command
		 */
		if(command.getName().equalsIgnoreCase("permissions")) {
			if(args.length == 1) {
				String action = args[0];
				if(action.equalsIgnoreCase("save")) {
					cmd.save();
					return true;
				} else if(action.equalsIgnoreCase("reload")) {
					for(World world : wm.getAllWorlds())
						world.load();
					sendMessage(sender, "All worlds reloaded!");
					return true;
				} else if(action.equalsIgnoreCase("cleanup")) {
					sendMessage(sender, "Cleaning up files!");
					wm.cleanup();
					return true;
				}
			}
			return oldPermissions.onCommand(sender, command, label, args);
		}
		return true;
	}

}
