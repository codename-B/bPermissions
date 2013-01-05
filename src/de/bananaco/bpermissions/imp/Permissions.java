package de.bananaco.bpermissions.imp;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import de.bananaco.bpermissions.api.Calculable;
import de.bananaco.bpermissions.api.CalculableType;
import de.bananaco.bpermissions.api.World;
import de.bananaco.bpermissions.api.WorldManager;
import de.bananaco.bpermissions.imp.loadmanager.MainThread;
import de.bananaco.bpermissions.imp.loadmanager.TaskRunnable;
import de.bananaco.bpermissions.unit.PermissionsTest;
import de.bananaco.permissions.ImportManager;
import de.bananaco.permissions.fornoobs.BackupPermissionsCommand;
import de.bananaco.permissions.fornoobs.ForNoobs;
import de.bananaco.permissions.interfaces.PromotionTrack;

public class Permissions extends JavaPlugin {

	private final Map<String, String> mirrors = new HashMap<String, String>();
	private final Mirrors mrs = new Mirrors(mirrors);

	public SuperPermissionHandler handler;
	private Listener loader;
	// Change to public for people to hook into if they really need to
	public Map<String, Commands> commands;
	private WorldManager wm;
	private DefaultWorld world;
	private Config config;

	protected static JavaPlugin instance = null;

	private MainThread mt;
	
	@Override
	public void onDisable() {
		// Cancel tasks
		getServer().getScheduler().cancelTasks(this);

		mt.setRunning(false);
		// accomodate async
		System.out.println(blankFormat("Waiting for tasks to finish..."));
		while(mt.hasTasks()) {
			// wait
		}
		System.out.println(blankFormat("Disabled"));
	}

	@Override
	public void onLoad() {
		// Load the world mirroring setup
		mrs.load();
	}

	@Override
	public void onEnable() {
		// start main thread
		mt = MainThread.getInstance();
		mt.start();
		
		instance = this;
		// Only happens after onEnable(), prevent NPE's
		config = new Config();
		// Load the config.yml
		config.load();
		// And test
		boolean onlineMode = getServer().getOnlineMode();
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
		handler = new SuperPermissionHandler(this);
		loader = new WorldLoader(this, mirrors);
		world = new DefaultWorld(this);
		// Set the default world to our defaults
		wm.setDefaultWorld(world);
		// load the default world
		world.load();
		// Load the default Map for Commands
		commands = new HashMap<String, Commands>();
		// Register loader events
		getServer().getPluginManager().registerEvents(loader, this);
		// Register handler events
		getServer().getPluginManager().registerEvents(handler, this);
		// Setup all online players
		//handler.setupAllPlayers();
		// Load our custom nodes (if any)
		new CustomNodes().load();
		
		// REMOVED
		// getServer().getScheduler().scheduleSyncRepeatingTask(this, new SuperPermissionHandler.SuperPermissionReloader(handler), 5, 5);
		// And print a nice little message ;)		
		Debugger.log(blankFormat("Enabled"));
		// print dino
		//printDinosaurs();
		
		// set main thread enabled
		mt.setStarted(true);
		// setup all players
		final World world = this.world;
		mt.schedule(new TaskRunnable() {
			public void run() {
				Bukkit.getScheduler().scheduleSyncDelayedTask(instance, new Runnable() {
					public void run() {
						world.setupAll();
					}
				}, 0);
			}
			public TaskType getType() {
				return TaskType.SERVER;
			}
		});
	}

