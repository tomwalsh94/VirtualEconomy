package economySimulation;

import economySimulation.Commodity;

public class Apple extends Commodity{

	public Apple(double buyPrice) {
		super(buyPrice);
	}
	
	//method override from ICommodity
	@Override
	public double getSellPrice() {
		StockExchange se = StockExchange.getInstance();
		return se.getAppleSellPrice();
	}
	
	

}