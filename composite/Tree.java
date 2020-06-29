package il.co.ilrd.composite;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.io.*;

public class Tree {
	private FolderT newRoot;
	
	public Tree(String path) {		
		newRoot = new FolderT(path);
	}
	
	public void print() {
		newRoot.print(0);
	}	
	
	/****************************************************/
	
	private abstract class FileComponent {
		protected String name;
		public abstract void print(int indentation);
	}
	
	/****************************************************/

	private class FolderT extends FileComponent {

		ArrayList<FileComponent> list = new ArrayList<>();
		
		public FolderT(String path) {
			name = path;
			File[] fileList = Paths.get(path).toFile().listFiles();
			
			for(File iterator : fileList) {
				String newPath = iterator.getAbsolutePath();
				
				if(iterator.isFile()) {
					list.add(new FileT(newPath));
				}
				else {
					list.add(new FolderT(newPath));
				}
			}
		}

		@Override
		public void print(int spaces) {
			
			for(int i = 0; i < spaces; ++ i) {
				System.out.print("   ");				
			}
			
			System.out.println(Paths.get(name).getFileName());	
			
			for(FileComponent file: list) {
				file.print(spaces + 1);
			}
		}
	}
	
	/****************************************************/

	private class FileT extends FileComponent {
			
		public FileT(String path) {
			name = path;
		}

		@Override
		public void print(int spaces) {
			for(int i = 0; i < spaces; ++ i) {
				System.out.print("   ");				
			}
			System.out.println(Paths.get(name).getFileName());
		}
	}
}