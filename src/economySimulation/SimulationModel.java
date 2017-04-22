package economySimulation;

import economySimulation.Agent;
import java.util.ArrayList;

import org.jgap.Chromosome;
import org.jgap.Configuration;
import org.jgap.FitnessFunction;
import org.jgap.Gene;
import org.jgap.Genotype;
import org.jgap.InvalidConfigurationException;
import org.jgap.impl.DefaultConfiguration;
import org.jgap.impl.IntegerGene;

import economySimulation.StockExchange;
import repast.simphony.context.Context;
import repast.simphony.dataLoader.ContextBuilder;
import repast.simphony.engine.schedule.ScheduledMethod;

public class SimulationModel implements ContextBuilder {
	
	//user input variables
	private int appleNum = 200;
	private double appleBuyPrice = 10;
	
	private int bananaNum = 100;
	private double bananaBuyPrice = 20;
	
	private static int agentNum = 2;
	private static double agentStarting = 1000; //initial wealth for each agent.
	
	private ArrayList<Agent> agents = new ArrayList<Agent>();
	
	private Configuration config;
	
	private Genotype population;
	
	//build method gets called at start of simulation.
	@Override
	public Context build(Context context) {
		try {
			buildGA();
		} catch (InvalidConfigurationException e) {
			e.printStackTrace();
		}
		//create the agents, add each one to the context, and to the agents list.
		for (int i = 0; i < agentNum; i++) {
			Agent agent = new Agent(agentStarting);
			context.add(agent);
			agents.add(agent);
		}
		System.out.println(agentNum + " agents created");
		
		//init the stockExchange
		StockExchange se = StockExchange.getInstance();
		se.init(appleBuyPrice, bananaBuyPrice, appleNum, bananaNum, agents);
		context.add(se);
		return context;
	}
	
	private void buildGA () throws InvalidConfigurationException {//Genetic Algorithm
		//build the config and the Genotype population.
		config = new DefaultConfiguration();
		FitnessFunction evaluator = new Evaluator();
		config.setFitnessFunction(evaluator);
		Gene[] genes = new Gene[1];
		genes[0] = new IntegerGene(1, 40);
		Chromosome chrom = new Chromosome (genes);
		config.setSampleChromosome(chrom);//only needs a sample, so get first learning agent
		config.setPopulationSize(agentNum);
		
		population = Genotype.randomInitialGenotype(config);
		System.out.println("buildGA hit");
	}
	
	@ScheduledMethod(start=2, interval=50, priority=14)
	public void setLearningAgentGenes() {
		System.out.println("setLearningAgentGenes hit");
		Chromosome winner = population.getFittestChromosome();
		//set target margin to value of fittest agent's target margin.
		for (int i = 0; i < agents.size(); i++) {
			agents.get(i).setTargetMargin((int) winner.getGene(0).getAllele());
		}
		population.evolve();
	}
	
}
