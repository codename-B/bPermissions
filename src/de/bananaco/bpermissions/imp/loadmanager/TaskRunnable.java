package de.bananaco.bpermissions.imp.loadmanager;

public interface TaskRunnable extends Runnable {
	
	static enum TaskType {
		SAVE,
		LOAD,
		SERVER
	}
	
	public TaskType getType();

}
