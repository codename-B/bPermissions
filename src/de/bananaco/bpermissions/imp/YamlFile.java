package de.bananaco.bpermissions.imp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
/**
 * A handy utility to autocorrect common
 * yaml formatting errors
 */
public class YamlFile {
	
	private final File file;
	
	private final List<String> data = new ArrayList<String>();
	
	public YamlFile(File file, boolean fix) {
		this(file);
		if(fix) {
			parse();
			save();
		}
	}
	
	public YamlFile(File file) {
		this.file = file;
	}
	
	/**
	 * Reads all the data from the file into a List<String>
	 */
	public void load() {
		try {
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
		
		String line;
		while((line = br.readLine()) != null) {
			data.add(line);
		}
		
		br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Goes through the List<String> of the file data and attempts to correct any errors
	 */
	public void parse() {
		List<String> newData = new ArrayList<String>();
		if(data.size() == 0)
			load();
		for(int i=0; i<data.size(); i++) {
			String line = data.get(i);
			// Replace tabs with 2 spaces
			if(line.contains("\t")) {
				System.err.println("line "+i+" of "+file.getName() +" contained a Yaml error. A fix was attempted.");
				while(line.contains("\t"))
					line = line.replace("\t", "  ");
			}
			// Make sure keys have a key: at the end
			if(!line.replaceAll(" ", "").endsWith(":") && !line.replaceAll(" ", "").startsWith("-") && !line.endsWith("[]") && !line.contains(": ")) {
				System.err.println("line "+i+" of "+file.getName() +" contained a Yaml error. A fix was attempted.");
				line = line+":";
			}
			// Make sure that all 'strings' in a - 'string' list are escaped
			if(line.replaceAll(" ", "").startsWith("- ") && line.replaceAll(" ", "").replace("- ", "").startsWith("'") && !line.endsWith("'")) {
				System.err.println("line "+i+" of "+file.getName() +" contained a Yaml error. A fix was attempted.");
				line = line+"'";
			}
			// Ignore blank lines
			if(line.replaceAll(" ", "").equals("")) {
				System.err.println("line "+i+" of "+file.getName() +" contained a Yaml error. A fix was attempted.");
			}
			else {
			newData.add(line);
			}
		}
		
		data.clear();
		data.addAll(newData);
	}
	
	/**
	 * Saves the parsed data to the file (either corrected via parse() or original)
	 */
	public void save() {
		try {
			PrintWriter br = new PrintWriter((new FileOutputStream(file)));
			for(int i=0; i<data.size(); i++) {
				br.println(data.get(i));
			}
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Returns the original file
	 */
	public File getFile() {
		return file;
	}
	
}
