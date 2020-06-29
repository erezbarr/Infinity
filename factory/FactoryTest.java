package il.co.ilrd.factory;

import java.util.function.Function;

public class FactoryTest {
	
	public static void main(String[] args) {

		System.out.println(" ");
		System.out.println("Testing Lambda");
		System.out.println(" ");		
		
		LambdaFactory<Animal, Integer, Object> factory = new LambdaFactory<Animal, Integer, Object>();
		Function<Object, Dog> createDog = (dog1) -> new Dog();
		factory.add(5, createDog);
		factory.create(5).sayHello();	
		
		System.out.println(" ");
		System.out.println("********** Testing Anonimus **********");
		System.out.println(" ");
		
	
		
		AnonimusFactory<Animal, Integer, Object> factoryAN = new AnonimusFactory<Animal, Integer, Object>();
		
		factoryAN.add(1, new Function<Object, Cat>() {

			@Override
			public Cat apply(Object t) {
				Cat new_cat = new Cat((String)t);
				return new_cat;
			}		
		});
		
		factoryAN.create(1, "White");
		
		System.out.println(" ");
		System.out.println("********** Testing Static **********");
		System.out.println(" ");
		
		factory.add(2, Cat::createPurpleCats);// constructor in cat class
		factory.create(2);
				
		System.out.println(" ");
		System.out.println("********** Testing Instance **********");
		System.out.println(" ");		
		
		Cat mitzi = new Cat();
		factory.add(3, mitzi::createMiztiCats);// constructor in cat class
		factory.create(3);		
		
		System.out.println(" ");
		System.out.println("********** Testing Particular Type **********");
		System.out.println(" ");	
		
		LambdaFactory<Animal, Integer, Animal> factoryFive = new LambdaFactory<>();
		factoryFive.add(7,Animal::dog5);
		Animal moreDog = factoryFive.create(7, new Animal());
		System.out.println();
		
		
		
	}
}



