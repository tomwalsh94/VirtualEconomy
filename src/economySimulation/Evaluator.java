package economySimulation;

import org.jgap.Chromosome;
import org.jgap.FitnessFunction;

public class Evaluator extends FitnessFunction {
	
	//static serializer meant static agent, and fitness is always returned as starting!
	//private static final long serialVersionUID = 1L;
	private LearningAgent agent;
	
	public Evaluator(LearningAgent learningAgent) {
		agent = learningAgent;
	}
	
	@Override
	public int evaluate(Chromosome chrom) {
		System.out.println("profit margin gene: " +(int) chrom.getGene(0).getAllele()); 
		System.out.println("quantity gene: " +(int) chrom.getGene(1).getAllele());
		agent.setProfitMargin((int) chrom.getGene(0).getAllele());
		agent.setCommodityQuantity((int) chrom.getGene(1).getAllele());
		if ((int) agent.predictNetWorth() > 0) {
			return (int) agent.predictNetWorth();
		} else return 1;
	}
	
}
