package economySimulation;

import java.util.ArrayList;

import repast.simphony.engine.schedule.ScheduledMethod;
import java.util.concurrent.ThreadLocalRandom;

import org.jgap.Chromosome;
import org.jgap.Gene;
import org.jgap.impl.IntegerGene;

public class Agent extends Chromosome{
	
	//goal/ objective of the agent is to maximise their total value of owned commodities through
	//trading on the stockechange and producing new goods
	
	//learning agent will represent a chromosome with genes that can mutate
	//the gene each LA will have is its target trading margin
	
	private static final long serialVersionUID = 1L;
	//operational capital for the agent to use in investments.
	private Capital capital;
	//percieved value of commodity
	private double percievedValueApple;
	private double percievedValueBanana;
	//bid increment
	private double increment = 0.5;
	//desired quantity of commodity
	private int desiredQuantityApple;
	private int desiredQuantityBanana;
	//arraylist to hold what commodities the agent has.
	private ArrayList<Commodity> portfolio;
	//hold a reference to the stock market to buy and sell commodities
	StockExchange se = StockExchange.getInstance();
	//trend for direction in which perceivedValue for commodity will follow at each step
	//this will be set every 100 steps
	private double trend;
	private int trendcount;
	private static final int interval = 100; //number of steps trend will be set for
	//profitMargin that agent will use to decide when to sell
	private double profitMargin;
	
	//initialise agents with a demand
	public Agent (double starting) {
		super(createGenes());
		profitMargin = 0;
		capital = new Capital(starting);
		portfolio = new ArrayList<Commodity>();
		//percievedValueApple = ThreadLocalRandom.current().nextInt(1, 21); //value between 1 and 10
		//percievedValueBanana = ThreadLocalRandom.current().nextInt(1, 21);
		//desiredQuantityApple = ThreadLocalRandom.current().nextInt(1, 11);
		//desiredQuantityBanana = ThreadLocalRandom.current().nextInt(1, 11);		
	}
	
	@ScheduledMethod(start=1, interval=1, priority=12)
	public void preStep() {
		//takeStock();
		System.out.println("---------------------------------------");
		//set percieved Value for each commodity
		percievedValueApple = ThreadLocalRandom.current().nextInt(1, 21); //value between 1 and 10
		percievedValueBanana = ThreadLocalRandom.current().nextInt(1, 21);
		desiredQuantityApple = ThreadLocalRandom.current().nextInt(1, 11);
		desiredQuantityBanana = ThreadLocalRandom.current().nextInt(1, 11);
		System.out.println("perceived values set");
	}
	
	@ScheduledMethod(start=1, interval=1, priority=11)
	public void step() {
		System.out.println("Agent stepping");

		//one iteration of simulation
		//subsumption architecture, if (cond) then method(); return;
		//look at some kind of decision-making strategy/payoff matrix for deciding what action to take.
		
		//sellCommod 
		double portfolioValue = 0;
		for (int i = 0; i < portfolio.size(); i++) {
			portfolioValue = portfolioValue + portfolio.get(i).getBuyPrice();
		}
		
		//check if sellprice is greater profit margin threshold for each item.
		//if so, sell commodity to stock exchange.
		Commodity com = null;
		for (int i = 0; i < portfolio.size(); i++) {
			com = portfolio.get(i);
			if (com instanceof Apple) {
				System.out.println("buy price and profit margin: " + (com.getBuyPrice() + profitMargin));
				System.out.println("sell price: " + se.getAppleSellPrice());
				if ((com.getBuyPrice() + profitMargin) < se.getAppleSellPrice()) {
					
					System.out.println("============================");
					System.out.println("Agent selling commodity apple");
					System.out.println("Commodity buy price: " + com.getBuyPrice());
					System.out.println("Commodity sell price: " + se.getAppleSellPrice());
					System.out.println("Profit: " + (se.getAppleSellPrice() - com.getBuyPrice()));
					System.out.println("============================");
					
					sellCommodity(com);
				}
			}
			if (com instanceof Banana) {
				System.out.println("buy price and profit margin: " + (com.getBuyPrice() + profitMargin));
				System.out.println("sell price: " + se.getBananaSellPrice());
				if ((com.getBuyPrice() + profitMargin) < se.getBananaSellPrice()) {
					
					System.out.println("============================");
					System.out.println("Agent selling commodity banana");
					System.out.println("Commodity buy price: " + com.getBuyPrice());
					System.out.println("Commodity sell price: " + se.getBananaSellPrice());
					System.out.println("Profit: " + (se.getBananaSellPrice() - com.getBuyPrice()));
					System.out.println("============================");
					
					sellCommodity(com);
				}
			}
		}
	}
	
