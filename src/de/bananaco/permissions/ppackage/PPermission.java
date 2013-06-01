package de.bananaco.permissions.ppackage;

public class PPermission {
	
	private final String name;
	private final boolean isTrue;
	
	public PPermission(String name, boolean isTrue) {
		this.name = name;
		this.isTrue = isTrue;
	}
	
	public String getName() {
		return name;
	}
	
	public boolean isTrue() {
		return isTrue;
	}

    @Override
    public String toString() {
        return isTrue?name:("-"+name);
    }

}
