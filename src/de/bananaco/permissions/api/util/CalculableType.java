package de.bananaco.permissions.api.util;

public enum CalculableType {

	GROUP("group"),
	USER("user");
	
	private final String name;
	
	CalculableType(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
	
}
