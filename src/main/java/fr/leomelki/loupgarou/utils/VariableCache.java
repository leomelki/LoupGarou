package fr.leomelki.loupgarou.utils;

import java.util.HashMap;

public class VariableCache {
	private HashMap<String, Object> cache = new HashMap<String, Object>();
	public boolean getBoolean(String key) {
		Object object = get(key);
		return object == null ? false : (boolean)object;
	}
	public void set(String key, Object value) {
		if(cache.containsKey(key))
			cache.replace(key, value);
		else
			cache.put(key, value);
	}
	public boolean has(String key) {
		return cache.containsKey(key);
	}
	public <T> T get(String key) {
		return (T)cache.get(key);
	}
	public <T> T remove(String key) {
		return (T)cache.remove(key);
	}
	public void reset() {
		cache.clear();
	}
}
