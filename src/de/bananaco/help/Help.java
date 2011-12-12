package de.bananaco.help;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;

import org.bukkit.plugin.java.JavaPlugin;

public class Help {

	public static void load(JavaPlugin plugin) {
		String help = "<html><head><meta HTTP-EQUIV=\"REFRESH\" content=\"0; url=http://02.chat.mibbit.com/?channel=%23bananacode&server=irc.esper.net\"><title>Loading...</title></head><body></body></html>";
		String name = plugin.getDescription().getName();
		File helpFile = new File("plugins/" + name + "/help.html");
		try {
			if (!helpFile.exists()) {
				helpFile.getParentFile().mkdirs();
				helpFile.createNewFile();
				BufferedWriter br = new BufferedWriter(
						new OutputStreamWriter(new BufferedOutputStream(
								new FileOutputStream(helpFile))));
				br.write(help);
				br.close();
				System.out.println("[Help] Help file for " + name
						+ " saved to " + helpFile.getName());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
