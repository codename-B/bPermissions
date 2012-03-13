package de.bananaco.bpermissions.spout;

import java.io.File;

import de.bananaco.bpermissions.api.WorldManager;
import de.bananaco.permissions.interfaces.PromotionTrack;

public class Config {
	
	private final File file = new File("plugins/bPermissions/config.yml");
	private YamlConfiguration config = new YamlConfiguration();
	
	private String trackType = "multi";
	private PromotionTrack track = null;
	
	private boolean useGlobalFiles = false;
	
	private boolean autoSave = true;
	
	private boolean offlineMode = false;
		
	public void load() {
		try {
			loadUnsafe();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void loadUnsafe() throws Exception {
		// Your standard create if not exist shizzledizzle
		if(!file.exists()) {
			if(file.getParentFile() != null)
				file.getParentFile().mkdirs();
			file.createNewFile();
		}
		config.load(file);
		// set the value to default
		config.set("auto-save", config.get("auto-save", autoSave));
		config.set("track-type", config.get("track-type", trackType));
		// set the debugger value to default
		config.set("debug-mode", Debugger.setDebug(config.getBoolean("debug-mode", Debugger.getDebug())));
		config.set("allow-offline-mode", config.get("allow-offline-mode", offlineMode));
		config.set("use-global-files", config.get("use-global-files", useGlobalFiles));
		// then load it into memory
		useGlobalFiles = config.getBoolean("use-global-files");
		autoSave = config.getBoolean("auto-save");
		trackType = config.getString("track-type");
		offlineMode = config.getBoolean("allow-offline-mode");
		// then load our PromotionTrack
		if(trackType.equalsIgnoreCase("multi")) {
			track = new MultiGroupPromotion();
		} else if(trackType.equalsIgnoreCase("lump")) {
			track = new LumpGroupPromotion();
		}
		else {
			track = new SingleGroupPromotion();
		}
		// Then set the worldmanager
		WorldManager.getInstance().setAutoSave(autoSave);
		// Load the track
		track.load();
		// finally save the config
		config.save(file);
	}
	
	public boolean getUseGlobalFiles() {
		return useGlobalFiles;
	}
	
	public PromotionTrack getPromotionTrack() {
		return track;
	}
	
	public boolean getAllowOfflineMode() {
		return offlineMode;
	}

}
