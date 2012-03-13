package de.bananaco.bpermissions.spout;

import java.util.HashMap;
import java.util.Map;

import org.spout.api.ChatColor;
import org.spout.api.command.CommandSource;
import org.spout.api.event.Listener;
import org.spout.api.player.Player;
import org.spout.api.plugin.CommonPlugin;

import de.bananaco.bpermissions.api.World;
import de.bananaco.bpermissions.api.WorldManager;
import de.bananaco.bpermissions.api.util.Calculable;
import de.bananaco.bpermissions.api.util.CalculableType;
import de.bananaco.permissions.interfaces.PromotionTrack;

public class Permissions extends CommonPlugin {
	
	private final Map<String, String> mirrors = new HashMap<String, String>();
	private final Mirrors mrs = new Mirrors(mirrors);
	
	public SuperPermissionHandler handler;
	private Listener loader;
	// Change to public for people to hook into if they really need to
	public Map<String, Commands> commands;
	private WorldManager wm;
	private DefaultWorld world;
	private Config config;
	
	@Override
	public void onDisable() {
		if(wm != null) {
		for(World world : wm.getAllWorlds())
			world.save();
		}
		System.out.println(blankFormat("Disabled"));
	}

	@Override
	public void onLoad() {
		// Load the world mirroring setup
		mrs.load();
		super.onLoad();
	}

	@Override
	public void onEnable() {
		// Only happens after onEnable(), prevent NPE's
		config = new Config();
		// Load the config.yml
		config.load();
		// And test
		boolean onlineMode = true;
		// Don't allow online mode servers to run bPermissions by default
		if(config.getAllowOfflineMode() == false && onlineMode == false) {
			System.err.println(blankFormat("Please check config.yml to enable offline-mode use"));
			this.setEnabled(false);
			return;
		}
		// Get the instance
		wm = WorldManager.getInstance();
		// Set the global file flag
		wm.setUseGlobalFiles(config.getUseGlobalFiles());
		handler = new SuperPermissionHandler();
		loader = new WorldLoader(this, mirrors);
		world = new DefaultWorld(this);
		// Set the default world to our defaults
		wm.setDefaultWorld(world);
		// Load the default users.yml and groups.yml
		world.load();
		// Load the default Map for Commands
		commands = new HashMap<String, Commands>();
		// Register loader events
		this.getGame().getEventManager().registerEvents(loader, this);
		// Register handler events
		this.getGame().getEventManager().registerEvents(handler, this);
		// Setup all online players
		//handler.setupAllPlayers();
		// And print a nice little message ;)
		System.out.println(blankFormat("Enabled"));
	}
	
	public static String blankFormat(String message) {
		return "[bPermissions] "+message;
	}
	
	public static String format(String message) {
		ChatColor vary = ChatColor.BRIGHT_GREEN;
		if(message.contains("!")) {
			vary = ChatColor.RED;
		} else if(message.contains(":")) {
			vary = ChatColor.CYAN;
		}
		return ChatColor.BLUE+"[bPermissions] "+vary+message;
	}
	
	public static boolean hasPermission(Player player, String node) {
		return WorldManager.getInstance().getWorld(player.getEntity().getWorld().getName()).getUser(player.getName()).hasPermission(node);
	}
	
	public void sendMessage(CommandSource sender, String message) {
		sender.sendMessage(format(message));
	}
	
	public boolean has(CommandSource sender, String perm) {
		if(sender instanceof Player)
			return sender.hasPermission(perm);
		else
		return true;
	}

