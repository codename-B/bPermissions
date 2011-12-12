package de.bananaco.permissions.debug;

public class MCMA {

	public static MCMA debug = null;
	public static boolean bug = false;

	public static MCMA getDebugger() {
		if (debug == null)
			debug = new MCMA();
		return debug;
	}

	public static void setDebugging(boolean bugger) {
		bug = bugger;
	}

	public void log(String world) {
		if (bug)
			System.out.println("[MCMA:reload:" + world + "]");
	}

}
