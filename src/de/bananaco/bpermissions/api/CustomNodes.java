package de.bananaco.bpermissions.api;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class CustomNodes {
	
	private Map<String, Permission> permissions = new HashMap<String, Permission>();
	private static CustomNodes instance = new CustomNodes();
	
	public static void loadNodes(Collection<Permission> nodes) {
		if(nodes != null && nodes.size() > 0) { 
			for(Permission perm : nodes) {
				instance.permissions.put(perm.nameLowerCase(), perm);
			}
		}
	}
	
	public static boolean contains(String node) {
		// lowercase checking
		node = node.toLowerCase();
		return instance.permissions.containsKey(node);
	}
	
	public static Map<String, Boolean> getChildren(String node) {
		// lowercase checking
		node = node.toLowerCase();
		if(!contains(node)) {
			return new HashMap<String, Boolean>();
		}
		Map<String, Boolean> children = instance.permissions.get(node).getChildren();
		Map<String, Boolean> store = new HashMap<String, Boolean>();
		for(String child : new HashSet<String>(children.keySet())) {
			if(contains(child)) {
				store.putAll(getChildren(child));
			}
		}
		children.putAll(store);
		return children;
	}

}
