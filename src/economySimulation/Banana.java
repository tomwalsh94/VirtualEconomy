package economySimulation;

import economySimulation.Commodity;

public class Banana extends Commodity {
	
	public Banana (double buyPrice) {
		super(buyPrice);
	}

	//method override from ICommodity
	@Override
	public double getSellPrice() {
		StockExchange se = StockExchange.getInstance();
		return se.getBananaSellPrice();
	}
	
}
