package de.bananaco.permissions.debug;

public class Debugger {
	
	private static boolean bug = true;
	private static Debugger debug = null;
	
	public static Debugger getDebugger() {
		if(debug == null)
			debug = new Debugger();
		return debug;
	}
	
	public static void setDebugging(boolean bugger) {
		bug = bugger;
	}
	
	public void log(String data) {
		if(bug)
		System.out.println("[bPermissions - debug] "+data);
	}

}
