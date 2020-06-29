package il.co.ilrd.factory;

public class LegendaryAnimal extends Cat {

	public LegendaryAnimal() {
		System.out.println("Legendary Ctor");
	}

	public LegendaryAnimal factoryLA(Integer key) {
		return new  LegendaryAnimal();
	}
	
	static {
		System.out.println("Static block Legendary Animal");
	}
	
	public void sayHello() {
		System.out.println("Legendary Hello!");
	}

	@Override
	public String toString() {
		return "LegendaryAnimal with ID: " + ID;
	}

	@Override
	protected void finalize() throws Throwable {
		System.out.println("finalize LegendaryAnimal with ID: " + this.ID);
		super.finalize();
	}
}
