package de.bananaco.permissions;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.java.JavaPlugin;

public class SuperPermissionHandler {
	private final Player p;

	public SuperPermissionHandler(Player p) {
		this.p = p;
	}

	public void setupPlayer(List<String> nodes, JavaPlugin plugin) {
		PermissionAttachment att = p.addAttachment(plugin);
		for (String node : nodes) {
			if (node.contains("^")) {
				att.setPermission(node.replace("^", ""), false);
			} else {
				att.setPermission(node, true);
			}
		}
	}

	public void unsetupPlayer() {
		Set<PermissionAttachmentInfo> pAtt = p.getEffectivePermissions();
		if (pAtt != null) {
			for (PermissionAttachmentInfo pInfo : pAtt) {
				PermissionAttachment pa = pInfo.getAttachment();
				if(pa != null) {
				Map<String, Boolean> paNodes = pa.getPermissions();
				if (paNodes != null) {
					Set<String> keys = paNodes.keySet();
					for (String key : keys) {
						pa.unsetPermission(key);
					}
				}
			}
			}
			p.recalculatePermissions();
		}
	}

}
