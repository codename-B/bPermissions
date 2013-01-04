package de.bananaco.bpermissions.imp.loadmanager;

public interface TaskThread {
	/**
	 * If there are any currently scheduled tasks, return true
	 * @return tasks.size() > 0
	 */
	public boolean hasTasks();
	
	/**
	 * If the tasks are currently running
	 * @return running
	 */
	public boolean isRunning();
	
	/**
	 * Used to prevent any more tasks from running
	 * @param running
	 */
	public void setRunning(boolean running);
	
	/**
	 * Schedule a task to be run
	 * @param r
	 */
	public void schedule(Runnable r);

}
