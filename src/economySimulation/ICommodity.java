package economySimulation;

public interface ICommodity {

	public double getBuyPrice();
	
	public void setBuyPrice(double price);
	
	public double getSellPrice();
	
	//no need for setSellPrice, this held at the StockExchange  <-- appleSellPrice & bananaSellPrice
	
}
