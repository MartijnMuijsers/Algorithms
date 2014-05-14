package tue.algorithms.other;

public abstract class Debug {
	
	public static final boolean displayMethods = false;
	
	public static void log(String message) {
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
