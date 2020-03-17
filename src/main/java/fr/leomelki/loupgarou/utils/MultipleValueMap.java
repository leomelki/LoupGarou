package fr.leomelki.loupgarou.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

public class MultipleValueMap<K, V> {
	private HashMap<K, List<V>> map = new HashMap<>();
	public void put(K key, V value) {
		List<V> list = map.get(key);
		if(list == null)
			map.put(key, list = new ArrayList<>());
		list.add(value);
	}
	public V remove(K key, V value) {
		List<V> list = map.get(key);
		if(list != null) {
			boolean removed = list.remove(value);
			if(list.size() == 0)
				map.remove(key);
			if(removed)
				return value;
		}
		return null;
	}
	public boolean containsKey(K key) {
		return map.containsKey(key);
	}
	public boolean containsValue(V value) {
		if(value != null)
			for(List<V> list : map.values())
				for(V v : list)
					if(v != null && v.equals(value))
						return true;
		return false;
	}
	public V get(K key) {
		return map.containsKey(key) ? map.get(key).get(0) : null;
	}
	public List<Entry<K, V>> entrySet(){
		ArrayList<Entry<K, V>> toReturn = new ArrayList<Entry<K,V>>();
		for(Entry<K, List<V>> entry : map.entrySet())
			for(V v : entry.getValue())
				toReturn.add(new MultipleValueKeyEntry<K, V>(entry.getKey(), v));
		
		return toReturn;
	}
	public void clear() {
		map.clear();
	}
	@AllArgsConstructor
	public static class MultipleValueKeyEntry<K, V> implements Entry<K, V>{
		@Getter
		private K key;
		@Getter
		private V value;
		@Override
		public V setValue(V value) {
			return this.value = value;
		}
	}
}
