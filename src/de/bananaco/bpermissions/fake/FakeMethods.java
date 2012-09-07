package de.bananaco.bpermissions.fake;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class FakeMethods {
	
	public static class FakeMethodsWrapper extends FakeMethods {
		
		public FakeMethodsWrapper(Map<String, Object> data) {
			super(data);
		}
		
	}
	
	public static final String SEPARATOR = "\\.";
	
	protected Map<String, Object> data;
	
	public FakeMethods() {
		this.data = new LinkedHashMap<String, Object>();
	}
	
	public FakeMethods(Map<String, Object> data) {
		this.data = data;
	}
	
	@SuppressWarnings("unchecked")
	public FakeMethods getFakeMethods(String path) {
		Object o = get(path);
		if(o instanceof Map) {
			Map<String, Object> d = (Map<String, Object>) o;
			FakeMethods fm = new FakeMethodsWrapper(d);
			return fm;
		} else {
			return null;
		}
	}
	
	@SuppressWarnings("unchecked")
	public Object get(String path) {
		String[] p = splitPath(path);
		Map<String, Object> data = this.data;
		// parts
		Object r = null;
		String pi = p[0];
		for(int i=0; i<p.length; i++) {
			pi = p[i];
			if(i < p.length-1) {
				data = (Map<String, Object>) data.get(pi);
			}
		}
		r = data.get(pi);
		return r;
	}
	
	@SuppressWarnings("unchecked")
	public void set(String path, Object value) {
		String[] p = splitPath(path);
		Map<String, Object> data = this.data;
		// parts
		String pi = p[0];
		for(int i=0; i<p.length; i++) {
			pi = p[i];
			if(data.containsKey(pi) && data.get(pi) instanceof Map) {
				data = (Map<String, Object>) data.get(pi);
			} else if(i < p.length-1) {
				data.put(pi, new LinkedHashMap<String, Object>());
				data = (Map<String, Object>) data.get(pi);
			}
		}
		data.put(pi, value);
	}
	
	public String getString(String path) {
		Object o = get(path);
		if(o instanceof String) {
			return (String) o;
		}
		return null;
	}
	
	public String getString(String path, String def) {
		String p = getString(path);
		if(p == null) {
			return def;
		}
		return p;
	}
	
	private String[] splitPath(String path) {
		return path.split(SEPARATOR);
	}

	@SuppressWarnings("unchecked")
	public List<String> getStringList(String path) {
		Object o = get(path);
		if(o instanceof List) {
			List<String> l = (List<String>) o;
			return l;
		}
		return null;
	}

	public Set<String> getKeys(boolean b) {
		return data.keySet();
	}

}
