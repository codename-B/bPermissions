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

public class Permissions extends JavaPlugin {

	private final Map<String, String> mirrors = new HashMap<String, String>();
	private Listener loader = new WorldLoader(mirrors);
	private Map<CommandSender, Commands> commands = new HashMap<CommandSender, Commands>();
	private WorldManager wm = WorldManager.getInstance();
	
	@Override
	public void onDisable() {
		System.out.println(blankFormat("Disabled"));
	}

	@Override
	public void onEnable() {
		getServer().getPluginManager().registerEvent(Event.Type.WORLD_INIT, loader, Priority.Normal, this);
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

	// TODO proper commands
	
	@Override
	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {
		boolean allowed = true;

		if (sender instanceof Player)
			allowed = hasPermission((Player) sender, "bPermissions.admin")
					|| sender.isOp();

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
				if(calc == null) {
					sendMessage(sender, "Nothing is selected!");
				} else if (calc.getType() != type) {
					sendMessage(sender, "Please select a "+type.getName()+", you currently have a "+opposite.getName()+" selected.");
				} else {
					cmd.setCalculable(type, args[0]);
				}
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
					} else if(action.equalsIgnoreCase("list")) {
						if(value.equalsIgnoreCase("groups") || value.equalsIgnoreCase("group")) {
							cmd.listGroups();
						} else if(value.equalsIgnoreCase("permissions") || value.equalsIgnoreCase("permission")) {
							cmd.listPermissions();
						}
					} else if(action.equalsIgnoreCase("addperm")) {
						cmd.addPermission(value);
					} else if(action.equalsIgnoreCase("rmperm")) {
						cmd.removePermission(value);
					} else if(action.equals("has")) {
						cmd.hasPermission(value);
					} else {
						sendMessage(sender, "Please consult the command documentation!");
					}
				}
			} else if(args.length == 3 && args[0].equalsIgnoreCase("value")) {
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
				} else if(action.equalsIgnoreCase("reload")) {
					for(World world : wm.getAllWorlds())
						world.load();
					sendMessage(sender, "All worlds reloaded!");
				}
			}
			return true;
		}
		return true;
	}

}
