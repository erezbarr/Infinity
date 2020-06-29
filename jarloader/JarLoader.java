package il.co.ilrd.jarloader;

import java.util.List;
import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

public class JarLoader {
	JarFile jar;
	String interfaceName;

	public JarLoader() {
	}
	
	public List<Class<?>> load(String interfaceName, String jarpath) throws ClassNotFoundException, IOException{
		List<Class<?>> listToReturn = new LinkedList<>();
		
		jar = new JarFile(new File(jarpath));
		this.interfaceName = interfaceName;
		for (Enumeration<JarEntry> list = jar.entries(); list.hasMoreElements(); ) {
			ZipEntry entry = (ZipEntry) list.nextElement();
			
			if (entry.getName().contains(".class")) {
				String name = entry.getName().substring(0, entry.getName().indexOf("."));
				name = name.replace("/", ".");
				Class<?> c = Class.forName(name);//searches only in our package
				Class<?>[] interfaceList = c.getInterfaces();
				for (Class<?> class1 : interfaceList) {
//					System.out.println(class1.getName());
					if (class1.getName().contains(interfaceName)) {
						listToReturn.add(c);
						System.out.println(c.getName());

					}
				}					
			}
			
		}
		
		
		return listToReturn;
	}
}

