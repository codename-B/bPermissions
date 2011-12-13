package no.superperms.thankyou;

import org.bukkit.plugin.Plugin;

public class Provider {
	
	private static PermissionProvider provider = new DefaultPermissionProvider();
	
	public static PermissionProvider getProvider() {
		return provider;
	}
	
	public static void setProvider(PermissionProvider newProvider, Plugin plugin) {
		System.out.println("["+plugin.getDescription().getName()+"]"+" registered as PermissionProvider");
		provider = newProvider;
	}

}
