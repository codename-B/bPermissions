package de.bananaco.bpermissions.fake;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class FakeYaml extends FakeMethods {

	public static void main(String[] args) {
		StringBuffer sb = new StringBuffer();
		sb.append("users:").append("\n");
		sb.append("  ").append("codename_B:").append("\n");
		sb.append("  ").append("  ").append("groups:").append("\n");
		sb.append("  ").append("  ").append("- awesome").append("\n");
		sb.append("  ").append("  ").append("- admin").append("\n");
		sb.append("  ").append("  ").append("permissions: [test, ib]").append("\n");
		sb.append("  ").append("  ").append("extra: []").append("\n");
		char[] ch = sb.toString().toCharArray();
		byte[] bt = new byte[ch.length];
		for(int i=0; i<ch.length; i++) {
			bt[i] = (byte) ch[i];
		}
		try {
			ByteArrayInputStream is = new ByteArrayInputStream(bt);
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			FakeYaml fml = new FakeYaml();
			fml.load(is);
			fml.save(os);
			bt = os.toByteArray();
			is = new ByteArrayInputStream(bt);
			fml.load(is);
			System.out.println(fml.data.toString());
			System.out.println(fml.getStringList("users.codename_B.permissions"));
			fml.set("users.codename_B.meta.prefix", "test");
			System.out.println(fml.getString("users.codename_B.meta.prefix"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void debug(FakeYaml fml) {
		System.out.println(fml.data.toString());
	}

	public static final String SPACER = "  ";

	@SuppressWarnings("unchecked")
	public void load(InputStream is) throws Exception {
		List<String> lines = new ArrayList<String>();
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		while(true) {
			String line = br.readLine();
			if(line == null) {
				break;
			}
			lines.add(line);
		}
		br.close();
		Map<String, Object> buffer = new LinkedHashMap<String, Object>();
		Map<Integer, String> nesting = new LinkedHashMap<Integer, String>();
		for(int i=0; i<lines.size(); i++) {
			String line, next;
			line = lines.get(i);
			if(i == lines.size()-1) {
				next = "";
			} else {
				next = lines.get(i+1);
			}
			Type type = getType(line, next);
			if(type == Type.PATH) {
				int n = getNesting(line);
				if(n > 0) {
					Map<String, Object> data = buffer;
					for(int m=0; m<n; m++) {
						data = (Map<String, Object>) data.get(nesting.get(m));
					}
					data.put(getKey(line), new LinkedHashMap<String, Object>());
				} else {
					buffer.put(getKey(line), new LinkedHashMap<String, Object>());
				}
				nesting.put(n, getKey(line));
			} else if(type == Type.EMPTYARRAY) {
				List<String> array = new ArrayList<String>();
				int n = getNesting(line);
				if(n > 0) {
					Map<String, Object> data = buffer;
					for(int m=0; m<n; m++) {
						data = (Map<String, Object>) data.get(nesting.get(m));
					}
					data.put(getKey(line), array);
				} else {
					buffer.put(getKey(line), array);
				}
			} else if(type == Type.LINEARRAY) {
				int j = 1;
				List<String> array = new ArrayList<String>();

				while(true) {
					if(j+i>=lines.size()) {
						break;
					}
					String l = getLineFromArray(lines.get(i+j));
					j++;
					if(l.isEmpty()) {
						break;
					} else {
						array.add(l);
					}
				}
				// move counter forward
				int n = getNesting(line);
				if(n > 0) {
					Map<String, Object> data = buffer;
					for(int m=0; m<n; m++) {
						String nest = nesting.get(m);
						data = (Map<String, Object>) data.get(nest);
					}
					data.put(getKey(line), array);
				} else {
					buffer.put(getKey(line), array);
				}

			} else if(type == Type.ARRAY) {
				//String[] parts = line.split(": ");
				String partA = line.substring(0, line.indexOf(":"));
				String partB = line.substring(line.indexOf(":")+2, line.length());
				String arr = partB.substring(1, partB.length()-1);
				List<String> array = new ArrayList<String>();
				String[] ar = arr.split(", ");
				for(int j=0; j<ar.length; j++) {
					String a = ar[j];
					if(!a.isEmpty()) {
						array.add(a);
					}
				}
				int n = getNesting(partA);
				if(n > 0) {
					Map<String, Object> data = buffer;
					for(int m=0; m<n; m++) {
						data = (Map<String, Object>) data.get(nesting.get(m));
					}
					data.put(getKey(partA), array);
				} else {
					buffer.put(getKey(partA), array);
				}
			} else if(type == Type.KEY) {
				String[] parts = split(line);
				int n = getNesting(line);
				if(n > 0) {
					Map<String, Object> data = buffer;
					for(int m=0; m<n; m++) {
						data = (Map<String, Object>) data.get(nesting.get(m));
					}
					data.put(getKey(parts[0]), parts[1]);
				} else {
					buffer.put(getKey(parts[0]), parts[1]);
				}
			} else if(type == Type.BLANK) {
				// do nothing, it's blank
			}
		}
		// load to data
		data.putAll(buffer);
	}

	public void save(OutputStream os) {
		List<String> lines = new ArrayList<String>();
		for(String key : data.keySet()) {
			Object o = data.get(key);
			toArray(key, o, lines, 0);
		}
		PrintWriter pw = new PrintWriter(new OutputStreamWriter(os));
		for(int i=0; i<lines.size(); i++) {
			pw.println(lines.get(i));
		}
		pw.close();
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void toArray(String key, Object o, List<String> lines, int n) {
		StringBuffer sb = new StringBuffer();
		// add lines
		for(int i=0; i<n; i++) {
			sb.append("  ");
		}
		String start = sb.toString();
		// type of o
		Type type = getType(o);
		if(type == Type.PATH) {
			lines.add(start+key+":");
			Map<String, Object> data = (Map<String, Object>) o;
			for(String k : data.keySet()) {
				toArray(k, data.get(k), lines, n+1);
			}
		} else if(type == Type.EMPTYARRAY) {
			lines.add(start+key+": []");
		} else if(type == Type.LINEARRAY) {
			lines.add(start+key+":");
			List li = (List) o;
			// add lines
			for(int i=0; i<li.size(); i++) {
				lines.add(start+"- "+li.get(i).toString());
			}
		} else if(type == Type.KEY) {
			lines.add(start+key+": '"+o.toString()+"'");
		}
	}

	@SuppressWarnings("rawtypes")
	private Type getType(Object o) {
		if(o instanceof Map) {
			return Type.PATH;
		}
		if(o instanceof List) {
			if(((List) o).size() == 0) {
				return Type.EMPTYARRAY;
			}
			return Type.LINEARRAY;
		}
		return Type.KEY;
	}

	private Type getType(String line, String next) {
		if(line.isEmpty()) {
			return Type.BLANK;
		}
		String eline = line.replaceAll(" ", "");
		if(line.toCharArray()[0] == " ".toCharArray()[0] && eline.isEmpty()) {
			return Type.BLANK;
		}
		if(eline.startsWith("-")) {
			return Type.BLANK;
		}
		if(line.toCharArray()[0] == "#".toCharArray()[0]) {
			return Type.BLANK;
		}
		if(line.endsWith(":") && !next.endsWith(":") && next.replaceAll(" ", "").startsWith("-")) {
			return Type.LINEARRAY;
		}
		if(eline.endsWith(":[]")) {
			return Type.EMPTYARRAY;
		}
		if(line.contains(": ") && eline.endsWith("]")) {
			String spl = eline.substring(eline.indexOf(":")+1, eline.length());
			if(spl.startsWith("[")) {
				return Type.ARRAY;
			}
		}
		if(eline.toCharArray()[eline.length()-1] == ":".toCharArray()[0]) {
			return Type.PATH;
		}
		if(line.contains(": ")) {
			return Type.KEY;
		}
		return Type.BLANK;
	}

	private String getKey(String line) {
		while(line.startsWith(SPACER)) {
			line = line.substring(SPACER.length(), line.length());
		}
		if(line.endsWith("[]")) {
			line = line.substring(0, line.length()-2);
		}
		if(line.endsWith(": ")) {
			line = line.substring(0, line.length()-2);
		}
		if(line.endsWith(":")) {
			line = line.substring(0, line.length()-1);
		}
		while(line.endsWith(" ")) {
			line = line.substring(0, line.length()-1);
		}
		return line;
	}

	private int getNesting(String line) {
		line = line.split(":")[0];
		int nesting = 0;
		while(line.startsWith(SPACER)) {
			line = line.substring(SPACER.length(), line.length());
			nesting++;
		}
		return nesting;
	}

	private String getLineFromArray(String line) {
		while(line.startsWith(SPACER)) {
			line = line.substring(SPACER.length(), line.length());
		}
		// not an array line
		if(!line.startsWith("-")) {
			return "";
		}
		line = line.substring(2, line.length());
		return line;
	}

	private String[] split(String path) {
		// trim whitespace
		while(path.startsWith(SPACER)) {
			path = path.substring(SPACER.length(), path.length()); 
		}
		String[] parts = new String[2];
		parts[0] = path.substring(0, path.indexOf(": "));
		parts[1] = path.substring(path.indexOf(": ")+2, path.length());
		// trim "string" char
		if(parts[1].startsWith("'")) {
			parts[1] = parts[1].substring(1, parts[1].length());
		}
		if(parts[1].endsWith("'")) {
			parts[1] = parts[1].substring(0, parts[1].length()-1);
		}
		return parts;
	}

	static enum Type {

		BLANK,
		PATH,
		KEY,
		EMPTYARRAY,
		LINEARRAY,
		ARRAY;

	}

}
