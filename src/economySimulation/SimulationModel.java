package economySimulation;

import economySimulation.Agent;
import java.util.ArrayList;
import economySimulation.StockExchange;
import repast.simphony.context.Context;
import repast.simphony.dataLoader.ContextBuilder;
import repast.simphony.engine.schedule.ScheduledMethod;

public class SimulationModel implements ContextBuilder {
	
	//parameter list
	private String[] params = getInitParams();
	
	//user input variables
	//for experiment, these will remain constant, but can be changed by user if required.
	private int appleNum = 200;	
	private int bananaNum = 100;
	private static int agentNum = 10;
	private static double agentStarting = 100000; //initial wealth for each agent.
	private double agentMargin = 15; //profit margin for agent
	private static int agentQuantity = 25;
	private static boolean learning; //whether or not the agents use GLA.
	
	private ArrayList<Agent> agents = new ArrayList<Agent>();
	
	@Override
	public Context build(Context context) {		
		//create the agents, add each one to the context, and to the agents list.
		for (int i = 0; i < agentNum; i++) {
			Agent agent = null;
			try {
				agent = new LearningAgent(agentStarting, agentMargin, agentQuantity);
			} catch (Exception e) {
				e.printStackTrace();
			}
			context.add(agent);
			agents.add(agent);
		}
		System.out.println(agentNum + " agents created");
		
		//init the stockExchange
		StockExchange se = StockExchange.getInstance();
		se.init(appleNum, bananaNum, agents);
		context.add(se);
		return context;
	}
	
	public String[] getInitParams() {
		String[] params = {"agentMargin", "agentQuantity"};
		return params;
	}
	
	public void setAgentMargin(double margin) {
		agentMargin = margin;
	}
	
	public double getAgentMargin() {
		return agentMargin;
	}

}
