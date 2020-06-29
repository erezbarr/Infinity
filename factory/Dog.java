package il.co.ilrd.factory;

public class Dog extends Animal {

	public Dog() {
		super(2);
		System.out.println("Dog Ctor");
	}
	
	static {
		System.out.println("Static block Dog");
	}
	
	public void sayHello() {
		System.out.println("Dog Hello!");
		System.out.println("I have " + num_legs + " legs");
	}

	{
		System.out.println("Instance initialization block Dog");
	}
	
	@Override
	public String toString() {
		return "Dog with ID: " + ID;
	}

	@Override
	protected void finalize() throws Throwable {
		System.out.println("finalize Dog with ID: " + this.ID);
		super.finalize();
	}
	
	private int num_legs = 4;
}
