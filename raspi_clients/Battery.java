package il.co.ilrd.raspi_clients;

public class Battery {
	private int powerSupply = 100;
	
	public boolean isLow() {
		--powerSupply;
		if (powerSupply < 10) {
			return true;
		}
		return false;
	}
}