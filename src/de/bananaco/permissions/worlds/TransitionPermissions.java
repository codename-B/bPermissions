package de.bananaco.permissions.worlds;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bukkit.entity.Player;

import de.bananaco.permissions.interfaces.TransitionSet;

public class TransitionPermissions implements TransitionSet {
	private final Map<String, ArrayList<String>> transet;
	TransitionPermissions(Map<String, ArrayList<String>> transet) {
		this.transet = transet;
	}
	@Override
	public void addTransNode(Player player, String node) {
		addTransNode(player.getName(), node);
	}

	@Override
	public void addTransNode(String player, String node) {
		ArrayList<String> nodes;
		if(transet.containsKey(player))
			nodes = transet.get(player);
		else
			nodes = new ArrayList<String>();
		if(!nodes.contains(node))
		nodes.add(node);
		else
			return;
	}

	@Override
	public void removeTransNode(Player player, String node) {
		removeTransNode(player.getName(), node);
	}

	@Override
	public void removeTransNode(String player, String node) {
		if(transet.get(player)==null)
			return;
		if(transet.get(player).contains(node))
			transet.get(player).remove(node);
		else
			return;
	}

	@Override
	public void clearTransNodes(Player player) {
		clearTransNodes(player.getName());
	}

	@Override
	public void clearTransNodes(String player) {
		if(transet.get(player)==null)
			return;
		transet.get(player).clear();
	}

	@Override
	public List<String> listTransNodes(Player player) {
		return listTransNodes(player.getName());
	}

	@Override
	public List<String> listTransNodes(String player) {
		if(transet.get(player) == null)
			return new ArrayList<String>();
		else
			return transet.get(player);
	}
	

}
