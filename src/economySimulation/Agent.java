package economySimulation;

import java.util.ArrayList;
import repast.simphony.engine.schedule.ScheduledMethod;
import java.util.concurrent.ThreadLocalRandom;

public class Agent {
	
	//goal/ objective of the agent is to maximise their total value of owned commodities through
	//trading on the stock exchange and producing new goods
	
	//operational capital for the agent to use in investments.
	private Capital capital;
	//perceived value of commodity
	protected double desiredValueApple;
	protected double desiredValueBanana;
	//bid increment
	protected double increment = 0.5;
	//desired quantity of commodity
	protected int desiredQuantity;
	//arraylist to hold what commodities the agent has.
	protected ArrayList<Commodity> portfolio;
	//hold a reference to the stock market to buy and sell commodities
	StockExchange se = StockExchange.getInstance();
	//profitMargin that agent will use to decide when to sell
	protected double profitMargin;
			
	//initialise agents with a demand, margin and desired quantity
	public Agent (double starting, double margin, int quantity) {
		capital = new Capital(starting);
		portfolio = new ArrayList<Commodity>();
		profitMargin = margin;
		desiredQuantity = quantity;
	}
	
	@ScheduledMethod(start=1, interval=1, priority=12)
	public void preStep() {
		System.out.println("---------------------------------------");
		//set perceived Value for each commodity
		desiredValueApple = ThreadLocalRandom.current().nextInt(1, 101); //value between 1 and 40
		desiredValueBanana = ThreadLocalRandom.current().nextInt(1, 101);
		//desiredQuantity = ThreadLocalRandom.current().nextInt(1, 11);
	}
	
	@ScheduledMethod(start=1, interval=1, priority=11)
	public void step() {
		System.out.println("Agent acting");
		checkSellPrice();
	}
	
	@ScheduledMethod(start=1, interval=1, priority=9)
	public void postStep() {
		takeStock(true); //print stock to console
		System.out.println("---------------------------------------");
	}
	
	public void checkSellPrice() {
		//check if sellprice is greater than buy price and profit margin for each item owned.
		//if so, sell commodity to stock exchange.
		Commodity com = null;
		for (int i = 0; i < portfolio.size(); i++) {
			com = portfolio.get(i);
			if (com instanceof Apple) {
				if ((com.getBuyPrice() + profitMargin) < se.getAppleSellPrice()) {
					//call the stock exchange to buy the commodity
					se.buyCommodity(com, this);
				}
			}
			if (com instanceof Banana) {
				if ((com.getBuyPrice() + profitMargin) < se.getBananaSellPrice()) {
					//call the stock exchange to buy the commodity
					se.buyCommodity(com, this);
				}
			}
		}
	}
	
	public double getProfitMargin() {
		return profitMargin;
	}
	
	public void setProfitMargin(int margin) {
		this.profitMargin = (double) margin;
	}
	
	//declare assets to the stock exchange. 
	public int declareAssets(String item, boolean print) {
		int count = 0;
		if (item == "A") {
			for (int i = 0; i < portfolio.size(); i++) {
				if (portfolio.get(i) instanceof Apple) {
					count++;
				}
			}
			if (print) {
				System.out.println("Agent number of apples: " + count);
			}
		} else if (item == "B") {
			for (int i = 0; i < portfolio.size(); i++) {
				if (portfolio.get(i) instanceof Banana) {
					count++;
				}
			}
			if (print) {
				System.out.println("Agent number of bananas: " + count);
			}
		}
		return count;
	}
	
	public double takeStock(Boolean print) {
		//net worth of the agent.
		double portfolioValue = 0;
		for (int i = 0; i < portfolio.size(); i++) {
			portfolioValue = portfolioValue + portfolio.get(i).getSellPrice();
		}
		double netWorth = portfolioValue + capital.getValue();
		if (print) {
			System.out.println("Profit margin: " + profitMargin);
			System.out.println("Net worth: " + netWorth);
			System.out.println("Capital: " +capital.getValue());
			System.out.println("Portfolio value: " +portfolioValue);
		}
		declareAssets("A", print);
		declareAssets("B", print);
		return netWorth;
	}
	
	public double getNetWorth() {
		return takeStock(false);
	}
	
	public boolean canBid(Commodity item) {
		if (item instanceof Apple) {
			if ((desiredQuantity * desiredValueApple) < capital.getValue()) {
				return true;
			}
		} else if (item instanceof Banana) {
			if ((desiredQuantity * desiredValueBanana) < capital.getValue()) {
				return true;
			}
		}
		return false;
	}
	
	public double makeBid(){
		return increment;
	}
	
	public double getCommodityPercievedValue(Commodity item) {
		if (item instanceof Apple) {
			return desiredValueApple;
		} else if (item instanceof Banana) {
			return desiredValueBanana;
		} else return 0;
	}
	
	public int getCommodityQuantity() {
		return desiredQuantity;
	}
	
	public void setCommodityQuantity(int quantity) {
		desiredQuantity = quantity;
	}

	public ArrayList<Commodity> getPortfolio() {
		return this.portfolio;
	}
	
	public void setPortfolio(ArrayList<Commodity> commodities) {
		this.portfolio.addAll(commodities);
	}
	
	public void setPortfolio(Commodity item) {
		this.portfolio.add(item);
	}
	
	public void setCapital(double amount) {
		if (amount > 0) {
			capital.setValue(amount);
		}
	}
	
	public double getCapital() {
		return capital.getValue();
	}

}
