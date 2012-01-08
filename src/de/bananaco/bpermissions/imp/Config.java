package de.bananaco.bpermissions.imp;

import java.io.File;

import org.bukkit.configuration.file.YamlConfiguration;

import de.bananaco.permissions.interfaces.PromotionTrack;

public class Config {
	
	private final File file = new File("plugins/bPermissions/config.yml");
	private YamlConfiguration config = new YamlConfiguration();
	
	private String trackType = "multi";
	private PromotionTrack track = null;
	
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
			file.getParentFile().mkdirs();
			file.createNewFile();
		}
		config.load(file);
		// set the value to default
		config.set("track-type", config.get("track-type", trackType));
		// then load it into memory
		trackType = config.getString("track-type");
		// then load our PromotionTrack
		if(trackType.equalsIgnoreCase("multi")) {
			track = new MultiGroupPromotion();
		} else if(trackType.equalsIgnoreCase("lump")) {
			track = new LumpGroupPromotion();
		}
		else {
			track = new SingleGroupPromotion();
		}
		// Load the track
		track.load();
		// finally save the config
		config.save(file);
	}
	
	public PromotionTrack getPromotionTrack() {
		return track;
	}

}
