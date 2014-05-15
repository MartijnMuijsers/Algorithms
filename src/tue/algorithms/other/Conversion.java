package tue.algorithms.other;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

public abstract class Conversion {
	
	public static <T> T[] toArray(Collection<? extends T> collection, T[] array) throws IllegalArgumentException {
		if (array.length != collection.size()) {
			throw new IllegalArgumentException("Conversion.toArray(collection, array): array must have same size as given collection!");
		}
		int i = 0;
		for (T object : collection) {
			array[i] = object;
			i++;
		}
		return array;
	}
	
	public static Object[] toArray(Collection<? extends Object> collection) {
		Object[] array = new Object[collection.size()];
		int i = 0;
		for (Object object : collection) {
			array[i] = object;
			i++;
		}
		return array;
	}
	
	public static <T> ArrayList<T> toArrayList(Collection<T> collection) {
		ArrayList<T> arrayList = new ArrayList<T>(collection.size());
		for (T object : collection) {
			arrayList.add(object);
		}
		return arrayList;
	}
	
	public static <T> ArrayList<T> toArrayList(T[] array) {
		ArrayList<T> arrayList = new ArrayList<T>(array.length);
		for (T object : array) {
			arrayList.add(object);
		}
		return arrayList;
	}
	
	public static <T> HashSet<T> toHashSet(Collection<T> collection) {
		HashSet<T> hashSet = new HashSet<T>(collection.size());
		for (T object : collection) {
			hashSet.add(object);
		}
		return hashSet;
	}
	
	public static <T> HashSet<T> toHashSet(T[] array) {
		HashSet<T> hashSet = new HashSet<T>(array.length);
		for (T object : array) {
			hashSet.add(object);
		}
		return hashSet;
	}
	
}