	public boolean onCommand(CommandSource sender, String command,
			String label, String[] args) {
		boolean allowed = true;

		if (sender instanceof Player)
			allowed = hasPermission((Player) sender, "bPermissions.admin");
		
		/*
		 * Promote/Demote shizzledizzle
		 */
		if(args.length > 0 && (command.equalsIgnoreCase("promote") || command.equalsIgnoreCase("demote"))) {
			// Define some global variables
			String player = args[0];
			String name = "default";
			String world = null;
			if(args.length > 1)
				name = args[1];
			if(args.length > 2)
				world = args[2];
			// Check for permission
			if(!has(sender, "tracks."+name)) {
				sendMessage(sender, "You don't have permission to use promotion tracks!");
				return true;
			}
			if(command.equalsIgnoreCase("promote")) {
				PromotionTrack track = config.getPromotionTrack();
				if(track.containsTrack(name)) {
					track.promote(player, name, world);
					sendMessage(sender, "Promoted along the track: "+name+" in "+(world==null?"all worlds":"world: "+world));
				} else {
					sendMessage(sender, "That track ("+name+") does not exist");
				}
			}
			else if(command.equalsIgnoreCase("demote")) {
				PromotionTrack track = config.getPromotionTrack();
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
		if (!commands.containsKey(getName(sender)))
			commands.put(getName(sender), new Commands());

		Commands cmd = commands.get(getName(sender));
		/*
		 * Selecting and displaying the currently selected world
		 */
		if (command.equalsIgnoreCase("world")) {
			World world = cmd.getWorld();
			if (args.length == 0) {
				if (world == null) {
					sendMessage(sender, "No world selected.");
				} else {
					sendMessage(sender, "Currently selected world: " + world.getName());
				}
			} else if (args.length == 1) {
				cmd.setWorld(args[0], sender);
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
		if (command.equalsIgnoreCase("user") || command.equalsIgnoreCase("group")) {
			Calculable calc = cmd.getCalculable();
			CalculableType type = command.equalsIgnoreCase("user")?CalculableType.USER:CalculableType.GROUP;
			CalculableType opposite = !command.equalsIgnoreCase("user")?CalculableType.USER:CalculableType.GROUP;
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
				cmd.setCalculable(type, args[0], sender);
			} else if(args.length == 2) {
				if(calc == null) {
					sendMessage(sender, "Nothing is selected!");
				} else if (calc.getType() != type) {
					sendMessage(sender, "Please select a "+type.getName()+", you currently have a "+opposite.getName()+" selected.");
				} else {
					String action = args[0];
					String value = args[1];
					if(action.equalsIgnoreCase("addgroup")) {
						cmd.addGroup(value, sender);
					} else if(action.equalsIgnoreCase("rmgroup")) {
						cmd.removeGroup(value, sender);
					} else if(action.equalsIgnoreCase("setgroup")) {
						cmd.setGroup(value, sender);
					}
					else if(action.equalsIgnoreCase("list")) {
						value = value.toLowerCase();
						if(value.equalsIgnoreCase("groups") || value.equalsIgnoreCase("group") || value.equalsIgnoreCase("g")) {
							cmd.listGroups(sender);
						} else if(value.startsWith("perm") || value.equalsIgnoreCase("p")) {
							cmd.listPermissions(sender);
						}
					} else if(action.equalsIgnoreCase("meta")) {
						cmd.showValue(value, sender);
					}
					else if(action.equalsIgnoreCase("addperm")) {
						cmd.addPermission(value, sender);
					} else if(action.equalsIgnoreCase("rmperm")) {
						cmd.removePermission(value, sender);
					} else if(action.equals("has")) {
						cmd.hasPermission(value, sender);
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
					cmd.setValue(args[1], args[2], sender);
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
		if(command.equalsIgnoreCase("permissions")) {
			if(args.length == 2) {
				if(args[0].equalsIgnoreCase("import")) {
					sender.sendMessage("Not enabled on spout!");
					return true;
				}
			}
			if(args.length == 1) {
				if(!(sender instanceof Player)) {
					if(args[0].equalsIgnoreCase("debug")) {
						if(Debugger.getDebug()) {
							for(World world : wm.getAllWorlds()) {
								Debugger.log(world);
							}
							return true;
						} else {
							sender.sendMessage("Please enable debug mode to use this command.");
							return true;
						}
					}
				}
				String action = args[0];
				if(action.equalsIgnoreCase("save")) {
					sendMessage(sender, "All worlds saved!");
					cmd.save();
					return true;
				} else if(action.equalsIgnoreCase("reload")) {
					// Reload all changes
					for(World world : wm.getAllWorlds()) {
						world.load();
					}
					// Iterate through all players and ensure 100% that they're setup
					// !! Should occur automatically now !!
					//for(Player player : getServer().getOnlinePlayers()) {
					//	handler.setupPlayer(player, wm.getWorld(player.getWorld().getName()));
					//}
					sendMessage(sender, "All worlds reloaded!");
					return true;
				} else if(action.equalsIgnoreCase("cleanup")) {
					sendMessage(sender, "Cleaning up files!");
					wm.cleanup();
					return true;
				} else if(action.equalsIgnoreCase("helpme")) {
					sendMessage(sender, "Not enabled on spout!");
					return true;
				} else if(action.equalsIgnoreCase("backup")) {
					sendMessage(sender, "Not enabled on spout!");
					return true;
				}
			}
			return false;
		}
		return true;
	}

	private String getName(CommandSource sender) {
		if(sender instanceof Player)
			return sender.getName();
		return "CONSOLE";
	}

}