	@ScheduledMethod(start=1, interval=1, priority=9)
	public void postStep() {
		takeStock();
		System.out.println("---------------------------------------");
		//evaluate effectiveness of strategy for this step
	}
	
	public void setTrend() {
		trend = ThreadLocalRandom.current().nextInt(1, 41);//new random 1-40
		trendcount = 0;
	}
	
	public void setMaxBid(Commodity item) {
		trendcount ++;
		double cmb = 0;
		if (item instanceof Apple) {
			cmb = percievedValueApple;//CurrentMaxBid
		} else if (item instanceof Banana) {
			cmb = percievedValueBanana;//CurrentMaxBid
		}
		//T(n) = T(n-1) + ((cmb - nmb) * (T(n) / T(interval)))
		cmb = (cmb + (cmb - trend) * (trendcount / interval));
		System.out.println("max bid: " + cmb);
		if (item instanceof Apple) {
			percievedValueApple = cmb;//CurrentMaxBid
		} else if (item instanceof Banana) {
			percievedValueBanana = cmb;//CurrentMaxBid
		}
	}
	
	public void setTargetMargin(int margin) {
		this.profitMargin = (double) margin;
	}
	
	public void sellCommodity(Commodity com) {
		se.buyCommodity(com, this);
	}
	
	private static Gene[] createGenes() {
		Gene[] genes = new Gene[1];
		genes[0] = new IntegerGene(1, 40);
		return genes;
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
	
	public double takeStock() {
		//net worth of the agent.
		double portfolioValue = 0;
		for (int i = 0; i < portfolio.size(); i++) {
			portfolioValue = portfolioValue + portfolio.get(i).getSellPrice();
		}
		double netWorth = portfolioValue + capital.getValue();
		System.out.println("Net worth: " + netWorth);
		System.out.println("Capital: " +capital.getValue());
		System.out.println("Portfolio value: " +portfolioValue);
		declareAssets("A", true);
		declareAssets("B", true);
		return netWorth;
	}
	
	public boolean canBid(Commodity item) {
		if (item instanceof Apple) {
			if ((desiredQuantityApple * percievedValueApple) < capital.getValue()) {
				return true;
			}
		} else if (item instanceof Banana) {
			if ((desiredQuantityBanana * percievedValueBanana) < capital.getValue()) {
				return true;
			}
		}
		return false;
	}
	
	public double makeBid(){
		return increment;
	}
	
	@Override //chromosome method
	public int getFitnessValue() {
		return (int) takeStock();
	}
	
	public double getCommodityPercievedValue(Commodity item) {
		if (item instanceof Apple) {
			return percievedValueApple;
		} else if (item instanceof Banana) {
			return percievedValueBanana;
		} else return 0;
	}
	
	public int getCommodityQuantity(Commodity item) {
		if (item instanceof Apple) {
			return desiredQuantityApple;
		} else if (item instanceof Banana) {
			return desiredQuantityBanana;
		} else {
			return 0;
		}
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
		capital.setValue(amount);
	}
	
	public double getCapital() {
		return capital.getValue();
	}
	
}
