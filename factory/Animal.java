package il.co.ilrd.factory;

public class Animal {
	static {
		System.out.println("Static block Animal 1");
	}
	
	{
		System.out.println("Instance initialization block Animal");
	}
	
	public Animal() {
		System.out.println("Animal Ctor");
		this.ID = ++counter;
		sayHello();
		showCounter();
		System.out.println(toString());
		System.out.println(super.toString());
	}
	
	public Animal (int num_masters) {
		System.out.println("Animal Ctor int");
		this.ID = ++counter;
		this.num_masters = num_masters;
	}
	
	public void sayHello() {
		System.out.println("Animal Hello!");
		System.out.println("I have " + num_legs + " legs");
	}
	
	public static void showCounter() {
		System.out.println(counter);
	}
	
	public int getNumMasters() {
		return this.num_masters;
	}
	
	@Override
	public String toString() {
		return "Animal with ID: " + ID;
	}
	
	@Override
	protected void finalize() throws Throwable {
		System.out.println("finalize Animal with ID: " + this.ID);
		super.finalize();
	}
	
	static {
		System.out.println("Static block Animal 2");
	}
	
	private int num_legs = 5;
	public static int counter = 0;
	private int num_masters = 1;
	int ID;
	
	public Dog dog5() {
		return new Dog();
	}
}
