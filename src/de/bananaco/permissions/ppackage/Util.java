package de.bananaco.permissions.ppackage;

public class Util {
	
	public static PPermission loadPerm(String permission) {
		
		if(permission.endsWith(":true")) {
            return new PPermission(permission.substring(0, permission.length()-6), false);
		} else if(permission.endsWith(":false")) {
            return new PPermission(permission.substring(0, permission.length()-6), false);
		} else if(permission.endsWith(": true")) {
            return new PPermission(permission.substring(0, permission.length()-6), false);
		} else if(permission.endsWith(": false")) {
            return new PPermission(permission.substring(0, permission.length()-7), false);
		} else  if(permission.startsWith("-")) {
		    return new PPermission(permission.substring(1), true);
		}
		return new PPermission(permission, true);
	}

}
