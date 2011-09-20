package de.bananaco.permissions.json;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;


public class JSONPermission {
	
	JSONObject jsonmain;
	File file;
	public JSONPermission(File file) {
		this.file = file;
		jsonmain = new JSONObject();
	}
	
	public void load() {
		try {
		JSONParser ps = new JSONParser();
		BufferedReader br = new BufferedReader(new InputStreamReader(new DataInputStream(new FileInputStream(file))));
		String line = br.readLine();
		JSONObject js = (JSONObject) ps.parse(line);
		this.jsonmain = js;
		br.close();
		} catch (Exception e) {
			this.jsonmain = new JSONObject();
			System.out.println("New jsonarray loaded");
			//e.printStackTrace();
		}
	}
	
	public void save() {
		try {
		if(!file.exists()) {
			file.getParentFile().mkdirs();
			file.createNewFile();
		}
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new DataOutputStream(new FileOutputStream(file)),"UTF-8"));
		bw.write(toString());
		bw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public String getDefault() {
		String defaultGroup = "default";
		if(jsonmain.containsKey("default"))
		defaultGroup = jsonmain.get("default").toString();
		return defaultGroup;
	}
	
	@SuppressWarnings("unchecked")
	public Map<String, Map<String, List<String>>> get() {
		Map<String, Map<String, List<String>>> main = new HashMap<String, Map<String, List<String>>>();
		Map<String, List<String>> playerGroups = new HashMap<String, List<String>>();
		Map<String, List<String>> groupPermissions = new HashMap<String, List<String>>();
		if(jsonmain.containsKey("players")) {
		JSONObject jsonPlayers = (JSONObject) jsonmain.get("players");
		if(jsonPlayers != null) {
			// The list of players
			Set<String> keyset = jsonPlayers.keySet();
			for(String key : keyset) {
				List<String> groups = new ArrayList<String>();
				JSONArray jsonGroups = (JSONArray) jsonPlayers.get(key);
				for(int i=0; i<jsonGroups.size(); i++)
					groups.add(jsonGroups.get(i).toString());
				playerGroups.put(key, groups);
			}
		}
		}
		if(jsonmain.containsKey("groups")) {
		JSONObject jsonGroups = (JSONObject) jsonmain.get("groups");
		if(jsonGroups != null) {
			// The list of players
			Set<String> keyset = jsonGroups.keySet();
			for(String key : keyset) {
				List<String> permissions = new ArrayList<String>();
				JSONArray jsonPermissions = (JSONArray) jsonGroups.get(key);
				for(int i=0; i<jsonPermissions.size(); i++)
					permissions.add(jsonPermissions.get(i).toString());
				groupPermissions.put(key, permissions);
			}
		}
		}
		main.put("players", playerGroups);
		main.put("groups", groupPermissions);
		return main;
	}
	
	@SuppressWarnings("unchecked")
	public void put(String defaultGroup, Map<String, List<String>> players, Map<String, List<String>> groups) {
	JSONObject jsonmain = new JSONObject();
	JSONObject jsonPlayers = new JSONObject();
	JSONObject jsonGroups = new JSONObject();
	for(String player : players.keySet()) {
		JSONArray jsongroups = new JSONArray();
		for(String group : players.get(player))
			jsongroups.add(group);
		jsonPlayers.put(player, jsongroups);
	}
	
	for(String group : groups.keySet()) {
		JSONArray jsonpermissions = new JSONArray();
		for(String permission : groups.get(group))
			jsonpermissions.add(permission);
		jsonGroups.put(group, jsonpermissions);
	}
	jsonmain.put("players", jsonPlayers);
	jsonmain.put("groups", jsonGroups);
	jsonmain.put("default", defaultGroup);
	this.jsonmain = jsonmain;
	}
	
	public String toString() {
		return jsonmain.toJSONString();
	}

}
