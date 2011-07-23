package com.ubempire.permissions;

import java.util.List;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;

public class SuperPermissionHandler {
	private Permissions Permissions;
	private Player p;
	SuperPermissionHandler(Player p, Permissions Permissions)
	{
	this.p=p;
	this.Permissions=Permissions;
	}

	public void setupPlayer(List<String> nodes) {
		PermissionAttachment att = p.addAttachment(Permissions);
		for (String node : nodes) {
			att.setPermission(node, true);
		}
		Permissions.pf.permissions.put(p.getName(), att);
	}


	public void unsetupPlayer() {
		if (Permissions.pf.playerPermissions.containsKey(p.getName())
				&& Permissions.pf.permissions.containsKey(p.getName())) {
			List<String> nodes = Permissions.pf.playerPermissions.get(p.getName());
			for (String node : nodes) {
				Permissions.pf.permissions.get(p.getName()).unsetPermission(node);
			}
			p.removeAttachment(Permissions.pf.permissions.get(p.getName()));
			Permissions.pf.permissions.remove(p.getName());
			Permissions.pf.playerPermissions.remove(p.getName());
			Permissions.pf.playerGroups.remove(p.getName());
		}
	}

}
