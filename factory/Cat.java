package il.co.ilrd.factory;

public class Cat extends Animal {

	public Cat() {
		this("black");
		System.out.println("Cat Ctor");
		this.num_masters = 2;
	}

	public static Cat createPurpleCats(Object obj) {
		return new Cat("Purple");
	}	

	public  Cat createMiztiCats(Object obj) {
		return new Cat("Gray");
	}	
		
	static {
		System.out.println("Static block Cat");
	}
	
	public Cat(String colors) {
		this.colors = colors;
		System.out.println("Cat Ctor with color: " + this.colors);
	}
	
	@Override
	public String toString() {
		return "Cat with ID: " + ID;
	}

	@Override
	protected void finalize() throws Throwable {
		System.out.println("finalize Cat with ID: " + this.ID);
		super.finalize();
	}
	
	private String colors;
	private int num_masters = 5;
}
