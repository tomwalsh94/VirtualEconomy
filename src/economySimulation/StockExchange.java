package economySimulation;

import java.util.ArrayList;
import repast.simphony.engine.schedule.ScheduledMethod;

public class StockExchange {		
	
	private ArrayList<Commodity> commodities = new ArrayList<Commodity>(); 
	
	private ArrayList<Agent> agents = new ArrayList<Agent>();
	
	//these variables will be used for graphing supply and demand within the economy. 
	private double appleSellPrice = 0;
	private int currentAppleQuantity = 0;
	private double bananaSellPrice = 0;
	private int currentBananaQuantity = 0;
	
	private double exchangeRate = 0;
		
	//the stockExchange will implement a singleton design pattern.
	private static StockExchange se = new StockExchange();
	
	private StockExchange() {
		//exists only to enforce singleton design pattern.
	}
	public static StockExchange getInstance() {
		return se;
	}
	
	@ScheduledMethod(start=1000, interval=1000, priority=1)
	public void experiments () {
		double profitMargin = 0;
		for (int i = 0; i < agents.size(); i++) {
			profitMargin = profitMargin + agents.get(i).getProfitMargin();
		}
		
		double commodQ = 0;
		for (int i = 0; i < agents.size(); i++) {
			commodQ = commodQ + agents.get(i).getCommodityQuantity();
		}
		
		System.out.println("***************************************");
		System.out.println("Average learning agent proft margin: " + (profitMargin / agents.size()));
		System.out.println("Average learning agent commodity quantity: " + (commodQ / agents.size()));
		System.out.println("***************************************");
	}
	
	public void init(int appleQuantity, int bananaQuantity, ArrayList<Agent> createdAgents) {
		System.out.println("Stock exchange initialised");
		//create commodityFactory object
		CommodityFactory cf = new CommodityFactory();
		
		//set the variables used to keep track of commodities;
		currentAppleQuantity = appleQuantity;
		
		//create the commodities and put them in the arrayList.
		for (int i = 0; i < appleQuantity; i++) {
			Commodity a = cf.makeCommodity("A");
			commodities.add(a);
		}
		for (int i = 0; i < bananaQuantity; i++) {
			Commodity b = cf.makeCommodity("B");
			commodities.add(b);
		}
		
		//add the created agents to the agents arraylist so the stockmarket can keep track.
		agents.addAll(createdAgents);
		setExchangeRate();
	}
	
	@ScheduledMethod(start=1, interval=1, priority=10)
	public void step() {
		System.out.println("---------------------------------------");
		//initiate auction for apples and bananas.
		auction(new Apple(appleSellPrice));
		auction(new Banana(bananaSellPrice));
		tabulateGoods();
		setExchangeRate();
		printAttributes();
	}
	
	public void auction(Commodity item) {
		//multiunit English-style auction for agents to allocate resources.
		System.out.println("****************************");
		System.out.println("Auction starting");
		System.out.println("****************************");
		double bidValue = 0;
		double reservePrice = 0;
		Agent leadingBidder = null;
		ArrayList<Agent> bidders = new ArrayList<Agent>();
		bidders.addAll(agents);
		
		//remove agents who do not have the capital to bid
		for (int i = 0; i < bidders.size(); i++) {
			if (! bidders.get(i).canBid(item)) {
				bidders.remove(i);
			}
		}
		
		//assumes there is more than one agent in the simulation.
		//determine the winning bidder
		if (bidders.size() > 1) {
			while (bidders.size() != 1) { //there is no limit on rounds of bidding, so loop until only one agent remains.
				for (int i = 0; i < bidders.size(); i++) {
					if (bidders.get(i).getCommodityPercievedValue(item) > bidValue 
							&& (bidders.get(i).getCapital() > ((bidders.get(i).makeBid()) + bidValue) * bidders.get(i).getCommodityQuantity()) ) {
						//agent values the item higher than the current bid, so place a bid
						bidValue = bidValue + bidders.get(i).makeBid();
						leadingBidder = bidders.get(i);
					} else {
						//agent can't afford bid value, or doesn't want to buy it.
						bidders.remove(i);
					}
				}
			}
		}
		//sell the item to the winning bidder if the reserve price has been met.
		if (leadingBidder != null && bidValue >= reservePrice) {
			if (item instanceof Apple) {
				appleSellPrice = bidValue;
			} else if (item instanceof Banana) {
				bananaSellPrice = bidValue;
			}
			sellCommodity(item, leadingBidder, bidValue);
			if (item instanceof Apple) {
				System.out.println("Item: Apple");
			} else if (item instanceof Banana) {
				System.out.println("Item: Banana");
			}
			System.out.println("Quantity: "+leadingBidder.getCommodityQuantity());
			System.out.println("Price: " + bidValue);
			System.out.println("Total: " + (bidValue * leadingBidder.getCommodityQuantity()));
		} else {
			System.out.println("Reserve bid not reached for item " + item.toString());
		}
		System.out.println("---------------------------------------");
	}
	
