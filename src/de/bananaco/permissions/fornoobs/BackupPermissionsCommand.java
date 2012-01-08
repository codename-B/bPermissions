package de.bananaco.permissions.fornoobs;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.bukkit.plugin.java.JavaPlugin;

public class BackupPermissionsCommand {

	BackupPermissions file;

	public BackupPermissionsCommand(JavaPlugin plugin) {
		String name = plugin.getDescription().getName() + "_"
				+ plugin.getDescription().getVersion();
		this.file = new BackupPermissions("plugins/" + name + ".zip");
	}

	public void backup() {
		try {
			if (!file.exists())
				file.createNewFile();

			ZipOutputStream ze = file.getZipOutputStream();
			File pl = new File("plugins/bPermissions");
			File[] files = pl.listFiles();
			loop(files, ze);
			ze.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void loop(File[] subfiles, ZipOutputStream os) {
		for (File file : subfiles) {
			if (file.isDirectory() || file.getName().endsWith("/")
					|| file.getName().endsWith("\\")) {
				loop(file.listFiles(), os);
			} else
				try {
					write(file, os);
				} catch (Exception e) {
					e.printStackTrace();
				}
		}
	}

	public void write(File input, ZipOutputStream os) throws Exception {
		// The name of the .mcregion file
		String name = input.getPath();
		// Creates an entry in the zip for this name
		ZipEntry e = new ZipEntry(name);
		os.putNextEntry(e);
		// Reads from the original file, per byte
		BufferedInputStream is = new BufferedInputStream(new DataInputStream(
				new FileInputStream(input)));
		int isb = 0;
		while ((isb = is.read()) >= 0)
			// Writes to our zip, per byte
			os.write(isb);
		// Closes the inputstream
		is.close();
		// Closes the zipentry
		os.closeEntry();
	}

}