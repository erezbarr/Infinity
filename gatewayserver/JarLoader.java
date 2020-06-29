package il.co.ilrd.gatewayserver;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.jar.JarFile;

public class JarLoader {
	private final static String EMPTY_STRING = "";
	private final static String CLASS_SUFFIX  = ".class";
	private final static String SLASH  = "/";
	private final static String DOT  = ".";
	private final static String FILE_PREFIX = "file://";
	private final static String LOADING_MSG = "JarLoader just loaded class: ";
	private final static String VERSION_METHOD = "getVersion";
	private static Map<String, Integer> VersionsMap = new HashMap<>();

	public static List<Class<?>> load(String interfaceName, String jarPath) throws Exception {
		List<Class<?>> relevantClassesList = new LinkedList<>();
		
		try (JarFile jarFile = new JarFile(new File(jarPath));
			URLClassLoader classLoader = new URLClassLoader(new URL[] {new URL(FILE_PREFIX + jarPath)});){
			Enumeration<?> jarEntries = jarFile.entries();
			
			while(jarEntries.hasMoreElements()) {
				String iterEntry = jarEntries.nextElement().toString();
				
				if(iterEntry.contains(CLASS_SUFFIX)) {	
					iterEntry = setUpPathToLoad(iterEntry);
					Class<?> iterClass = classLoader.loadClass(iterEntry);
					
					for (Class<?> implementedInterfaceClass : iterClass.getInterfaces()) {
						if(implementedInterfaceClass.getName().contains(interfaceName) 
								&& versionIsHigherThanCurrent(iterClass)) {
							relevantClassesList.add(iterClass);
							System.out.println(LOADING_MSG + iterClass);
							break;
						}
					}
				}
			}
		} catch (IOException | 
				 ClassNotFoundException | 
				 InstantiationException | 
				 IllegalAccessException | 
				 IllegalArgumentException | 
				 InvocationTargetException | 
				 NoSuchMethodException | 
				 SecurityException e) {
			throw e;
		}
		
		return relevantClassesList;
		
		
	}
	
	private static String setUpPathToLoad (String path) {
		path = path.replace(SLASH, DOT);
		path = path.replace(CLASS_SUFFIX, EMPTY_STRING);
		
		return path;
	}
	
	private static boolean versionIsHigherThanCurrent(Class<?> iterClass) throws InstantiationException, 
																	   			 IllegalAccessException, 
																	   			 IllegalArgumentException, 
																	   			 InvocationTargetException, 
																	   			 NoSuchMethodException, 
																	   			 SecurityException {
		final String VERSION_CHANGE_MSG1 = "current version of ";
		final String VERSION_CHANGE_MSG2 = " is: ";
		final String VERSION_CHANGE_MSG3 = " ,new version is: ";
		boolean answer = false;
		Integer iterClassVersion = getVersionFromClass(iterClass);
		Integer currClassVersion = VersionsMap.get(iterClass.getName());
		
		if(currClassVersion != null && iterClassVersion > currClassVersion) {
			System.out.println(VERSION_CHANGE_MSG1 + iterClass + VERSION_CHANGE_MSG2 + currClassVersion + VERSION_CHANGE_MSG3 + iterClassVersion);
			VersionsMap.put(iterClass.getName(), iterClassVersion);
				
			answer = true;
		}
		
		return answer;
	}
	
	private static Integer getVersionFromClass(Class<?> iterClass) throws NoSuchMethodException, 
																		 SecurityException, 
																		 InstantiationException, 
																		 IllegalAccessException, 
																		 IllegalArgumentException, 
																		 InvocationTargetException {
		Method method = iterClass.getDeclaredMethod(VERSION_METHOD);
		Object instance = iterClass.getDeclaredConstructor().newInstance();
		
		return (Integer)method.invoke(instance);
	}
	
}