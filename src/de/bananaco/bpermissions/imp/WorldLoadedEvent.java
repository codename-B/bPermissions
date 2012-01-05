package de.bananaco.bpermissions.imp;

import org.bukkit.event.Event;

import de.bananaco.bpermissions.api.World;

public class WorldLoadedEvent extends Event {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5867873188522303881L;
	private final World world;
	
	protected WorldLoadedEvent(World world) {
		super(Event.Type.CUSTOM_EVENT);
		this.world = world;
	}
	
	public World getWorld() {
		return world;
	}

}
