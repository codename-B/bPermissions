package de.bananaco.permissions.fornoobs;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.zip.ZipOutputStream;

public class BackupPermissions extends File {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5400853311732538433L;

	public BackupPermissions(String arg0) {
		super(arg0);
	}

	public ZipOutputStream getZipOutputStream() {
		ZipOutputStream ze = null;
		try {
			ze = new ZipOutputStream(new BufferedOutputStream(
					new FileOutputStream(this)));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ze;
	}

}
