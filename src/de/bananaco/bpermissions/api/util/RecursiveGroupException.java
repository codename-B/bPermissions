package de.bananaco.bpermissions.api.util;

public class RecursiveGroupException extends Exception {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7284838455798776408L;

	public RecursiveGroupException(Calculable c) {
		super("Recursive groups detected for "+c.getName());
	}

}
