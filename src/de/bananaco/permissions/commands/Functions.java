package de.bananaco.permissions.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class Functions implements CommandExecutor {

    static enum FunctionType {
        PLAYER(new String[] {"pl", "player"}),
        PACKAGE(new String[] {"pa", "pack", "package"});
        private final String[] aliases;
        private FunctionType(String[] aliases) {
            this.aliases = aliases;
        }
        public static FunctionType getType(String s) {
            for(FunctionType type : FunctionType.values()) {
                for(String al : type.aliases) {
                    if(s.equalsIgnoreCase(al)) {
                        return type;
                    }
                }
            }
            return null;
        }
    }

    static enum ActionType {
        ADD(new String[] {"ad", "pl", "add"}),
        REMOVE(new String[] {"rm", "remo", "remove", "remov"}),
        SET(new String[] {"s", "se", "set"});
        private final String[] aliases;
        private ActionType(String[] aliases) {
            this.aliases = aliases;
        }
        public static ActionType getType(String s) {
            for(ActionType type : ActionType.values()) {
                for(String al : type.aliases) {
                    if(s.equalsIgnoreCase(al)) {
                        return type;
                    }
                }
            }
            return null;
        }
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        return true;
    }

    public void execute(CommandSender sender, FunctionType type, ActionType action) {

    }

}
