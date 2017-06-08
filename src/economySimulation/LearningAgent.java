package economySimulation;

import java.util.concurrent.ThreadLocalRandom;

import org.jgap.Chromosome;
import org.jgap.Configuration;
import org.jgap.FitnessFunction;
import org.jgap.Gene;
import org.jgap.Genotype;
import org.jgap.impl.DefaultConfiguration;
import org.jgap.impl.IntegerGene;

public class LearningAgent extends Agent{
	
	//genetic learning algorithm
	private Genotype population;
	
	public LearningAgent(double starting, double margin, int quantity) throws Exception {
		super(starting, margin, quantity);
		//create Genetic Algorithm 
		//construct the gene
		Gene[] genes = new Gene[2];
		genes[0] = new IntegerGene(5, 25); //profit margin
		genes[1] = new IntegerGene(5, 25); //desired quantity
		//construct configuration object which handles- what
		Configuration conf = new DefaultConfiguration();
		//define fitness function which GA uses to select fittest chromosomes
		FitnessFunction evaluator = new Evaluator(this);
		conf.setFitnessFunction(evaluator);
		//construct a sample chromosome the configuration will use to define fitness
		Chromosome sampleChromosome = new Chromosome(genes);
		conf.setSampleChromosome(sampleChromosome);
		
		conf.setPopulationSize(10);
		
		population = Genotype.randomInitialGenotype(conf);
	}
	
	@Override
	public void preStep() {
		Chromosome[] chroms = population.getChromosomes();
		
		for (int i = 0; i < chroms.length; i++) {
			System.out.print(chroms[i].toString());
			System.out.println(chroms[i].getFitnessValue());

		}
		//System.out.println(population.getChromosomes());
		System.out.println("===============");
		System.out.println(population.getFittestChromosome().toString());
		//get the value(allele) for each gene and set it to the correct agent attribute.
		setProfitMargin((int) population.getFittestChromosome().getGene(0).getAllele() ); 
		setCommodityQuantity((int) population.getFittestChromosome().getGene(1).getAllele() );
		//next generation of chromosomes and genetic mutation.
		population.evolve(10);
		System.out.println(population.getFittestChromosome().toString());
		System.out.println(population.getFittestChromosome().getFitnessValue());
		desiredValueApple = ThreadLocalRandom.current().nextInt(1, 101);
		desiredValueBanana = ThreadLocalRandom.current().nextInt(1, 101);
	}
	
	public double predictNetWorth() {
		Commodity com = null;
		double predictedSales = 0;
		for (int i = 0; i < portfolio.size(); i++) {
			com = portfolio.get(i);
			if (com instanceof Apple) {
				System.out.println(com.getBuyPrice() + " " + profitMargin + " " + se.getAppleSellPrice());
				if ((com.getBuyPrice() + profitMargin) < se.getAppleSellPrice()) {
					System.out.println(se.getAppleSellPrice() + " " + com.getBuyPrice());
					predictedSales = predictedSales + se.getAppleSellPrice();
					//System.out.println("Predicted sales: " + predictedSales);
				}
			}
			if (com instanceof Banana) {
				if ((com.getBuyPrice() + profitMargin) < se.getBananaSellPrice()) {
					predictedSales = predictedSales + se.getBananaSellPrice();
					//System.out.println("Predicted sales: " + predictedSales);
				}
			}
		}
		System.out.println("Final Predicted sales: " + predictedSales);
		return predictedSales + takeStock(false);
	}
	
}
