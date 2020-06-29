package il.co.ilrd.jarloader;

import java.io.IOException;

public class MainJarLoader {
	public static void main(String[] args) throws Exception {
		new JarLoader().load("RunMe", "/home/student/git/erez-barr/fs/Ex3Jar/testJar");
	}
}
