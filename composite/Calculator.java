package il.co.ilrd.composite;

import java.util.HashMap;

public class Calculator {
		HashMap<String, Operator> mapOperator = new HashMap<>();
		
		interface Operator{
			int operate(int a, int b);
		}
		
		public Calculator() {
			Operator lambda = (a,b) -> {return a + b;};
			addOperator("+", lambda);
			
			addOperator("-", new Operator(){
			
				@Override
				public int operate(int a, int b) {
					return a - b;
				}
			});
			
		class Multiplication implements Operator{
				@Override
				public int operate(int a, int b) {
					return a*b;
				}
			}
		
			addOperator("*", new Multiplication());
			addOperator("/", new Divisor());
		}
		
		
		public void addOperator(String key, Operator o) {
			mapOperator.put(key, o);
		}
		
			public int operate(String key, int a, int b) {
				return mapOperator.get(key).operate(a, b);
		}
		class Divisor implements Operator{
		
			@Override
			public int operate(int a, int b) {
				return a/b;
			}
		
		}
}

