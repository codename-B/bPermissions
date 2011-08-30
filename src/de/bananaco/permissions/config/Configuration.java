package de.bananaco.permissions.config;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class Configuration extends ConfigurationNode {
	public static final String tab = "\t";
	private final File file;
	public Configuration(String string) {
		super(new HashMap<String,Object>());
		this.file = new File(string);
	}
	
	public Configuration(File file) {
		super(new HashMap<String, Object>());
		this.file = file;
	}
	private String convert(List<String> input) {
		String output = "";
		for(int i=0; i<input.size(); i++) {
			String in = input.get(i);
			in = in.replaceAll(":", "");
			if(i==input.size()-1)
				output = output+in;
			else
				output = output+in+".";
		}
		return output;
	}
	public void load() {
		try {
			if(!file.exists()) {
				file.getAbsoluteFile().getParentFile().mkdirs();
				file.createNewFile();
			}
			clear();
			readFile();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void save() {
		try {
			if(!file.exists()) {
				file.getAbsoluteFile().getParentFile().mkdirs();
				file.createNewFile();
			}
			saveFile();
			load();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	private int count(String input) {
		int i=0;
		while(input.startsWith(":")) {
			input = input.replaceFirst(":", "");
			i++;
		}
		return i;
	}
	private void saveFile() throws Exception {
		ArrayList<String> output = new ArrayList<String>();
		HashSet<String> comments = new HashSet<String>();
		
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new DataOutputStream(new FileOutputStream(file)),"UTF-8"));
		if(this.getComment("") != null)
			output.add(getAndRemoveComment(""));
		for(String key : getAll().keySet()) {
			String[] split = key.split("\\.");
			for(int i=0; i<split.length; i++) {
				String line = "";
				for(int p=0; p<=i; p++)
				line = (p==i)?line + split[p]:line + split[p] + ".";
				
				String addition = split[i]+":";
				for(int p=0; p<i; p++)
				addition = ":"+addition;
				for(int p=0; p<i; p++)
				addition = "\t"+addition;
				String tabs = "";
				for(int p=0; p<i; p++)
					tabs = "\t"+tabs;
				
				String comment = getComment(line);
				
				if(!output.contains(addition)) {
				output.add(addition);
				if(comment != null  && !comments.contains(line))
					output.add(tabs + comment);
					comments.add(line);
				}
			}
			String addition = "";
			for(int p=0; p<split.length; p++)
			addition = "\t"+addition;
			List<String> props = this.getStringList(key);
			for(String prop : props)
				output.add(addition+prop);
		}
		for(String line : output)
			bw.write(line+"\r");
		bw.flush();
		bw.close();
		output.clear();
		comments.clear();
	}
	private String strip(String line) {
		while(line.contains("\t"))
			line = line.replace("\t", "");
		return line;
	}
	private void readFile() throws Exception {
		ArrayList<String> lines = new ArrayList<String>();
		ArrayList<String> pLines = new ArrayList<String>();
		BufferedReader br = new BufferedReader(new InputStreamReader(new DataInputStream(new FileInputStream(file))));
		String line;
		int lastNotComment = 0;
		while((line = br.readLine()) != null)
			lines.add(strip(line));
		br.close();
		for(int i=0; i<lines.size(); i++) {
			line = lines.get(i);
			if(line.startsWith("#")) {
				this.comment(convert(pLines), line);
			}
			else if(line.endsWith(":") && !line.startsWith(":")) {
				pLines.clear();
				pLines.add(line);
				lastNotComment = i;
			}
			else if(line.endsWith(":")) {
				int thiscount = count(line);
				for(int p=pLines.size()-1; p>=thiscount; p--)
				pLines.remove(p);
				pLines.add(line);
				lastNotComment = i;
				}
			else if(!line.endsWith(":") && i>0 && lines.get(lastNotComment).endsWith(":")) {
				lastNotComment = i;
				ArrayList<String> props = new ArrayList<String>();
				for(int p=i; !lines.get(p).endsWith(":"); p++) {
					props.add(lines.get(p));
					if(p+1==lines.size())
						break;
				}
				this.setProperty(this.convert(pLines), props);
			}
	}
	lines.clear();
	pLines.clear();
	}
		
}