package economySimulation;

import org.jgap.Chromosome;
import org.jgap.FitnessFunction;

public class Evaluator extends FitnessFunction {
	
	public Evaluator() {
		
	}
	
	@Override
	public int evaluate(Chromosome agent) {
		return agent.getFitnessValue();
	}
	
	//static method to return allele of target margin.
	//a_potentialSolution.getGene(a_position).getAllele();
	
}
