package tue.algorithms.other;

import java.util.ArrayList;
import java.util.HashMap;

public abstract class Debug {
	
	public static final boolean enabled = true;
	
	public static final boolean displayMethods = false;
	
	private static HashMap<String, ArrayList<String>> heldMessages = new HashMap<String, ArrayList<String>>();
	private static String holdingName = null;
	
	public static void startHold(String name) {
		if (enabled) {
			holdingName = name;
			heldMessages.put(name, new ArrayList<String>());
		}
	}
	
	public static void stopHold() {
		if (enabled) {
			holdingName = null;
		}
	}
	
	public static void release(String name) {
		if (enabled) {
			if (heldMessages.containsKey(name)) {
				for (String message : heldMessages.get(name)) {
					log(message);
				}
				if (name.equals(holdingName)) {
					heldMessages.put(name, new ArrayList<String>());
				} else {
					heldMessages.remove(name);
				}
			}
		}
	}
	
	public static void log(Object obj) {
		log(obj.toString());
	}
	
	public static void log(int x) {
		log(""+x);
	}
	
	public static void log(long x) {
		log(""+x);
	}
	
	public static void log(char x) {
		log(""+x);
	}
	
	public static void log(short x) {
		log(""+x);
	}
	
	public static void log(byte x) {
		log(""+x);
	}
	
	public static void log(float x) {
		log(""+x);
	}
	
	public static void log(double x) {
		log(""+x);
	}
	
	public static void log(String message) {
		if (enabled) {
			if (holdingName != null) {
				heldMessages.get(holdingName).add(message);
				return;
			}
			StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[2];
			String className = stackTraceElement.getClassName();
			for (int index = className.indexOf("."); index != (-1); index = className.indexOf(".")) {
				className = className.substring(index+1);
			}
			String methodName = stackTraceElement.getMethodName();
			String fileName = stackTraceElement.getFileName();
			if (fileName.endsWith(".java")) {
				fileName = fileName.substring(0, fileName.length()-5);
			}
			if (displayMethods) {
				if (className.contains("$" + fileName + "$") || className.startsWith(fileName + "$") || className.endsWith("$" + fileName) || className.equals(fileName)) {
					System.out.println("[" + className + "." + methodName + "] " + message);
				} else {
					System.out.println("[" + fileName + "/" + className + "." + methodName + "] " + message);
				}
			} else {
				if (className.contains("$" + fileName + "$") || className.startsWith(fileName + "$") || className.endsWith("$" + fileName) || className.equals(fileName)) {
					System.out.println("[" + className + "] " + message);
				} else {
					System.out.println("[" + fileName + "/" + className + "] " + message);
				}
			}
		}
	}
	
}
