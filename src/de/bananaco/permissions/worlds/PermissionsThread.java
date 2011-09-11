package de.bananaco.permissions.worlds;

public class PermissionsThread extends Thread {
	
	private final Runnable r;
	public PermissionsThread(Runnable r) {
		this.r = r;
	}
	
	public void run() {
		r.run();
		interrupt();
	}
	public static void run(Runnable r) {
		new PermissionsThread(r).start();
	}
}
