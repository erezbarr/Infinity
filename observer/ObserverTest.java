package il.co.ilrd.observer;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class ObserverTest {

	@Test
	void testRegitser() {
		Subject subject = new Subject();
		Observer observer = new Observer();
		
		observer.regitser(subject);
		subject.updateAll(10);
	}

	@Test
	void testUnregitser() {
		Subject subject = new Subject();
		Observer observer = new Observer();
		
		observer.regitser(subject);
		subject.updateAll(20);
	
		observer.unregitser(subject);
		subject.updateAll(30);
	
	}

	@Test
	void testUpdateAll() {
		Subject subject1 = new Subject();
		Subject subject2 = new Subject();
		Observer observer = new Observer();
		
		observer.regitser(subject1);
		observer.regitser(subject2);
		subject1.updateAll(40);	
		subject2.stopUpdate();	
	
	}

}
