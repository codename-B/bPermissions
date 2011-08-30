package de.bananaco.permissions.config;

import java.util.Map;

public class CommentNode {
	public final Map<String, String> map;
	protected CommentNode(Map<String, String> map) {
		this.map = map;
	}
	public void clear() {
		map.clear();
	}
	public void comment(String node, String comment) {		
		if(map.containsKey(node))
		map.put(node, map.get(node)+"\r#"+comment.replace("#", ""));
		else
		map.put(node, "#"+comment.replace("#", ""));
	}
	public String getAndRemoveComment(String node) {
		if(!map.containsKey(node))
			return null;
		String comment = map.get(node);
		map.remove(node);
		return comment;
	}
	public String getComment(String node) {
		if(!map.containsKey(node))
			return null;
		String comment = map.get(node);
		return comment;
	}
}
