package economySimulation;

public abstract class Commodity implements ICommodity{

	private double buyPrice;
	
	//constructor
	public Commodity(double buyPrice) {
		this.buyPrice = buyPrice;
	}
	
	//getter
	public double getBuyPrice() {
		return buyPrice;
	}
	//setter, when commodity is bought by a new agent
	public void setBuyPrice(double price) {
		this.buyPrice = price;
	}
	
}