	public static void printDinosaurs() {
		String dino = 	"            __ "+"\n"+
				"           / _)"+"\n"+
				"    .-^^^-/ /  "+"\n"+
				" __/       /"+"\n"+
				"<__.|_|-|_|"+"\n"+
				"==DINOSAUR==";
		System.out.println("\n"+dino);
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
			if(args.length > 1)
				name = args[1];
			if(args.length > 2)
				world = args[2];
			// Check for permission
			if(!has(sender, "tracks."+name)) {
				sendMessage(sender, "You don't have permission to use promotion tracks!");
				return true;
			}
			if(command.getName().equalsIgnoreCase("promote")) {
				PromotionTrack track = config.getPromotionTrack();
				if(track.containsTrack(name)) {
					// user validity check
					if(config.trackLimit()) {
						boolean isValid = true;
						Player s = (sender instanceof Player)?(Player) sender:null;
						if(world == null) {
							for(org.bukkit.World w : Bukkit.getWorlds()) {
								boolean v = new ValidityCheck(s, track, name, player, w.getName()).isValid();
								if(!v) {
									isValid = false;
								}
							}
						} else {
							isValid = new ValidityCheck(s, track, name, player, world).isValid();
						}
						if(!isValid) {
							sender.sendMessage(ChatColor.RED+"Invalid promotion!");
							return true;
						}
					}
					// or the rest
					track.promote(player, name, world);
					sendMessage(sender, "Promoted along the track: "+name+" in "+(world==null?"all worlds":"world: "+world));
					this.showPromoteOutput(sender, player);
				} else {
					sendMessage(sender, "That track ("+name+") does not exist");
				}
			}
			else if(command.getName().equalsIgnoreCase("demote")) {
				PromotionTrack track = config.getPromotionTrack();
				if(track.containsTrack(name)) {
					// user validity check
					if(config.trackLimit()) {
						boolean isValid = true;
						Player s = (sender instanceof Player)?(Player) sender:null;
						if(world == null) {
							for(org.bukkit.World w : Bukkit.getWorlds()) {
								boolean v = new ValidityCheck(s, track, name, player, w.getName()).isValid();
								if(!v) {
									isValid = false;
								}
							}
						} else {
							isValid = new ValidityCheck(s, track, name, player, world).isValid();
						}
						if(!isValid) {
							sender.sendMessage(ChatColor.RED+"Invalid promotion!");
							return true;
						}
					}
					// or the rest
					track.demote(player, name, world);
					sendMessage(sender, "Demoted along the track: "+name+" in "+(world==null?"all worlds":"world: "+world));
					this.showPromoteOutput(sender, player);
				} else {
					sendMessage(sender, "That track ("+name+") does not exist");
				}
			}
			//ApiLayer.update();
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
		if (command.getName().equalsIgnoreCase("world")) {
			World world = cmd.getWorld();
			if (args.length == 0) {
				if (world == null) {
					sendMessage(sender, "No world selected.");
				} else {
					sendMessage(sender, "Currently selected world: " + world.getName());
				}
			} else if (args.length == 1) {
				cmd.setWorld(args[0], sender);
			} else if(args.length == 3 && args[0].equalsIgnoreCase("mirror")) {
				String worldFrom = args[1];
				String worldTo = args[2];
				mirrors.put(worldFrom, worldTo);
				mrs.save();
				sender.sendMessage(worldFrom+" mirrored to "+worldTo);
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
					} else if(action.equalsIgnoreCase("cmeta")) {
						cmd.clearMeta(value, sender);
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
					//ApiLayer.update();
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
		if(command.getName().equalsIgnoreCase("exec")) {
			String name = "null";
			CalculableType type = CalculableType.USER;
			String action = "null";
			String value = "null";
			String world = null;
			for(String c : args) {
				if(c.startsWith("u:") || c.startsWith("g:")) {
					if(c.startsWith("u:")) {
						type = CalculableType.USER;
					} else {
						type = CalculableType.GROUP;
					}
					name = c.split(":")[1];
				} else if(c.startsWith("a:")) {
					action = c.split(":")[1];
				} else if(c.startsWith("v:")) {
					value = c.split(":")[1];
				} else if(c.startsWith("w:")) {
					world = c.split(":")[1];
				}
			}
			String message = ChatColor.GOLD+"Executing action: "+ChatColor.GREEN+action+" "+value+ChatColor.GOLD+" in "+ChatColor.GREEN+(world==null?"all worlds":"world: "+world);
			String message2 = ChatColor.GOLD+"Action applied to "+ChatColor.GREEN+type.getName()+" "+name;

			sender.sendMessage(message);
			sender.sendMessage(message2);
			ExtraCommands.execute(name, type, action, value, world);
			//ApiLayer.update();
		}
		/*
		 * And now your standard "permissions" command
		 */
		if(command.getName().equalsIgnoreCase("permissions")) {
			if(args.length == 2) {
				if(args[0].equalsIgnoreCase("import")) {
					sender.sendMessage("Importing from "+args[1]);
					try {
						if(args[1].equalsIgnoreCase("yml")) {
							new ImportManager(this).importYML();
						}
						if(args[1].equalsIgnoreCase("pex")) {
							new ImportManager(this).importPEX();
						}
						if(args[1].equalsIgnoreCase("p3")) {
							new ImportManager(this).importPermissions3();
						}
						if(args[1].equalsIgnoreCase("gm")) {
							new ImportManager(this).importGroupManager();
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
					return true;
				}
			}
			if(args.length == 1) {
				if(sender instanceof ConsoleCommandSender) {
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
					if(args[0].equalsIgnoreCase("debugperms")) {
						Player[] players = Bukkit.getOnlinePlayers();
						if(players.length == 0) {
							System.err.println("You need some online players!");
						} else {
							for(Player player : players) {
								PermissionsTest.test(player);
							}
						}
						return true;
					}
					if(args[0].equalsIgnoreCase("debugset")) {
						Player[] players = Bukkit.getOnlinePlayers();
						if(players.length == 0) {
							System.err.println("You need some online players!");
						} else {
							Player player = players[0];
							BukkitCompat.runTest(player, this);
						}
						return true;
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
					// async -> sync
					mt.schedule(new TaskRunnable() {
						public void run() {
							Bukkit.getScheduler().scheduleSyncDelayedTask(instance, new Runnable() {
								public void run() {
									world.setupAll();
								}
							}, 0);
						}
						public TaskType getType() {
							return TaskType.SERVER;
						}
					});
					sendMessage(sender, "All worlds reloading!");
					return true;
				} else if(action.equalsIgnoreCase("cleanup")) {
					sendMessage(sender, "Cleaning up files!");
					wm.cleanup();
					return true;
				} else if(action.equalsIgnoreCase("examplefiles")) {
					sendMessage(sender, "Created example files!");
					// Create the example file
					new ForNoobs(this).addAll();
					return true;
				} else if(action.equalsIgnoreCase("backup")) {
					sendMessage(sender, "Creating backup!");
					new BackupPermissionsCommand(this).backup();
					return true;
				}
			}
			return false;
		}
		return true;
	}

	public void showPromoteOutput(CommandSender sender, String player) {
		sender.sendMessage("The player: "+ChatColor.GREEN+player+ChatColor.WHITE+" now has these groups");
		for(World world : WorldManager.getInstance().getAllWorlds()) {
			List<String> groups = world.getUser(player).serialiseGroups();
			String[] g = groups.toArray(new String[groups.size()]);
			String gr = Arrays.toString(g);
			sender.sendMessage("In world: "+world.getName()+" "+gr);
		}
	}

	private String getName(CommandSender sender) {
		if(sender instanceof Player)
			return sender.getName();
		return "CONSOLE";
	}

}