	private void sellCommodity(Commodity com, Agent bidder, double price) {
		//get commodity from stock listing
		ArrayList<Commodity> toSell = new ArrayList<Commodity>();
		int quantity = bidder.getCommodityQuantity();
		Commodity counter = null;
		if (com instanceof Apple) {
			for (int i = 0; commodities.size() > i && quantity > 0; i++) {
				counter = commodities.get(i);
				if (counter instanceof Apple) {
					counter.setBuyPrice(price);//update buy price to reflect how much was paid for commodity.
					toSell.add(counter);//add to bill
					commodities.remove(counter);//remove the stock markets reference to the commodity.
					quantity--;
				}
			}
		} else if (com instanceof Banana) {
			for (int i = 0; commodities.size() > i && quantity > 0; i++) {
				counter = commodities.get(i);
				if (counter instanceof Banana) {
					counter.setBuyPrice(price);//update buy price to reflect how much was paid for commodity.
					toSell.add(counter);//add to bill
					commodities.remove(counter);//remove the stock markets reference to the commodity.
					quantity--;
				}
			}
		}
		bidder.setPortfolio(toSell);
		bidder.setCapital(bidder.getCapital() - (price * bidder.getCommodityQuantity()));
	} 
	
	public void buyCommodity(Commodity com, Agent seller) {
		if (com instanceof Apple) {
			seller.setCapital(seller.getCapital() + appleSellPrice);
		} else if (com instanceof Banana) {
			seller.setCapital(seller.getCapital() + bananaSellPrice);
		}
		seller.getPortfolio().remove(com);
		commodities.add(com);	
	}
	
	public void tabulateGoods() {
		//this method will tabulate the gross product quantities in the economy.
		//commodities arraylist owned by the stockexchange
		int appleCount = 0;
		int bananaCount = 0;
		for (int i = 0; i < commodities.size(); i++) {
			if (commodities.get(i) instanceof Apple) {
				appleCount++;
			} else if (commodities.get(i) instanceof Banana) {
				bananaCount++;
			}
		}
		//go through each agent and ask them to declare what commodities they have.
		for (int i = 0; i < agents.size(); i++) {
			appleCount = appleCount + agents.get(i).declareAssets("A", false);
			bananaCount = bananaCount + agents.get(i).declareAssets("B", false);
		}
		currentAppleQuantity = appleCount;
		currentBananaQuantity = bananaCount;
	}
	
	public void printAttributes() {
		System.out.println("---------------------------------------");
		System.out.println("Apple sell price " + appleSellPrice);
		System.out.println("Apple quantity " + currentAppleQuantity);
		System.out.println("Banana sell price " + bananaSellPrice);
		System.out.println("Banana quantity " + currentBananaQuantity);
		System.out.println("Exchange rate A/B: " + exchangeRate);
		System.out.println("Commodities owned by stock market: " + commodities.size());
		System.out.println("---------------------------------------");
	}
	
	private void setExchangeRate() { 
		if (appleSellPrice != 0 && bananaSellPrice != 0) {
			exchangeRate = appleSellPrice / bananaSellPrice;
		} else exchangeRate = 0;
	}
	
	public double getExchangeRate() {
		return exchangeRate;
	}
	
	public double getAppleSellPrice() {
		return appleSellPrice;
	}
	public double getBananaSellPrice() {
		return bananaSellPrice;
	}
	
	public int getAppleQuantity() {
		return currentAppleQuantity;
	}
	
	public int getBananaQuantity() {
		return currentBananaQuantity;
	}

}
