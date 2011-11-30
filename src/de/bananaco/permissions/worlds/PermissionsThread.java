package de.bananaco.permissions.worlds;

public class PermissionsThread extends Thread {

	public static void run(Runnable r) {
		new PermissionsThread(r).start();
	}

	private final Runnable r;

	public PermissionsThread(Runnable r) {
		this.r = r;
	}

	public void run() {
		r.run();
		interrupt();
	}
}
