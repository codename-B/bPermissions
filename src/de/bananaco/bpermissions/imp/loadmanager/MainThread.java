package de.bananaco.bpermissions.imp.loadmanager;

import java.util.ArrayList;
import java.util.List;

import de.bananaco.bpermissions.imp.Debugger;

public class MainThread extends Thread implements TaskThread {
	
	private static MainThread thread = new MainThread();
	
	public static MainThread getInstance() {
		return thread;
	}
	
	// a list of tasks
	private List<Runnable> tasks = new ArrayList<Runnable>();
	private boolean running = true;
	private boolean started = false;
	
	@Override
	public void run() {
		while(running) {
			check();
		}
	}
	
	/**
	 * Internal method, check scheduler
	 */
	private synchronized void check() {
		try {
			if(getTasks().size() > 0) {
				Runnable r = getTasks().get(0);
				//Debugger.log("Runnable at "+System.currentTimeMillis());
				r.run();
				getTasks().remove(0);
			} else {
				sleep(10);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Internal method, concurrent modification exception prevention
	 * @return List<Runnable>
	 */
	private synchronized List<Runnable> getTasks() {
		return tasks;
	}
	
	// from the interface
	
	public boolean hasTasks() {
		return getTasks().size() > 0;
	}
	
	public boolean isRunning() {
		return running;
	}
	
	public void setRunning(final boolean running) {
		TaskRunnable r = new TaskRunnable() {
			public void run() {
				thread.running = running;
			}
			public TaskType getType() {
				return TaskType.SERVER;
			}
		};
		schedule(r);
	}
	
	public boolean getStarted() {
		return started;
	}
	
	public void setStarted(final boolean started) {
		TaskRunnable r = new TaskRunnable() {
			public void run() {
				thread.started = started;
				Debugger.log("Set started: "+started);
			}
			public TaskType getType() {
				return TaskType.SERVER;
			}
		};
		schedule(r);
	}
	
	public void schedule(TaskRunnable r) {
		getTasks().add(r);
	}

}
