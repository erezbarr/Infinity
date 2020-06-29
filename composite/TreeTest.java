package il.co.ilrd.composite;

import org.junit.jupiter.api.Test;

class TreeTest {

	@Test
	void testTree() {
		Tree myTree = new Tree("/home/student/git/erez-barr/fs/");
				
		myTree.print();
		
	}
}
